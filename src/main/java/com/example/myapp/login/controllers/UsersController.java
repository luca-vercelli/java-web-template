package com.example.myapp.login.controllers;

import com.example.myapp.factory.Factory;
import com.example.myapp.login.db.User;
import com.example.myapp.login.util.PasswordAuthentication;

public class UsersController {

	private static UsersController instance;
	private static PasswordAuthentication PAinstance;

	public static UsersController getInstance() {
		if (instance == null) {
			instance = Factory.getInstance().createObject(UsersController.class);
			PAinstance = Factory.getInstance().createObject(PasswordAuthentication.class);
		}
		return instance;
	}

	public UsersController() {
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
