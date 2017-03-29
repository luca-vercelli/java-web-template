package com.example.myapp.util.interceptors;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;

/**
 * An abstract interceptor, with a Logger.
 *
 */
public abstract class AbstractInterceptor extends com.opensymphony.xwork2.interceptor.AbstractInterceptor {

	private static final long serialVersionUID = 5249297359119595386L;

	/**
	 * Why the hell com.opensymphony.xwork2.interceptor.AbstractInterceptor does
	 * not have this?
	 */
	protected final static Logger LOG = Logger.getLogger(ActionSupport.class);

}
