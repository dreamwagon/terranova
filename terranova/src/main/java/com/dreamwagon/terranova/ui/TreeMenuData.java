package com.dreamwagon.terranova.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class TreeMenuData {

	public TreeMenuData(String name, EventHandler<ActionEvent> event) {
		super();
		this.name = name;
		this.event = event;
	}
	private String name;
	private EventHandler<ActionEvent> event;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public EventHandler<ActionEvent> getEvent() {
		return event;
	}
	public void setEvent(EventHandler<ActionEvent> event) {
		this.event = event;
	}

	@Override
	public String toString() {
		return name;
	}
}
