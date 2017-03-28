/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.actions;

import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;

import com.opensymphony.xwork2.ActionSupport;

@InterceptorRefs({ @InterceptorRef("defaultStack") })
public class CheckLogin extends ActionSupport {

	private static final long serialVersionUID = 3120439958944110875L;

	//TODO: a webservice that checks for login
	
}
