/**
 * 
 */
package com.smarsh.preindex.dal;

import com.smarsh.preindex.client.MongoClient;
import com.smarsh.preindex.model.IndexMetaData;

/**
 * @author sridhar.kanakasai
 *
 */
public class IndexMetaDataRepo {
	
	private static IndexMetaDataRepo instance = null;
	private IndexMetaDataRepo() {
	}
	
	public static IndexMetaDataRepo getInstance() {
		if(instance == null) {
			synchronized (IndexMetaDataRepo.class) {
				if(instance == null) {
					instance = new IndexMetaDataRepo();
				}
			}
		}
		return instance;
	}
	
	public boolean isIndexPresent(IndexMetaData indexMetaData) {
		
		MongoClient mongoClient = MongoClient.getInstance();
		//mongoClient.getDatabaseNames().forEach(System.out::println);
		return false;
	}
	
	public static void main(String[] args) {
		IndexMetaDataRepo repo = IndexMetaDataRepo.getInstance();
		repo.isIndexPresent(null);
	}
	
}
