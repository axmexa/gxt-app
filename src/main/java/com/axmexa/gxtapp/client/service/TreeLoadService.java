package com.axmexa.gxtapp.client.service;

import java.util.List;

import com.axmexa.gxtapp.client.model.Item;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("loadtree")
public interface TreeLoadService extends RemoteService {

	public Item[] getItems();
}
