/**
 * 
 */
package com.smarsh.preindex.repo.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.smarsh.preindex.model.IndexMetaData;

/**
 * @author sridhar.kanakasai
 *
 */
@Repository
public interface IIndexMetaDataRepository extends MongoRepository<IndexMetaData,String> {

	@Query("{'indexName' : ?0 , 'siteId' : ?1 , 'indexAppType' : ?2 , 'indexVersion' : ?3}")
	List<IndexMetaData> findByIndexNameSiteIdAppTypeAndIndexVersion(
			String indexName, 
			String siteId, 
			String indexAppType, 
			String indexVersion);

	@Query(value="{'indexName' : ?0 , 'siteId' : ?1 , 'indexAppType' : ?2 , 'indexVersion' : ?3}", delete = true)
	List<IndexMetaData> deleteByIndexNameSiteIdAppTypeAndIndexVersion(
			String indexName, 
			String siteId, 
			String indexAppType,
			String indexVersion);
	
}
