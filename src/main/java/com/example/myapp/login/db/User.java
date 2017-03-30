/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.db;

import java.security.Principal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * A login user. This class implements Principal, so it can be integrated with
 * JAAS.
 *
 */
@Entity
@Table(name = "APP_USERS")
public class User implements Principal {

	private final static boolean LOGIN_USING_EMAIL = false; // this will keep
															// email and
															// username fields
															// equal

	private String username;
	private String email;
	private String encryptedPassword;
	private String personName;
	private String personSurname;
	private Date birthdate;
	private Boolean active = true;

	/**
	 * This is JAAS username, and also user's primary key.
	 */
	@Override
	@Id
	@Column(name="USERNAME")
	public String getName() {
		return username;
	}

	public void setName(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		String ret = "user #" + username;
		return ret;
	}

	@Column(name = "EMAIL")
	public String getEmail() {
		return LOGIN_USING_EMAIL ? this.username : this.email;
	}

	public void setEmail(String email) {
		this.email = email;
		if (LOGIN_USING_EMAIL)
			this.username = email;
	}

	// === OTHER IMPORTANT NON-KEY FIELDS ===============================
	
	@Column(name = "ACTIVE")
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Column(name = "PASSWD")
	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	// === OTHER FIELDS ===============================
	@Column(name = "NAME")
	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	@Column(name = "SURNAME")
	public String getPersonSurname() {
		return personSurname;
	}

	public void setPersonSurname(String personSurname) {
		this.personSurname = personSurname;
	}

	@Column(name = "BIRTHDATE")
	@Temporal(value = TemporalType.DATE)
	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

}
