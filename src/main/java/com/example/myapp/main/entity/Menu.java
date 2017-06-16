/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.main.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.example.myapp.authorization.entity.Role;

@Entity
@Table(name = "APP_MENU")
@XmlRootElement
public class Menu {

	private Long id;
	private String description; // FIXME what about i18n?
	private Integer ordering;
	private Menu parentMenu;
	
	private List<Menu> submenus = new ArrayList<>();
	private List<Page> pages = new ArrayList<>();
	private Set<Role> authorizedRoles = new HashSet<Role>();

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

	@Column(name = "ORDERING")
	public Integer getOrdering() {
		return ordering;
	}

	public void setOrdering(Integer ordering) {
		this.ordering = ordering;
	}

	@ManyToOne
	@JoinColumn(name = "PARENT_MENU_ID")
	public Menu getParentMenu() {
		return parentMenu;
	}

	public void setParentMenu(Menu parentMenu) {
		this.parentMenu = parentMenu;
	}

	@JoinTable(name = "APP_MENU_PAGES", joinColumns = {
			@JoinColumn(name = "MENU_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "PAGE_ID", referencedColumnName = "ID") })
	@ManyToMany
	@OrderBy("ordering")
	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	@OneToMany(mappedBy = "parentMenu")
	@OrderBy("ordering")
	public List<Menu> getSubmenus() {
		return submenus;
	}

	public void setSubmenus(List<Menu> submenus) {
		this.submenus = submenus;
	}

	/**
	 * No roles means that every user is authorized.
	 * @return
	 */
	@OneToMany
	@JoinTable(name = "APP_MENU_ROLES", joinColumns = @JoinColumn(name = "MENU_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
	public Set<Role> getAuthorizedRoles() {
		return authorizedRoles;
	}

	public void setAuthorizedRoles(Set<Role> authorizedRoles) {
		this.authorizedRoles = authorizedRoles;
	}

}
