/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.factory.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "APP_IMPLEMENTATIONS")
public class Implementations {
	private Long id;
	private String origClassName;
	private String replaceClassName;

	@Id
	@GeneratedValue
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "ORIG_CLASS_NAME")
	public String getOrigClassName() {
		return origClassName;
	}

	public void setOrigClassName(String origClassName) {
		this.origClassName = origClassName;
	}

	@Column(name = "REPL_CLASS_NAME")
	public String getReplaceClassName() {
		return replaceClassName;
	}

	public void setReplaceClassName(String replaceClassName) {
		this.replaceClassName = replaceClassName;
	}

}
