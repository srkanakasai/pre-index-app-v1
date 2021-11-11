/**
 * 
 */
package com.smarsh.preindex.ingestion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.smarsh.preindex.config.PreIndexMetaConfigs;
import com.smarsh.preindex.exception.MetaDataCreationException;
import com.smarsh.preindex.model.IndexMetaData;
import com.smarsh.preindex.repo.mongo.IndexMetaDataRepository;

/**
 * @author sridhar.kanakasai
 *
 */
@Service
public class IndexMetaDataService {
	@Autowired
	private IndexMetaDataRepository repository;

	@Autowired
	private PreIndexMetaConfigs configs;

	public boolean isExisting(IndexMetaData metaData) {
		List<IndexMetaData> indexDocs = this.repository.findByIndexNameSiteIdAppTypeAndIndexVersion(
				metaData.getIndexName(), 
				metaData.getSiteId(),
				metaData.getIndexAppType(), 
				metaData.getIndexVersion());
		
		if(indexDocs == null || CollectionUtils.isEmpty(indexDocs))
			return false;
		else
			return true;
	}

	public IndexMetaData createIndexMetaData(IndexMetaData metaData) throws MetaDataCreationException{

		if(!configs.getIsMongoPersistenceEnabled())
			return null;

		return repository.insert(metaData);
	}

	public void deleteIndexMetaData(IndexMetaData metaData) {
		this.repository.deleteByIndexNameSiteIdAppTypeAndIndexVersion(
				metaData.getIndexName(), 
				metaData.getSiteId(),
				metaData.getIndexAppType(), 
				metaData.getIndexVersion());
	}
}
