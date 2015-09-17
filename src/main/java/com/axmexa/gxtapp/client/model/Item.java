package com.axmexa.gxtapp.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

@SuppressWarnings("serial")
public class Item implements Serializable{
	
	public interface ItemProperties extends PropertyAccess<Item> {
		@Path("id")
		ModelKeyProvider<Item> key();
		@Path("name")
	    ValueProvider<Item, String> name();
	  }
	
	@Id
	private Integer id;
	@Field
	private String name;
	@Field
	private boolean isDir = false;
	@Field
	private List<Item> items;

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	private static int COUNTER = 0;

	public Item() {
		this.id = Integer.valueOf(COUNTER++);
		isDir = false;
	}

	public boolean isDir() {
		return isDir;
	}

	public Item asDir() {
		items = new ArrayList<Item>();
		isDir = true;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Item(Item item){
		this(item.getName());
		isDir = false;
	}
	
	public Item(String name) {
		super();
		this.id = Integer.valueOf(COUNTER++);
		this.name = name;
		isDir = false;
	}

	@Override
	public String toString() {
		return "Item(" + getName() + ")";
	}

	public void addItem(Item it) {
		if (this.isDir())
			items.add(it);
		else 
			throw new UnsupportedOperationException("You can add childs only to isDir items ");
	}
}
