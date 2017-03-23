package com.example.myapp.firstrun.actions;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import com.example.myapp.authorization.db.AuthUser;
import com.example.myapp.authorization.db.Role;
import com.example.myapp.crud.EntityManagerFactory;
import com.example.myapp.firstrun.db.Setup;
import com.opensymphony.xwork2.ActionSupport;

/**
 * We assume that tables already exist, and we populate them.
 *
 */
public class FirstRun extends ActionSupport {

	private static final long serialVersionUID = 2334736997192749615L;

	@Override
	public String execute() {

		EntityManager em = EntityManagerFactory.createEntityManager(); // FIXME
																		// ...
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<Long> query;
			query = em.createQuery("SELECT COUNT(*) FROM Setup", Long.class);
			Long n = query.getSingleResult();
			if (n == 0L) {
				// first access to database. We have to create a default user:
				// admin, with password admin, and role admin
				Role r = new Role();
				r.setDescription("admin");
				em.persist(r);

				AuthUser u = new AuthUser();
				u.setActive(true);
				u.setName("Admin");
				u.setUserId("admin");
				u.setEmail("admin@example.com");
				em.persist(u);

				u.getRoles().add(r);

				em.persist(u);

				Setup s = new Setup();
				s.setSetupDate(new Date());
				em.persist(s);
				tx.commit();
			} else {
				tx.commit();
				addActionError("firstrun.already.run");
				return ERROR;
			}

		} catch (Exception exc) {
			if (tx != null && tx.isActive())
				tx.rollback();
		}
		return ERROR;
	}
}
