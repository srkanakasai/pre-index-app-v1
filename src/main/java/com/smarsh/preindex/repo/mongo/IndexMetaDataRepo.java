/**
 * 
 */
package com.smarsh.preindex.repo.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.client.MongoClient;
import com.smarsh.preindex.model.IndexMetaData;

/**
 * @author sridhar.kanakasai
 *
 */
@Repository
public class IndexMetaDataRepo {
	
	@Autowired
	private MongoClient mongoClient;
	
	public boolean isIndexPresent(IndexMetaData indexMetaData) {
		
		mongoClient.listDatabaseNames().iterator().forEachRemaining(System.out::println);
		return false;
	}
	
}
