/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.crud.resources;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.example.myapp.crud.GenericManager;

import odata.jpa.AbstractDataManager;
import odata.jpa.AbstractRestResourcesEndpoint;

/**
 * REST WS Endpoint
 */
@Stateless
public class GenericRestResources extends AbstractRestResourcesEndpoint {

	@Inject
	GenericManager manager;

	@Override
	public AbstractDataManager manager() {
		return manager;
	}
}
