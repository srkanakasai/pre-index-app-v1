/*
 * ==================================================================================================================
 *  Copyright (c) 2015-2016, Actiance, Inc., All rights reserved
 *  ------------------
 *  @author Devesh Cheraku <a href="mailto:dcheraku@actiance.com">dcheraku@actiance.com</a>
 *  @version $Id: IndexMetaData.java,v 1.0 Thu Jun 22 13:45:44 IST 2016 dcheraku Exp $
 *  @since 1.0.0
 *  ------------------
 *
 * ==================================================================================================================
 */

package com.smarsh.preindex.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document (collection = "index_metadata")
public class IndexMetaData implements Serializable,Comparable<IndexMetaData> {

	private static final long serialVersionUID = 1L;

	public static final String indexAppType_string = 	"indexAppType";
	public static final String cluster_string = 	"cluster";
	public static final String indexName_string = "indexName";
	public static final String fromDateTime_string ="fromDate";
	public static final String maxDateTime_string ="maxDate";
	public static final String maxProcessedDateTime_string ="maxProcessedDate";
	public static final String toDateTime_string = 	"toDate";
	public static final String indexVersion_string	 ="indexVersion";	
	public static final String createDateTime_string ="createDateTime";
	public static final String modifiedDateTime_string ="modifiedDateTime";
	public static final String shardCount_string = 	"shardCount";
	public static final String replicaCount_string = 	"replicaCount";
	public static final String sequenceNumber_string ="sequenceNumber";
	public static final String activeFl_string = 	"activeFl";
	public static final String indexFull_string = 	"indexFull";
	public static final String siteId_string = 	"siteId";

	@Id
	private String _id;
	
	private String indexAppType;
	private String cluster;
	
	@Indexed(unique=true)
	private String indexName;
	
	private Date fromDate;
	private Date toDate;
	private Date maxDate;
	private Date maxProcessedDate;
	private String indexVersion;
	private Date createDateTime;
	private Date modifiedDateTime;
	private int shardCount;
	private int replicaCount;
	private int sequenceNumber;
	private boolean activeFl;
	private boolean indexFull;
	private String siteId;

	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public String getIndexAppType() {
		return indexAppType;
	}
	public void setIndexAppType(String indexType) {
		this.indexAppType = indexType;
	}
	public String getIndexVersion() {
		return indexVersion;
	}
	public void setIndexVersion(String indexVersion) {
		this.indexVersion = indexVersion;
	}
	public int getShardCount() {
		return shardCount;
	}
	public void setShardCount(int shardCount) {
		this.shardCount = shardCount;
	}
	public int getReplicaCount() {
		return replicaCount;
	}
	public void setReplicaCount(int replicaCount) {
		this.replicaCount = replicaCount;
	}
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public boolean isActiveFl() {
		return activeFl;
	}
	public void setActiveFl(boolean activeFl) {
		this.activeFl = activeFl;
	}
	public boolean isIndexFull() {
		return indexFull;
	}
	public void setIndexFull(boolean indexFull) {
		this.indexFull = indexFull;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public Date getMaxDate() {
		return maxDate;
	}
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}
	public Date getMaxProcessedDate() {
		return maxProcessedDate;
	}
	public void setMaxProcessedDate(Date maxProcessedDate) {
		this.maxProcessedDate = maxProcessedDate;
	}
	public Date getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}
	public Date getModifiedDateTime() {
		return modifiedDateTime;
	}
	public void setModifiedDateTime(Date modifiedDateTime) {
		this.modifiedDateTime = modifiedDateTime;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IndexMetaData [_id=").append(_id).append(", indexAppType=").append(indexAppType)
				.append(", cluster=").append(cluster).append(", indexName=").append(indexName).append(", fromDate=")
				.append(fromDate).append(", toDate=").append(toDate).append(", maxDate=").append(maxDate)
				.append(", maxProcessedDate=").append(maxProcessedDate).append(", indexVersion=").append(indexVersion)
				.append(", createDateTime=").append(createDateTime).append(", modifiedDateTime=")
				.append(modifiedDateTime).append(", shardCount=").append(shardCount).append(", replicaCount=")
				.append(replicaCount).append(", sequenceNumber=").append(sequenceNumber).append(", activeFl=")
				.append(activeFl).append(", indexFull=").append(indexFull).append(", siteId=").append(siteId)
				.append("]");
		return builder.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cluster == null) ? 0 : cluster.hashCode());
		result = prime * result
				+ ((indexName == null) ? 0 : indexName.hashCode());
		result = prime * result
				+ ((indexAppType == null) ? 0 : indexAppType.hashCode());
		result = prime * result
				+ ((indexVersion == null) ? 0 : indexVersion.hashCode());
		result = prime * result + sequenceNumber;
		result = prime * result + shardCount;
		result = prime * result + replicaCount;
		result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexMetaData other = (IndexMetaData) obj;
		if (activeFl != other.activeFl)
			return false;
		if (cluster == null) {
			if (other.cluster != null)
				return false;
		} else if (!cluster.equals(other.cluster))
			return false;
		if (createDateTime == null) {
			if (other.createDateTime != null)
				return false;
		} else if (!createDateTime.equals(other.createDateTime))
			return false;
		if (fromDate != other.fromDate)
			return false;
		if (indexFull != other.indexFull)
			return false;
		if (indexName == null) {
			if (other.indexName != null)
				return false;
		} else if (!indexName.equals(other.indexName))
			return false;
		if (indexAppType == null) {
			if (other.indexAppType != null)
				return false;
		} else if (!indexAppType.equals(other.indexAppType))
			return false;
		if (indexVersion != other.indexVersion)
			return false;
		if (maxDate == null) {
			if (other.maxDate != null)
				return false;
		} else if (!maxDate.equals(other.maxDate))
			return false;
		if (modifiedDateTime == null) {
			if (other.modifiedDateTime != null)
				return false;
		} else if (!modifiedDateTime.equals(other.modifiedDateTime))
			return false;
		if (sequenceNumber != other.sequenceNumber)
			return false;
		if (shardCount != other.shardCount)
			return false;
		if (replicaCount != other.replicaCount)
			return false;
		if (siteId == null) {
			if (other.siteId != null)
				return false;
		} else if (!siteId.equals(other.siteId))
			return false;
		if (toDate != other.toDate)
			return false;
		return true;
	}
	
	@Override
	public int compareTo(IndexMetaData otherMetadata) {
		if (this == otherMetadata)
			return 0;
		if (otherMetadata == null)
			return -1;

		if(!(cluster.equals(otherMetadata.cluster)))
			return cluster.compareTo(otherMetadata.cluster);

		if(!(siteId.equals(otherMetadata.siteId)))
			return siteId.compareTo(otherMetadata.siteId);
		
		if(!indexVersion.equals(otherMetadata.indexVersion))
			return indexVersion.compareTo(otherMetadata.indexVersion);

		if(!indexAppType.equals(otherMetadata.indexAppType))
			return indexAppType.compareTo(otherMetadata.indexAppType);

		if(!fromDate.equals(otherMetadata.fromDate))
			return fromDate.compareTo(otherMetadata.fromDate);
		
		return (int)(sequenceNumber-otherMetadata.sequenceNumber);
	}

	public IndexMetaData insertClone(){

		IndexMetaData metaDataInsert = (IndexMetaData)this.clone();
		metaDataInsert.setModifiedDateTime(null);
		metaDataInsert.setCreateDateTime(null);
		metaDataInsert.setIndexFull(false);
		return metaDataInsert;
	}


	@Override
	public Object clone(){
		try{
			return super.clone();
		}
		catch(CloneNotSupportedException exception){
			IndexMetaData metaData = new IndexMetaData();
			metaData.setIndexName(getIndexName() );
			metaData.setCluster(getCluster() );
			metaData.setIndexAppType(getIndexAppType() );
			metaData.setFromDate(getFromDate() );
			metaData.setToDate(getToDate() );
			metaData.setMaxDate(getMaxDate() );
			metaData.setIndexVersion(getIndexVersion() );
			metaData.setCreateDateTime(getCreateDateTime() );
			metaData.setModifiedDateTime(getModifiedDateTime() );
			metaData.setShardCount(getShardCount() );
			metaData.setReplicaCount(getReplicaCount() );
			metaData.setSequenceNumber(getSequenceNumber() );
			metaData.setActiveFl(isActiveFl() );
			metaData.setIndexFull(isIndexFull() );
			metaData.setSiteId(getSiteId() );
			return metaData;
		}
	}
}
