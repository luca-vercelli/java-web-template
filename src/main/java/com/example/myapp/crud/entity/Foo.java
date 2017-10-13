/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.crud.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "EXAMPLE_FOO")
@XmlRootElement
public class Foo {

	private Long id;
	private String name;
	private Integer height;
	private String address;
	private Double age;
	private Date birthday;
	// TODO add some ENUM
	private List<Bar> bars = new ArrayList<>();

	@Id
	@GeneratedValue
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "NAME", length = 50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "HEIGHT")
	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	@Column(name = "ADDRESS", length = 500)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "AGE")
	public Double getAge() {
		return age;
	}

	public void setAge(Double age) {
		this.age = age;
	}

	@Column(name = "BIRTHDAY")
	@Temporal(value = TemporalType.DATE)
	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@OneToMany
	public List<Bar> getFoos() {
		return bars;

	}

	public void setFoos(List<Bar> bars) {
		this.bars = bars;

	}
}
