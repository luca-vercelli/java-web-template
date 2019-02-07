/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.example.myapp.crud.resources.GenericRestResources;

import odata.jpa.AbstractJAXRSApplication;

/**
 * Configures a JAX-RS endpoint.
 */
@ApplicationPath("/rest")
public class JAXRSConfiguration extends AbstractJAXRSApplication {

	@Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = super.getClasses();

        classes.add(GenericRestResources.class);

        return classes;
    }
}