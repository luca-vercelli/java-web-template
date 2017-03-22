package com.example.myapp.crud;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;











//FIXME still needed?











/**
 * Configures and provides access to Hibernate sessions.
 */
public class HibernateUtil {

	private static SessionFactory sessionFactory;
	private static Configuration configuration;
	private static Logger log = Logger.getLogger(HibernateUtil.class);

	private HibernateUtil() {
	}

	/**
	 * Create if needed, then get SessionFactory. A Configuration is created and
	 * configured if needed.
	 */
	public synchronized static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory = getConfiguration().buildSessionFactory();
		}
		return sessionFactory;
	}

	/**
	 * Rebuild hibernate session factory (but not its internal Configuration).
	 * 
	 */
	public synchronized static void rebuildSessionFactory() {
		try {
			sessionFactory = getConfiguration().buildSessionFactory();
		} catch (Exception e) {
			log.error("%%%% Error Creating SessionFactory %%%%");
			e.printStackTrace();
		}
	}

	/**
	 * Close the single hibernate session instance.
	 * 
	 * @throws HibernateException
	 */
	public static void closeSession() throws HibernateException {
		Session session = sessionFactory.getCurrentSession();
		if (session != null) {
			session.close();
		}
	}

	/**
	 * Get current connection, or create one as needed.
	 * 
	 * @return
	 */
	public static Session getSession() {
		// Should work with either thread and JTA
		Session session = null;
		try {
			session = getSessionFactory().getCurrentSession();
		} catch (HibernateException exc) {
		}

		if (session == null)
			session = getSessionFactory().openSession();
		return (Session) session;
	}

	/**
	 * Create and configure if needed, then get Configuration object
	 * 
	 */
	public synchronized static Configuration getConfiguration() {
		if (configuration == null) {
			configuration = new Configuration();
			configuration.configure();
		}
		return configuration;
	}

	/**
	 * Create new Configuration object, without configuring it. Use this before
	 * getSessionFactory if you want e.g. to change the configuration filename:
	 * 
	 * <code>HibernateUtil.newConfiguration().configure(somefilename)</code>
	 */
	public synchronized static Configuration newConfiguration() {
		configuration = new Configuration();
		return configuration;
	}

}
