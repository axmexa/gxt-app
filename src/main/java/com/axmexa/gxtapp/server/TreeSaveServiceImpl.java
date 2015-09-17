package com.axmexa.gxtapp.server;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.axmexa.gxtapp.client.model.Item;
import com.axmexa.gxtapp.client.model.ItemIDs;
import com.axmexa.gxtapp.client.service.TreeSaveService;
import com.axmexa.gxtapp.server.config.GxtAppContextListener;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TreeSaveServiceImpl extends RemoteServiceServlet implements TreeSaveService {

	MongoTemplate template;
	Logger logger = Logger.getLogger(TreeSaveServiceImpl.class.getSimpleName());
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		super.init(config);
		final ApplicationContext springContext = (ApplicationContext) getServletContext().getAttribute(GxtAppContextListener.GxtAppConfig);
		
		template = springContext.getBean(MongoTemplate.class);
	}
	
	@Override
	public String saveTree(Item[] rootNodes) {
		
		logger.warning("Try to remove all items & list root Ids");
		logger.warning("Items to be remove: " + template.findAll(Item.class));
		
		template.remove(new Query(), Item.class);
		template.remove(new Query(), ItemIDs.class);
		
		logger.warning("All items & list root Ids removed");
		
		
		logger.warning("Try to save new root nodes: " + Arrays.asList(rootNodes));
		
		ItemIDs rootIDs = new ItemIDs(); 
		for (Item item : rootNodes) {
			rootIDs.addId(item.getId());
		}
		
		template.insert(rootIDs);
		template.insertAll(Arrays.asList(rootNodes));
		
		String resMessage = "Saved Items: " + template.findAll(Item.class);
		
		logger.warning(resMessage);
		
		return resMessage;
	}
	
}
