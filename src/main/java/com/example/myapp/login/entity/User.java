/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.entity;

import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.security.auth.Subject;

import com.example.myapp.main.entity.Role;
import com.example.myapp.main.enums.Boolean;

/**
 * A login user. This class implements Principal, so it can be integrated with
 * JAAS. The key is *not* the email, instead it is a username.
 *
 */
@Entity
@Table(name = "APP_USERS")
public class User implements Principal {

	private String username;
	private String email;
	private String encryptedPassword;
	private String personName;
	private String personSurname;
	private Date birthdate;
	private Boolean active = Boolean.Y;

	private Set<Role> roles = new HashSet<Role>();

	/**
	 * This is JAAS username, and also user's primary key.
	 */
	@Override
	@Id
	@Column(name = "USERNAME", unique = true)
	public String getName() {
		return username;
	}

	public void setName(String username) {
		this.username = username;
	}

	/**
	 * I need to override this method, due to Javassist's current limitations.
	 * 
	 * @see java.security.Principal#implies(javax.security.auth.Subject)
	 */
	@Override
	public boolean implies(Subject subject) {
		if (subject == null)
			return false;
		return subject.getPrincipals().contains(this);
	}

	@Override
	public String toString() {
		return "user #" + username;
	}

	@Column(name = "EMAIL")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	// === OTHER IMPORTANT NON-KEY FIELDS ===============================

	@Column(name = "ACTIVE")
	@Enumerated(EnumType.ORDINAL)
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

	// == RELATIONS ==============================

	@OneToMany
	@JoinTable(name = "APP_USER_ROLES", joinColumns = @JoinColumn(name = "USERNAME"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}
