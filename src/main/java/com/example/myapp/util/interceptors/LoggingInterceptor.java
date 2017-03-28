/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.util.interceptors;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class LoggingInterceptor implements Interceptor {

	private static final long serialVersionUID = 294058339606947197L;

	private static Logger log = Logger.getLogger(LoggingInterceptor.class);

	@Override
	public void init() {
		log.info("Initializing LoggingInterceptor...");
	}

	@Override
	public void destroy() {
		log.info("Destroying LoggingInterceptor...");
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		String actionClassName = invocation.getAction().getClass().getName();
		long startTime = System.currentTimeMillis();
		log.info("Before calling action: " + actionClassName);

		String result = invocation.invoke();

		long endTime = System.currentTimeMillis();
		log.info("After calling action: " + actionClassName + " Time taken: " + (endTime - startTime) + " ms");

		return result;
	}

}
