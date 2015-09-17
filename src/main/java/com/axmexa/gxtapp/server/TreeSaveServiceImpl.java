package com.axmexa.gxtapp.server;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.axmexa.gxtapp.client.model.Item;
import com.axmexa.gxtapp.client.service.TreeSaveService;
import com.axmexa.gxtapp.server.config.GxtAppContextListener;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TreeSaveServiceImpl extends RemoteServiceServlet implements TreeSaveService {

	MongoTemplate template;
	Logger logger = Logger.getLogger(TreeSaveServiceImpl.class.getSimpleName());
	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		logger.warning("Strart init");
		super.init(config);
		final ApplicationContext springContext = (ApplicationContext) getServletContext().getAttribute(GxtAppContextListener.SPRING_CONTEXT);
		
		template = springContext.getBean(MongoTemplate.class);
		logger.warning("template: " + template);
	}
	
	@Override
	public String saveTree(Item[] rootNodes) {
		logger.warning("Db Name: " + template.getDb().getName());
		logger.warning("Items to save:" + Arrays.asList(rootNodes));
		StringBuilder sb = new StringBuilder("Before insert: ");
		sb.append(template.findAll(Item.class));
		template.insertAll(Arrays.asList(rootNodes));
		sb.append("\nAfter insert: ");
		sb.append(template.findAll(Item.class));
		
		return sb.toString();
	}

}
