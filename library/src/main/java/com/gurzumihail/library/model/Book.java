package com.gurzumihail.library.model;

import java.util.Objects;

public class Book {
	
	private int id;
	private String title;
	private String author;
	private boolean available;
	private int userID;
	
	
	public Book(int id, String title, String author) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.available = true;
		this.userID = -1;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public boolean isAvailable() {
		return available;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(author, available, id, title, userID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(author, other.author) && available == other.available && id == other.id
				&& Objects.equals(title, other.title) && userID == other.userID;
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", title=" + title + ", author=" + author + "]";
	}
}
