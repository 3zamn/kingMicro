package com.dobbu.login.entity;

import java.io.Serializable;

/**
 * @author Administrator
 *
 */
public class Good implements Serializable {
	
	private static final long serialVersionUID = -2000814403879577591L;
	
	String name;
	int price;
	
	public Good(String name, int price) {
		super();
		this.name = name;
		this.price = price;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
}
