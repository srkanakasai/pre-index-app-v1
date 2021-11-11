package com.smarsh.preindex.repo.es;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.smarsh.preindex.common.Constants;
import com.smarsh.preindex.common.UTIL;
import com.smarsh.preindex.config.PreIndexMetaConfigs;
import com.smarsh.preindex.exception.IndexCreationException;
import com.smarsh.preindex.exception.PreIndexRunTimeException;

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
	
	@Autowired
	private PreIndexMetaConfigs preIndexConfigs;
	
	public void index(final String indexName,
			final Integer shardCount,
			final Integer replicaCount,
			boolean deleteExisting) throws IndexCreationException {
		
		if(!preIndexConfigs.getIsEsPersistenceEnabled())
			return;
		
		try {
			if (this.isExisting(indexName)) {
				if (!deleteExisting) {
					return;
				}

				this.deleteIndex(indexName);
			}

			final CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
			createIndexRequest
				.settings(settings, XContentType.JSON)
				.settings(Settings
						.builder()
						.put(Constants.ELASTICSEARCH_SETTINGS_NUM_SHARDS, shardCount)
						.put(Constants.ELASTICSEARCH_SETTINGS_NUM_REPLICAS, replicaCount)
						.build());
			if (mappings != null) {
				createIndexRequest.mapping(mappings, XContentType.JSON);
			}

			CreateIndexResponse response = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
			if(response.isAcknowledged()) {
				LOG.info("Index : "+indexName+" Created successfully");
			}
		} catch (final IOException ioe) {
			LOG.error(ioe.getMessage(), ioe);
			throw new IndexCreationException(indexName, ioe);
		}
		catch (final Exception e) {
			LOG.error(e.getMessage(), e);
			throw new PreIndexRunTimeException(e);
		}
	}

	public boolean isExisting(String indexName) throws PreIndexRunTimeException{
		try {
			final boolean indexExists = client
					.indices()
					.exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
			return indexExists;
		} catch (final Exception ioe) {
			LOG.error(ioe.getMessage(), ioe);
			throw new PreIndexRunTimeException(indexName, ioe);
		}
	}

	public boolean deleteIndex(String indexName) throws PreIndexRunTimeException{
		try {
			AcknowledgedResponse deleteResponse = client
					.indices()
					.delete(
							new DeleteIndexRequest(indexName),
							RequestOptions.DEFAULT
							);
			return deleteResponse.isAcknowledged();
		} catch (final Exception ioe) {
			LOG.error(ioe.getMessage(), ioe);
			throw new PreIndexRunTimeException(indexName, ioe);
		}

	}
}
