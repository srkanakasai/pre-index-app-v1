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

/**
 * @author sridhar.kanakasai
 *
 */
@EnableElasticsearchRepositories(basePackages = "com.smarsh.preindex.repo.es")
@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

	@Value("${es.url}")
	private String elasticSearchUrl;
	
	@Override
	public @Bean RestHighLevelClient elasticsearchClient() {
		final ClientConfiguration config = ClientConfiguration.builder()
				.connectedTo(elasticSearchUrl)
				.build();
		return RestClients.create(config).rest();
	}

}
