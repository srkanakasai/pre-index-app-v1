package com.smarsh.preindex.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.smarsh.preindex.common.Constants;

@Component
@Configuration
@ConfigurationProperties(prefix = "preindex")
public class PreIndexMetaConfigs {
	
	private int shards;
	private int shardsize;
	private int fillThreshold;
	private BigDecimal indexToDocMem;
	private String tenant;
	private Boolean enableOutliers;
	
	public int getShards() {
		return shards;
	}
	public void setShards(int shards) {
		this.shards = shards;
	}
	public int getShardsize() {
		return shardsize;
	}
	public void setShardsize(int shardsize) {
		this.shardsize = shardsize;
	}
	public int getFillThreshold() {
		return fillThreshold;
	}
	public void setFillThreshold(int fillThreshold) {
		this.fillThreshold = fillThreshold;
	}
	public BigDecimal getIndexToDocMem() {
		return indexToDocMem;
	}
	public void setIndexToDocMem(int indexToDocMem) {
		this.indexToDocMem = Constants.IndexToDataRatio(indexToDocMem).get();
	}
	public String getTenant() {
		return tenant;
	}
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
	public Boolean getEnableOutliers() {
		return enableOutliers;
	}
	public void setEnableOutliers(Boolean enableOutliers) {
		this.enableOutliers = enableOutliers;
	}
	
	
}
