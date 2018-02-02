/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.install;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

/**
 * This is the point where database is really populated for the first time.
 */
@WebServlet("/install")
public class InstallServlet extends HttpServlet {

	private static final long serialVersionUID = -2284681713126471835L;

	@Inject
	Logger LOG;

	@EJB
	InstallEJB ejb;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.getWriter().write("Preliminary checks...");

		if (!ejb.checkIfDbExists()) {
			response.getWriter().write("Cannot connect to database, or tables not created. Nothing done.");
			return;
		}

		if (ejb.checkIfDbPopulated()) {
			response.getWriter().write("Database was not empty. Nothing done.");
			return;
		}

		response.getWriter().write("Populating database...");

		ejb.populateDatabase();

		response.getWriter().write("Done.");
	}
}
