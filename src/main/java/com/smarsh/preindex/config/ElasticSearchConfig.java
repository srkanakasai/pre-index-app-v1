/**
 * 
 */
package com.smarsh.preindex.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Component;

/**
 * @author sridhar.kanakasai
 *
 */
@Component
@EnableElasticsearchRepositories(basePackages = "com.smarsh.preindex.repo.es")
@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

	@Value("${es.url}")
	private String elasticSearchUrl;
	
	@Value("${server.archive.number_of_replicas}")
	private Integer archiveNumberOfReplicas;
	
	@Value("${alcatraz.site.id}")
	private String sideId;
	
	@Value("${indexmanager.archive.schemaVersion}")
	private String indexManagerArchiveSchemaVersion;
	
	@Override
	public @Bean RestHighLevelClient elasticsearchClient() {
		final ClientConfiguration config = ClientConfiguration.builder()
				.connectedTo(elasticSearchUrl)
				.build();
		return RestClients.create(config).rest();
	}

	public String getElasticSearchUrl() {
		return elasticSearchUrl;
	}

	public Integer getArchiveNumberOfReplicas() {
		return archiveNumberOfReplicas;
	}

	public String getSideId() {
		return sideId;
	}

	public String getIndexManagerArchiveSchemaVersion() {
		return indexManagerArchiveSchemaVersion;
	}
	
	
	
}
