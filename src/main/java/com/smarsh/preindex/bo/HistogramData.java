package com.smarsh.preindex.bo;

import java.math.BigDecimal;
import java.util.Date;

import com.smarsh.preindex.common.Constants;


public class HistogramData {

	private String region;
	private Date date;
	private BigDecimal sizeInKb;
	private Integer hour;
	private double indexSize;
	private Outliers outliers;
	public HistogramData(String region, Date date, BigDecimal sizeInKb, Integer hour, double indexSize, Outliers outliers) {
		super();
		this.region = region;
		this.date = date;
		this.sizeInKb = sizeInKb;
		this.hour = hour;
		this.indexSize = indexSize;
		this.outliers = outliers;
	}
	public String getRegion() {
		return region;
	}
	public Date getDate() {
		return date;
	}
	public BigDecimal getSizeInKb() {
		return sizeInKb;
	}
	public Integer getHour() {
		return hour;
	}
	public double getIndexSize() {
		return indexSize;
	}
	public String getDateInString() {
		return Constants.dateFormat.get().format(date);
	}
	public Outliers getOutliers() {
		return outliers;
	}
	@Override
	public String toString() {
		return "HistogramData [region=" + region + ", date=" + Constants.dateFormat.get().format(date) + ", sizeInKb=" + sizeInKb + ", hour=" + hour
				+ ", indexSize=" + indexSize + "]";
	}
}
