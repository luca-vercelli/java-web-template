package odata.jpa;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

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

	@Override
	public Response toResponse(Exception e) {

		int status;
		String message;
		if (e instanceof WebApplicationException) {
			// don't print stack trace
			// Unluckily, someone prints it anyway.
			status = ((WebApplicationException) e).getResponse().getStatus();

			if (status == Status.INTERNAL_SERVER_ERROR.getStatusCode())
				e.printStackTrace();

			message = Status.fromStatusCode(status).getReasonPhrase();
			if (e.getMessage() != null)
				message += " - " + e.getMessage();
		} else {
			e.printStackTrace();
			status = Status.INTERNAL_SERVER_ERROR.getStatusCode();
			message = Status.INTERNAL_SERVER_ERROR.getReasonPhrase();
		}

		ODataExceptionBean errorMessage = new ODataExceptionBean(status, message);

		return Response.status(status).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
	}
}