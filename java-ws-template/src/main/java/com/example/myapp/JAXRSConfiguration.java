/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp;

import javax.ws.rs.ApplicationPath;

import odata.jpa.AbstractJAXRSApplication;

/**
 * Configures a JAX-RS endpoint.
 */
@ApplicationPath("/rest")
public class JAXRSConfiguration extends AbstractJAXRSApplication {
	
}