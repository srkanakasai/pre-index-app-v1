/**
 * 
 */
package com.smarsh.preindex.repo.mongo;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.smarsh.preindex.config.PreIndexMetaConfigs;
import com.smarsh.preindex.exception.MetaDataCreationException;
import com.smarsh.preindex.model.IndexMetaDataDO;

/**
 * @author sridhar.kanakasai
 *
 */
@Repository
public class IndexMetaDataRepository {
	
	private static Logger LOG = Logger.getLogger(IndexMetaDataRepository.class);
	
	@Autowired
	private IIndexMetaDataRepository repository;

	@Autowired
	private PreIndexMetaConfigs configs;

	public boolean isExisting(IndexMetaDataDO metaData) {
		List<IndexMetaDataDO> indexDocs = this.repository.findByIndexNameSiteIdAppTypeAndIndexVersion(
				metaData.getIndexName(), 
				metaData.getSiteId(),
				metaData.getIndexAppType(), 
				metaData.getIndexVersion());
		
		if(indexDocs == null || CollectionUtils.isEmpty(indexDocs))
			return false;
		else
			return true;
	}

	public IndexMetaDataDO createIndexMetaData(IndexMetaDataDO metaData) throws MetaDataCreationException{

		if(!configs.getIsMongoPersistenceEnabled()) {
			LOG.debug("Skipping Mongo Meta persistence as wowo is OFF- isEsPersistenceEnabled="+configs.getIsMongoPersistenceEnabled());
			return null;
		}

		return repository.insert(metaData);
	}

	public void deleteIndexMetaData(IndexMetaDataDO metaData) {
		this.repository.deleteByIndexNameSiteIdAppTypeAndIndexVersion(
				metaData.getIndexName(), 
				metaData.getSiteId(),
				metaData.getIndexAppType(), 
				metaData.getIndexVersion());
	}
}
