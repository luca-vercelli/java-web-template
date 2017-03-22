package com.example.myapp.crud;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.transaction.Transaction;

import org.hibernate.criterion.CriteriaQuery;
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
		EntityManager em = EntityManagerFactory.createEntityManager();
		em.close();
	}

	@Test
	public void testPersist() {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = EntityManagerFactory.createEntityManager();
			tx = em.getTransaction();
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
			if (em != null)
				em.close();
		}

		em = null;
		tx = null;
		try {
			em = EntityManagerFactory.createEntityManager();
			tx = em.getTransaction();
			TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE email= :email", User.class)
					.setParameter(";email", "goofy@example.com");
			List<User> users = query.getResultList();

			assertNotNull(users);
			assertFalse(users.isEmpty());
			assertTrue(users.size() == 1);

			//restore original DB
			em.remove(users.get(0));
			tx.commit();
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
