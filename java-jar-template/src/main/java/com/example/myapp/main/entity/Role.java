/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.main.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "APP_ROLE")
@XmlRootElement
public class Role implements Serializable {

	private static final long serialVersionUID = -1688293920379485224L;

	private Long id;
	private String roleName;
	private String description;
	private Set<User> users = new HashSet<User>();

	public Role() {
	}

	public Role(String roleName, String description) {
		this.roleName = roleName;
		this.description = description;
	}

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "ROLE_NAME", nullable = false, unique = true)
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * Map using ROLE_NAME and USERNAME, not ID. This is for jdbcRealm
	 * compatibility.
	 */
	@ManyToMany
	@JoinTable(name = "APP_USER_ROLE", joinColumns = @JoinColumn(name = "ROLE_NAME", referencedColumnName = "ROLE_NAME"), inverseJoinColumns = @JoinColumn(name = "USERNAME", referencedColumnName = "USERNAME"))
	@XmlTransient
	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Role #" + description;
	}

	@Override
	public int hashCode() {
		return ("" + id).hashCode();
	}

	@Override
	public boolean equals(Object o2) {
		if (o2 == null || !(o2 instanceof Role))
			return false;
		Role r2 = (Role) o2;
		if (r2.id == null || this.id == null)
			return false;
		return r2.id.equals(this.id);
	}
}
