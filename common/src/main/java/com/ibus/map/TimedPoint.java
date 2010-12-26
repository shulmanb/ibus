package com.ibus.map;

/**
 * A timed point
 * 
 * @author Home
 *
 */
public class TimedPoint extends Point {
	/**
	 * A time in seconds since the previous stop
	 */
	protected long time;
	public TimedPoint(){super();}
	
	public TimedPoint(double longitude, double latitude, long time) {
		super(longitude, latitude);
		this.time = time;
	}

	public long getTime() {
		return time;
	}
	
	public void setTime(long ts){
		time = ts;
	}
	public Point toPoint(){
		return new Point(getLongitude(), getLatitude());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (time ^ (time >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if(obj == null)
			return false;
		
		if (!obj.getClass().equals(TimedPoint.class))
			return false;

		
		Point other = ((Point)obj).round();
		Point round = round();
		
		if(other.getLatitude() == round.getLatitude() && 
		   other.getLongitude() == round.getLongitude() &&
		   time == ((TimedPoint)obj).time){
			return true;
		}
		
		return false;
	}
	
	

}
