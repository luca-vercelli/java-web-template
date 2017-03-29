package com.example.myapp.crud;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.myapp.login.db.User;

public class HibernateTest {

	@BeforeClass
	public static void setUp() {
		// may require a different factory
	}

	@AfterClass
	public static void shutDown() {
	}

	@Test
	public void testEntityManager() {
		EntityManager em = EntityManagerUtil.getEntityManager();
		em.close();
	}

	@Test
	public void testPersist() {
		EntityManager em = EntityManagerUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			User u = new User();
			u.setName("Goofy's");
			u.setSurname("Goofy");
			u.setEmail("goofy@example.com");
			em.persist(u);

			tx.commit();
		} finally {
			if (tx != null && tx.isActive())
				tx.rollback();
		}

		try {
			tx.begin();

			TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE email= :email", User.class)
					.setParameter("email", "goofy@example.com");
			List<User> users = query.getResultList();

			// restore original DB
			for (User u : users)
				em.remove(u);

			tx.commit();

			assertNotNull(users);
			assertFalse(users.isEmpty());
			assertEquals(1, users.size());

		} finally {
			if (tx != null && tx.isActive())
				tx.rollback();
			if (em != null)
				em.close();
		}

	}

	@Test
	public void testRelations() {
		fail("TODO"); // TODO
	}
}
