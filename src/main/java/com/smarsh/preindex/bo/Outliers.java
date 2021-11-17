package com.smarsh.preindex.bo;

import java.util.Arrays;
import java.util.Date;

public class Outliers {

	private Date[] outliers;
	public Outliers(Date[] d) {
		this.outliers = d;
	}
	
	public Date getLowerBound() {
		return outliers[0];
	}
	
	public Date getUpperBound() {
		return outliers[outliers.length-1];
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Outliers [outliers=").append(Arrays.toString(outliers)).append("]");
		return builder.toString();
	}
}
