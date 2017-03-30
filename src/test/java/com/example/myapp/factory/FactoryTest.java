package com.example.myapp.factory;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.myapp.crud.EntityManagerUtil;
import com.example.myapp.factory.db.Implementations;

public class FactoryTest {

	@BeforeClass
	public static void setUp() {
		// may require a different factory
	}

	@AfterClass
	public static void shutDown() {
	}

	@Test
	public void testFactory() {

		Factory f = Factory.getInstance();
		Foo obj1 = f.createObject(Foo.class);
		assertTrue(obj1.getClass().equals(Foo.class));
		
		EntityManager em = EntityManagerUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {

			tx.begin();
			
			Implementations i = new Implementations();
			i.setOrigClassName(Foo.class.getName());
			i.setReplaceClassName(Bar.class.getName());
			em.persist(i);

			tx.commit();

			Foo obj2 = f.createObject(Foo.class);

			// restore original db
			tx.begin();
			i = em.find(Implementations.class, i.getId());
			em.remove(i);
			tx.commit();

			assertTrue(obj2.getClass().equals(Bar.class));

		} finally {
			if (tx != null && tx.isActive())
				tx.rollback();
		}
	}

	public static class Foo {

	}

	public static class Bar extends Foo {

	}
}
