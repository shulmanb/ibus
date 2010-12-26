package com.ibus.navigation.map;

import com.ibus.map.Point;
import com.ibus.map.StopsRoute;

public interface INavigator {
	public StopsRoute navigate(Point origin, Point destination, String submap);
}
