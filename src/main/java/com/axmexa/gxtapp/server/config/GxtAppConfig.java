package com.axmexa.gxtapp.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class GxtAppConfig {

	@Bean
	public MongoFactoryBean mongo() {
		final MongoFactoryBean factory = new MongoFactoryBean();
		factory.setHost("localhost");
		return factory;
	}

	@Bean
    public SimpleMongoDbFactory mongoDbFactory() throws Exception{
        return new SimpleMongoDbFactory( mongo().getObject(), "movies" );
    }
	
	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoDbFactory());
	}
}
