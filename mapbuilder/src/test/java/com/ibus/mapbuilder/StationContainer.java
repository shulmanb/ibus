package com.ibus.mapbuilder;

public class StationContainer {
	public static class Station {
		public String ts;
		public String line;
		public String longit;
		public String lat;
		public String submap;
		
		public String toString() {
			return submap+" "+ts+" "+line+" "+longit+" "+lat;
		}

	}

	
	public Station station;
	
	public String toString() {
		return station.toString();
	}

}
