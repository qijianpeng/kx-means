package util;

import java.util.List;

import entity.Cluster;
import entity.PointBox;

public interface ClusterUtilsInterface {

	/**
	 * 读取文件中的数据
	 * @param dataPath 数据source file
	 * @param pointBox element 类型
	 * @return 所有数据点集
	 */
	public List<PointBox> inputData(String dataPath);
	/**
	 * @param alpha K值因子
	 * @param k 划分的簇数
	 * @param pointList 数据点集
	 * @return k个数据簇
	 */
	public List<Cluster> initClusters(double alpha,int k,List<PointBox> pointList);
	/**
	 * k-means++算法初始化簇心
	 * @param k
	 * @param pointList
	 * @return
	 */
	public List<Cluster> initClustersPlusPlus(int k,List<PointBox> pointList);
	/**
	 * 返回点到其它数据簇的距离
	 * @param pointBox 
	 * @param cluster
	 * @return
	 */
	public double getDistanceBetweenPointAndCluster(PointBox pointBox,Cluster cluster);
	/**
	 * 返回两个数据簇之间的距离
	 * @param clusterA
	 * @param clusterB
	 * @return
	 */
	public double getDistanceBetweenTwoClusters(Cluster clusterA,Cluster clusterB);
	/**
	 * 获取整体的Je收敛值(SSE)
	 * @param clusterList
	 * @return double型常数
	 */
	public double getTotalJe(List<Cluster> clusterList);
	/**
	 * 将数据点分散至各个数据簇
	 * @param pointList 点集
	 * @param clusterList 刚初始化的空的簇集
	 */
	public void distributeAllPointToClusters(List<PointBox> pointList,List<Cluster> clusterList);
	/**
	 * 执行一次K-Means算法
	 * 
	 */
	public void calcOneTime(List<Cluster> clusterList);
	/**
	 * K-Means算法的迭代计算部分
	 * @param pointList 点集
	 * @param clusterList 数据簇
	 * @param iterationTimes 迭代次数，无要求则填 null
	 */
	public void iterationCalc(List<Cluster> clusterList, Integer iterationTimes);
	/**
	 * Top N 算法，合并前N个相聚较近的簇,返回合并后的所有簇集
	 * @param clusterList
	 * @param n 合并前n个较近的簇
	 * @param k 最终合并后剩余簇数
	 * @return
	 */
	public List<Cluster> topN(List<Cluster> clusterList,int n);

	/**
	 * 合并两个数据簇，获得合并后的一个数据簇
	 * @param clusterA
	 * @param clusterB
	 * @return
	 */
	public Cluster combineTwoClusters(Long cid,Cluster clusterA,Cluster clusterB);
	/**
	 * 计算聚类的轮廓系数
	 * 运行结果分析
	 * a(o) = sum(distance(o,o'))/(|Ci| - 1)  o'∈ Ci
	 * b(o) = min{ sum[distance(o,o')]/|Cj| }  o'∈ Cj, 1<= j <= k, j != i
	 * s(o) = {b(o) - a(o)}/max{a(o),b(o)}
	 * 将得到数据的轮廓系数值
	 * @param clusterList
	 */
	public Double evaluateResult(List<Cluster> clusterList);

	/**
	 * 删除空簇
	 * @param clusterList
	 */
	public void deleteEmptyCluster(List<Cluster> clusterList);
	/**
	 * 运行传统的k-means算法，aplha的大小与数据点的多少有关
	 * @param path
	 * @param k
	 * @param alpha
	 * @param initMethod 初始化数据簇方法，"k-means","k-means++"两种
	 */
	public List<Cluster> executeKMeans(String dataPath,int k, double alpha,String initMethod);
	/**
	 * 运行传统的包含Top-N算法的k-means算法
	 * 该算法指定一个大于K的largerK进行聚类运算，带簇集稳定后执行top-n合并算法，将距离较近的簇集进行合并后
	 * 再执行聚类运算。以此反复，直到簇集达到K为止。
	 * @param dataPath 
	 * @param k
	 * @param alpha 
	 * @param largerK top-n 算法参数
	 * @param n top-n 算法参数
	 */
	public List<Cluster> executeKMeansWithTopN(String dataPath,int k, double alpha,int largerK,int n,String initMethod);
	/**
	 * 高效的k-means算法
	 * @param dataPath
	 * @param k
	 * @param alpha
	 * @param initMethod
	 * @return
	 */
	public List<Cluster> executeKmeansEfficiency(String dataPath,int k, double alpha,String initMethod);
	/**
	 * 高效的包含top-n的k-means算法
	 * @param dataPath
	 * @param k
	 * @param alpha
	 * @param largerK
	 * @param n
	 * @param initMethod
	 * @return
	 */
	public List<Cluster> executeKmeansEfficiencyWithTopN(String dataPath,int k, double alpha,int largerK,int n,String initMethod);
}
