/**
 * 
 */
package com.smarsh.preindex.transformer;

import java.util.Date;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smarsh.preindex.bo.IndexMetaDataBO;
import com.smarsh.preindex.common.IndexType;
import com.smarsh.preindex.common.UTIL;
import com.smarsh.preindex.config.ElasticSearchConfig;
import com.smarsh.preindex.model.IndexMetaDataDO;

/**
 * @author sridhar.kanakasai
 *
 */
@Component
public class IndexMetaDataTransformer implements Function<IndexMetaDataBO, IndexMetaDataDO> {

	@Autowired
	private ElasticSearchConfig esConfigs;
	
	private int replicaCount;
	private boolean activeFl;
	private boolean indexFull;
	private String cluster;
	private String indexType;
	private String siteId;
	private String mappingSchemaVersion;

	@PostConstruct
	public void init() {
		replicaCount = esConfigs.getArchiveNumberOfReplicas();
		activeFl = true;
		indexFull = false;
		cluster = "data";
		indexType = IndexType.ARCHIVE.name();
		siteId = esConfigs.getSideId();
		mappingSchemaVersion = esConfigs.getIndexManagerArchiveSchemaVersion();
	}

	@Override
	public IndexMetaDataDO apply(IndexMetaDataBO metaDataBO) {
		Date currentDate = UTIL.getGMTDate(System.currentTimeMillis());

		IndexMetaDataDO indexMetaData = new IndexMetaDataDO();
		indexMetaData.setActiveFl(activeFl);
		indexMetaData.setCluster(cluster);
		indexMetaData.setCreateDateTime(currentDate);
		indexMetaData.setFromDate(metaDataBO.getFromDate());
		indexMetaData.setToDate(metaDataBO.getToDate());
		indexMetaData.setMaxDate(new Date(UTIL.getEndOfDay(metaDataBO.getFromDate())));
		indexMetaData.setModifiedDateTime(currentDate);
		indexMetaData.setMaxProcessedDate(currentDate);
		indexMetaData.setIndexAppType(indexType);
		indexMetaData.setIndexFull(indexFull);
		indexMetaData.setIndexName(metaDataBO.getIndexName());
		indexMetaData.setIndexVersion(mappingSchemaVersion);
		indexMetaData.setReplicaCount(replicaCount);
		indexMetaData.setShardCount(metaDataBO.getShardCount());
		indexMetaData.setSequenceNumber(metaDataBO.getSequenceNumber());
		indexMetaData.setSiteId(siteId);

		return indexMetaData;
	}

}
