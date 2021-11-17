/**
 * 
 */
package com.smarsh.preindex.repo.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.smarsh.preindex.model.IndexMetaDataDO;

/**
 * @author sridhar.kanakasai
 *
 */
@Repository
public interface IIndexMetaDataRepository extends MongoRepository<IndexMetaDataDO,String> {

	@Query("{'indexName' : ?0 , 'siteId' : ?1 , 'indexAppType' : ?2 , 'indexVersion' : ?3}")
	List<IndexMetaDataDO> findByIndexNameSiteIdAppTypeAndIndexVersion(
			String indexName, 
			String siteId, 
			String indexAppType, 
			String indexVersion);

	@Query(value="{'indexName' : ?0 , 'siteId' : ?1 , 'indexAppType' : ?2 , 'indexVersion' : ?3}", delete = true)
	List<IndexMetaDataDO> deleteByIndexNameSiteIdAppTypeAndIndexVersion(
			String indexName, 
			String siteId, 
			String indexAppType,
			String indexVersion);
	
}
