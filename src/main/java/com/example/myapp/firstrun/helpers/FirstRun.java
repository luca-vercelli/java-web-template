/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.firstrun.helpers;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;

import com.example.myapp.login.helpers.UsersManager;
import com.example.myapp.main.entity.Menu;
import com.example.myapp.main.entity.Page;
import com.example.myapp.main.entity.Role;
import com.example.myapp.main.entity.Settings;
import com.example.myapp.main.entity.User;
import com.example.myapp.main.enums.BooleanYN;

/**
 * This is the point where database is really populated for the first time.
 */
@Stateless
public class FirstRun {

	@Inject
	Logger LOG;

	@PersistenceContext
	EntityManager em;

	@Inject
	UsersManager usersManager;

	public void populateDatabase() {

		LOG.info("Populating database...");

		// "ADMIN" ROLE AND USER
		Role r = new Role("admin");
		em.persist(r);

		User u = new User("admin", "admin@example.com", "Admin", ".", BooleanYN.Y, null);
		usersManager.setPassword(u, "admin".toCharArray());
		em.persist(u);

		u.getRoles().add(r);

		em.persist(u);

		// "USER" ROLE AND USER
		r = new Role("user");
		em.persist(r);

		u = new User("user", "user@example.com", "User", ".", BooleanYN.Y, null);
		usersManager.setPassword(u, "user".toCharArray());
		em.persist(u);

		u.getRoles().add(r);

		em.persist(u);

		// SETTINGS record
		Settings s = new Settings();
		em.persist(s);

		LOG.info("Done.");

		// PAGES
		Menu menuGeneral = new Menu("menu.section_general", 10, null, null);
		em.persist(menuGeneral);

		Menu menuLiveon = new Menu("menu.section_liveon", 20, null, null);
		em.persist(menuLiveon);

		Menu menuHome = new Menu("menu.home", 10, menuGeneral, "home");
		
		Page p1 = new Page("index.jsp", "page.dashboard", 10, null);
		em.persist(p1);
		Page p2 = new Page("index2.jsp", "page.dashboard2", 20, null);
		em.persist(p2);
		Page p3 = new Page("index3.jsp", "page.dashboard3", 30, null);
		em.persist(p3);
		
		List<Page> pages = menuHome.getPages();
		pages.add(p1);
		pages.add(p2);
		pages.add(p3);
		em.persist(menuHome);

	}
}
