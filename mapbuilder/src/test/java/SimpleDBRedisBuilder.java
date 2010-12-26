import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.ibus.map.Point;
import com.ibus.mapbuilder.Line1;
import com.ibus.mapbuilder.Line2;
import com.ibus.mapbuilder.PointContainer;
import com.ibus.mapbuilder.StationContainer;
import com.ibus.mapbuilder.db.SimpleDBRedisBuilderDB;
//@Ignore
public class SimpleDBRedisBuilder {
	@Test
	public void test() {
		// Jedis jedis = new Jedis("localhost", 6379);
		// jedis.set("key", "value");
		// System.out.println(jedis.get("key"));

		SimpleDBRedisBuilderDB srb = new SimpleDBRedisBuilderDB("localhost",
				6379, "AKIAIJPN5YYDFNRZHUSQ", "0F6RjfqqS6sUjl1E886suHDWrrVPL5WMGeWipYtB");
		Gson gson = new Gson();
		String	session = UUID.randomUUID().toString(); 
		srb.createRecordingSession(session, "2","il_yoqneam");
		try {
			for (String str : Line2.LINE2_DATA) {
				if (str.contains("point")) {
					PointContainer p = gson.fromJson(str, PointContainer.class);
					srb.addPoint(session, new Point(Double.valueOf(p.point.longit),Double.valueOf(p.point.lat)), Long.valueOf(p.point.ts));
				} else {
					StationContainer s = gson.fromJson(str,
							StationContainer.class);
					srb.addStation(session, new Point(Double.valueOf(s.station.longit),Double.valueOf(s.station.lat)), Long.valueOf(s.station.ts));
				}
			}
			srb.flushRoute(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
