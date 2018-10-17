package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.ClusterUtils;

import entity.superclass.Point;


public class Cluster {
	//fileds
	private Long cid;//簇编号
	private Point mean;//簇的平均值
	private List<PointBox> points = new ArrayList<PointBox>();//本数据簇集
	private Long pointNum = 0L;//簇中包含点数
	private boolean changeFlag = false;//簇集改变标记 true：有点的变化 false：无变化
	private Double clusterJe=0.0;
	private Long movedPointsNum = 0L;
	private Double radius = 0.0;
	//-----update:copy
	private Double clusterJeCopy = 0.0;
	private Point meanCopy = new Point();
	private ClusterUtils clusterUtils = new ClusterUtils();
	public Double getClusterJeCopy() {
		return clusterJeCopy;
	}
	public Point getMeanCopy() {
		return meanCopy;
	}
	public void setClusterJeCopy(Double clusterJeCopy) {
		this.clusterJeCopy = clusterJeCopy;
	}
	public void setMeanCopy(Point meanCopy) {
		this.meanCopy.getAttrs().clear();
		this.meanCopy.length = 0;
		for(Double value: meanCopy.getAttrs()){
			this.meanCopy.addAttr(value);
		}
	}
	public void updateMeanAndSSEByAddOnePoint(PointBox pointBox){
		int n = points.size();
		double clusterJe = 0.0;
		for(int i = 0; i < this.mean.length; i++){
		//	System.out.println(this.mean.length);
			double value = meanCopy.getAttrs().get(i)+(pointBox.getAttrs().get(i)
					-meanCopy.getAttrs().get(i))/(n+1);
			this.getMeanCopy().getAttrs().set(i, value);
			clusterJe += Math.pow(pointBox.getAttrs().get(i) - meanCopy.getAttrs().get(i), 2);
		}
		this.clusterJeCopy = clusterJeCopy + n*clusterJe/(n+1);
		
	}
	public void updateMeanAndSSEByDeleteOnePoint(PointBox pointBox){
		int n = points.size();
		double clusterJe = 0.0;
		for(int i = 0; i < this.mean.length; i++){
			double value = meanCopy.getAttrs().get(i)+(meanCopy.getAttrs().get(i)-
					pointBox.getAttrs().get(i))/(n-1);
			this.getMeanCopy().getAttrs().set(i, value);
			clusterJe += Math.pow(pointBox.getAttrs().get(i) - meanCopy.getAttrs().get(i), 2);
		}
		
		this.clusterJeCopy = clusterJeCopy - n*clusterJe/(n-1);
	}
	//-----update by increase
	public Double getRadius() {
		return radius;
	}
	public void setRadius(Double radius) {
		this.radius = radius;
	}
	public Long getMovedPointsNum() {
		return movedPointsNum;
	}
	public void setMovedPointsNum(Long movedPointsNum) {
		this.movedPointsNum = movedPointsNum;
	}
	//construtors
	public Cluster() {
		super();
	}
	/**
	 * 
	 * @param cid 簇集编号
	 * @param mean 簇的平均值
	 */
	public Cluster(Long cid, Point mean) {
		super();
		this.cid = cid;
		this.mean = mean;
		this.meanCopy = mean;
	}
	/**
	 * 
	 * @param cid 簇集编号
	 */
	public Cluster(Long cid) {
		super();
		this.cid = cid;
	}
	//start:cluster's methods-------------------------------
	public void movedPointsIncrease(Long step){
		this.movedPointsNum += step;
	}
	public void updateMean() {
		// TODO Auto-generated method stub
		Iterator<PointBox> it = points.iterator();
		double xSum = 0.0;
		double ySum = 0.0;
		int i = 0;
		List<Double> sumList = new ArrayList<Double>();
		for(int j = 0 ; j < this.mean.length; j++){
			sumList.add(0.0);
		}
		while(it.hasNext()){
			PointBox pointBox = it.next();
			for(int j = 0 ; j < pointBox.length; j++){
				sumList.set(j,sumList.get(j)+ pointBox.getAttrs().get(j));
			}
			i++;
		}
		if(pointNum != null && pointNum != 0)
			for(int j = 0; j < this.mean.length ; j++ ){
				mean.getAttrs().set(j, sumList.get(j)/pointNum);
			}
		
	}
	/**
	 * 遍历数据簇中所有点，重新计算Je值,并更新点到簇心distance的值。
	 */
	public void updateClusterJe(){
		clusterJe = 0.0;
		Iterator<PointBox> it = points.iterator();
		
		while(it.hasNext()){
			PointBox pointBox = it.next();
			int size = pointBox.length;
			double squreValue = 0.0;
			for(int i = 0; i < size; i++){
				squreValue += Math.pow(pointBox.getAttrs().get(i)-mean.getAttrs().get(i), 2);
			}
			//累加Je值
			clusterJe = clusterJe + squreValue;
			//同时更新点到簇心的距离
			double distance = Math.sqrt(squreValue);
			pointBox.setDistance(distance);
		}
	}
	public void updateRadius(){
		List<PointBox> pointList = this.getPoints();
		double maxDistance = 0.0;
		for(PointBox point:pointList){
			if(maxDistance < point.getDistance())maxDistance = point.getDistance();				
		}
		this.radius = maxDistance;
	}

	//end:cluster's methods-------------------------------
	//cluster attribute's methods
	public Long getCid() {
		return cid;
	}
	public Point getMean() {
		return mean;
	}
	public List<PointBox> getPoints() {
		return points;
	}
	public Long getPointNum() {
		return pointNum;
	}
	public boolean isChangeFlag() {
		return changeFlag;
	}
	public Double getClusterJe() {
		return clusterJe;
	}
	public void setCid(Long cid) {
		this.cid = cid;
	}
	public void setMean(Point mean) {
		this.mean = mean;
	}
	public void setPoints(List<PointBox> points) {
		this.points = points;
	}
	public void setPointNum(Long pointNum) {
		this.pointNum = pointNum;
	}
	public void setChangeFlag(boolean changeFlag) {
		this.changeFlag = changeFlag;
	}
	public void setClusterJe(Double clusterJe) {
		this.clusterJe = clusterJe;
	}
}
