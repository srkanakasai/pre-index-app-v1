package com.smarsh.preindex.common;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

public class Constants {
	public static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
		@Override
		public SimpleDateFormat get() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	public static final Date parse(String date) {
		try {
			return dateFormat.get().parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * TODO : dymanic ingestion
	 */
	public static final int MAX_NUM_OF_SHARDS_PER_INDEX = 12;
	public static final int SHARD_SIZE_IN_GB = 20;
	public static final int OCCUPENCY_PERCENTAGE = 80;
	public static final int ROUND_OFF_FACTOR = 50;
	
	//public static final int MAX_SIZE_PER_INDEX = 200000000;
	public static final Supplier<BigDecimal> IndexToDataRatio(Integer percent){
		return ()->BigDecimal.valueOf((double)percent/100);
	}
	
	public static final long kilo = 1024;
	public static final long mega = kilo * kilo;
	public static final long giga = mega * kilo;
	public static final long tera = giga * kilo;
	
	public static final String ALCATRAZ_SITE = "alcatraz.site.id";
	public static final String INDEXMANAGER_ARCHIVE_SCHEMA_VERSION = "indexmanager.archive.schemaVersion";
	
	/**
	 * ES settings
	 */
	public static final String ELASTICSEARCH_SETTINGS_NUM_SHARDS = "index.number_of_shards";
	public static final String ELASTICSEARCH_SETTINGS_NUM_REPLICAS = "index.number_of_replicas";
}
