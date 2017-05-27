/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.crud.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

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

	@Id
	@GeneratedValue
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "ENTITY")
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

	@OneToMany(mappedBy = "grid")
	public List<GridColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<GridColumn> columns) {
		this.columns = columns;
	}
}
