package com.example.myapp.main.util;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A producer is required for injecting a SL4J Logger.
 * 
 * @see e.g. http://www.pavel.cool/jee-tips/Injecting-Logger/ and
 *      http://www.devsniper.com/injectable-logger-with-cdi/
 *
 */
@Named
@Singleton
public class LoggerProducer {

	@Produces
	public Logger produceLogger(InjectionPoint injectionPoint) {
		return LoggerFactory.getLogger(injectionPoint.getBean().getBeanClass());
	}

}