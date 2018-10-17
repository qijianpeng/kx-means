package entity;

import entity.superclass.Point;

public class PointBox extends Point {

	//fileds
	private Long cid;//cluster id
	private Double distance;//distance between cluster and point.
	//constructors
	public PointBox() {
	}
	
	public PointBox(Long pid) {
		super(pid);
	}

	
	public PointBox(Long pid, Long cid) {
		super(pid);
		this.cid = cid;
	}

	//------------------------------------------
	public Long getCid() {
		return cid;
	}
	public Double getDistance() {
		return distance;
	}
	public void setCid(Long cid) {
		this.cid = cid;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
}
