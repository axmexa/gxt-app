package com.axmexa.gxtapp.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("getlines")
public interface GetLinesService extends RemoteService{

	public List<String> getLines(String fileName, String charset);
}
