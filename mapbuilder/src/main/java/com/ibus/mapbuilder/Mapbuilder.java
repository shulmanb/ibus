package com.ibus.mapbuilder;

import java.util.UUID;

import com.google.inject.Inject;
import com.ibus.map.Point;
import com.ibus.mapbuilder.db.IBuilderDB;
import com.ibus.mapbuilder.db.SimpleDBRedisBuilderDB;

public class Mapbuilder implements IMapBuilder{

	private IBuilderDB db;
	
	@Inject
	public void setDb(IBuilderDB db) {
		this.db = db;
	}

	public Mapbuilder(){}
	
	/**
	 * @param redisHost
	 * @param redisPort
	 * @param awsKey
	 * @param awsSecret
	 */
	public Mapbuilder(String redisHost,int redisPort, String awsKey, String awsSecret){
		db = new SimpleDBRedisBuilderDB(redisHost, redisPort, awsKey, awsSecret);
	}
	
	@Override
	public String initiateRouteRecording(long ts, Point point, String lineName, String submap) {
		//generate session key
		String sessionID = UUID.randomUUID().toString();
		//start session in  redis
		db.createRecordingSession(sessionID,lineName, submap);
		//add starting point and ts
		db.addStation(sessionID, point, ts);
		return sessionID;
	}

	@Override
	public void addPoint(String sessionID, long ts, Point point) {
		db.addPoint(sessionID, point, ts);
	}

	@Override
	public void addStation(String sessionID, long ts, Point point) {
		//add station to redis
		db.addStation(sessionID, point, ts);
		
	}


	@Override
	public void finishRouteRecording(String sessionID, long ts, Point point) {
		//add last point/station to the route in redis
		db.addStation(sessionID, point, ts);
		//flush the route to a persistent store
		db.flushRoute(sessionID);
	}
}
