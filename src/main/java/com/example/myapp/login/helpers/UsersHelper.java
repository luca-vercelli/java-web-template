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

	public String getEncryptedPassword(char[] unencryptedPassword) {
		return PAinstance.hash(unencryptedPassword);
	}

	/**
	 * Set user password, then clear password array
	 * 
	 * @param u
	 * @param unencryptedPassword
	 */
	public void setPassword(User u, char[] unencryptedPassword) {
		u.setEncryptedPassword(getEncryptedPassword(unencryptedPassword));
		clearArray(unencryptedPassword);
	}

	/**
	 * Test password, then clear password array
	 */
	public boolean testPassword(User u, char[] unencryptedPassword) {
		boolean ret = u.getEncryptedPassword().equals(getEncryptedPassword(unencryptedPassword));
		clearArray(unencryptedPassword);
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
