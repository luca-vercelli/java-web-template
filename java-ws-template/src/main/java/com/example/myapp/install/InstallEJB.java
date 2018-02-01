/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.install;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;

import com.example.myapp.crud.entity.Grid;
import com.example.myapp.crud.entity.GridColumn;
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
public class InstallEJB {

	@Inject
	Logger LOG;

	@PersistenceContext
	EntityManager em;

	@Inject
	UsersManager usersManager;

	public boolean checkIfDbExists() {

		if (em == null)
			return false;

		// is transaction required?
		try {
			TypedQuery<Long> query = em.createQuery("SELECT COUNT(*) FROM Settings", Long.class);
			query.getSingleResult();
			return true;

		} catch (Exception exc) {

			LOG.error("Error during /install", exc);

			return false;
		}
	}

	public boolean checkIfDbPopulated() {

		// is transaction required?
		try {
			TypedQuery<Long> query = em.createQuery("SELECT COUNT(*) FROM Settings", Long.class);
			Long n = query.getSingleResult();
			return n > 0;

		} catch (Exception exc) {

			LOG.error("Error during /install", exc);

			return false;
		}
	}

	public void populateDatabase() {
		// "ADMIN" ROLE AND USER
		Role roleAdmin = new Role("admin", "Webapp administrator");
		em.persist(roleAdmin);

		User u = new User("admin", "admin@example.com", "Admin", ".", BooleanYN.Y, null);
		u.setEncryptedPassword("8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918");
		// u.setEncryptedPassword("d033e22ae348aeb5660fc2140aec35850c4da997");
		// usersManager.setPassword(u, "admin".toCharArray());
		em.persist(u);

		roleAdmin.getUsers().add(u);
		em.persist(roleAdmin);

		// "STANDARD" ROLE AND "USER" USER
		Role roleStandard = new Role("standard", "Standard user");
		em.persist(roleStandard);

		u = new User("user", "user@example.com", "User", ".", BooleanYN.Y, null);
		u.setEncryptedPassword("04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb");
		// u.setEncryptedPassword("12dea96fec20593566ab75692c9949596833adc9");
		// usersManager.setPassword(u, "user".toCharArray());
		em.persist(u);

		roleStandard.getUsers().add(u);
		em.persist(roleStandard);

		// SETTINGS record
		Settings s = new Settings();
		em.persist(s);

		// PAGES
		Menu menuGeneral = new Menu("menu.section_general", 10, null, null);
		em.persist(menuGeneral);

		Menu menuLiveon = new Menu("menu.section_liveon", 20, null, null);
		em.persist(menuLiveon);

		Page page;
		List<Page> pages;
		Menu menu;

		menu = new Menu("menu.home", 10, menuGeneral, "home");
		pages = menu.getPages();

		page = new Page("index.jsp", "page.dashboard", 10, null);
		em.persist(page);
		pages.add(page);

		page = new Page("index2.jsp", "page.dashboard2", 20, null);
		em.persist(page);
		pages.add(page);

		page = new Page("index3.jsp", "page.dashboard3", 30, null);
		em.persist(page);
		pages.add(page);

		em.persist(menu);

		menu = new Menu("menu.admin", 15, menuGeneral, "table");
		pages = menu.getPages();

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

		em.persist(menu);

		menu = new Menu("menu.tables", 20, menuGeneral, "table");
		pages = menu.getPages();

		page = new Page("tables.jsp", "page.tables", 10, null);
		em.persist(page);
		pages.add(page);

		page = new Page("tables_dynamic.jsp", "page.tables_dynamic", 20, null);
		em.persist(page);
		pages.add(page);

		em.persist(menu);

		menu = new Menu("menu.forms", 30, menuGeneral, "edit");
		pages = menu.getPages();

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

		em.persist(menu);

		menu = new Menu("menu.ui_elements", 40, menuGeneral, "desktop");
		pages = menu.getPages();

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

		em.persist(menu);

		menu = new Menu("menu.data_presentation", 50, menuGeneral, "bar-chart-o");
		pages = menu.getPages();

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

		em.persist(menu);

		menu = new Menu("menu.layouts", 60, menuGeneral, "clone");
		pages = menu.getPages();

		page = new Page("fixed_sidebar.jsp", "page.layout_fixed_sidebar", 10, null);
		em.persist(page);
		pages.add(page);

		page = new Page("fixed_footer.jsp", "page.layout_fixed_footer", 20, null);
		em.persist(page);
		pages.add(page);

		em.persist(menu);

		menu = new Menu("menu.more", 10, menuLiveon, "bug");
		pages = menu.getPages();

		page = new Page("e_commerce.jsp", "page.e_commerce", 10, null);
		em.persist(page);
		pages.add(page);

		page = new Page("projects.jsp", "page.project", 20, null);
		em.persist(page);
		pages.add(page);

		page = new Page("project_detail.jsp", "page.project_detail", 30, null);
		em.persist(page);
		pages.add(page);

		page = new Page("contacts.jsp", "page.contacts", 40, null);
		em.persist(page);
		pages.add(page);

		page = new Page("profile.jsp", "page.profile", 50, null);
		em.persist(page);
		pages.add(page);

		em.persist(menu);

		Menu menuMultilevel = new Menu("menu.multilevel", 20, menuLiveon, "sitemap");
		List<Menu> submenus = menuMultilevel.getSubmenus();

		page = new Page("level2.jsp", "page.level_two", 10, null);
		em.persist(page);

		menu = new Menu("menu.level_one_a", 10, menuLiveon, "sitemap");
		menu.getPages().add(page);
		em.persist(menu);
		submenus.add(menu);

		menu = new Menu("menu.level_one_b", 20, menuLiveon, "sitemap");
		menu.getPages().add(page);
		em.persist(menu);
		submenus.add(menu);

		menu = new Menu("menu.level_one_c", 30, menuLiveon, "sitemap");
		menu.getPages().add(page);
		em.persist(menu);
		submenus.add(menu);

		em.persist(menuMultilevel);

		menu = new Menu("menu.extras", 30, menuLiveon, "bug");
		pages = menu.getPages();

		page = new Page("page_403.jsp", "page.error403", 10, null);
		em.persist(page);
		pages.add(page);

		page = new Page("page_404.jsp", "page.error404", 20, null);
		em.persist(page);
		pages.add(page);

		page = new Page("page_500.jsp", "page.error500", 30, null);
		em.persist(page);
		pages.add(page);

		page = new Page("plain_page.jsp", "page.plain", 40, null);
		em.persist(page);
		pages.add(page);

		page = new Page("page_login.jsp", "page.login", 50, null);
		em.persist(page);
		pages.add(page);

		page = new Page("pricing_tables.jsp", "page.pricing_tables", 60, null);
		em.persist(page);
		pages.add(page);

		em.persist(menu);

		// GRIDS
		Grid grid;

		grid = new Grid("Role", "grid.role.default");
		grid.getColumns().add(new GridColumn(grid, "id", "grid.role.id", 10, BooleanYN.Y));
		grid.getColumns().add(new GridColumn(grid, "description", "grid.role.description", 20, BooleanYN.N));
		em.persist(grid);

		grid = new Grid("User", "grid.user.default");
		grid.getColumns().add(new GridColumn(grid, "id", "grid.user.id", 10, BooleanYN.Y));
		grid.getColumns().add(new GridColumn(grid, "name", "grid.user.username", 20, BooleanYN.N));
		grid.getColumns().add(new GridColumn(grid, "personName", "grid.user.name", 30, BooleanYN.N));
		grid.getColumns().add(new GridColumn(grid, "personSurname", "grid.user.surname", 40, BooleanYN.N));
		em.persist(grid);

		grid = new Grid("Page", "grid.page.default");
		grid.getColumns().add(new GridColumn(grid, "id", "grid.page.id", 10, BooleanYN.Y));
		grid.getColumns().add(new GridColumn(grid, "description", "grid.page.description", 20, BooleanYN.N));
		em.persist(grid);

		grid = new Grid("Menu", "grid.menu.default");
		grid.getColumns().add(new GridColumn(grid, "id", "grid.menu.id", 10, BooleanYN.Y));
		grid.getColumns().add(new GridColumn(grid, "description", "grid.menu.description", 20, BooleanYN.N));
		em.persist(grid);

	}
}
