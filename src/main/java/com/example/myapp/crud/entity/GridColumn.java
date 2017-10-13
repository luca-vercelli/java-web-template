/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
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
import javax.xml.bind.annotation.XmlTransient;

import com.example.myapp.main.enums.BooleanYN;

@Entity
@Table(name = "APP_GRID_COLUMN")
public class GridColumn {
	private Long id;
	private Grid grid;
	private Integer ordering;
	private String columnDefinition;
	private String description;
	private BooleanYN readOnly;

	public GridColumn() {
	}

	/**
	 * Full constructor.
	 */
	public GridColumn(Grid grid, String columnDefinition, String description, Integer order, BooleanYN readOnly) {
		this.grid = grid;
		this.columnDefinition = columnDefinition;
		this.description = description;
		this.ordering = order;
		this.readOnly = readOnly;
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

	@ManyToOne
	@JoinColumn(name = "GRID_ID", nullable = false)
	@XmlTransient
	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	@Column(name = "ORDERING") // "ORDER" not always allowed
	public Integer getOrdering() {
		return ordering;
	}

	public void setOrdering(Integer order) {
		this.ordering = order;
	}

	/**
	 * This formula must be understood both by HQL and Javascript...
	 * 
	 * @return
	 */
	@Column(name = "COL_DEF")
	public String getColumnDefinition() {
		return columnDefinition;
	}

	public void setColumnDefinition(String columnDefinition) {
		this.columnDefinition = columnDefinition;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "READ_ONLY", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	public BooleanYN getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(BooleanYN readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public String toString() {
		return "Grid Column #" + description + " for Grid #" + getGrid().getDescription();
	}

	@Override
	public int hashCode() {
		return ("" + id).hashCode();
	}

	@Override
	public boolean equals(Object o2) {
		if (o2 == null || !(o2 instanceof GridColumn))
			return false;
		GridColumn r2 = (GridColumn) o2;
		if (r2.id == null || this.id == null)
			return false;
		return r2.id.equals(this.id);
	}

}
