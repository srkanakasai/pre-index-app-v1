package com.smarsh.preindex.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

@Configuration
@PropertySource("classpath:application.properties")
public class MongoConfig {
	
	@Value( "${mongo.host}" )
	private String mongoHost;

 
    /*public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/test");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();
        
        return MongoClients.create(mongoClientSettings);
    }*/
 
	public @Bean MongoClientFactoryBean mongo() {
		MongoClientFactoryBean mongo = new MongoClientFactoryBean();
		mongo.setHost(mongoHost);
		return mongo;
	}

}
