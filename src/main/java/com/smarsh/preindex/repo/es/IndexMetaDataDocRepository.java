/**
 * 
 */
package com.smarsh.preindex.repo.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.smarsh.preindex.model.IndexMetaDataDoc;

/**
 * @author sridhar.kanakasai
 *
 */
public interface IndexMetaDataDocRepository extends ElasticsearchRepository<IndexMetaDataDoc, String>{

}
