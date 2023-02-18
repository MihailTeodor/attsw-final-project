package com.gurzumihail.library.model;

import java.util.Objects;
import java.util.Set;

public class User {
	
	private int id;
	private String name;
	private Set<Book> rentedBooks;

	
	public User(int id, String name, Set<Book> rentedBooks) {
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
	public Set<Book> getRentedBooks() {
		return rentedBooks;
	}
	public void setRentedBooks(Set<Book> rentedBooks) {
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
