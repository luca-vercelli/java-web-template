/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.main.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.example.myapp.authorization.entity.Role;

@Entity
@Table(name = "APP_PAGE")
public class Page {

	private Long id;
	private String url;
	private String description;

	private Set<Role> authorizedRoles = new HashSet<Role>();

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "URL")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@OneToMany
	@JoinTable(name = "APP_PAGE_ROLES", joinColumns = @JoinColumn(name = "PAGE_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
	public Set<Role> getAuthorizedRoles() {
		return authorizedRoles;
	}

	public void setAuthorizedRoles(Set<Role> authorizedRoles) {
		this.authorizedRoles = authorizedRoles;
	}
}
