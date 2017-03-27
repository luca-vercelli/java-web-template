package com.example.myapp.login.helpers;

import com.example.myapp.factory.Factory;
import com.example.myapp.login.db.User;
import com.example.myapp.login.util.PasswordAuthentication;

public class UsersHelper {

	private static UsersHelper instance;

	private PasswordAuthentication PAinstance = Factory.getInstance().createObject(PasswordAuthentication.class);

	public static UsersHelper getInstance() {
		if (instance == null) {
			instance = Factory.getInstance().createObject(UsersHelper.class);
		}
		return instance;
	}

	public UsersHelper() {
	}

	/**
	 * Return a hash for given password.
	 * @param pwd
	 * @return
	 */
	public String getEncryptedPassword(String pwd) {
		return PAinstance.hash(pwd); //FIXME how to get char[] from Struts2 action?
	}

	/**
	 * Set user password
	 * 
	 * @param u
	 * @param unencryptedPassword
	 */
	public void setPassword(User u, String unencryptedPassword) {
		u.setEncryptedPassword(getEncryptedPassword(unencryptedPassword));
	}

	/**
	 * Test user password
	 */
	public boolean testPassword(User u, String unencryptedPassword) {
		boolean ret = u.getEncryptedPassword().equals(getEncryptedPassword(unencryptedPassword));
		return ret;
	}

	/**
	 * Fill array with zeroes
	 * 
	 * @param array
	 */
	public void clearArray(char[] array) {
		for (int i = 0; i < array.length; ++i)
			array[i] = 0;
	}

}
