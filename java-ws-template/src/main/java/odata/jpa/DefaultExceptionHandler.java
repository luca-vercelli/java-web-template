package odata.jpa;

import javax.ejb.EJBException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import odata.jpa.beans.ODataExceptionBean;

/**
 * I don't like the standard implementation: it gives printStackTrace() for
 * manually launched exceptions (which is useless) and not for uncatched
 * exceptions (which is very useful).
 * 
 * Response is returned in JSON. A better implementation would produce XML or
 * JSON according to accepted headers.
 */
@Provider
public class DefaultExceptionHandler implements ExceptionMapper<Exception> {

	public static final Logger LOG = LoggerFactory.getLogger(DefaultExceptionHandler.class);

	@Override
	public Response toResponse(Exception e) {

		int status;
		String message;
		
		// EJBException / EJBTransactionRolledbackException usually unuseful
		while (e instanceof EJBException && e.getCause() != null && e.getCause() instanceof Exception) {
			e = (Exception) e.getCause();
		}
		
		if (e instanceof WebApplicationException) {
			// don't print stack trace, if client issue
			// Unluckily, some library prints it anyway.
			status = ((WebApplicationException) e).getResponse().getStatus();

			if (status == Status.INTERNAL_SERVER_ERROR.getStatusCode())
				LOG.error("Internal server error", e);

			message = Status.fromStatusCode(status).getReasonPhrase();
			if (e.getMessage() != null)
				message += " - " + e.getMessage();
			
		} else if (e instanceof IllegalArgumentException) {
			status = Status.BAD_REQUEST.getStatusCode();
			message = e.getMessage();
			
		} else {
			LOG.error("Internal server error", e);
			status = Status.INTERNAL_SERVER_ERROR.getStatusCode();
			message = Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
		}

		ODataExceptionBean errorMessage = new ODataExceptionBean(status, message);

		return Response.status(status).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
	}
}
