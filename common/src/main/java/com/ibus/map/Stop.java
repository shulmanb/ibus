package com.ibus.map;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Home
 *
 */
public class Stop extends Point{
	
	/**
	 *list of nodes in the stop (each node for a line) 
	 */
	private transient Map<String,Node> nodes;
	
	/**
	 * The stops description 
	 */
	protected String desc = null;

	private boolean isForeign;
	
	public Stop(){
		nodes = new HashMap<String,Node>();
		this.isForeign = false;
	}
	
	public Stop(String desc, Point p){
		this(p.round());
		this.desc = desc;
		this.isForeign = false;
		nodes = new HashMap<String,Node>();
	}

	public Stop(String desc, Point p, boolean isForeign){
		this(desc,p);
		this.isForeign = isForeign;
	}
	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@JsonIgnore
	public Point getStopsPoint() {
		return this;
	}

	@JsonIgnore
	public Collection<Node> getNodes() {
		return Collections.unmodifiableCollection(nodes.values());
	}
	
	@JsonIgnore
	protected Collection<String> getLinesInStop() {
		return Collections.unmodifiableCollection(nodes.keySet());
	}



	public Stop(Point p) {
		super(p.round());
		nodes = new HashMap<String,Node>();
	}
	
	public Stop addNode(Node node){
		nodes.put(node.getLine(),node);
		return this;
	}
	
	public void removeNode(String name){
		nodes.remove(name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@JsonIgnore
	public String getId(){
		return toString();
	}

	/**
	 * checks wether the stops ae connected by a direct line
	 * @param stop
	 * @return
	 */
	public boolean connectedTo(Stop stop) {
		for (Node n : stop.getNodes()) {
			for (Node n1 : getNodes()) {
				if(n.getLine().equals(n1.getLine())){
					return true;
				}
			}
		}
		return false;
	}
}
