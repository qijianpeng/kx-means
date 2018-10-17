package util;

import java.util.ArrayList;
import java.util.List;

public class NMI {
	public static double nmi(List<List<Integer>> clusters, List<List<Integer>> classes, int total){
		//System.out.println("total size :"+total);
	/*	for(int i = 0; i < clusters.size(); i++){
			System.out.print(clusters.get(i).size()+",");
		}*/
		
		Double nmi = 0.0;
		for(int k = 0; k < clusters.size(); k++){
			List<Integer> cluster = clusters.get(k);
			for(int j = 0; j < classes.size(); j++){
				List<Integer> cl = classes.get(j);
				int interNum = intersection1(cluster, cl).size();
				if(interNum == 0 )continue;
				//System.out.println(interNum*1.0/total);
				Double numerator = interNum*1.0/total;
				//System.out.println(1.0*cl.size()*cluster.size());
				Double log = log2((interNum*1.0/(1.0*cl.size()*cluster.size()))*total);
				nmi = nmi + numerator*log;
			//	System.out.println("("+k+","+j+") 交集数目： "+interNum+"(cluster:"+cluster.size()+",classe:"+cl.size()+")\tNMI指数："+nmi);
			}
		}
		double clusterEntropy = entropy(clusters,total);
		double classesEntropy = entropy(classes,total);
//System.out.println("cluster entropy: "+clusterEntropy+"\tclasse entropy:"+classesEntropy+"\tnmi_total:"+nmi);		
//		System.out.println(nmi);
		return 2.0*nmi/(clusterEntropy+classesEntropy);
	}
	private static double entropy(List<List<Integer>> clusters , int total){
		double sum_p = 0.0;
		for(List<Integer> cluster : clusters){
			double p = cluster.size()*1.0/total;
			sum_p += p*log2(p);
		}
		return -sum_p;
	}
	public static double log2(double N){
		return Math.log(1.0*N)/Math.log(2.0);
	}
	private static List<Integer> intersection1(List<Integer> v1, List<Integer>v2){
		List<Integer> result = new ArrayList<Integer>();
		for(Integer e:v1){
			if(v2.contains(e))result.add(e);
		}
		
		return result;
	}
	/**
	 * 计算两个有序（从小到大）集合的交集，并返回该交集的高效算法
	 * @param v1 
	 * @param v2
	 * @return
	 */
	private static List<Integer> intersection(List<Integer> v1, List<Integer>v2){
		List<Integer> result = new ArrayList<Integer>();
		int minIndex =0 , maxIndex=0;
		List<Integer> minList = v1.size() >= v2.size() ? v2:v1;
		List<Integer> maxList = v1.size() < v2.size() ? v2:v1;
		for(;;){
			//1. 同时遍历两个列表，直到其中一个或两个遍历到链表末端
			if(minList.get(minIndex).equals(maxList.get(maxIndex))){
				result.add(minList.get(minIndex));
				minIndex++;maxIndex++;
			}
			else if(minList.get(minIndex) > maxList.get(maxIndex))maxIndex++;
			else minIndex++;
			//其中有链表遍历到末端时，当前的一个元素有可能还未找到相应的匹配，此时对另一条链表进行遍历比较
			if(minIndex == minList.size()-1 && maxIndex != maxList.size()-1){
				List<Integer> list = maxList.subList(maxIndex, maxList.size()-1);
				for(Integer value : list){
					if(minList.get(minIndex).equals(value)){
						{
							result.add(value);break;
						}
					}
				}
				break;
			}
			if(minIndex != minList.size()-1 && maxIndex == maxList.size()-1){
				List<Integer> list = minList.subList(minIndex, minList.size()-1);
				for(Integer value : list){
					if(maxList.get(maxIndex).equals(value)){
						{
							result.add(value);break;
						}
					}
				}
				break;
			}
			if(minIndex == minList.size()-1 && maxIndex == maxList.size()-1){
				Integer value = minList.get(minIndex);
				if(maxList.get(maxIndex).equals(value)){
					result.add(value);break;
				}
				break;
			}
		}
		return result;
	}

/*	*//**
	 * 计算聚类cluster中的成员在类classes中出现的概率
	 * @param cluster
	 * @param classes
	 * @return
	 *//*
	public static double probability(List<Integer> cluster, List<Integer> classes){
		double p = 0.0;
		int length = intersection(cluster,classes).size();
		if(length > 0) p = length*1.0/classes.size();
		return p;
	}*/
}
