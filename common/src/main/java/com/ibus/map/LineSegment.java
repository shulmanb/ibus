package com.ibus.map;

import java.util.ArrayList;

/**
 * Represents a lane segments between two stops
 * @author Home
 */
public class LineSegment {
	
	private String lineId;
	
	private String line;
	
	private int segmentIndx;
	/**
	 * The start stop 
	 */
	private Stop start;
	/**
	 * The end stop
	 */
	private Stop end;
	
	/**
	 * segment default duration
	 */
	private int duration;
	
	
	/**
	 * Ordered list of points on the segment
	 */
	private ArrayList<TimedPoint> points;
	
	
	
	public int getSegmentIndx() {
		return segmentIndx;
	}

	public void setSegmentIndx(int segmentIndx) {
		this.segmentIndx = segmentIndx;
	}

	public void setPoints(ArrayList<TimedPoint> points) {
		this.points = points;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public void setStart(Stop start) {
		this.start = start;
	}

	public void setEnd(Stop end) {
		this.end = end;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}

	public Stop getStart() {
		return start;
	}

	public Stop getEnd() {
		return end;
	}

	public ArrayList<TimedPoint> getPoints() {
		return points;
	}

	public String getLineId() {
		return lineId;
	}

	public String getLine() {
		return line;
	}

	
	public LineSegment(){}
	
	public LineSegment(LineSegment ls) {
		this.lineId = ls.lineId;
		this.line = ls.line;
		this.start = ls.start;
		this.end = ls.end;
		this.points = ls.points;
		this.duration = ls.duration;
	}
	
	public LineSegment(String lineId, String line,Stop start, Stop end,
			ArrayList<TimedPoint> points) {
		this.lineId = lineId;
		this.line = line;
		this.start = start;
		this.end = end;
		this.points = points;
		this.duration = (int) (points.get(points.size()-1).time - points.get(0).time);
	}
	public LineSegment(String laneId, Stop start, Stop end) {
		this.lineId = laneId;
		this.start = start;
		this.end = end;
		this.points = new ArrayList<TimedPoint>();
	}
	
	public LineSegment addPoint(TimedPoint p, int i){
		points.add(i, p);
		return this;
	}
	public LineSegment addPoint(TimedPoint p){
		points.add(p);
		return this;
	}


	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + duration;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + ((lineId == null) ? 0 : lineId.hashCode());
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
		LineSegment other = (LineSegment) obj;
		if (duration != other.duration)
			return false;
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
		if (lineId == null) {
			if (other.lineId != null)
				return false;
		} else if (!lineId.equals(other.lineId))
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
		return "LineSegment [lineId=" + lineId + ", line=" + line
				+ ", segmentIndx=" + segmentIndx + ", start=" + start
				+ ", end=" + end + ", duration=" + duration + ", points="
				+ points + "]";
	}
	
	
}
