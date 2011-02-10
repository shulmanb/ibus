import java.util.Arrays;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.ibus.map.Point;
import com.ibus.mapbuilder.Mapbuilder;
import com.ibus.mapbuilder.db.SimpleDBRedisBuilderDB;
import com.ibus.mapbuilder.module.MapbuilderModule;
//
public class MapBuilderTester {
	@Test @Ignore
	public void testMapBuilder() {
		// Jedis jedis = new Jedis("localhost", 6379);
		// jedis.set("key", "value");
		// System.out.println(jedis.get("key"));

		
		
		MapbuilderModule.initialize("localhost",6379, "AKIAIJPN5YYDFNRZHUSQ", "0F6RjfqqS6sUjl1E886suHDWrrVPL5WMGeWipYtB");
		Mapbuilder mb = MapbuilderModule.getMapbuilder();
		
		Gson gson = new Gson();
		StationContainer start = gson.fromJson(Line1.LINE1_DATA[0],
				StationContainer.class);

		String sesId = mb.initiateRouteRecording(Long.parseLong(start.station.ts), 
							      new Point(Float.valueOf(start.station.longit),Float.valueOf(start.station.lat)), 
								  start.station.line, 
								  start.station.submap);
		
		try {
			String[] arr = Arrays.copyOfRange(Line1.LINE1_DATA, 1, Line1.LINE1_DATA.length-2);
			for (String str : arr) {
				if (str.contains("point")) {
					PointContainer p = gson.fromJson(str, PointContainer.class);
					mb.addPoint(sesId,Long.valueOf(p.point.ts), new Point(Float.valueOf(p.point.longit),Float.valueOf(p.point.lat)));
				} else {
					StationContainer s = gson.fromJson(str,
							StationContainer.class);
					
					mb.addStation(sesId, Long.valueOf(s.station.ts), new Point(Float.valueOf(s.station.longit),Float.valueOf(s.station.lat)));
				}
			}
			StationContainer s1 = gson.fromJson(Line1.LINE1_DATA[Line1.LINE1_DATA.length-1],
					StationContainer.class);

			mb.finishRouteRecording(sesId, Long.parseLong(s1.station.ts), new Point(Float.valueOf(s1.station.longit),Float.valueOf(s1.station.lat)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
