package com.example.myapp.factory.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "APP_CLASS_REPLACEMENT")
public class ClassReplacement {
	private Long id;
	private String origClassName;
	private String replaceClassName;

	@Id
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
