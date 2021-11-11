/**
 * 
 */
package com.smarsh.preindex.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.smarsh.preindex.exception.IndexCreationException;
import com.smarsh.preindex.exception.MetaDataCreationException;
import com.smarsh.preindex.exception.PreIndexRunTimeException;
import com.smarsh.preindex.model.IndexMetaData;
import com.smarsh.preindex.repo.es.EsIndexMetaDataRepo;
import com.smarsh.preindex.repo.mongo.IndexMetaDataRepository;

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
	private IndexMetaDataRepository metaDataService;
	
	@Recover
	public void recover(IndexCreationException exception, 
			final IndexMetaData metaData,
			boolean deleteExisting) {
		boolean isDocPresent = this.metaDataService.isExisting(metaData);
		if(isDocPresent)
			this.metaDataService.deleteIndexMetaData(metaData);
	}
	
	@Recover
	public void recover(MetaDataCreationException exception, 
			final IndexMetaData metaData,
			boolean deleteExisting) {
		boolean isIndexPresent = this.esIndexMetaDataRepo.isExisting(metaData.getIndexName());
		if(isIndexPresent)
			this.esIndexMetaDataRepo.deleteIndex(metaData.getIndexName());
	}

	@Retryable(
			include = {
					IndexCreationException.class,
					MetaDataCreationException.class,
					PreIndexRunTimeException.class
			},
			maxAttemptsExpression = "${indexretry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${indexretry.maxDelay}")
			)
	public void index(
			final IndexMetaData metaData,
			boolean deleteExisting) throws 
	IndexCreationException, MetaDataCreationException {

		try {
			boolean isIndexPresent = this.esIndexMetaDataRepo.isExisting(metaData.getIndexName());
			boolean isDocPresent = this.metaDataService.isExisting(metaData);

			if( isIndexPresent
					&& isDocPresent) {
				LOG.info("Index = "+metaData.getIndexName()+" already exist");
				return;
			}

			if(!isIndexPresent) {
				this.esIndexMetaDataRepo.index(
						metaData.getIndexName(), 
						metaData.getShardCount(), 
						metaData.getReplicaCount(),
						deleteExisting);
			}

			if(!isDocPresent) {
				this.metaDataService.createIndexMetaData(metaData);
			}

		} catch (IndexCreationException e) {
			e.printStackTrace();
			throw e;
		} catch (MetaDataCreationException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	

}
