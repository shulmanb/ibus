package com.ibus.tracer;

/**
 * Status on the user on route
 * @author Home
 *
 */
public class Status{
	public enum StatusOnRoute{
		ON_THE_WAY_TO_STATION,
		WAITING_TO_TRANSAPORT,
		ON_THE_WAY_TO_DESTIANTION,
		ARRIVED,
		ON_THE_TANSPORT,
	}
	
	/**
	 * The status on route
	 */
	private StatusOnRoute status;
	/**
	 * Number of stations till end of the trip on the current line
	 */
	private int stationsTillOut;
	/**
	 * Time in minutes till end of the trip on the current line
	 */
	private int estimatedTimeTillOut;
	
	/**
	 * Time in minutes till the destination stop
	 */
	private int estimatedTimeTillDest;
	/**
	 * Current line name
	 */
	private String currLineName;
	/**
	 * Current line id
	 */
	private String currLineId;
	/**
	 * next line name
	 */
	private String nextLineName;
	/**
	 * next lien id
	 */
	private String nextLineId;
	
	
	public int getStationsTillOut() {
		return stationsTillOut;
	}
	public Status withStationsTillOut(int stationsTillOut) {
		this.stationsTillOut = stationsTillOut;
		return this;
	}
	public int getEstimatedTimeTillOut() {
		return estimatedTimeTillOut;
	}
	public Status withEstimatedTimeTillOut(int estimatedTimeTillOut) {
		this.estimatedTimeTillOut = estimatedTimeTillOut;
		return this;
	}
	public int getEstimatedTimeTillDest() {
		return estimatedTimeTillDest;
	}
	public Status withEstimatedTimeTillDest(int estimatedTimeTillDest) {
		this.estimatedTimeTillDest = estimatedTimeTillDest;
		return this;
	}
	public String getCurrLineName() {
		return currLineName;
	}
	public Status withCurrLineName(String currLineName) {
		this.currLineName = currLineName;
		return this;
	}
	public String getCurrLineId() {
		return currLineId;
		
	}
	public Status withCurrLineId(String currLineId) {
		this.currLineId = currLineId;
		return this;
	}
	public String getNextLineName() {
		return nextLineName;
	}
	public Status withNextLineName(String nextLineName) {
		this.nextLineName = nextLineName;
		return this;
	}
	public String getNextLineId() {
		return nextLineId;
	}
	public Status withNextLineId(String nextLineId) {
		this.nextLineId = nextLineId;
		return this;
	}
	public void setStationsTillOut(int stationsTillOut) {
		this.stationsTillOut = stationsTillOut;
	}
	public void setEstimatedTimeTillOut(int estimatedTimeTillOut) {
		this.estimatedTimeTillOut = estimatedTimeTillOut;
	}
	public void setEstimatedTimeTillDest(int estimatedTimeTillDest) {
		this.estimatedTimeTillDest = estimatedTimeTillDest;
	}
	public void setCurrLineName(String currLineName) {
		this.currLineName = currLineName;
	}
	public void setCurrLineId(String currLineId) {
		this.currLineId = currLineId;
	}
	public void setNextLineName(String nextLineName) {
		this.nextLineName = nextLineName;
	}
	public void setNextLineId(String nextLineId) {
		this.nextLineId = nextLineId;
	}
	public StatusOnRoute getStatus() {
		return status;
	}
	public void setStatus(StatusOnRoute status) {
		this.status = status;
	}
	
	public Status withStatus(StatusOnRoute status) {
		this.status = status;
		return this;
	}
	
}