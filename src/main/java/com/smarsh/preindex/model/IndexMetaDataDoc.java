package com.smarsh.preindex.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = Indices.IndexMetaDataDoc_INDEX)
@Setting(settingPath = "static/es-settings.json")
public class IndexMetaDataDoc {

	@Id
	@Field(type = FieldType.Keyword)
	private String id;
	@Field(type = FieldType.Text)
	private String indexId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIndexId() {
		return indexId;
	}
	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}
}
