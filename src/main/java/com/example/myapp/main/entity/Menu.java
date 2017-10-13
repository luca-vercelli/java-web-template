/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.main.entity;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "APP_MENU")
@XmlRootElement
public class Menu implements Serializable {

	private static final long serialVersionUID = -8243231626953359682L;

	private Long id;
	private String description;
	private Integer ordering;
	private Menu parentMenu;
	private String icon;

	private List<Menu> submenus = new ArrayList<>();
	private List<Page> pages = new ArrayList<>();
	private Set<Role> authorizedRoles = new HashSet<Role>();

	public Menu() {
	}

	public Menu(String description, Integer ordering, Menu parentMenu, String icon) {
		this.description = description;
		this.ordering = ordering;
		this.parentMenu = parentMenu;
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

	@Column(name = "ORDERING")
	public Integer getOrdering() {
		return ordering;
	}

	public void setOrdering(Integer ordering) {
		this.ordering = ordering;
	}

	@Column(name = "ICON")
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@ManyToOne
	@JoinColumn(name = "PARENT_MENU_ID")
	@XmlTransient
	public Menu getParentMenu() {
		return parentMenu;
	}

	public void setParentMenu(Menu parentMenu) {
		this.parentMenu = parentMenu;
	}

	@JoinTable(name = "APP_MENU_PAGES", joinColumns = @JoinColumn(name = "MENU_ID", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "PAGE_ID", referencedColumnName = "ID"))
	@ManyToMany
	@XmlTransient // FIXME should not be
	@OrderBy("ordering")
	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}

	@OneToMany(mappedBy = "parentMenu")
	@XmlTransient
	@OrderBy("ordering")
	public List<Menu> getSubmenus() {
		return submenus;
	}

	public void setSubmenus(List<Menu> submenus) {
		this.submenus = submenus;
	}

	/**
	 * No roles means that every user is authorized.
	 * 
	 * @return
	 */
	@ManyToMany
	@JoinTable(name = "APP_MENU_ROLES", joinColumns = @JoinColumn(name = "MENU_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
	@XmlTransient
	public Set<Role> getAuthorizedRoles() {
		return authorizedRoles;
	}

	public void setAuthorizedRoles(Set<Role> authorizedRoles) {
		this.authorizedRoles = authorizedRoles;
	}

	@Override
	public String toString() {
		return "Menu #" + description;
	}

	@Override
	public int hashCode() {
		return ("" + id).hashCode();
	}

	@Override
	public boolean equals(Object o2) {
		if (o2 == null || !(o2 instanceof Menu))
			return false;
		Menu r2 = (Menu) o2;
		if (r2.id == null || this.id == null)
			return false;
		return r2.id.equals(this.id);
	}
}
