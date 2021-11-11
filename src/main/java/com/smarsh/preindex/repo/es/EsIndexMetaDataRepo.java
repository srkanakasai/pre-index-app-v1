package com.smarsh.preindex.repo.es;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.smarsh.preindex.common.UTIL;

@Repository
public class EsIndexMetaDataRepo {

	private static final String STATIC_MAPPINGS = "static/mappings/pre-index-mappings.json";
	private static final String ES_SETTINGS = "static/es-settings.json";
	private static Logger LOG = Logger.getLogger(EsIndexMetaDataRepo.class);
	private final String settings;
	private final String mappings;

	public EsIndexMetaDataRepo() {
		settings = UTIL.loadAsString(ES_SETTINGS);
		if (settings == null) {
			LOG.error("Failed to load index settings");
			throw new BeanCreationException("ES Settings not loaded properly");
		}

		mappings = UTIL.loadAsString(STATIC_MAPPINGS);
		if (mappings == null) {
			LOG.error("Failed to load mappings for index with name docindexmetadata");
			throw new BeanCreationException("STATIC_MAPPINGS not loaded properly");
		}
	}

	@Autowired
	private RestHighLevelClient client;

	public void index(final String indexName, boolean deleteExisting) {

		try {
			final boolean indexExists = client
					.indices()
					.exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
			if (indexExists) {
				if (!deleteExisting) {
					return;
				}

				client.indices().delete(
						new DeleteIndexRequest(indexName),
						RequestOptions.DEFAULT
						);
			}

			final CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
			createIndexRequest.settings(settings, XContentType.JSON);
			if (mappings != null) {
				createIndexRequest.mapping(mappings, XContentType.JSON);
			}

			client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
