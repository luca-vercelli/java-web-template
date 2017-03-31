package com.example.myapp.firstrun.actions;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import com.example.myapp.authorization.db.Role;
import com.example.myapp.crud.EntityManagerUtil;
import com.example.myapp.firstrun.db.Setup;
import com.example.myapp.login.db.User;
import com.example.myapp.login.helpers.UsersHelper;
import com.opensymphony.xwork2.ActionSupport;

/**
 * We assume that tables already exist, and we populate them.
 *
 */
// FIXME is it possible to switch to hibernate.hbm2ddl.auto=CREATE on the fly?
@InterceptorRefs({ @InterceptorRef("defaultStack") })
public class FirstRun extends ActionSupport {

	private static final long serialVersionUID = 2334736997192749615L;

	@Override
	public String execute() {

		EntityManager em = EntityManagerUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<Long> query;
			query = em.createQuery("SELECT COUNT(*) FROM Setup", Long.class);
			Long n = query.getSingleResult();
			System.out.println("" + n + " rows found in APP_SETUP, and " + (n == 0L));
			tx.commit();
			
			if (n == 0L) {
				// first access to database. We have to create a default user:
				// admin, with password admin, and role admin
				tx.begin();
				Role r = new Role();
				r.setDescription("admin");
				em.persist(r);

				User u = new User();
				u.setActive(true);
				u.setName("admin");
				u.setPersonName("Admin");
				u.setPersonSurname(".");
				u.setEmail("admin@example.com");
				
				UsersHelper.getInstance().setPassword(u, "admin");
				em.persist(u);

				u.getRoles().add(r);

				em.persist(u);

				Setup s = new Setup();
				s.setSetupDate(new Date());
				em.persist(s);
				tx.commit();
				
				addActionMessage(getText("firstrun.done"));
				return SUCCESS;

			} else {
				addActionError(getText("firstrun.err.already.run"));
				return SUCCESS; // could also be ERROR
			}

		} catch (Exception exc) {
			if (tx != null && tx.isActive())
				tx.rollback();
			LOG.error("Uncaught exception:", exc);
			return ERROR;
		}
	}
}
