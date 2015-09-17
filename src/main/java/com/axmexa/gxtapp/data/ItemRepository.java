package com.axmexa.gxtapp.data;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.axmexa.gxtapp.client.model.Item;

public interface ItemRepository extends MongoRepository<Item, Integer> {

	Item findById(String id);
	List<Item> findByParentId(String id);
}
