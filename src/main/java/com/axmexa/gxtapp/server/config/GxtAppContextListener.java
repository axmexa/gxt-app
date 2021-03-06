package com.axmexa.gxtapp.server.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@WebListener
public class GxtAppContextListener implements ServletContextListener {


	public static final String GxtAppConfig = "GxtAppConfig";

	@Override
	public void contextInitialized(ServletContextEvent e) {
		
		final ApplicationContext context = new AnnotationConfigApplicationContext(GxtAppConfig.class);
		ServletContext servletContext = e.getServletContext();
		servletContext.setAttribute(GxtAppConfig, context);
		

	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

}
