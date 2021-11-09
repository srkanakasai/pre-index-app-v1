package com.smarsh.preindex.client;

import com.smarsh.preindex.dal.IndexMetaDataRepo;

public class MongoClient {
	
	private static MongoClient instance = null;
	private static String HOST = null;
	private static int PORT = -1;
	
	private MongoClient() {
		HOST = System.getProperty("mongo.host", "192.168.204.129");
		PORT = Integer.parseInt(System.getProperty("mongo.port", "27017"));
	}
	
	public static MongoClient getInstance() {
		if(instance == null) {
			synchronized (IndexMetaDataRepo.class) {
				if(instance == null) {
					instance = new MongoClient();
				}
			}
		}
		return instance;
	}
}
