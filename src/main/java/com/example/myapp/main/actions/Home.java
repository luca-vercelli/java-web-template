/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.main.actions;

import com.opensymphony.xwork2.ActionSupport;

/**
 * This Action is called before loading the homepage.
 * Currently useless.
 *
 */
public class Home extends ActionSupport {

	private static final long serialVersionUID = 7397484529732988537L;

	@Override
	public String execute() {
		LOG.debug("Entering Home action");
		return SUCCESS;
	}
}
