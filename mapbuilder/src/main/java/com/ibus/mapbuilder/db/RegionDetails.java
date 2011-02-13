package com.ibus.mapbuilder.db;

import com.ibus.map.Point;

public class RegionDetails {
	private Point southeast;
	private Point northwest;
	private int length;
	private String regionId;
	
	public Point getSoutheast() {
		return southeast;
	}
	public void setSoutheast(Point southeast) {
		this.southeast = southeast;
	}
	public Point getNorthwest() {
		return northwest;
	}
	public void setNorthwest(Point northwest) {
		this.northwest = northwest;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getRegionId() {
		return regionId;
	}
	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}
	
}
