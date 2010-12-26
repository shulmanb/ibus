package com.ibus.tracer;

import com.google.inject.Inject;
import com.ibus.map.LineSegment;
import com.ibus.map.Point;
import com.ibus.map.StopsRoute;
import com.ibus.map.TimedPoint;
import com.ibus.tracer.Status.StatusOnRoute;
import com.ibus.tracer.db.ISessionDB;
import com.ibus.tracer.db.RedisSessionDB;

/**
 * 
 * Implements the realtime tracing functionality
 * @author Home
 *
 */
public class Tracer implements ITracer {

	private enum StatusOnSegment{
		MOVED_ON_SEGMENT,
		UNMOVED_ON_SEGMENT,
		MOVED_TO_NEXT_SEGMENT
	}
	
	private static final double MAX_DISTANCE_FROM_POINT_ON_SEGMENT = 0.3;
	private static final double NEAR_STOP = 0.01; //10 meters
	
	private ISessionDB sesDb = null;

	public Tracer(){}
	
	public Tracer(String host, int port){
		sesDb = new RedisSessionDB(host, port);	
	}
	
	@Inject
	public void setSesDb(ISessionDB sesDb) {
		this.sesDb = sesDb;
	}
	/**
	 * Stores the user location regardless of the route
	 * @see com.ibus.tracer.ITracer#storeLocation(java.lang.String, com.ibus.map.TimedPoint)
	 */
	@Override
	public void storeLocation(String sessionId, TimedPoint 	point) 
		throws InvalidSessionException{
		checkSessionValidity(sessionId);
		sesDb.storeLocation(sessionId, point);
	}
	/**
	 * Check in the user to route. Prepares the route to navigation in the in mem db
	 * @see com.ibus.tracer.ITracer#checkInToRoute(java.lang.String, java.lang.String)
	 */
	@Override
	public void checkInToRoute(String session, String routeId) 
	   	   throws InvalidSessionException, InvalidRouteException{
		checkSessionValidity(session);
		//retrieve the route
		StopsRoute sr = sesDb.getRoute(routeId);
		//prepare the route
		sesDb.chcekInToRoute(session, routeId);
		boolean first = true;
		for(LineSegment ls:sr.getRoute()){
			//the route should be stored in a convenient way to navigation
			//list of segments (once the segment is done it will be removed)
			if(first){
				sesDb.setNextSegmentStop(session,ls.getStart());
				Status stat = new Status().withStatus(StatusOnRoute.ON_THE_WAY_TO_STATION);
				sesDb.updateStatus(session, stat);
				first = false;
			}
			sesDb.addSegment(session,ls);
			//TODO: add #of stops, ttd, lines change info to the session route status
		}
		sesDb.storeDestinationCoordinates(session, sr.getDest());
	}
	
	private void checkSessionValidity(String session)
			throws InvalidSessionException {
		if(!sesDb.pingSession(session)){
			throw new InvalidSessionException(session);
		}
	}
	/**
	 * Updates the user location on route, calculates its status on route, notifies if the route should be changed
	 * @throws UserOffRouteException 
	 * @see com.ibus.tracer.ITracer#storeLocationOnRoute(java.lang.String, com.ibus.map.TimedPoint)
	 */
	@Override
	public Status storeLocationOnRoute(String sessionId, TimedPoint point) 
	 	   throws InvalidSessionException, UserOffRouteException{
		//store as last reported location
		checkSessionValidity(sessionId);
		storeLocation(sessionId, point);
		Status status = sesDb.getStatusOnRoute(sessionId);
		if(status.getStatus() == StatusOnRoute.ON_THE_WAY_TO_DESTIANTION){
			storeLocationToDestination(sessionId, point, status);
		}else{
			storeLocationToFinalStop(sessionId, point, status);
		}
		return status;
	}
	
	/**
	 * Update location when user is on his way to a destination, after final stop
	 * @param sessionId
	 * @param point
	 * @param status
	 */
	private void storeLocationToDestination(String sessionId, TimedPoint point,
			Status status) {
		Point dest = sesDb.getDestinationCoordinates(sessionId);
		if(point.distnaceFrom(dest)<NEAR_STOP){
			status.setStatus(StatusOnRoute.ARRIVED);
			sesDb.updateStatus(sessionId, status);
		}
	}

	
	/**
	 * Update location when user is on his way to a final stop
	 * 
	 * @param sessionId
	 * @param point
	 * @param status
	 * @throws UserOffRouteException
	 */
	private void storeLocationToFinalStop(String sessionId, TimedPoint point,
			Status status) throws UserOffRouteException {
		Point nextStop = sesDb.getNextSegmentStop(sessionId);
		
		//if status is on a way to next stop
		boolean segmentMoved = false;
		boolean reachedFinal = false;
		StatusOnSegment sos = StatusOnSegment.UNMOVED_ON_SEGMENT;
		if(status.getStatus() == StatusOnRoute.ON_THE_WAY_TO_STATION){
			//check if in the station
			Point p = sesDb.getNextSegmentStop(sessionId);
			if(p.distnaceFrom(point) < NEAR_STOP){
				status.setStatus(StatusOnRoute.WAITING_TO_TRANSAPORT);
				moveToNextSegment(sessionId, status);
				segmentMoved = true;
			}
		}else{
			for(;;){
				sos = updateLocationOnSegment(sessionId, point, status,nextStop);
				if(sos == StatusOnSegment.MOVED_TO_NEXT_SEGMENT){
					//check if segment ended
					reachedFinal = moveToNextSegment(sessionId, status);
					if(reachedFinal){
						break;
					}
					segmentMoved = true;
				}else{
					break;
				}
			}
		}
		if(reachedFinal || segmentMoved || sos != StatusOnSegment.UNMOVED_ON_SEGMENT){
			sesDb.updateStatus(sessionId,status);
		}
	}
	
	
	/**
	 * Updates user location on a line segment
	 * 
	 * @param sessionId
	 * @param point
	 * @param status
	 * @param nextStop 
	 * @return
	 * @throws UserOffRouteException
	 */
	private StatusOnSegment updateLocationOnSegment(String sessionId, TimedPoint point,
			Status status, Point nextStop) throws UserOffRouteException {
		//get next 10 points and find the closest
		int movIndx = 0;
		int index = -1;
		StatusOnSegment son = StatusOnSegment.UNMOVED_ON_SEGMENT;
		boolean reachedEnd = false;
		for(;;){
			TimedPoint[] points = sesDb.getNextPointsInSegment(sessionId,20);
			if(points == null || points.length == 0){
				//reached the end of this segment
				son = StatusOnSegment.MOVED_TO_NEXT_SEGMENT;
				break;
			}
			index = findClosestPoint(points, point);
			//if last point on segment and near next station move to next segment
			//or if last point on a segment and point is closer to next station than to the last point
			
			if(shouldMoveToNextLineSegment(point, nextStop, index, points)){
				index++;
				reachedEnd = true;
				break;
			}
			if(index > -1){
				break;	
			}
			movIndx += 20;
		}
		if(movIndx + index >= 0){
			//moved from the first point
			//check if previous status was WAITING FOR TRANSPORT and now moved and update new status
			if(status.getStatus() == StatusOnRoute.WAITING_TO_TRANSAPORT){
				status.setStatus(StatusOnRoute.ON_THE_TANSPORT);
			}
			if(reachedEnd){
				son = StatusOnSegment.MOVED_TO_NEXT_SEGMENT;
			}else{
				son  = StatusOnSegment.MOVED_ON_SEGMENT;
			}
			//remove unneeded points
			if(movIndx + index > 0){
				sesDb.removePointsFromSegment(sessionId,movIndx+index);
			}
		}
		return son;
	}
	/**
	 * @param point the current point
	 * @param nextStop the next stop
	 * @param index current index
	 * @param points current segment points
	 * @return
	 */
	private boolean shouldMoveToNextLineSegment(TimedPoint point, Point nextStop,
			int index, TimedPoint[] points) {
		if(!(index == points.length-1 && points.length < 20)){
			//not last point in the line segment
			return false;
		}
		if(point.distnaceFrom(nextStop)<NEAR_STOP){
			//the point is in the radius of the next stop
			return true;
		}else if(point.distnaceFrom(nextStop)<point.distnaceFrom(points[index]) &&
				point.distnaceFrom(points[index-1])>point.distnaceFrom(points[index])){
			//check the case when the bus already passed the next stop, and current point is geografically
			//not between the two last points
			
			return true;
		}
		return false;
	}
	
	
	private boolean moveToNextSegment(String sessionId, Status status) {
		//TODO: add a possibility to a remote segment (that requires walking, (cluster of stations))
		LineSegment ls = sesDb.popNextSegment(sessionId);
		if(ls == null){
			status.setStatus(StatusOnRoute.ON_THE_WAY_TO_DESTIANTION);
			sesDb.setNextSegmentStop(sessionId, null);
			return true;
		}
		sesDb.setNextSegmentStop(sessionId, ls.getEnd());
		if(!ls.getLineId().equals(status.getCurrLineId())){
			//we are switching the line
			status.setCurrLineId(ls.getLineId());
			status.setCurrLineName(ls.getLine());
			status.setStatus(StatusOnRoute.WAITING_TO_TRANSAPORT);
			//TODO: populate next line and estimates
		}
		//current segment will be stored as a list of points, points that are done will be removed
		for(TimedPoint p:ls.getPoints()){
			sesDb.addPointForNextSegment(sessionId,p);
		}
		return false;
	}
	
	/**
	 * Finds the closest point to the current location
	 * 
	 * @param points
	 * @param point
	 * @return
	 */
	private int findClosestPoint(TimedPoint[] points, TimedPoint point)throws UserOffRouteException {
		double distance = Integer.MAX_VALUE;
		boolean afterCurve = false;
		int indx = -1;
		for(int i = 0;i < points.length;i++){
			double dist = points[i].distnaceFrom(point);
			if(dist < distance){
				indx = i;
				distance = dist;
			}else{
				//we will check another point after the closest to make sure
				if(afterCurve){
					indx = i - 2;
					break;
				}else{
					afterCurve = true;
				}
			}
		}
		if(distance > MAX_DISTANCE_FROM_POINT_ON_SEGMENT){
			if(indx == points.length){
				//point not here, should try next points
				indx = -1;
			}else{
				//probably went of from the route
				throw new UserOffRouteException("User is off route ");
			}
		}
		return indx;
	}
	@Override
	public TimedPoint getLocation(String sessionId)
	       throws InvalidSessionException{
		checkSessionValidity(sessionId);
		return sesDb.getLastLocation(sessionId);
	}
	
	@Override
	public String getCheckInStatus(String sessionId)
	 	   throws InvalidSessionException{
		checkSessionValidity(sessionId);
		return sesDb.getCheckInStatus(sessionId);
	}

	@Override
	public void storeTemporaryRoute(StopsRoute route)
			throws InvalidSessionException {
		sesDb.storeRoute(route);
	}
}
