package com.example.myapp.main.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handle non-trapped exceptions.
 * 
 * This will print exceptions in logs. However user will see an ugly page.
 * 
 * @see https://stackoverflow.com/questions/31289470
 *
 */
@Provider
public class DebugExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		exception.printStackTrace();
		return Response.serverError().entity(exception.getMessage()).build();
	}
}