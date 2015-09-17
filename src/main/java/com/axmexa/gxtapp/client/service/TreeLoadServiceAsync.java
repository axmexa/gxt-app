package com.axmexa.gxtapp.client.service;

import java.util.List;

import com.axmexa.gxtapp.client.model.Item;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeLoadServiceAsync {

	void getItems(AsyncCallback<Item[]> callback);

}
