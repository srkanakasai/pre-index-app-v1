package com.smarsh.preindex.bo;

import java.util.Date;

public class IndexMetaDataBO {

	private String indexName;
	private Date fromDate;
	private Date toDate;
	private int shardCount;
	private int sequenceNumber;
	
	public String getIndexName() {
		return indexName;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public int getShardCount() {
		return shardCount;
	}
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public IndexMetaDataBO(String indexName, Date fromDate, Date toDate, int shardCount, int sequenceNumber) {
		super();
		this.indexName = indexName;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.shardCount = shardCount;
		this.sequenceNumber = sequenceNumber;
	}

	public static class Builder{
		private String indexName;
		private Date fromDate;
		private Date toDate;
		private int shardCount;
		private int sequenceNumber;
		public Builder setIndexName(String indexName) {
			this.indexName = indexName;
			return this;
		}
		public Builder setFromDate(Date fromDate) {
			this.fromDate = fromDate;
			return this;
		}
		public Builder setToDate(Date toDate) {
			this.toDate = toDate;
			return this;
		}
		public Builder setShardCount(int shardCount) {
			this.shardCount = shardCount;
			return this;
		}
		public Builder setSequenceNumber(int sequenceNumber) {
			this.sequenceNumber = sequenceNumber;
			return this;
		}
		public IndexMetaDataBO build() {
			return new IndexMetaDataBO(indexName, fromDate, toDate, shardCount, sequenceNumber);
		}
		
	}
	
}
