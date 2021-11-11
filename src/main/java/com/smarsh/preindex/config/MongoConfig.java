package com.smarsh.preindex.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

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
		MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), dataBaseName);
		return mongoTemplate;
	}

	/*@Autowired private MongoDbFactory mongoDbFactory;

	@Autowired private MongoMappingContext mongoMappingContext;

	@Bean
	public MappingMongoConverter mappingMongoConverter() {

		DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));

		return converter;
	}*/

}
