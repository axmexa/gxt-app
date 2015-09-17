package com.axmexa.gxtapp.client.service;

import com.axmexa.gxtapp.client.model.Item;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TreeSaveServiceAsync {

	void saveTree(Item[] rootNodes, AsyncCallback<String> callback);
	
}
