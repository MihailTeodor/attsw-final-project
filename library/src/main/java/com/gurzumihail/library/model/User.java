package com.gurzumihail.library.model;

import java.util.List;
import java.util.Objects;

public class User {
	
	private int id;
	private String name;
	private List<Book> rentedBooks;
	
	
	
	public User(int id, String name, List<Book> rentedBooks) {
		super();
		this.id = id;
		this.name = name;
		this.rentedBooks = rentedBooks;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Book> getRentedBooks() {
		return rentedBooks;
	}
	public void setRentedBooks(List<Book> rentedBooks) {
		this.rentedBooks = rentedBooks;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, rentedBooks);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return id == other.id && Objects.equals(name, other.name) && Objects.equals(rentedBooks, other.rentedBooks);
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + "]";
	}
	
	
	
	
}
