package entity.superclass;

import java.util.ArrayList;
import java.util.List;

public class Point {
	//fileds
	private Long pid;
	private List<Double> attrs = new ArrayList<Double>();
	//constructors
	
	public int length = 0;
	public List<Double> getAttrs() {
		return attrs;
	}
	public void setAttrs(List<Double> attrs) {
		this.attrs = attrs;
	}
	public void addAttr(Double attr){
		this.length++;
		this.attrs.add(attr);
	}
	public Point() {
		super();
	}
	public Point(Long pid) {
		super();
		this.pid = pid;
	}
	//access
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	
}
