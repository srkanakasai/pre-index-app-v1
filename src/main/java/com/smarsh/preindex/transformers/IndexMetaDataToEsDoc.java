/**
 * 
 */
package com.smarsh.preindex.transformers;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.smarsh.preindex.model.IndexMetaData;
import com.smarsh.preindex.model.IndexMetaDataDoc;

/**
 * @author sridhar.kanakasai
 *
 */
@Component
public class IndexMetaDataToEsDoc implements Function<IndexMetaData, IndexMetaDataDoc> {

	@Override
	public IndexMetaDataDoc apply(IndexMetaData data) {
		
		IndexMetaDataDoc doc = new IndexMetaDataDoc();
		doc.setId(data.getIndexName());
		doc.setIndexId(data.getIndexName());
		return doc;
	}

}
