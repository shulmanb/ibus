package com.ibus.navigation.map;

import com.google.inject.Inject;
import com.ibus.map.Point;
import com.ibus.map.StopsRoute;
import com.ibus.navigation.map.db.SimpleDBMapLoader;

public class Navigator implements INavigator {
	private LinesMapFactory factory;
	
	@Inject
	public Navigator(LinesMapFactory factory){
		this.factory = factory;
	}
	
	public Navigator(String awskey, String awssecret){
		SimpleDBMapLoader ml = new SimpleDBMapLoader(awskey, awssecret);
		factory = new LinesMapFactory();
		factory.setMapLoader(ml);
	}

	@Override
	public StopsRoute navigate(Point origin, Point destination, String submap) {
		StopsRoute route = factory.loadMap(submap, true).findRoute(origin, destination);
		return route;
	}
	
	
}
