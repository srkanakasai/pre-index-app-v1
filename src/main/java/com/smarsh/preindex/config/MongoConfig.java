package com.smarsh.preindex.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@PropertySource("classpath:application.properties")
public class MongoConfig {

	@Value( "${mongo.host}" )
	private String mongoHost;
	
	@Value( "${mongo.db}" )
	private String dataBaseName;

	public @Bean MongoClient mongoClient() {
		return MongoClients.create(String.format("mongodb://%s:27017", mongoHost));
	}

	public @Bean MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoClient(), dataBaseName);
	}

}
