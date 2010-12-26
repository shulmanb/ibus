package com.ibus.navigation.map;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import com.ibus.map.Node;
import com.ibus.map.Point;
import com.ibus.map.Stop;

public class NodesRouteTest {

	@Test
	public void testIsBetter() {
		Point start = new Point(0,0);
		Point end = new Point(2,2);

		Point start1 = new Point((float)0.1,(float)0.1);
		Point end1 = new Point((float)2.1,(float)2.1);

		Point origin = new Point((float)0.2,(float)0.2);
		Point destination = new Point((float)2.2,(float)2.2);;

		
		Collection<Node> path = new LinkedList<Node>();
		path.add(new Node("a",1));
		path.add(new Node("b",2));
		NodesRoute nr1 = new NodesRoute(start, end, path, 10);

		Collection<Node> path1 = new LinkedList<Node>();
		path.add(new Node("c",3));
		path.add(new Node("d",4));
		NodesRoute nr2 = new NodesRoute(start1, end1, path1, 10);

		Collection<Node> path3 = new LinkedList<Node>();
		path.add(new Node("a",1));
		path.add(new Node("b",2));
		NodesRoute nr3 = new NodesRoute(start1, end1, path, 510);

		
		assertTrue(nr1.isBetter(null, origin, destination));
		assertTrue(nr2.isBetter(nr1, origin, destination));
		assertTrue(!nr3.isBetter(nr1, origin, destination));

	}

	@Test
	public void testContains() {
		Point start = new Point(0,0);
		Point end = new Point(1,1);
		Collection<Node> path = new LinkedList<Node>();
		path.add(new Node("a",1));
		path.add(new Node("b",2));
		NodesRoute nr = new NodesRoute(start, end, path, 10);
		Stop stop = new Stop(new Point((float)0.4,(float)0.4));
		stop.setDesc("A");
		stop.addNode(new Node("a",1));
		stop.addNode(new Node("c",2));
		Stop stop1 = new Stop(new Point((float)2,(float)2));
		stop1.setDesc("D");
		stop.addNode(new Node("d",3));
		stop.addNode(new Node("e",4));
		assertTrue(!nr.contains(stop1));
	}

}
