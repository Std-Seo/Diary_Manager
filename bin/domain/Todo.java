package domain;

import java.time.LocalDate;

public class Todo {
	private int id;
	private String name;
	private LocalDate date;
	
	public Todo() {
		//nothing
	}
	
	public Todo(int id, String name, LocalDate date) {
		this.id = id;
		this.name = name;
		this.date = date;
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

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return name + " (" + date + ")";
	}	
	
}
