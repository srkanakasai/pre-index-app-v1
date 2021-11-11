/**
 * 
 */
package com.smarsh.preindex.ingestion;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smarsh.preindex.model.IndexMetaData;
import com.smarsh.preindex.repo.es.EsIndexMetaDataRepo;
import com.smarsh.preindex.repo.mongo.IndexMetaDataRepo;

/**
 * @author sridhar.kanakasai
 *
 */
@Service
public class IndexingService {

	private static Logger LOG = Logger.getLogger(IndexingService.class);

	@Autowired
	private EsIndexMetaDataRepo esIndexMetaDataRepo;

	@Autowired
	private IndexMetaDataRepo indexMetaDataRepo;

	public void index(final IndexMetaData metaData, boolean deleteExisting) {

		try {
			esIndexMetaDataRepo.index(metaData.getIndexName(), deleteExisting);
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
