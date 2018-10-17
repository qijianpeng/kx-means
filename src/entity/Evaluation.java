package entity;

public class Evaluation {

	private double value=0.0;
	private Long time=0L; 
	private Long iterTimes=0L;
	public double getValue() {
		return value;
	}
	public Long getTime() {
		return time;
	}
	public Long getIterTimes() {
		return iterTimes;
	}
	public void setIterTimes(Long iterTimes) {
		this.iterTimes = iterTimes;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public void eva(Long iterTimes) {
		this.iterTimes = iterTimes;
	}
	
}
