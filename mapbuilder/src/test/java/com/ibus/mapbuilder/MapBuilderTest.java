package com.ibus.mapbuilder;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import redis.clients.jedis.Jedis;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.ibus.map.Point;
import com.ibus.mapbuilder.db.IBuilderDB;
import com.ibus.mapbuilder.db.SimpleDBRedisBuilderDB;
public class MapBuilderTest {

	private  Mapbuilder mb = new Mapbuilder(); 
	private Jedis jedis;
	private AmazonSimpleDBClient sdb;
	
	@Before
	public void setUpBefore() throws Exception {
		jedis = mock(Jedis.class);
		sdb = mock(AmazonSimpleDBClient.class);
		IBuilderDB bdb = new SimpleDBRedisBuilderDB(jedis, sdb);
		mb.setDb(bdb);
	}

	@Test
	public void testInitiateRouteRecording() {
		String ses = mb.initiateRouteRecording(0, new Point(0,0), "1", "yoqneam");
		verify(jedis).setex(ses,60*60*12, "yoqneam:1");
		verify(jedis).rpush("list:"+ses, "");
		verify(jedis).expire("list:"+ses, 60*60*24);
		verify(jedis).rpush("list:"+ses, "{\"lat\":0.0,\"lon\":0.0,\"ts\":0,\"isStation\":true}");
		verifyNoMoreInteractions(jedis, sdb);
	}

	@Test
	public void testAddPoint() {
		mb.addPoint("ses", 1, new Point(1,1));
		verify(jedis).rpush("list:ses", "{\"lat\":1.0,\"lon\":1.0,\"ts\":1,\"isStation\":false}");
		verifyNoMoreInteractions(jedis, sdb);
	}

	@Test
	public void testAddStation() {
		mb.addStation("ses", 1, new Point(1,1));
		verify(jedis).rpush("list:ses", "{\"lat\":1.0,\"lon\":1.0,\"ts\":1,\"isStation\":true}");
		verifyNoMoreInteractions(jedis, sdb);
	}

	@Test
	public void testFinishRouteRecording() {
		//mock jedis.get session details
		when(jedis.get("ses")).thenReturn("yoqneam:1");
		//mock retrieve points
		final List<String> lst = new LinkedList<String>(); 
		lst.add("{\"lat\":0.0,\"lon\":0.0,\"ts\":0,\"isStation\":true}");
		lst.add("{\"lat\":1.0,\"lon\":1.0,\"ts\":1,\"isStation\":false}");
		lst.add("{\"lat\":2.0,\"lon\":2.0,\"ts\":2,\"isStation\":true}");
		when(jedis.lrange("list:ses",1, -1)).thenReturn(lst);
		
		//mock call to simple db
		when(sdb.getAttributes(any(GetAttributesRequest.class))).
			thenReturn(new GetAttributesResult());
		
		mb.finishRouteRecording("ses", 2,new Point(1,1));
		verify(jedis).rpush("list:ses", "{\"lat\":1.0,\"lon\":1.0,\"ts\":2,\"isStation\":true}");
		verify(jedis).get("ses");
		verify(jedis).lrange("list:ses",1, -1);
		verify(jedis).del("ses","list:ses");

		ArgumentCaptor<PutAttributesRequest> putAttributes = ArgumentCaptor.forClass(PutAttributesRequest.class);
		ArgumentCaptor<BatchPutAttributesRequest> batchPutAttributes = ArgumentCaptor.forClass(BatchPutAttributesRequest.class);
		
		verify(sdb,times(1)).getAttributes(any(GetAttributesRequest.class));
		verify(sdb,times(3)).putAttributes(putAttributes.capture());
		verify(sdb, times(2)).batchPutAttributes(batchPutAttributes.capture());
		verifyNoMoreInteractions(jedis, sdb);
		
		List<PutAttributesRequest> putAttrs = putAttributes.getAllValues();
		List<BatchPutAttributesRequest> batchPutAttrs = batchPutAttributes.getAllValues();
		
		PutAttributesRequest lineSegments = putAttrs.get(0);
		assertEquals(3,lineSegments.getAttributes().size());
		assertEquals("lineName",lineSegments.getAttributes().get(0).getName());
		assertEquals("1",lineSegments.getAttributes().get(0).getValue());
		assertEquals("submap",lineSegments.getAttributes().get(1).getName());
		assertEquals("yoqneam",lineSegments.getAttributes().get(1).getValue());
		assertEquals("0",lineSegments.getAttributes().get(2).getName());
		assertEquals("{\"lineId\":\"ses\",\"line\":\"1\",\"segmentIndx\":0,\"start\":{\"longitude\":0.0,\"latitude\":0.0},\"end\":{\"longitude\":2.0,\"latitude\":2.0},\"duration\":2}",lineSegments.getAttributes().get(2).getValue());

		PutAttributesRequest stationsList = putAttrs.get(1);
		assertEquals("{\"longitude\":0.0,\"latitude\":0.0}",stationsList.getAttributes().get(0).getValue());
		assertEquals("{\"longitude\":2.0,\"latitude\":2.0}",stationsList.getAttributes().get(1).getValue());

		PutAttributesRequest lineStationsList = putAttrs.get(2);
		assertEquals("{\"longitude\":0.0,\"latitude\":0.0}",lineStationsList.getAttributes().get(0).getValue());
		assertEquals("{\"longitude\":2.0,\"latitude\":2.0}",lineStationsList.getAttributes().get(1).getValue());

		
		BatchPutAttributesRequest points = batchPutAttrs.get(0);
		assertEquals("ses_0",points.getItems().get(0).getName());
		assertEquals("{\"time\":0,\"longitude\":0.0,\"latitude\":0.0}",points.getItems().get(0).getAttributes().get(0).getValue());
		assertEquals("{\"time\":1,\"longitude\":1.0,\"latitude\":1.0}",points.getItems().get(0).getAttributes().get(1).getValue());
		assertEquals("{\"time\":2,\"longitude\":2.0,\"latitude\":2.0}",points.getItems().get(0).getAttributes().get(2).getValue());
		
		BatchPutAttributesRequest stations = batchPutAttrs.get(1);
		assertEquals("0.0_0.0",stations.getItems().get(0).getName());
		assertEquals("submap",stations.getItems().get(0).getAttributes().get(0).getName());
		assertEquals("yoqneam",stations.getItems().get(0).getAttributes().get(0).getValue());
		assertEquals("lat",stations.getItems().get(0).getAttributes().get(1).getName());
		assertEquals("0.0",stations.getItems().get(0).getAttributes().get(1).getValue());
		assertEquals("lon",stations.getItems().get(0).getAttributes().get(2).getName());
		assertEquals("0.0",stations.getItems().get(0).getAttributes().get(2).getValue());
		assertEquals("lines",stations.getItems().get(0).getAttributes().get(3).getName());
		assertEquals("[\"ses\"]",stations.getItems().get(0).getAttributes().get(3).getValue());
		assertEquals("linesNames",stations.getItems().get(0).getAttributes().get(4).getName());
		assertEquals("[\"1\"]",stations.getItems().get(0).getAttributes().get(4).getValue());

		assertEquals("2.0_2.0",stations.getItems().get(1).getName());
		assertEquals("submap",stations.getItems().get(1).getAttributes().get(0).getName());
		assertEquals("yoqneam",stations.getItems().get(1).getAttributes().get(0).getValue());
		assertEquals("lat",stations.getItems().get(1).getAttributes().get(1).getName());
		assertEquals("2.0",stations.getItems().get(1).getAttributes().get(1).getValue());
		assertEquals("lon",stations.getItems().get(1).getAttributes().get(2).getName());
		assertEquals("2.0",stations.getItems().get(1).getAttributes().get(2).getValue());
		assertEquals("lines",stations.getItems().get(1).getAttributes().get(3).getName());
		assertEquals("[\"ses\"]",stations.getItems().get(1).getAttributes().get(3).getValue());
		assertEquals("linesNames",stations.getItems().get(1).getAttributes().get(4).getName());
		assertEquals("[\"1\"]",stations.getItems().get(1).getAttributes().get(4).getValue());

	}

	@Test
	public void testFinishRouteRecording_BaTches() {
		//mock jedis.get session details
		when(jedis.get("ses")).thenReturn("yoqneam:1");
		//mock retrieve points
		final List<String> lst = new LinkedList<String>(); 
		lst.add("{\"lat\":0.0,\"lon\":0.0,\"ts\":0,\"isStation\":true}");
		for(int i = 0;i<500;i++){
			lst.add("{\"lat\":1.0"+i+",\"lon\":1.0"+i+",\"ts\":1,\"isStation\":false}");
		}
		lst.add("{\"lat\":2.0,\"lon\":2.0,\"ts\":2,\"isStation\":true}");
		when(jedis.lrange("list:ses",1, -1)).thenReturn(lst);
		
		//mock call to simple db
		when(sdb.getAttributes(any(GetAttributesRequest.class))).
			thenReturn(new GetAttributesResult());
		
		mb.finishRouteRecording("ses", 2,new Point(2,2));

		verify(jedis).rpush("list:ses", "{\"lat\":2.0,\"lon\":2.0,\"ts\":2,\"isStation\":true}");
		verify(jedis).get("ses");
		verify(jedis).lrange("list:ses",1, -1);
		verify(jedis).del("ses","list:ses");

		ArgumentCaptor<PutAttributesRequest> putAttributes = ArgumentCaptor.forClass(PutAttributesRequest.class);
		ArgumentCaptor<BatchPutAttributesRequest> batchPutAttributes = ArgumentCaptor.forClass(BatchPutAttributesRequest.class);
		
		verify(sdb,times(1)).getAttributes(any(GetAttributesRequest.class));
		verify(sdb,times(3)).putAttributes(putAttributes.capture());
		verify(sdb, times(3)).batchPutAttributes(batchPutAttributes.capture());
		verifyNoMoreInteractions(jedis, sdb);
		

	}


}
