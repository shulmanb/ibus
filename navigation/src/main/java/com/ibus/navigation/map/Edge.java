package com.ibus.navigation.map;

import com.ibus.map.Stop;


/**
 * Represents an edge on the map
 * @author Home
 *
 */
class Edge{
	Stop start;
	Stop end;
	String line;

	public Edge(Stop start, Stop end, String line) {
		this.start = start;
		this.end = end;
		this.line = line;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		Edge other = (Edge) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Edge [start=" + start + ", end=" + end + ", line=" + line + "]";
	}
	
	
}