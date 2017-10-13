/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "APP_PAGE")
@XmlRootElement
public class Page {

	private Long id;
	private String url;
	private String description;
	private Integer ordering;
	private String icon;

	private Set<Role> authorizedRoles = new HashSet<Role>();

	public Page() {
	}

	public Page(String url, String description, Integer ordering, String icon) {
		this.url = url;
		this.description = description;
		this.ordering = ordering;
		this.icon = icon;
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

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "URL", unique = true, nullable = false)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "ORDERING")
	public Integer getOrdering() {
		return ordering;
	}

	public void setOrdering(Integer ordering) {
		this.ordering = ordering;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * No roles means that every user is authorized.
	 * 
	 * @return
	 */
	@ManyToMany
	@JoinTable(name = "APP_PAGE_ROLES", joinColumns = @JoinColumn(name = "PAGE_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "ID"))
	@XmlTransient
	public Set<Role> getAuthorizedRoles() {
		return authorizedRoles;
	}

	public void setAuthorizedRoles(Set<Role> authorizedRoles) {
		this.authorizedRoles = authorizedRoles;
	}

	@Override
	public String toString() {
		return "Page #" + description;
	}

	@Override
	public int hashCode() {
		return ("" + id).hashCode();
	}

	@Override
	public boolean equals(Object o2) {
		if (o2 == null || !(o2 instanceof Page))
			return false;
		Page r2 = (Page) o2;
		if (r2.id == null || this.id == null)
			return false;
		return r2.id.equals(this.id);
	}
}
