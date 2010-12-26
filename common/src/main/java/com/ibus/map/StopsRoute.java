package com.ibus.map;

import java.util.List;

public class StopsRoute {
	private String routeId;
	private int weight;
	private List<LineSegment> route;
	private Point dest;
	
	
	public StopsRoute(){};
	
	public StopsRoute(String routeId,int weight, List<LineSegment> route, Point dest) {
		this.dest = (dest==null)?null:new Point(dest);
		this.weight = weight;
		this.route = route;
		this.routeId = routeId;
	}

	
	public Point getDest() {
		return dest;
	}

	public void setDest(Point dest) {
		this.dest =(dest==null)?null:new Point(dest);
	}

	public int getWeight() {
		return weight;
	}
	
	public List<LineSegment> getRoute() {
		return route;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public void setRoute(List<LineSegment> route) {
		this.route = route;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result + ((routeId == null) ? 0 : routeId.hashCode());
		result = prime * result + weight;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StopsRoute other = (StopsRoute) obj;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (routeId == null) {
			if (other.routeId != null)
				return false;
		} else if (!routeId.equals(other.routeId))
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StopsRoute [routeId=" + routeId + ", weight=" + weight
				+ ", route size=" + route.size() + ", dest=" + dest + "]";
	}
	
	
}
