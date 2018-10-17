package entity;

public class CombineCluster {

	private String vertexSetString="[";
	private Cluster cluster;
	public Cluster getCluster() {
		return cluster;
	}
	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
	/**
	 * 判断是否包含当前节点（簇心）
	 * @param v
	 * @return
	 */
	public boolean isContainVertex(int v){
		if(vertexSetString.contains("["+v+"]"))return true;
		return false;
	}
	/**
	 * 添加一个节点
	 * @param v
	 */
	public void addVertex(int v){
		if(!this.vertexSetString.endsWith("]"))this.vertexSetString = this.vertexSetString+v+ "]";
		else this.vertexSetString = this.vertexSetString +",["+v+"]";
	}
	public String getVertexSetString() {
		return vertexSetString;
	}
	
}
