package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entity.Cluster;
import entity.Side;

public class MatrixUtils {
	
	private static ClusterUtilsInterface clusterUtils = new ClusterUtils();
	/**
	 * 该函数将构造一个簇之间距离的上三角矩阵，并对该矩阵进行压缩存储进顺序链表中
	 * @param clusterList 数据簇集合
	 * @return 1D 数组
	 */
	public static List<Side> upperTriangularMatrix(List<Cluster> clusterList){
		// TODO Auto-generated method stub
		int colSize = clusterList.size();
		int rowSize = clusterList.size();
		int clusterSize = clusterList.size();
		int matrixSize = clusterSize*(clusterSize -1)/2;
		List<Side>  matrix = new ArrayList<Side>(matrixSize);
		for(int row = 1; row <= rowSize; row++){
			Cluster clusterA = clusterList.get(row - 1);
			for(int col = row+1; col <= colSize; col++){//注意，是从row+1开始
				Cluster clusterB = clusterList.get(col - 1);
				double distance = clusterUtils.getDistanceBetweenTwoClusters(clusterA, clusterB);
			//	int index = getIndexFromTwoId(clusterList.size(),row,col);
				Side side = new Side(row,col,distance);
				matrix.add(side);
				
			}
		}
		
		Collections.sort(matrix,new Compare());
		/*for(Side side:matrix){
			System.out.print((int)side.getSideLength()+"\t");
		}
		System.out.println();*/
		return matrix;
	}
	
	/**
	 * 计算第row行col列元素在matrix列表中的下标
	 * @param cidQuantity 数据簇总数
	 * @param row 第row行
	 * @param col 第col列
	 * @return 元素下标
	 */
	public static int getIndexFromTwoId(int cidQuantity,int row,int col){
		if(row > col){
			int tmp = col;
			col = row;
			row = tmp;
		}
		return (((2*cidQuantity - row)*(row - 1))/2 +(col - row));
		
	}
	
	public static ClusterUtilsInterface getClusterUtils() {
		return clusterUtils;
	}
	public static void setClusterUtils(ClusterUtilsInterface clusterUtils) {
		MatrixUtils.clusterUtils = clusterUtils;
	}
}
