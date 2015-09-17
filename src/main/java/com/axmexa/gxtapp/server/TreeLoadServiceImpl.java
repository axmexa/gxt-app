package com.axmexa.gxtapp.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.axmexa.gxtapp.client.model.Item;
import com.axmexa.gxtapp.client.model.ItemIDs;
import com.axmexa.gxtapp.client.service.TreeLoadService;
import com.axmexa.gxtapp.server.config.GxtAppContextListener;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;

public class TreeLoadServiceImpl extends RemoteServiceServlet implements TreeLoadService {

	MongoTemplate template;
	private static final Logger logger = Logger.getLogger(TreeLoadServiceImpl.class.getSimpleName());
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		final ApplicationContext springContext = (ApplicationContext) getServletContext().getAttribute(GxtAppContextListener.GxtAppConfig);
		template = springContext.getBean(MongoTemplate.class);
	}

	@Override
	public Item[] getItems() {

		ItemIDs itemIDs = template.find(new Query(), ItemIDs.class).get(0);
		
		List<Item> result = new ArrayList<>();
		for (Integer id : itemIDs.getIds()) {
			result.add(template.findById(id, Item.class));
		}
		
		return result.toArray(new Item[0]);
	}

}
