package com.smarsh.preindex.bo;

import java.util.Date;

public class BatchInput {
	private String tenant;
	private String region;
	private String fileName;
	private Outliers outliers;
	
	public BatchInput(String tenant, String region, String fileName, Date[] outliers) {
		super();
		this.tenant = tenant;
		this.region = region;
		this.fileName = fileName;
		this.outliers = new Outliers(outliers);
	}
	public String getTenant() {
		return tenant;
	}
	public String getRegion() {
		return region;
	}
	public String getFileName() {
		return fileName;
	}
	public Outliers getOutliers() {
		return outliers;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BatchInput [tenant=").append(tenant).append(", region=").append(region).append(", fileName=")
				.append(fileName).append(", outliers=").append(outliers).append("]");
		return builder.toString();
	}
}
