package ctrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import entity.Cluster;
import entity.PointBox;
import etc.Configuration;
import util.ClusterUtils;
import util.EvaluationUtils;
import util.IOTools;
import util.NMI;

public class MainTime {
	public static String name="k_means";
	public static void main(String[] a) throws IOException{
		ClusterUtils clusterUtils = new ClusterUtils();
		String dataPath =  Configuration.path;
		int k = Configuration.k;// k
		double alpha = 1.0/Configuration.total*1.0;
		int largerK = 16 ;//k star
		List<Cluster>clusterList = clusterUtils.executeKmeansEfficiencyWithTopN(dataPath, k, alpha, 2*k, 2, "k-means++");
		System.out.println();
	}
}
//List<Cluster>clusterList = clusterUtils.executeKMeans(dataPath, k, alpha,"k-means++");
//List<Cluster>clusterList = clusterUtils.executeKmeansEfficiency(dataPath, k, alpha, initMethod);
//List<Cluster>clusterList = clusterUtils.executeKMeansWithTopN(dataPath, k, alpha, largerK, n, initMethod);