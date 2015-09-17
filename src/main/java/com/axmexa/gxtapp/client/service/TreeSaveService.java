package com.axmexa.gxtapp.client.service;

import com.axmexa.gxtapp.client.model.Item;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("savetree")
public interface TreeSaveService extends RemoteService {

	String saveTree(Item[] rootNodes);
}
