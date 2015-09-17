package com.axmexa.gxtapp.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GetLinesServiceAsync {

	void getLines(String fileName, String charset,
			AsyncCallback<List<String>> callback);

}
