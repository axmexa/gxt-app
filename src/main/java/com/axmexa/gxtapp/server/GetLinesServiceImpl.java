package com.axmexa.gxtapp.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.axmexa.gxtapp.client.model.Item;
import com.axmexa.gxtapp.client.service.GetLinesService;
import com.google.gwt.dev.util.collect.HashSet;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GetLinesServiceImpl extends RemoteServiceServlet implements GetLinesService{
	
	private static final Logger logger = Logger.getLogger(GetLinesServiceImpl.class.getSimpleName());
	
	@Override
	public List<String> getLines(String filePath, String chaeset) {
		
		ArrayList<String> list = new ArrayList<String>();
		try {
			list.addAll(parseAndDeleteTempFile(filePath, chaeset));
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);	 
			return list;
		}
		
		return list;
	}

	private List<String> parseAndDeleteTempFile(String filePath, String chaeset) throws Exception {
		
		File file = new File(filePath);
		logger.warning(filePath + " - " + chaeset);
		ArrayList<String> list = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(
		           new InputStreamReader(
		                      new FileInputStream(file), chaeset));) {
			for (String line; (line = br.readLine()) != null;) {
				if (!line.trim().equals("")) // only non-empty strings
						list.add(line.trim());
							
			}
		}catch (Exception e) {
			file.delete();
			throw new Exception(e);
		}
		file.delete();
		return list;
	}
	
	public static void main(String[] args) {
		ArrayList<Item> list = new ArrayList<Item>();
		list.add(new Item("one"));
		list.add(new Item("two"));
		list.add(new Item("one"));
		
		System.out.println(list);
//		ArrayList<Item> list2 = new ArrayList<Item>();
		Set<Item> list2 = new HashSet<Item>();
		for (Item item : list) {
			Item item2 = new Item(item);
			System.out.print(item2 + " --> "+list2);
			System.out.println(" " +list2.contains(item2));
				list2.add(item2);
		}
		System.out.println(list2);
	}
}
