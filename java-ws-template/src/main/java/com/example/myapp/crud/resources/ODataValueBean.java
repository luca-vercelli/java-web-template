package com.example.myapp.crud.resources;

public class ODataValueBean {

	private Object value;

	public ODataValueBean() {
	}

	public ODataValueBean(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
