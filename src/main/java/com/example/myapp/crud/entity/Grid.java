/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.crud.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * In order to display an Entity on a grid, or to export it to an Excel sheet,
 * it's a good idea to declare entities and columns in the tables Grid and
 * GridColumn. Columns can refer to HQL (SQL?) functions, or to other tables,
 * thanks to JPA's relations.
 * 
 * @author luca vercelli
 *
 */
@Entity
@Table(name = "APP_GRID")
@XmlRootElement
public class Grid {

	private Long id;
	private String entity;
	private String description;
	private List<GridColumn> columns = new ArrayList<>();

	public Grid() {
	}

	/**
	 * Full constructor
	 */
	public Grid(String entity, String description) {
		this.entity = entity;
		this.description = description;
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

	@Column(name = "ENTITY", nullable = false)
	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(mappedBy = "grid", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@XmlTransient // FIXME should not be
	public List<GridColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<GridColumn> columns) {
		this.columns = columns;
	}

	@Override
	public String toString() {
		return "Grid #" + description + " for " + entity;
	}

	@Override
	public int hashCode() {
		return ("" + id).hashCode();
	}

	@Override
	public boolean equals(Object o2) {
		if (o2 == null || !(o2 instanceof Grid))
			return false;
		Grid r2 = (Grid) o2;
		if (r2.id == null || this.id == null)
			return false;
		return r2.id.equals(this.id);
	}
}
