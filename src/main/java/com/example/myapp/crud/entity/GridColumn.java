/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.crud.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.example.myapp.main.enums.BooleanYN;

@Entity
@Table(name = "APP_GRID_COLUMN")
public class GridColumn {
	private Long id;
	private Grid grid;
	private Integer order;
	private String attributeName;
	private String description;
	private BooleanYN readOnly;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "GRID_ID")
	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	@Column(name = "ORDERING") // "ORDER" not always allowed
	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Column(name = "ATTR_NAME")
	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "READ_ONLY")
	@Enumerated(EnumType.ORDINAL)
	public BooleanYN getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(BooleanYN readOnly) {
		this.readOnly = readOnly;
	}

}
