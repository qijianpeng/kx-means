package entity;

public class Side {

	private int vertex_i = 0;
	private int vertex_j = 0;
	private double sideLength = 0.0;
	
	public Side(int vertex_i, int vertex_j, double sideLength) {
		super();
		this.vertex_i = vertex_i;
		this.vertex_j = vertex_j;
		this.sideLength = sideLength;
	}
	public int getVertex_i() {
		return vertex_i;
	}
	public int getVertex_j() {
		return vertex_j;
	}
	public double getSideLength() {
		return sideLength;
	}
	public void setVertex_i(int vertex_i) {
		this.vertex_i = vertex_i;
	}
	public void setVertex_j(int vertex_j) {
		this.vertex_j = vertex_j;
	}
	public void setSideLength(double sideLength) {
		this.sideLength = sideLength;
	}
	
}
