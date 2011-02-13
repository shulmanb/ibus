package com.ibus.map;

public class StopDetails extends Stop {

	private String[] lines;
	private String[] linesNames;
	private String submap;

	public StopDetails(){}
	public StopDetails(Point p, String desc, String[] lines) {
		super(p);
		this.desc = desc;
		this.lines = lines;
	}

	public StopDetails(Stop stop, String[] lines) {
		super(stop.getStopsPoint());
		this.desc = stop.getDesc();
		lines = stop.getLinesInStop().toArray(new String[0]);
	}
	
	public String[] getLines() {
		return lines;
	}

	public void setLines(String[] lines) {
		this.lines = lines;
	}
	
	public StopDetails withSubmap(String submap){
		this.submap = submap;
		return this;
	}

	
	public String[] getLinesNames() {
		return linesNames;
	}
	public void setLinesNames(String[] linesNames) {
		this.linesNames = linesNames;
	}
	public String getSubmap() {
		return submap;
	}
	public void setSubmap(String submap) {
		this.submap = submap;
	}
}
