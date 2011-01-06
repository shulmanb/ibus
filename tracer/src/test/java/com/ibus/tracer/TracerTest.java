/**
 * 
 */
package com.ibus.tracer;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;

import com.ibus.map.LineSegment;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopsRoute;
import com.ibus.map.TimedPoint;
import com.ibus.tracer.Status.StatusOnRoute;
import com.ibus.tracer.db.ISessionDB;
import com.ibus.tracer.db.RedisSessionDB;

/**
 * Integration tests for tracer with Reddis db
 * @author Home
 * 
 */
public class TracerTest {

	static Tracer tracer;
	static Jedis jedis;
	static SessionManager sm;
	static String ses;
	ObjectMapper mapper = new ObjectMapper();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		tracer = new Tracer();
		sm = new SessionManager();
		jedis = new Jedis("localhost", 6379);
		jedis.select(1);
		ISessionDB sesDb = new RedisSessionDB(jedis);
		sm.setSesDb(sesDb);
		tracer.setSesDb(sesDb);
	}

	@Before
	public void setUpTest() throws Exception {
		jedis.flushDB();
		ses = sm.createSession("testclient");
	}

	private StopsRoute preparePopulatedRouteWithLineSwitchOnStop() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		Stop start = new Stop(new Point(35.1036, 32.6596));
		Stop end = new Stop(new Point(35.0942, 32.6535));
		ArrayList<TimedPoint> pList = new ArrayList<TimedPoint>();
		pList.add(new TimedPoint(35.103659, 32.659668, 0));//30
		pList.add(new TimedPoint(35.101025, 32.657323, 70535));// 40
		pList.add(new TimedPoint(35.097579, 32.655391, 91526));// 50
		pList.add(new TimedPoint(35.094202, 32.653517, 106524));//60
		sr.setDest(new TimedPoint(35.094202, 32.653517, 106524));
		LineSegment ls = new LineSegment("2", "2", start, end, pList);
		sr.getRoute().add(ls);
		return sr;
	}

	private StopsRoute preparePopulatedRouteWithLineSwitchOnRemoteStopsWithWalkingDistance() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		Stop start = new Stop(new Point(35.0975, 32.6553));
		Stop end = new Stop(new Point(35.0942, 32.6535));
		ArrayList<TimedPoint> pList = new ArrayList<TimedPoint>();
		pList.add(new TimedPoint(35.097579, 32.655391, 0));
		pList.add(new TimedPoint(35.094202, 32.653517, 66524));
		sr.setDest(new TimedPoint(35.094202, 32.653517, 106524));
		LineSegment ls = new LineSegment("2", "2", start, end, pList);
		LineSegment walkingLs = new LineSegment(null, null, new Stop(new Point(35.1036, 32.6596)), start, null);
		sr.getRoute().add(walkingLs);
		sr.getRoute().add(ls);
		return sr;
	}
	
	
	private StopsRoute preparePopulatedRoute_OneLS() {
		List<LineSegment> lsList = new LinkedList<LineSegment>();
		Stop start = new Stop(new Point(35.1042, 32.6641));
		Stop end = new Stop(new Point(35.1036, 32.6596));
		ArrayList<TimedPoint> pList = new ArrayList<TimedPoint>();
		pList.add(new TimedPoint(35.104279, 32.664129, 0));//0
		pList.add(new TimedPoint(35.105817, 32.664377, 70535));// 10
		pList.add(new TimedPoint(35.105338, 32.662473, 91526));// 20
		pList.add(new TimedPoint(35.103659, 32.659668, 106524));//30
		LineSegment ls = new LineSegment("1", "1", start, end, pList);
		lsList.add(ls);
		StopsRoute sr = new StopsRoute("route", 106524, lsList,new TimedPoint(35.103659, 32.659668, 106524));
		return sr;
	}

	private StopsRoute preparePopulatedRoute_OneLS_ToRemotePoint() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		sr.setDest(new TimedPoint(35.104, 32.664, 0));
		return sr;
	}

	
	private StopsRoute preparePopulatedRoute_TwoLS() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		Stop start = new Stop(new Point(35.1036, 32.6596));
		Stop end = new Stop(new Point(35.0942, 32.6535));
		ArrayList<TimedPoint> pList = new ArrayList<TimedPoint>();
		pList.add(new TimedPoint(35.103659, 32.659668, 0));//30
		pList.add(new TimedPoint(35.101025, 32.657323, 70535));// 40
		pList.add(new TimedPoint(35.097579, 32.655391, 91526));// 50
		pList.add(new TimedPoint(35.094202, 32.653517, 106524));//60
		sr.setDest(new TimedPoint(35.094202, 32.653517, 106524));
		LineSegment ls = new LineSegment("1", "1", start, end, pList);
		sr.getRoute().add(ls);
		return sr;
	}

	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#storeLocation(java.lang.String, com.ibus.map.TimedPoint)}
	 * .
	 */
	@Test
	public void testStoreLocation() {
		try {
			TimedPoint p = new TimedPoint(1.0, 1.0, 1);
			tracer.storeLocation(ses, p);
			String s = jedis.hget(ses, "location");
			TimedPoint p1 = mapper.readValue(s, TimedPoint.class);
			assertEquals(p, p1);
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#getLocation(java.lang.String)}.
	 */
	@Test
	public void testGetLocation() {
		try {
			TimedPoint p = new TimedPoint(1.0, 1.0, 1);
			tracer.storeLocation(ses, p);
			TimedPoint p1 = tracer.getLocation(ses);
			assertEquals(p, p1);
		} catch (InvalidSessionException e) {
			fail("Exception Cought " + e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#storeTemporaryRoute(com.ibus.map.StopsRoute)}
	 * .
	 */
	@Test
	public void testStoreTemporaryRoute_EmptyRoute() {
		List<LineSegment> lsList = new LinkedList<LineSegment>();
		StopsRoute sr = new StopsRoute("route", 10, lsList,null);
		try {
			tracer.storeTemporaryRoute(sr);
		} catch (InvalidSessionException e) {
			fail("Exception Cought " + e.getMessage());
		}
		String str = jedis.get("route");
		StopsRoute sr1;
		try {
			sr1 = mapper.readValue(str, StopsRoute.class);
			assertEquals(sr, sr1);
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		} // gson.fromJson(str, StopsRoute.class);
	}

	@Test
	public void testStoreTemporaryRoute() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		try {
			tracer.storeTemporaryRoute(sr);
			String str = jedis.get("route");
			StopsRoute sr1 = mapper.readValue(str, StopsRoute.class);// gson.fromJson(str,
																		// StopsRoute.class);
			assertEquals(sr, sr1);
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}
	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#checkInToRoute(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testCheckInToRoute_NoRoute() {
		Object ex = null;
		try {
			tracer.checkInToRoute(ses, "1");
		} catch (InvalidSessionException e) {
			fail("Exception Cought " + e.getMessage());
		} catch (InvalidRouteException e) {
			ex = e;
		}
		assertNotNull(ex);
	}
	
	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#checkInToRoute(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testCheckInToRoute_NoSes() {
		Object ex = null;
		try {
			tracer.checkInToRoute("11", "1");
		} catch (InvalidSessionException e) {
			ex = e;
		} catch (InvalidRouteException e) {
			fail("Exception Cought " + e.getMessage());
		}
		assertNotNull(ex);
	}

	
	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#checkInToRoute(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testCheckInToRoute_OneLS() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());
			assertEquals(sr.getRouteId(), jedis.hget(ses, "checkIn"));
			
			String firststop = jedis.hget(ses + "status", "nextstop");
			Stop st = mapper.readValue(firststop, Stop.class);
			assertEquals(sr.getRoute().get(0).getStart(), st);

			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(stat.getStatus(), StatusOnRoute.ON_THE_WAY_TO_STATION);

			String lsStr = jedis.hget(ses + "status", "currls");
			assertEquals(null, lsStr);

			assertEquals(new Integer(1),jedis.llen(ses+"route"));

			String destStr = jedis.hget(ses, "destination");
			Point dest = mapper.readValue(destStr, Point.class);
			assertEquals(sr.getDest(), dest);
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#checkInToRoute(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testCheckInToRoute_TwoLS() {
		StopsRoute sr = preparePopulatedRoute_TwoLS();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());
			assertEquals(sr.getRouteId(), jedis.hget(ses, "checkIn"));

			String firststop = jedis.hget(ses + "status", "nextstop");
			Stop st = mapper.readValue(firststop, Stop.class);
			assertEquals(sr.getRoute().get(0).getStart(), st);

			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(stat.getStatus(), StatusOnRoute.ON_THE_WAY_TO_STATION);

			String lsStr = jedis.hget(ses + "status", "currls");
			assertEquals(null, lsStr);

			assertEquals(new Integer(2),jedis.llen(ses+"route"));
			
			String nextLsStr = jedis.lindex(ses+"route",0);
			assertEquals(mapper.readValue(nextLsStr, LineSegment.class), sr.getRoute().get(0));
			
			
			String destStr = jedis.hget(ses, "destination");
			Point dest = mapper.readValue(destStr, Point.class);
			assertEquals(sr.getDest(), dest);

		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}

	}
	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#getCheckInStatus(java.lang.String)}.
	 */
	@Test
	public void testGetCheckInStatus_NotCheckedIn() {
		try {
			assertEquals(null,tracer.getCheckInStatus(ses));
		} catch (InvalidSessionException e) {
			fail("Exception Cought " + e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#getCheckInStatus(java.lang.String)}.
	 */
	@Test
	public void testGetCheckInStatus_CheckedIn() {
		try {
			StopsRoute sr = preparePopulatedRoute_OneLS();
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses,sr.getRouteId());
			assertEquals(sr.getRouteId(),tracer.getCheckInStatus(ses));
		} catch (InvalidSessionException e) {
			fail("Exception Cought " + e.getMessage());
		} catch (InvalidRouteException e) {
			fail("Exception Cought " + e.getMessage());
		}
	}

	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#storeLocationOnRoute(java.lang.String, com.ibus.map.TimedPoint)}
	 * .
	 */
	@Test
	public void testStoreLocationOnRoute_ArriveToStation() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());

			assertEquals(new Integer(1),jedis.llen(ses+"route"));

			TimedPoint p = new TimedPoint(35.104279, 32.664129, 0);
			tracer.storeLocationOnRoute(ses, p);
			
			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.WAITING_TO_TRANSAPORT,stat.getStatus());

			String firststop = jedis.hget(ses + "status", "nextstop");
			Stop st = mapper.readValue(firststop, Stop.class);
			assertEquals(sr.getRoute().get(0).getEnd(), st);
			
			assertEquals(new Integer(0),jedis.llen(ses+"route"));

			assertEquals(new Integer(4), jedis.llen(ses+"segment"));
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}


	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#storeLocationOnRoute(java.lang.String, com.ibus.map.TimedPoint)}
	 * .
	 */
	@Test
	public void testStoreLocationOnRoute_StartFromRemotePoint_ArriveToStation() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());

			assertEquals(new Integer(1),jedis.llen(ses+"route"));

			TimedPoint p = new TimedPoint(35.104, 32.664, 0);
			tracer.storeLocationOnRoute(ses, p);

			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ON_THE_WAY_TO_STATION,stat.getStatus());

			String nextstop = jedis.hget(ses + "status", "nextstop");
			Stop st = mapper.readValue(nextstop, Stop.class);
			assertEquals(sr.getRoute().get(0).getStart(), st);
			
			assertEquals(new Integer(1),jedis.llen(ses+"route"));

			assertEquals(new Integer(0), jedis.llen(ses+"segment"));
			
			
			TimedPoint p1 = new TimedPoint(35.104279, 32.664129, 100);
			tracer.storeLocationOnRoute(ses, p1);
			
			statusStr = jedis.hget(ses, "status");
			stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.WAITING_TO_TRANSAPORT,stat.getStatus());

			nextstop = jedis.hget(ses + "status", "nextstop");
			st = mapper.readValue(nextstop, Stop.class);
			assertEquals(sr.getRoute().get(0).getEnd(), st);
			
			assertEquals(new Integer(0),jedis.llen(ses+"route"));

			assertEquals(new Integer(4), jedis.llen(ses+"segment"));
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}

	
	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#storeLocationOnRoute(java.lang.String, com.ibus.map.TimedPoint)}
	 * .
	 */
	@Test
	public void testStoreLocationOnRoute_ArriveToStation_StartMoving() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());
			TimedPoint p = new TimedPoint(35.104279, 32.664129, 0);
			tracer.storeLocationOnRoute(ses, p);
			
			TimedPoint p1 = new TimedPoint(35.104299, 32.664199, 1);
			tracer.storeLocationOnRoute(ses, p1);
			
			
			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ON_THE_TANSPORT, stat.getStatus());

			String firststop = jedis.hget(ses + "status", "nextstop");
			Stop st = mapper.readValue(firststop, Stop.class);
			assertEquals(sr.getRoute().get(0).getEnd(), st);

			assertEquals(new Integer(4), jedis.llen(ses+"segment"));
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}
	
	
	
	/**
	 * Test method for
	 * {@link com.ibus.tracer.Tracer#storeLocationOnRoute(java.lang.String, com.ibus.map.TimedPoint)}
	 * .
	 */
	@Test
	public void testStoreLocationOnRoute_ArriveToStation_MoveOnePoint() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());
			TimedPoint p = new TimedPoint(35.104279, 32.664129, 0);
			tracer.storeLocationOnRoute(ses, p);
			
			TimedPoint p1 = new TimedPoint(35.105624,32.664444, 1);
			tracer.storeLocationOnRoute(ses, p1);
			
			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(stat.getStatus(), StatusOnRoute.ON_THE_TANSPORT);

			String firststop = jedis.hget(ses + "status", "nextstop");
			Stop st = mapper.readValue(firststop, Stop.class);
			assertEquals(sr.getRoute().get(0).getEnd(), st);

			assertEquals(new Integer(3), jedis.llen(ses+"segment"));
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}

	@Test
	public void testStoreLocationOnRoute_ArriveToStation_MoveOnePoint_MoveToNextSegment() {
		StopsRoute sr = preparePopulatedRoute_TwoLS();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());
			TimedPoint p = new TimedPoint(35.104279, 32.664129, 0);//0
			tracer.storeLocationOnRoute(ses, p);
			
			TimedPoint p1 = new TimedPoint(35.105624,32.664444, 1);//9
			tracer.storeLocationOnRoute(ses, p1);
			
			TimedPoint p2 = new TimedPoint(35.10351,32.659464, 1);//31
			tracer.storeLocationOnRoute(ses, p2);

			
			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ON_THE_TANSPORT, stat.getStatus());

			String firststop = jedis.hget(ses + "status", "nextstop");
			Stop st = mapper.readValue(firststop, Stop.class);
			assertEquals(sr.getRoute().get(1).getEnd(), st);

			assertEquals(new Integer(4), jedis.llen(ses+"segment"));
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}

	@Test
	public void testStoreLocationOnRoute_ArriveToStation_MoveOnePoint_MoveToDest() {
		StopsRoute sr = preparePopulatedRoute_OneLS();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());
			//arrive to station
			TimedPoint p = new TimedPoint(35.104279, 32.664129, 0);//0
			tracer.storeLocationOnRoute(ses, p);
			
			//move one point
			TimedPoint p1 = new TimedPoint(35.105624,32.664444, 1);//9
			tracer.storeLocationOnRoute(ses, p1);
			
			//arrive to destination
			TimedPoint p2 = new TimedPoint(35.103945,32.660069, 1);//29
			tracer.storeLocationOnRoute(ses, p2);

			TimedPoint p3 = new TimedPoint(35.10365, 32.65966, 1);
			tracer.storeLocationOnRoute(ses, p3);
			
			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ARRIVED, stat.getStatus());

			String firststop = jedis.hget(ses + "status", "nextstop");
			assertNotNull(firststop);
			assertEquals(new Integer(0), jedis.llen(ses+"segment"));
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}


	@Test
	public void testStoreLocationOnRoute_ArriveToStation_MoveOnePoint_MoveToFinal_MoveToDest() {
		StopsRoute sr = preparePopulatedRoute_OneLS_ToRemotePoint ();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());
			//arrive to station
			TimedPoint p = new TimedPoint(35.104279, 32.664129, 0);//0
			tracer.storeLocationOnRoute(ses, p);
			
			//move one point
			TimedPoint p1 = new TimedPoint(35.105624,32.664444, 1);//9
			tracer.storeLocationOnRoute(ses, p1);
			
			//arrive to destination
			TimedPoint p2 = new TimedPoint(35.103945,32.660069, 1);//29
			tracer.storeLocationOnRoute(ses, p2);

			TimedPoint p3 = new TimedPoint(35.10365, 32.65966, 1);
			tracer.storeLocationOnRoute(ses, p3);
			
			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ON_THE_WAY_TO_DESTIANTION, stat.getStatus());

			String firststop = jedis.hget(ses + "status", "nextstop");
			assertNotNull(firststop);
			assertEquals(new Integer(0), jedis.llen(ses+"segment"));
			
			TimedPoint p4 = new TimedPoint(35.104, 32.66405, 10);
			tracer.storeLocationOnRoute(ses, p4);
			
			statusStr = jedis.hget(ses, "status");
			stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ARRIVED, stat.getStatus());
			
			
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}
	
	@Test
	public void testTrackSwitchLineOnStop(){
		StopsRoute sr = preparePopulatedRouteWithLineSwitchOnStop();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());
			//arrive to station
			TimedPoint p = new TimedPoint(35.104279, 32.664129, 0);//0
			tracer.storeLocationOnRoute(ses, p);
			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.WAITING_TO_TRANSAPORT, stat.getStatus());

			//move one point
			TimedPoint p1 = new TimedPoint(35.105624,32.664444, 1);//9
			tracer.storeLocationOnRoute(ses, p1);
			statusStr = jedis.hget(ses, "status");
			stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ON_THE_TANSPORT, stat.getStatus());

			
			//arrive to intermidiate station
			TimedPoint p2 = new TimedPoint(35.103659, 32.659668, 106524);
			tracer.storeLocationOnRoute(ses, p2);
			statusStr = jedis.hget(ses, "status");
			stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.WAITING_TO_TRANSAPORT, stat.getStatus());

			//move one point
			TimedPoint p3 = new TimedPoint(35.101025, 32.657323, 70535);
			tracer.storeLocationOnRoute(ses, p3);
			statusStr = jedis.hget(ses, "status");
			stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ON_THE_TANSPORT, stat.getStatus());
		
			
			TimedPoint p4 = new TimedPoint(35.094202, 32.653517, 106524);
			tracer.storeLocationOnRoute(ses, p4);
			statusStr = jedis.hget(ses, "status");
			stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ARRIVED, stat.getStatus());
			
			
		} catch (Exception e) {
			fail("Exception Cought " + e.getMessage());
		}
	}

	@Test
	public void testTrackSwitchLineOnRemoteStopsWithinWalkingDistance(){
		StopsRoute sr = preparePopulatedRouteWithLineSwitchOnRemoteStopsWithWalkingDistance();
		try {
			tracer.storeTemporaryRoute(sr);
			tracer.checkInToRoute(ses, sr.getRouteId());
			//arrive to station
			TimedPoint p = new TimedPoint(35.104279, 32.664129, 0);//0
			tracer.storeLocationOnRoute(ses, p);
			String statusStr = jedis.hget(ses, "status");
			Status stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.WAITING_TO_TRANSAPORT, stat.getStatus());

			//move one point
			TimedPoint p1 = new TimedPoint(35.105624,32.664444, 1);//9
			tracer.storeLocationOnRoute(ses, p1);
			statusStr = jedis.hget(ses, "status");
			stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ON_THE_TANSPORT, stat.getStatus());

			
			//arrive to intermidiate station
			TimedPoint p2 = new TimedPoint(35.103659, 32.659668, 106524);
			tracer.storeLocationOnRoute(ses, p2);
			statusStr = jedis.hget(ses, "status");
			stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ON_THE_WAY_TO_STATION, stat.getStatus());

			//arrive to next station
			TimedPoint p3 = new TimedPoint(35.097579, 32.655391, 0);
			tracer.storeLocationOnRoute(ses, p3);
			statusStr = jedis.hget(ses, "status");
			stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.WAITING_TO_TRANSAPORT, stat.getStatus());
		
			
			//arrive to destination
			TimedPoint p4 = new TimedPoint(35.094202, 32.653517, 106524);
			tracer.storeLocationOnRoute(ses, p4);
			statusStr = jedis.hget(ses, "status");
			stat = mapper.readValue(statusStr, Status.class);
			assertEquals(StatusOnRoute.ARRIVED, stat.getStatus());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception Cought " + e.getMessage());
		}
	}
}
