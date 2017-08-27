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
		Role roleAdmin = new Role("admin");
		em.persist(roleAdmin);

		User u = new User("admin", "admin@example.com", "Admin", ".", BooleanYN.Y, null);
		usersManager.setPassword(u, "admin".toCharArray());
		em.persist(u);

		u.getRoles().add(roleAdmin);

		em.persist(u);

		// "USER" ROLE AND USER
		Role roleUser = new Role("user");
		em.persist(roleUser);

		u = new User("user", "user@example.com", "User", ".", BooleanYN.Y, null);
		usersManager.setPassword(u, "user".toCharArray());
		em.persist(u);

		u.getRoles().add(roleUser);

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

		Page page;
		List<Page> pages;

		Menu menuHome = new Menu("menu.home", 10, menuGeneral, "home");

		pages = menuHome.getPages();

		page = new Page("index.jsp", "page.dashboard", 10, null);
		em.persist(page);
		pages.add(page);

		page = new Page("index2.jsp", "page.dashboard2", 20, null);
		em.persist(page);
		pages.add(page);

		page = new Page("index3.jsp", "page.dashboard3", 30, null);
		em.persist(page);
		pages.add(page);

		em.persist(menuHome);

		Menu menuAdmin = new Menu("menu.admin", 15, menuGeneral, "table");
		pages = menuAdmin.getPages();

		page = new Page("crud.jsp?entity=User", "page.crud_users", 10, null);
		page.getAuthorizedRoles().add(roleAdmin);
		em.persist(page);
		pages.add(page);

		page = new Page("crud.jsp?entity=Role", "page.crud_roles", 20, null);
		page.getAuthorizedRoles().add(roleAdmin);
		em.persist(page);
		pages.add(page);

		page = new Page("crud.jsp?entity=Page", "page.crud_pages", 30, null);
		page.getAuthorizedRoles().add(roleAdmin);
		em.persist(page);
		pages.add(page);

		page = new Page("crud.jsp?entity=Menu", "page.crud_menus", 40, null);
		page.getAuthorizedRoles().add(roleAdmin);
		em.persist(page);
		pages.add(page);

		em.persist(menuAdmin);

		Menu menuTables = new Menu("menu.tables", 20, menuGeneral, "table");
		pages = menuTables.getPages();

		page = new Page("tables.jsp", "page.tables", 10, null);
		em.persist(page);
		pages.add(page);

		page = new Page("tables_dynamic.jsp", "page.tables_dynamic", 20, null);
		em.persist(page);
		pages.add(page);

		em.persist(menuTables);

		Menu menuForms = new Menu("menu.forms", 30, menuGeneral, "edit");
		pages = menuForms.getPages();

		page = new Page("form.jsp", "page.form", 10, null);
		em.persist(page);
		pages.add(page);

		page = new Page("form_advanced.jsp", "page.form_advanced", 20, null);
		em.persist(page);
		pages.add(page);

		page = new Page("form_validation.jsp", "page.form_validation", 30, null);
		em.persist(page);
		pages.add(page);

		page = new Page("form_wizards.jsp", "page.form_wizards", 40, null);
		em.persist(page);
		pages.add(page);

		page = new Page("form_upload.jsp", "page.form_upload", 50, null);
		em.persist(page);
		pages.add(page);

		page = new Page("form_buttons.jsp", "page.form_buttons", 60, null);
		em.persist(page);
		pages.add(page);

		em.persist(menuForms);

		Menu menuUIElements = new Menu("menu.ui_elements", 40, menuGeneral, "desktop");
		pages = menuForms.getPages();

		page = new Page("general_elements.jsp", "page.ui_general_elements", 10, null);
		em.persist(page);
		pages.add(page);

		page = new Page("media_gallery.jsp", "page.ui_media_gallery", 20, null);
		em.persist(page);
		pages.add(page);

		page = new Page("typography.jsp", "page.ui_typography", 30, null);
		em.persist(page);
		pages.add(page);

		page = new Page("icons.jsp", "page.ui_icons", 40, null);
		em.persist(page);
		pages.add(page);

		page = new Page("glyphicons.jsp", "page.ui_glyphicons", 50, null);
		em.persist(page);
		pages.add(page);

		page = new Page("widgets.jsp", "page.ui_widgets", 60, null);
		em.persist(page);
		pages.add(page);

		page = new Page("invoice.jsp", "page.ui_invoice", 70, null);
		em.persist(page);
		pages.add(page);

		page = new Page("inbox.jsp", "page.ui_inbox", 80, null);
		em.persist(page);
		pages.add(page);

		page = new Page("calendar.jsp", "page.ui_calendar", 90, null);
		em.persist(page);
		pages.add(page);

		em.persist(menuUIElements);

		Menu menuDataPresentation = new Menu("menu.data_presentation", 50, menuGeneral, "bar-chart-o");
		pages = menuForms.getPages();

		page = new Page("chartjs.jsp", "page.chartjs", 10, null);
		em.persist(page);
		pages.add(page);

		page = new Page("chartjs2.jsp", "page.chartjs2", 20, null);
		em.persist(page);
		pages.add(page);

		page = new Page("morisjs.jsp", "page.morisjs", 30, null);
		em.persist(page);
		pages.add(page);

		page = new Page("echarts.jsp", "page.echarts", 40, null);
		em.persist(page);
		pages.add(page);

		page = new Page("other_charts.jsp", "page.charts_other", 50, null);
		em.persist(page);
		pages.add(page);

		em.persist(menuDataPresentation);
	}
}
