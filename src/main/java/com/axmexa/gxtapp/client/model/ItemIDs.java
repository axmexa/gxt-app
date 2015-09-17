package com.axmexa.gxtapp.client.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Wrapper for store/load  ID`s of root items in mongo by SpringData 
 * 
 * @author ahmetshin
 *
 */
public class ItemIDs {

	@Field 
	private List<Integer> ids;

	public List<Integer> getIds() {
		return ids;
	}

	public void addId(Integer id){
		getIds().add(id);
	}
	
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	public ItemIDs() {
		super();
		this.ids = new ArrayList<Integer>();
	}
}
