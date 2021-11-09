/**
 * 
 */
package com.smarsh.preindex.ingestion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smarsh.preindex.model.IndexMetaData;
import com.smarsh.preindex.model.IndexMetaDataDoc;
import com.smarsh.preindex.repo.es.IndexMetaDataDocRepository;
import com.smarsh.preindex.transformers.IndexMetaDataToEsDoc;

/**
 * @author sridhar.kanakasai
 *
 */
@Service
public class PreIndexService {
	
	private final IndexMetaDataDocRepository docRepository;
	
	@Autowired
	private IndexMetaDataToEsDoc metaDataToEsDoc;
	@Autowired
	public PreIndexService(IndexMetaDataDocRepository repository) {
		this.docRepository = repository;
	}
	
	public void save(final List<IndexMetaData> indexMetaDataList) {
		IndexMetaDataDoc doc = metaDataToEsDoc.apply(indexMetaDataList.get(0));
		this.save(doc);
	}
	
	public void save(final IndexMetaDataDoc indexMetaDataDoc) {
		this.docRepository.save(indexMetaDataDoc);
	}
	
	public IndexMetaDataDoc findById(final String id) {
		return this.docRepository.findById(id).orElse(null);
	}
	
	/* TODO
	 * public IndexMetaDataDoc findByIndexName(final String indexName) {
	}*/

}
