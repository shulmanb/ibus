package com.ibus.navigation.map;

import junit.framework.TestCase;

import com.ibus.map.LineSegment;
import com.ibus.map.Node;
import com.ibus.map.Point;
import com.ibus.map.Stop;
import com.ibus.map.StopsRoute;
import com.ibus.navigation.dijkstra.RoutesMap;
import com.ibus.navigation.dijkstra.RoutesMapFactory;
import com.ibus.navigation.map.db.IMapDBLoader;


public class LanesMapTest extends TestCase{
	
	
	public static class DumyComplexMapDBLoader implements IMapDBLoader{
		//line 1
		Node nodeA = new Node("1", 0);//32.773694, 35.013176//Shilon/Yigal Alon
		Node nodeB = new Node("1", 1);//32.774696, 35.013755
		Node nodeC = new Node("1", 2);//32.776396, 35.013878
		Node nodeD = new Node("1", 3);//32.777411, 35.012360
		Node nodeE = new Node("1", 4);//32.780825, 35.01346
		Node nodeF = new Node("1", 5);//32.783468, 35.01507 //Ziv, trumpeldor
		
		//line 2
		Node nodeG = new Node("2", 6);//32.773388, 35.011282//yigal alon/via biram 
		Node nodeH = new Node("2", 7);//32.774227, 35.011215
		Node nodeI = new Node("2",8);//32.775242, 35.010172
		Node nodeJ = new  Node("2",9);//32.77730, 35.00984
		Node nodeK = new Node("2",10);//32.780473, 35.00964
		Node nodeL = new Node("2", 11);//32.78224, 35.01038
		Node nodeF1 = new Node("2", 12); //same as f

		Point a = new Point((float)32.773694, (float)35.013176);
		Point b = new Point((float)32.774696, (float)35.013755);
		Point c = new Point((float)32.776396, (float)35.013878);
		Point d = new Point((float)32.777411, (float)35.012360);
		Point e = new Point((float)32.780825, (float)35.01346);
		Point f = new Point((float)32.783468, (float)35.01507);
		Point g = new Point((float)32.773388, (float) 35.011282);
		Point h = new Point((float)32.774227, (float)35.011215);
		Point i = new Point((float)32.775242, (float)35.010172);
		Point j = new Point((float)32.77730, (float)35.00984);
		Point k = new Point((float)32.780473, (float)35.00964);
		Point l = new Point((float)32.78224, (float)35.01038);

		Stop stopA = new Stop("A", a).addNode(nodeA);////Shilon/Yigal Alon
		Stop stopB = new Stop("B", b).addNode(nodeB);//, 
		Stop stopC = new Stop("C", c).addNode(nodeC);//, 
		Stop stopD = new Stop("D", d).addNode(nodeD);//, 
		Stop stopE = new Stop("E", e).addNode(nodeE);//, 
		Stop stopF = new Stop("F", f).addNode(nodeF).addNode(nodeF1);//,  //Ziv, trumpeldor
		
		//line 2
		Stop stopG = new Stop("G", g).addNode(nodeG);//,//yigal alon/via biram 
		Stop stopH = new Stop("H", h).addNode(nodeH);//, 
		Stop stopI = new Stop("I",i).addNode(nodeI);//, 
		Stop stopJ = new  Stop("J",j).addNode(nodeJ);//, 
		Stop stopK = new Stop("K",k).addNode(nodeK);//, 
		Stop stopL = new Stop("L", l).addNode(nodeL);//, 

		@Override
		public NodesGraph getRoutesMap(String id) {
			
			Edge a_b  = new Edge(nodeA, nodeB,3);
			Edge b_c = new Edge(nodeB, nodeC, 3);
			Edge c_d = new Edge(nodeC, nodeD, 3);
			Edge d_e = new Edge(nodeD, nodeE, 5);
			Edge e_f = new Edge(nodeE, nodeF, 5);
			
			Edge g_h = new Edge(nodeG, nodeH, 2);
			Edge h_i = new Edge(nodeH, nodeI,3);
			Edge i_j = new Edge(nodeI, nodeJ, 3);
			Edge j_k = new Edge(nodeJ, nodeK, 3);
			Edge k_l = new Edge(nodeK, nodeL, 2);
			Edge l_f1 = new Edge(nodeL, nodeF1, 10);
			Edge f_f1 = new Edge(nodeF, nodeF1, 10);
			Edge f1_f = new Edge(nodeF1, nodeF, 10);
			NodesGraph graph = new NodesGraph();
			
			graph.edges =  new Edge[]{a_b, b_c, c_d, d_e, e_f, g_h, h_i, i_j, j_k, k_l, l_f1, f_f1, f1_f};
			graph.nodesNumber = 13;
			graph.segments = getLineSegments(id);
			return graph;
		}

		@Override
		public Stop[] getStops(String id) {
			return new Stop[]{stopA, stopB, stopC, stopD, stopE, stopF, stopG, stopH, stopI, stopJ, stopK, stopL};
		}

		public LineSegment[] getLineSegments(String id) {
			
			LineSegment a_b = new LineSegment("1", stopA, stopB);
			LineSegment b_c = new LineSegment("1", stopB, stopC);
			LineSegment c_d = new LineSegment("1", stopC, stopD);
			LineSegment d_e = new LineSegment("1", stopD, stopE);
			LineSegment e_f = new LineSegment("1", stopE, stopF);
			
			LineSegment g_h = new LineSegment("2", stopG, stopH);
			LineSegment h_i = new LineSegment("2", stopH, stopI);
			LineSegment i_j = new LineSegment("2", stopI, stopJ);
 			LineSegment j_k = new LineSegment("2", stopJ, stopK);
			LineSegment k_l = new LineSegment("2", stopK, stopL);
			LineSegment l_f = new LineSegment("2", stopL, stopF);

			return new LineSegment[]{a_b, b_c, c_d, d_e, e_f, g_h, h_i, i_j, j_k, k_l, l_f};
		}
		
	}
	
	
	public void testComplexRoute(){
		LinesMapFactory f = new LinesMapFactory();
		f.setMapLoader(new LanesMapTest.DumyComplexMapDBLoader()); 
		LinesMap map = f.loadMap("ttt");
		//start yigal alon  32.773077, 35.01240
		//end 32.784052, 35.014849
		
		StopsRoute route = map.findRoute(new Point(32.773077,35.01240), 
				      new Point(32.784052,35.014849));
		assertEquals(5, route.getRoute().size());
		assertEquals(19, route.getWeight());

	}
	
	public void testClosestStop(){
		LinesMapFactory f = new LinesMapFactory();
		f.setMapLoader(new LanesMapTest.DumyComplexMapDBLoader()); 
		LinesMap map = f.loadMap("ttt");
		Stop[] stops = map.findStopsNearby(new Point((float)32.773077,(float)35.01240),1);
		assertEquals("A", stops[0].getDesc());
		stops = map.findStopsNearby(new Point((float)32.773077,(float)35.01240),2);
		assertEquals("A", stops[0].getDesc());
		assertEquals("G", stops[1].getDesc());
	}
	
	public void testStopsInArea(){
		LinesMapFactory f = new LinesMapFactory();
		f.setMapLoader(new LanesMapTest.DumyComplexMapDBLoader()); 
		LinesMap map = f.loadMap("ttt");
		Stop[] stops = map.getStopsInArear(new Point((float)32.773077,(float)35.01240),500,500);
		assertTrue(stops.length== 10);
		stops = map.getStopsInArear(new Point((float)32.773077,(float)35.01240),1500,1500);
		assertTrue(stops.length== 12);
		stops = map.getStopsInArear(new Point((float)32.773077,(float)35.01240),190,190);
		assertTrue(stops.length== 4);
	}
	
	public void testAddCloseStopWithWalkingDistance(){
		RoutesMapFactory f = new RoutesMapFactory();
		RoutesMap m = f.createMap(4);
		LinesMap lm = new LinesMap(m, "");
		//line 1
		Node nodeA = new Node("1", 0);//32.773694, 35.013176//Shilon/Yigal Alon
		Node nodeD = new Node("1", 1);//32.777411, 35.012360
		
		//line 2
		Node nodeG = new Node("2", 2);//32.773388, 35.011282//yigal alon/via biram 
		Node nodeH = new Node("2", 3);//32.774227, 35.011215

		Point a = new Point((float)32.773694, (float)35.013176);
		Point d = new Point((float)32.777411, (float)35.012360);

		Point g = new Point((float)32.773388, (float) 35.011282);
		Point h = new Point((float)32.774227, (float)35.011215);

		Stop stopA = new Stop("A", a).addNode(nodeA);////Shilon/Yigal Alon
		Stop stopD = new Stop("D", d).addNode(nodeD);//, 
		
		//line 2
		Stop stopG = new Stop("G", g).addNode(nodeG);//,//yigal alon/via biram 
		Stop stopH = new Stop("H", h).addNode(nodeH);//, 

		m.addVertex(nodeA, nodeD, 1);
		m.addVertex(nodeG, nodeH, 3);
		lm.addStopToMap(stopA);
		lm.addStopToMap(stopD);
		lm.addStopToMap(stopG);
		lm.addStopToMap(stopH);
		assertTrue(m.getWeight(nodeA, nodeD)>0);
	}
	
	public void testJoinMapsNoMutuals(){
		RoutesMapFactory f = new RoutesMapFactory();
		RoutesMap m = f.createMap(2);
		RoutesMap m1 = f.createMap(2);
		LinesMap lm = new LinesMap(m, "");
		LinesMap lm1 = new LinesMap(m1, "");
		//line 1
		Node nodeA = new Node("1", 0);//32.773694, 35.013176//Shilon/Yigal Alon
		Node nodeD = new Node("1", 1);//32.777411, 35.012360
		
		//line 2
		Node nodeG = new Node("2", 0);//32.773388, 35.011282//yigal alon/via biram 
		Node nodeH = new Node("2", 1);//32.774227, 35.011215

		Point a = new Point((float)32.773694, (float)35.013176);
		Point d = new Point((float)32.777411, (float)35.012360);

		Point g = new Point((float)32.773388, (float) 35.011282);
		Point h = new Point((float)32.774227, (float)35.011215);

		Stop stopA = new Stop("A", a).addNode(nodeA);////Shilon/Yigal Alon
		Stop stopD = new Stop("D", d).addNode(nodeD);//, 
		
		//line 2
		Stop stopG = new Stop("G", g).addNode(nodeG);//,//yigal alon/via biram 
		Stop stopH = new Stop("H", h).addNode(nodeH);//, 

		m.addVertex(nodeA, nodeD, 1);
		m1.addVertex(nodeG, nodeH, 3);
		lm.addStopToMap(stopA);
		lm.addStopToMap(stopD);
		lm.addLineSegmentToMap(new LineSegment("1", stopA, stopD));
		lm1.addStopToMap(stopG);
		lm1.addStopToMap(stopH);
		lm1.addLineSegmentToMap(new LineSegment("2", stopG, stopH));
		lm.joinMaps(lm1);
		assertEquals(2,nodeG.getId());
		assertEquals(3,nodeH.getId());
		assertTrue(m.getWeight(nodeA, nodeD)>0);
		
		
	}
	public void testJoinMapsWithMutuals(){
		RoutesMapFactory f = new RoutesMapFactory();
		RoutesMap m = f.createMap(3);
		RoutesMap m1 = f.createMap(4);
		LinesMap lm = new LinesMap(m, "");
		LinesMap lm1 = new LinesMap(m1, "");
		//line 1
		Node nodeA = new Node("1", 0);//32.773694, 35.013176//Shilon/Yigal Alon
		Node nodeD = new Node("1", 1);//32.777411, 35.012360
		Node nodeD1 = new Node("1", 0);//32.777411, 35.012360
		
		//line 2
		Node nodeF = new Node("2", 2);//32.777411, 35.012360
		Node nodeF1 = new Node("2", 1);//32.777411, 35.012360
		Node nodeG = new Node("2", 2);//32.773388, 35.011282//yigal alon/via biram 
		Node nodeH = new Node("2", 3);//32.774227, 35.011215

		Point a = new Point((float)32.773694, (float)35.013176);
		Point d = new Point((float)32.777411, (float)35.012360);

		Point g = new Point((float)32.773388, (float) 35.011282);
		Point h = new Point((float)32.774227, (float)35.011215);

		Stop stopA = new Stop("A", a).addNode(nodeA);////Shilon/Yigal Alon
		Stop stopD = new Stop("D", d).addNode(nodeD).addNode(nodeF);//, 
		Stop stopD1 = new Stop("D", d).addNode(nodeD1).addNode(nodeF1);//, 
		
		//line 2
		Stop stopG = new Stop("G", g).addNode(nodeG);//,//yigal alon/via biram 
		Stop stopH = new Stop("H", h).addNode(nodeH);//, 

		m.addVertex(nodeA, nodeD, 1);
		
		m1.addVertex(nodeF1, nodeG, 3);
		m1.addVertex(nodeG, nodeH, 3);
		lm.addStopToMap(stopA);
		lm.addStopToMap(stopD);
		assertTrue(m.getWeight(nodeF, nodeD)>0);
		assertTrue(m.getWeight(nodeD, nodeF)>0);
		lm.addLineSegmentToMap(new LineSegment("1", stopA, stopD));
		lm1.addStopToMap(stopD1);
		assertTrue(m1.getWeight(nodeF1, nodeD1)>0);
		assertTrue(m1.getWeight(nodeD1, nodeF1)>0);
		lm1.addStopToMap(stopG);
		lm1.addStopToMap(stopH);
		lm1.addLineSegmentToMap(new LineSegment("2", stopD1, stopG));
		lm1.addLineSegmentToMap(new LineSegment("2", stopG, stopH));
		lm.joinMaps(lm1);
		assertEquals(3,nodeG.getId());
		assertEquals(4,nodeH.getId());
		assertTrue(m.getWeight(nodeA, nodeD)>0);
		assertTrue(m.getWeight(nodeA, nodeH)>0);
		//self connection
		assertTrue(m.getWeight(nodeF, nodeD)>0);
		
		
	}
	
}
