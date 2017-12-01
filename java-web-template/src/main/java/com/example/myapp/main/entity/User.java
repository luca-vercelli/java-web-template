/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.main.entity;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.security.auth.Subject;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.example.myapp.main.enums.BooleanYN;

/**
 * A login user. This class implements Principal, so it can be integrated with
 * JAAS. The key is <b>not</b> the email, instead it is a username. Uniqueness
 * is required on email and username.
 *
 */
@Entity
@Table(name = "APP_USER", indexes = { @Index(name = "idx_user_username", columnList = "USERNAME", unique = true),
		@Index(name = "idx_user_userpwd", columnList = "USERNAME, PASSWD", unique = true),
		@Index(name = "idx_user_email", columnList = "EMAIL", unique = true) })
@XmlRootElement
/*
 * @NamedQueries({
 * 
 * @NamedQuery(name = "findByUsernamePassword", query =
 * "from User where name = :name and active = :true"),
 * 
 * @NamedQuery(name = "findByEmailPassword", query =
 * "from User where name = :name and active = :true"),
 * 
 * @NamedQuery(name = "findByEmail", query =
 * "from User where name = :name and active = :true") }) // cannot // fix //
 * error
 */
public class User implements Principal, Serializable {

	private static final long serialVersionUID = 6226690496815744449L;

	private Long id;
	private String username;
	private String email;
	private String encryptedPassword;
	private String personName;
	private String personSurname;
	private Date birthdate;
	private BooleanYN active = BooleanYN.Y;
	private String passwordRecoveryCode;

	private Set<Role> roles = new HashSet<Role>();

	public User() {
	}

	public User(String username, String email, String personName, String personSurname, BooleanYN active,
			Date birthdate) {
		this.username = username;
		this.email = email;
		this.personName = personName;
		this.personSurname = personSurname;
		this.active = active;
		this.birthdate = birthdate;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This is JAAS username.
	 */
	@Override
	@Column(name = "USERNAME", unique = true, nullable = false)
	public String getName() {
		return username;
	}

	public void setName(String username) {
		this.username = username;
	}

	@Column(name = "EMAIL", unique = true)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "ACTIVE", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	public BooleanYN getActive() {
		return active;
	}

	public void setActive(BooleanYN active) {
		this.active = active;
	}

	@Column(name = "PASSWD")
	@XmlTransient
	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

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

	@Column(name = "PWD_RECOVERY_CODE")
	public String getPasswordRecoveryCode() {
		return passwordRecoveryCode;
	}

	public void setPasswordRecoveryCode(String passwordRecoveryCode) {
		this.passwordRecoveryCode = passwordRecoveryCode;
	}

	@ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
	@XmlTransient
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	/*
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

	@Override
	public int hashCode() {
		return username.hashCode();
	}

	@Override
	public boolean equals(Object o2) {
		if (o2 == null || !(o2 instanceof User))
			return false;
		User r2 = (User) o2;
		if (r2.username == null || this.username == null)
			return false;
		return r2.username.equals(this.username);
	}
}
