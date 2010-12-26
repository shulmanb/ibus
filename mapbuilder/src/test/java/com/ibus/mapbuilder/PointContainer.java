package com.ibus.mapbuilder;

public class PointContainer {
	public static class Point {
		public String ts;
		public String line;
		public String longit;
		public String lat;
		@Override
		public String toString() {
			return ts+" "+line+" "+longit+" "+lat;
		}
		
		
	}
	
	public Point point;
	
	public String toString() {
		return point.toString();
	}

}
