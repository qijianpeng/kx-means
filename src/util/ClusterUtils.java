package util;
/**
 * 愿上帝保佑，永无bug。
 * 就算有bug也是一眼看出。 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.Cluster;
import entity.CombineCluster;
import entity.PointBox;
import entity.Side;
import entity.superclass.Point;
import etc.Configuration;

public class ClusterUtils implements ClusterUtilsInterface {
	private Double oldJe = Double.MAX_VALUE;
	private Long itCount = 0L;
	public Long time = 0L;
	@Override
	public List<PointBox> inputData(String dataPath) {
		// TODO Auto-generated method stub
		
		List<PointBox> pointList = new ArrayList<PointBox>();
		try{
			String encoding = "utf-8";
			File file = new File(dataPath);
			if(file.isFile() && file.exists()){
				InputStreamReader read = 
						new InputStreamReader(new FileInputStream(file),encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineText = null;
				Long count = 0L;
				while((lineText = bufferedReader.readLine()) != null){
					
					lineText = lineText.trim();if(null == lineText) break;
					if(lineText.startsWith("#") ||lineText.startsWith("@") || lineText.startsWith("%"))continue;//忽略注释
					//-------------------------输入字符格式------------------------
					String[] values = lineText.split(Configuration.separated);
					PointBox pointBox = new PointBox(count);
					for(int i = Configuration.dmStart; i < Configuration.dm+Configuration.dmStart; i++){
						String value = values[i];
						if(null != value){
						/*	if(value.equals("?")){
								//	System.out.print(value+"\t");
									
									value=""+ new Random().nextInt(60);
									//System.out.println(value);
								}*/
								//---------------------归一化处理
							/*	if(i==33)
								pointBox.addAttr(Double.valueOf(value)/1000.0);
								else*/
							pointBox.addAttr(Double.valueOf(value));
						}else{
							pointBox.addAttr(0.0);
						}
					}
					pointList.add(pointBox);
					count++;
//if(count==10000)break;//
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{			
		}
		Configuration.total = pointList.size();
		return pointList;
	}
	public List<List<Integer>> inputClasses(String dataPath){
		List<List<Integer>> clusters = new ArrayList<List<Integer>>();
		Map<String,List<Integer>> clustersMap = new HashMap<String,List<Integer>>();
		//1.读取文件内容
		try{
			String encoding = "utf-8";
			File file = new File(dataPath);
			if(file.isFile() && file.exists()){
				InputStreamReader read = 
						new InputStreamReader(new FileInputStream(file),encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineText = null;
				Long count = 0L;
				while((lineText = bufferedReader.readLine()) != null){
					lineText = lineText.trim();if(null == lineText) break;
					if(lineText.startsWith("#") || lineText.startsWith("@") || lineText.startsWith("%"))continue;//忽略注释
					//-------------------------输入字符格式------------------------
					String[] values = lineText.split(Configuration.separated);
					String className = values[Configuration.classPos];
					if(null == clustersMap.get(className))clustersMap.put(className, new ArrayList<Integer>());
					clustersMap.get(className).add(count.intValue());
					count++;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{			
		}
		//2.按照类别将点分类到指定的标签下	
		Set<String>keySet = clustersMap.keySet();
		for(String key : keySet){
			clusters.add(clustersMap.get(key));
		}
		return clusters;
	}
	@Override
	public List<Cluster> initClusters(double alpha,int k, List<PointBox> pointList) {
		// TODO Auto-generated method stub
		List<Cluster> clusterList = new ArrayList<Cluster>();
		//1. 随机选取alpha*totalPoints个点计算得出一个mean值
		int pointsNum = pointList.size();
		boolean[] selectedFlag = new boolean[pointsNum+1];//选择标记
		int alphaCount = 1;
	/*	if(alphaCount*k > pointsNum) 
		{
			System.out.println("alpha is too large!");return null;
		}*/
		//System.out.print(alphaCount+"\t");
		
		Random random = new Random();
		alphaCount = 1;
		for(int j = 1; j <= k; j++){
			Point mean = new Point();
			//随机获取点，并计算获得mean值
			for(int i = 1; i <= alphaCount; i++){				
				Integer index = random.nextInt(pointList.size());
				while(selectedFlag[index]){	//如果已经被选取，继续获取下一个				
					index = random.nextInt(pointList.size());
					
				}
				selectedFlag[index] = true;
				PointBox pointBox = pointList.get(index);
				for(Double value: pointBox.getAttrs()){
					mean.addAttr(value);
				}
			}//end for
			Cluster cluster = new Cluster();
			cluster.setMean(mean);
			clusterList.add(cluster);
			cluster.setCid(Long.valueOf(j));
		}//end for
		return clusterList;
	}
	public List<Cluster> initClusters2(int k, List<PointBox> pointList){
		//寻找最远的点作为mean
		List<Cluster> clusterList = new ArrayList<Cluster>();
		//1.随机选择一个点作为起始点M1
		
		Random random = new Random();
		int index = random.nextInt(pointList.size());
		//System.out.println(index);
		Cluster cluster1 = new Cluster(1L);
		cluster1.setMean(pointList.get(index));
		clusterList.add(cluster1);
		//System.out.println(cluster1.getMean().getX()+","+cluster1.getMean().getY());
		//2.对剩余点进行扫描，得到距离M1...Mi_1 最远的点记为 Mi
		for(int j = 2; j<= k;j++){
			PointBox farPoint = null;
			double maxDistance = 0.0 , oldMaxDistance = 0.0;
			//计算每个点到各个簇的距离和
			for(int i = 0; i< pointList.size(); i++){
				PointBox pointBox = pointList.get(i);
				
				for(int p = 0; p<clusterList.size();p++){
					Cluster cluster = clusterList.get(p);
					double distance = getDistanceBetweenPointAndCluster(pointBox, cluster);
					maxDistance += distance;
				}
				if(maxDistance > oldMaxDistance){
					oldMaxDistance = maxDistance;
					maxDistance = 0.0;
					farPoint = pointBox;
				}
			}
			if(null != farPoint){
				Cluster clusterj = new Cluster(Long.valueOf(j));
				clusterj.setMean(farPoint);
				clusterList.add(clusterj);
//				System.out.println(clusterList.size());
				farPoint = null;
			}
		}
		return clusterList;
	}
	@Override
	public List<Cluster> initClustersPlusPlus(int k, List<PointBox> pointList) {
		// TODO Auto-generated method stub
		List<Cluster> clusterList = new ArrayList<Cluster>();
		Random random = new Random();
		//1.随机选择一个点作为第一个Centorid
		int firstCenter = random.nextInt(pointList.size());
		PointBox pointBox = pointList.get(firstCenter);
		Cluster cluster = new Cluster();cluster.setMean(pointBox);
		cluster.setCid(1L);
		clusterList.add(cluster);
		boolean selected[] = new boolean[pointList.size()];
		selected[firstCenter] = true;
		//2.开始选取剩余k-1个点
		Iterator<PointBox> pointIterator = pointList.iterator();
		int count = 1;
		int selectedIndex = 0;
		while(count < k){
			count++;
			//2.1计算所有点到 中心点集 最近的距离min d(x)将其存进数组distArray[i],相加后获得总和sum
			double[] distArray = new double[pointList.size()+1];
			Double sum = 0.0;
			
			while(pointIterator.hasNext()){
				PointBox point = pointIterator.next();
				if(!selected[selectedIndex]){
					double minDistance = getNearestDistanceBetweenClustersAndPoint(point,clusterList);
					distArray[point.getPid().intValue()] = minDistance;
					sum += minDistance;
				}
				selectedIndex ++;
			}
			selectedIndex = 0;
			//2.2随机获取一个数，将rd -= d(x),直到rd<0,记录此时在distArray的下标index'
			double rd = (random.nextDouble()*sum);
			int index = pointList.size();
			while(rd > 0){
				rd -= distArray[index--];
			}
			//2.3选择index'th个点作为第count个簇的中心
			Cluster newCluster = new Cluster();
			newCluster.setMean(pointList.get(index));
			newCluster.setCid(Long.valueOf(count));
			clusterList.add(newCluster);
			pointIterator = pointList.iterator();
			
		}
		return clusterList;
	}
	protected double getNearestDistanceBetweenClustersAndPoint(PointBox point,
			List<Cluster> clusterList) {
		// TODO Auto-generated method stub
		double mindistance = Double.MAX_VALUE;
		for(Cluster cluster:clusterList){
			double distance = getDistanceBetweenPointAndCluster(point, cluster);
			if(distance < mindistance){
				mindistance = distance;
			}
		}
		return mindistance;
	}
	@Override
	public double getDistanceBetweenPointAndCluster(PointBox pointBox,
			Cluster cluster) {
		// TODO Auto-generated method stub
		double distance = 0.0;
		//System.out.println(pointBox.getX()+"\t"+pointBox.getY());
		int size = pointBox.length;
		for(int i = 0; i < size; i++){
			distance += Math.pow(pointBox.getAttrs().get(i)-cluster.getMean().getAttrs().get(i), 2);
		}
	//	System.out.println(xLength+"\t"+yLength);
		distance = Math.sqrt(distance);
		return distance;
	}

	@Override
	public double getTotalJe(List<Cluster> clusterList) {
		// TODO Auto-generated method stub
		//循环遍历Cluster list ，累加clusterJe
		double totalJe = 0.0;
		Iterator<Cluster> it = clusterList.iterator();
		while(it.hasNext()){
			Cluster cluster = (Cluster)it.next();
			totalJe += cluster.getClusterJe();
		}
		return totalJe;
	}
	public Long totalMovedCount = 0L;
	private StringBuilder message = new StringBuilder();
	@Override
	public void iterationCalc(List<Cluster> clusterList, Integer iterationTimes) {
		// TODO Auto-generated method stub
		boolean isContinue = true;	
		int iterationCount = 0;//记录当前迭代的次数
		double oldTotalJe = Double.MAX_VALUE;
		message.append("\n---------------------------------------------------------\n");
		while(isContinue){
			Long count = 0L;
			itCount++;
			iterationCount++;//当前迭代次数 +1
			//1.遍历簇集， 获取当前一个簇集:
			Iterator<Cluster> clusterIterator = clusterList.iterator();
			while(clusterIterator.hasNext()){
				Cluster cluster = clusterIterator.next();
				cluster.setMovedPointsNum(0L);
				//1.1. 遍历当前簇集中的点:
				Iterator<PointBox> pointIterator = cluster.getPoints().iterator();
				while(pointIterator.hasNext()){
					//1.1.1. 将点与其它簇集比较，找出距离最近的簇closeCluster，并将该点移动到closeCluster.
					PointBox pointBox = pointIterator.next();
					Iterator<Cluster> clusterIterator2 = clusterList.iterator();
					double closedDistance = pointBox.getDistance();
					Cluster closedCluster = null;//保存最近的簇
					//与其他簇进行比较
					while(clusterIterator2.hasNext()){
						Cluster cluster2 = clusterIterator2.next();
						if(cluster2.getCid() == cluster.getCid())
						{	continue;  }
						else{//找出距离最近的簇closeCluster
							double tmpDistance = 0.0;//保存与簇的最近距离
							if(closedDistance > (tmpDistance = getDistanceBetweenPointAndCluster(pointBox, cluster2))){
								closedDistance = tmpDistance;
								closedCluster = cluster2;
							}
						}//end else
					}//end while
					if(null != closedCluster && null != closedCluster.getPointNum()){
						count++;
						cluster.setPointNum((cluster.getPointNum()-1));
						//更新当前簇
						cluster.movedPointsIncrease(1L);//移除点数+1
						pointIterator.remove();	
						//将该点移动到closeCluster.
						pointBox.setCid(cluster.getCid());
						closedCluster.getPoints().add(pointBox);
						closedCluster.setPointNum((closedCluster.getPointNum() + 1));
					}
				}//end while 当前簇集遍历
			}
			clusterIterator = clusterList.iterator();//指针回位
			//2.更新簇集的mean值.
			double newTotalJe = 0.0;
			message.append("\n"+iterationCount+" step. points  moved : \n");
			//System.out.println();
			Long movedCount = 0L;
			while(clusterIterator.hasNext()){
				Cluster cluster = clusterIterator.next();
				cluster.updateMean();
				cluster.updateClusterJe();
				//计算Total Je值
				newTotalJe += cluster.getClusterJe();
				movedCount += cluster.getMovedPointsNum();
				message.append(cluster.getCid()+"-->"+cluster.getMovedPointsNum()+"\t");
			}
			//System.out.print(count+"\t");
			count = 0L;
			clusterIterator = clusterList.iterator();//指针回位
			totalMovedCount += movedCount;
			message.append("moved points: "+movedCount);
			message.append("\nP.N.:");
			while(clusterIterator.hasNext()){
				Cluster cluster = clusterIterator.next();
				message.append(cluster.getCid()+"("+cluster.getPointNum()+")\t");
			}
			//System.out.print(count+"\t");
			//3.判断是否已经收敛.
			//3.1 若收敛或者达到要求的迭代次数，则停止计算，否则继续循环
			if(newTotalJe >= oldTotalJe){
				//System.out.println(oldTotalJe);
				isContinue = false;
			}
			else if((null != iterationTimes)&&(iterationTimes == iterationCount))isContinue = false;
			else oldTotalJe = newTotalJe;
			
		}
		
		message.append("\niteration finished...\n");
		message.append("total moved points: " + totalMovedCount+"\n");
	}

	public void iterationCalcEfficiency(List<Cluster> clusterList, Integer iterationTimes) {
		// TODO Auto-generated method stub
		boolean isContinue = true;	
		int iterationCount = 0;//记录当前迭代的次数
		double oldTotalJe = Double.MAX_VALUE;
		message.append("\n---------------------------------------------------------\n");
		while(isContinue){
			itCount++;
			iterationCount++;//当前迭代次数 +1
			//1.遍历簇集， 获取当前一个簇集:
			Iterator<Cluster> clusterIterator = clusterList.iterator();
			while(clusterIterator.hasNext()){
				Cluster cluster = clusterIterator.next();
				if(cluster.getPoints().size() == 0) {
					clusterIterator.remove();
					continue;
				}
				cluster.setMovedPointsNum(0L);
				List<Cluster> compareClusters = new ArrayList<Cluster>();
				//优化开始，将距离当前簇集cluster远的簇集删除
				for(Cluster clu : clusterList){
					double radiusA = cluster.getRadius();
					double radiusB = clu.getRadius();
					if(getDistanceBetweenTwoClusters(cluster, clu) > (2*(radiusA+radiusB))){
						continue ;
					}
					else{
						compareClusters.add(clu);
					}
				}
				//1.1. 遍历当前簇集中的点:
				Iterator<PointBox> pointIterator = cluster.getPoints().iterator();
				while(pointIterator.hasNext()){
					//1.1.1. 将点与其它簇集比较，找出距离最近的簇closeCluster，并将该点移动到closeCluster.
					PointBox pointBox = pointIterator.next();
					Iterator<Cluster> compareClustersIterator = compareClusters.iterator();
					double closedDistance = pointBox.getDistance();
					Cluster closedCluster = null;//保存最近的簇
					//与其他簇进行比较
					while(compareClustersIterator.hasNext()){
						Cluster cluster2 = compareClustersIterator.next();
						if(cluster2.getCid() == cluster.getCid())continue;						
						else{//找出距离最近的簇closeCluster
							double tmpDistance = 0.0;//保存与簇的最近距离
							if(closedDistance > (tmpDistance = getDistanceBetweenPointAndCluster(pointBox, cluster2))){
								closedDistance = tmpDistance;
								closedCluster = cluster2;
							}
						}//end else
					}//end while
					if(null != closedCluster && null != closedCluster.getPointNum()){
						//-------update:0526
						closedCluster.updateMeanAndSSEByAddOnePoint(pointBox);
						cluster.updateMeanAndSSEByDeleteOnePoint(pointBox);
						cluster.setPointNum((cluster.getPointNum()-1));
						//更新当前簇
						cluster.movedPointsIncrease(1L);//移除点数+1
						pointIterator.remove();	
						//将该点移动到closeCluster.
						pointBox.setCid(cluster.getCid());
						closedCluster.getPoints().add(pointBox);
						closedCluster.setPointNum((closedCluster.getPointNum() + 1));
					}
				}//end while 当前簇集遍历
			}
			clusterIterator = clusterList.iterator();//指针回位
			//2.更新簇集的mean值.
			double newTotalJe = 0.0;
			message.append("\n"+iterationCount+" step. points  moved : \n");
			//System.out.println();
			Long movedCount = 0L;
			while(clusterIterator.hasNext()){
				Cluster cluster = clusterIterator.next();
				cluster.updateMean();
				cluster.updateClusterJe();
				//--------------update:0526
				/*cluster.setMean(cluster.getMeanCopy());
				cluster.setClusterJe(cluster.getClusterJeCopy());*/
				//-------------------------
				cluster.updateRadius();
				//计算Total Je值
				newTotalJe += cluster.getClusterJe();
				movedCount += cluster.getMovedPointsNum();
				message.append(cluster.getCid()+"-->"+cluster.getMovedPointsNum()+"\t");
			}
	//		System.out.println(iterationCount+"\t"+movedCount);
			clusterIterator = clusterList.iterator();//指针回位
			totalMovedCount += movedCount;
			
			message.append("moved points: "+movedCount);
			message.append("\nP.N.:");
			while(clusterIterator.hasNext()){
				Cluster cluster = clusterIterator.next();
				message.append(cluster.getCid()+"("+cluster.getPointNum()+")\t");
			}
			
			//3.判断是否已经收敛.
			//3.1 若收敛或者达到要求的迭代次数，则停止计算，否则继续循环
			if(newTotalJe >= oldTotalJe){
				//System.out.println(oldTotalJe);
				isContinue = false;
			}
			else if((null != iterationTimes)&&(iterationTimes == iterationCount))isContinue = false;
			else oldTotalJe = newTotalJe;
			
		}		
		message.append("\niteration finished...\n");
		message.append("total moved points: " + totalMovedCount+"\n");
	}
	@Override
	public void distributeAllPointToClusters(List<PointBox> pointList,
			List<Cluster> clusterList) {
		// TODO Auto-generated method stub
		Iterator<PointBox> pointIterator = pointList.iterator();
		Iterator<Cluster> clusterIterator = null;
		while(pointIterator.hasNext()){
			double closedDistance = Double.MAX_VALUE;
			Cluster closedCluster = null;
			
			PointBox pointBox = pointIterator.next();
			clusterIterator = clusterList.iterator();//指针回位
			//----------查找最近的簇
			while(clusterIterator.hasNext()){
				Cluster cluster = clusterIterator.next();
				double tmpDistance = 0.0;
				if(closedDistance > (tmpDistance = getDistanceBetweenPointAndCluster(pointBox, cluster))){
					closedDistance = tmpDistance;
					closedCluster = cluster;
				}
			}
			if(null != closedCluster){
				//System.out.println(pointBox.getPid() +" --> "+closedCluster.getCid());
				if(null == closedCluster.getPointNum()) closedCluster.setPointNum(0L);
				closedCluster.setPointNum((closedCluster.getPointNum() + 1));
				closedCluster.getPoints().add(pointBox);
				pointBox.setCid(closedCluster.getCid());
			}else{
				System.out.println("-------------------distribute all point to cluster error!-----------------");
			}
		}
		//更新mean
		clusterIterator = clusterList.iterator();
	//	System.out.println("init---------------------points num:");
		while(clusterIterator.hasNext()){
			Cluster cluster = clusterIterator.next();
			cluster.updateMean();
			cluster.updateClusterJe();
			
			cluster.setMeanCopy(cluster.getMean());
			cluster.setClusterJeCopy(cluster.getClusterJe());
		}
	//	System.out.println();
	}

	@Override
	public void calcOneTime(List<Cluster> clusterList) {
		// TODO Auto-generated method stub
		Iterator<Cluster> clusterIterator = clusterList.iterator();
		Double je = 0.0;
		while(clusterIterator.hasNext()){
			Cluster cluster = clusterIterator.next();
		    je += cluster.getClusterJe();
		}
		if(je >= oldJe){
			System.out.println("----------迭代结束！共迭代："+itCount+" 次");
			return;
		}
		else oldJe = je;
		itCount++;
		clusterIterator = clusterList.iterator();
		while(clusterIterator.hasNext()){
			Cluster cluster = clusterIterator.next();
			cluster.setMovedPointsNum(0L);
			//1.1. 遍历当前簇集中的点:
			Iterator<PointBox> pointIterator = cluster.getPoints().iterator();
			while(pointIterator.hasNext()){
				//1.1.1. 将点与其它簇集比较，找出距离最近的簇closeCluster，并将该点移动到closeCluster.
				PointBox pointBox = pointIterator.next();
				Iterator<Cluster> clusterIterator2 = clusterList.iterator();
				double closedDistance = pointBox.getDistance();
				Cluster closedCluster = null;//保存最近的簇
				//与其他簇进行比较
				while(clusterIterator2.hasNext()){
					Cluster cluster2 = clusterIterator2.next();
					if(cluster2.getCid() == cluster.getCid())
					{	continue;  }
					else{//找出距离最近的簇closeCluster
						double tmpDistance = 0.0;//保存与簇的最近距离
						if(closedDistance > (tmpDistance = getDistanceBetweenPointAndCluster(pointBox, cluster2))){
							closedDistance = tmpDistance;
							closedCluster = cluster2;
						}
					}//end else
				}//end while
				if(null != closedCluster){
					//更新当前簇
					pointIterator.remove();	
					cluster.setPointNum(cluster.getPointNum() - 1);
					cluster.movedPointsIncrease(1L);
					//将该点移动到closeCluster.
					pointBox.setCid(cluster.getCid());
					closedCluster.setPointNum(closedCluster.getPointNum() + 1);
					closedCluster.getPoints().add(pointBox);
				}
			}
		}
		clusterIterator = clusterList.iterator();//指针回位
		//2.更新簇集的mean值.
		System.err.println(itCount+". points moved : ");
		Long movedCount = 0L;
		while(clusterIterator.hasNext()){
			Cluster cluster = clusterIterator.next();
			cluster.updateMean();
			cluster.updateClusterJe();
			movedCount += cluster.getMovedPointsNum();
			System.out.print(cluster.getCid()+"-->"+cluster.getMovedPointsNum()+"\t");
		}
		System.out.println(" total : "+movedCount);
	}
	
	public Long getItCount() {
		return itCount;
	}
	public void setItCount(Long itCount) {
		this.itCount = itCount;
	}
	@Override
	public double getDistanceBetweenTwoClusters(Cluster clusterA,
			Cluster clusterB) {
		// TODO Auto-generated method stub
		double distance = 0.0;
		int size = clusterA.getMean().length;
		Point meanA = clusterA.getMean();
		Point meanB = clusterB.getMean();
		for(int i = 0; i < size; i++){
			Double dA = meanA.getAttrs().get(i);
			Double dB = meanB.getAttrs().get(i);
			distance += Math.pow(dA-dB, 2);
		}
		distance = Math.sqrt(distance);
		return distance;
	}

	Integer mergeCount = Integer.MAX_VALUE;
	@Override
	public List<Cluster> topN(List<Cluster> clusterList,int n) {
		// TODO Auto-generated method stub
		//System.out.print(getFloor(n));
		int size = clusterList.size();
		//获取前n个元素
		if(n < 1 || n > (clusterList.size()/2))return null;
		List<Side> topNSideList = MatrixUtils.upperTriangularMatrix(clusterList).subList(0, n);
		List<CombineCluster> combineClustersList = new ArrayList<CombineCluster>();//待合并数据簇
		List<Cluster> resultClustersList = new ArrayList<Cluster>();
		Iterator<Side> it = topNSideList.iterator();
		//合并第一条边
		if(it.hasNext()){
			Side side1 = it.next();
			int vi = side1.getVertex_i();
			int vj = side1.getVertex_j();
			mergeCount -= 2;
		//	System.out.println(clusterList.size()+":vi("+(vi-1)+")vj("+(vj-1)+")");
			Cluster clusterVi = clusterList.get(vi - 1);
			Cluster clusterVj = clusterList.get(vj - 1);
			CombineCluster combineCluster = new CombineCluster();
			Cluster cluster = combineTwoClusters(1L,clusterVi,clusterVj);
			combineCluster.setCluster(cluster);
			combineCluster.addVertex(vi);combineCluster.addVertex(vj);
			combineClustersList.add(combineCluster);
			clusterList.set(vj-1, null);clusterList.set(vi-1, null);
		}
		
		while(it.hasNext()){
			boolean notCombineFlag = true;
			Side side = it.next();
			int vi = side.getVertex_i();
			int vj = side.getVertex_j();
			int combineSize = combineClustersList.size();
			for(int p = 0; p < combineSize ;p++){
				CombineCluster combineCluster = combineClustersList.get(p);
				if(combineCluster.isContainVertex(vi) && combineCluster.isContainVertex(vj))
				{
					notCombineFlag = false;break;
				}
				if(combineCluster.isContainVertex(vi)){
					//将Cj加入
					Long cid = combineCluster.getCluster().getCid();
					Cluster cluster = combineCluster.getCluster();
					Cluster clusterj = clusterList.get(vj - 1);
			//		System.out.println(clusterList.size()+":vj("+(vj-1)+")");
					if(null == clusterj)break;//已经被合并
					combineCluster.setCluster(combineTwoClusters(cid, cluster, clusterj)); 
					combineCluster.addVertex(vj);
					clusterList.set(vj-1, null);
					mergeCount--;
					notCombineFlag = false;break;
				}
				if(combineCluster.isContainVertex(vj)){
					//将Ci加入
					Long cid = combineCluster.getCluster().getCid();
					Cluster cluster = combineCluster.getCluster();
					Cluster clusteri = clusterList.get(vi - 1);
					if(null == clusteri)break;//已经被合并
			//		System.out.println(clusterList.size()+":vi("+(vi-1)+")");
					combineCluster.setCluster(combineTwoClusters(cid, cluster, clusteri));
					combineCluster.addVertex(vi);
					clusterList.set(vi-1, null);
					mergeCount--;
					notCombineFlag = false;break;
				}
			}
			if(notCombineFlag){
				//没有被合并的情况
				Cluster clusterVi = clusterList.get(vi - 1);
				Cluster clusterVj = clusterList.get(vj - 1);
				CombineCluster combineCluster = new CombineCluster();
				if(clusterVi == null && clusterVj == null)continue;
			//	System.out.println(clusterList.size()+":vi("+(vi-1)+")vj("+(vj-1)+")");
				Cluster cluster = combineTwoClusters(Long.valueOf(combineClustersList.size() + 1),
								clusterVi,clusterVj);
				combineCluster.setCluster(cluster);
				combineCluster.addVertex(vi);combineCluster.addVertex(vj);
				mergeCount -= 2;
				clusterList.set(vj-1, null);clusterList.set(vi-1, null);
				combineClustersList.add(combineCluster);
			}
			
		}
		for(int i = 0; i < combineClustersList.size(); i++){
			CombineCluster cc = combineClustersList.get(i);
			//System.out.print(cc.getVertexSetString()+"\t");
			resultClustersList.add(cc.getCluster());
		}
		for(int i = 0; i < clusterList.size(); i++){
			if(null != clusterList.get(i)){
				Cluster cluster = clusterList.get(i);
				cluster.setCid(Long.valueOf(resultClustersList.size()));
				resultClustersList.add(cluster);
			}
		}
		//iterationCalc(clusterList, null);
		//System.out.print(","+(size-resultClustersList.size()));
		//System.out.println(","+n);
		iterationCalcEfficiency(resultClustersList, null);
		return resultClustersList;
	}
    private static int getFloor(int n){
    	return (int)Math.floor((Math.sqrt(1+8*n)-1)/2);
    }
	@Override
	public Cluster combineTwoClusters(Long cid,Cluster clusterA, Cluster clusterB) {
		// TODO Auto-generated method stub
		List<PointBox> clusterApoints = clusterA.getPoints();
		List<PointBox> clusterBpoints = clusterB.getPoints();
		
		Long pointsNum = Long.valueOf(clusterApoints.size())
				+ Long.valueOf(clusterBpoints.size());
		clusterApoints.addAll(clusterBpoints);
		Iterator<PointBox> it = clusterApoints.iterator();
		while(it.hasNext()){
			PointBox pointBox = it.next();
			pointBox.setCid(cid);
		}
		Cluster cluster = new Cluster();
		cluster.setCid(cid);
		cluster.setPointNum(pointsNum);
		cluster.setPoints(clusterApoints);
		cluster.setMovedPointsNum(0L);
		Point mean = getMeanFromTwoClusters(clusterA, clusterB);
		Double sse = getSSEFromTwoClusters(clusterA, clusterB);
		cluster.setMean(mean);
		cluster.setClusterJe(sse);
		cluster.setMeanCopy(mean);
		cluster.setClusterJeCopy(sse);
		
		/*cluster.setMean(new Point());
		cluster.updateMean();
		cluster.updateClusterJe();*/
		return cluster;
	}
	public Point getMeanFromTwoClusters(Cluster clusterA,Cluster clusterB){
		int n = clusterA.getPoints().size();
		int k = clusterB.getPoints().size();
		Point meani = clusterA.getMean();
		Point meanj = clusterB.getMean();
		int size = clusterA.getMean().length;
		Point mean = new Point();
		List<Double> aAttrs = clusterA.getMean().getAttrs();
		List<Double> bAttrs = clusterB.getMean().getAttrs();
		for(int i = 0 ; i < size; i++){
			mean.addAttr((aAttrs.get(i)*n+bAttrs.get(i)*k)/(n+k));
		}
		return mean;
	}
	/**
	 * 〖SSE〗_c= 〖SSE〗_i+〖SSE〗_j+n*m_i^2+k*m_j^2-〖(m_i*n+m_j*k)〗^2/(n+k)
	 * @param clusterA
	 * @param clusterB
	 * @return
	 */
	public double getSSEFromTwoClusters(Cluster clusterA,Cluster clusterB){
		double SSEi = clusterA.getClusterJe();
		double SSEj = clusterB.getClusterJe();
		Point mi = clusterA.getMean();
		Point mj = clusterB.getMean();
		double SSEc = 0.0;
		int size = clusterA.getMean().length;
		double mi_2 = 0.0;// mi.getX()*mi.getX()+mi.getY()*mi.getY();
		double mj_2 = 0.0;//mj.getX()*mj.getX()+mj.getY()*mj.getY();
		for(int i = 0; i < size; i++){
			mi_2 += Math.pow(mi.getAttrs().get(i),2);
			mj_2 += Math.pow(mj.getAttrs().get(i),2);
		}
		int n = clusterA.getPoints().size();
		int k = clusterB.getPoints().size();
		double sub = 0.0;
		for(int i = 0 ; i < size; i++){
			sub += Math.pow(mi.getAttrs().get(i)*n+mj.getAttrs().get(i)*k,2);
		}
		sub /= (n+k);
		SSEc = SSEi + SSEj + n * mi_2 + k * mj_2 - sub;
		return SSEc;
	}
	@Override
	public Double evaluateResult(List<Cluster> clusterList) {
		// TODO 评估聚类结果好坏
		   /*1.对于第i个元素x_j，计算x_j与其同一个簇内的所有其他元素距离的平均值，记作a_j，用于量化簇内的凝聚度。
		   2.选取x_j外的一个簇b，计算x_j与b中所有点的平均距离，遍历所有其他簇，找到最近的这个平均距离,记作b_j，用于量化簇之间分离度。
		   3.对于元素x_j，轮廓系数s_j = (b_j – a_j)/max(a_j,b_j)
		   4.计算所有x的轮廓系数，求出平均值即为当前聚类的整体轮廓系数*/
	//	System.out.println("开始计算轮廓系数……");
		Double silhouetteCoefficient = 0.0;
		Long totalPoints = 0L;
		for(int i = 0; i < clusterList.size(); i++){
			Cluster cluster = clusterList.get(i);
			totalPoints += Long.valueOf(cluster.getPoints().size());
			List<PointBox> pointList = cluster.getPoints();
			for(int j = 0; j < pointList.size(); j++){
				PointBox x_j = pointList.get(j);
				Double s_j = 0.0;
				//计算a_j
				Double a_j = 0.0;//与其同一个簇内的所有其他元素距离的平均值
				for(int m = 0; m < pointList.size();m++){
					PointBox pointM = pointList.get(m);
					if(x_j == pointM)continue;
					Double distance = getDistanceBetweenTwoPoint(x_j,pointM);
					
					a_j += distance;
				}//end 
				if(a_j.equals(Double.NaN))System.out.println("------====");
				a_j = a_j/((cluster.getPoints().size())*1.0);
				//end 计算b_j
				Double b_j = Double.MAX_VALUE,b_jj = 0.0;
				for(int k = 0; k < clusterList.size(); k++){
					Cluster b = clusterList.get(k);
					if(b == cluster)continue;
					List<PointBox> bPointList = b.getPoints();
					for(PointBox point: bPointList){
						b_jj += getDistanceBetweenTwoPoint(x_j,point);
					}
					b_jj = b_jj/(bPointList.size()*1.0);
					if(b_j > b_jj){
						b_j = b_jj;
						b_jj = 0.0;
					}
				}//end 计算b_j
				
				s_j = (b_j - a_j)/Math.max(a_j,b_j);
				silhouetteCoefficient += s_j;
			}
		}
		//计算轮廓系数 silhouetteCoefficient
		silhouetteCoefficient /= (totalPoints*1.0);
		return silhouetteCoefficient;
		
	}
	public double getDistanceBetweenTwoPoint(PointBox piontA,PointBox pointB){
		double distance = 0.0;
		int size = piontA.length;

		for(int i = 0; i < size; i++){
			Double dA = piontA.getAttrs().get(i);
			Double dB = pointB.getAttrs().get(i);
			distance += Math.pow(dA-dB, 2);
		}
		distance = Math.sqrt(distance);
		return distance;
	}

	@Override
	public void deleteEmptyCluster(List<Cluster> clusterList) {
		// TODO Auto-generated method stub
		Iterator<Cluster> clusterIterator = clusterList.iterator();
		while(clusterIterator.hasNext()){
			Cluster cluster = clusterIterator.next();
			if(null==cluster || null == cluster.getMean()||
			   null == cluster.getPoints()|| 
			   cluster.getPoints().size() == 0) {
				System.out.println("空簇");
				clusterIterator.remove();
			}
		}
	}

	public Long getTotalMovedCount() {
		return totalMovedCount;
	}

	public String getMessage() {
		return message.toString();
	}

	public void setTotalMovedCount(Long totalMovedCount) {
		this.totalMovedCount = totalMovedCount;
	}

	public StringBuilder appendMessage(String message) {
		this.message.append(message);
		return this.message;
	}
	public void clearMessage() {
		this.message = new StringBuilder("");
	}

	@Override
	public List<Cluster> executeKMeans(String dataPath, int k, double alpha,String initMethod) {
		// TODO Auto-generated method stub
		List<PointBox> pointList = inputData(dataPath);
		time = System.currentTimeMillis();
		List<Cluster> clusterList = null;
		if("k-means".trim().equals(initMethod)){
			clusterList = initClusters(alpha,k,pointList);//传统方法
		}else if("k-means++".equals(initMethod)){
			clusterList = initClustersPlusPlus(k, pointList);
		}
		if(null == clusterList) return null;
		distributeAllPointToClusters(pointList,clusterList);
		//System.out.println("迭代计算中……");
		iterationCalc(clusterList, null);//传统的迭代计算
		return clusterList;
	}

	@Override
	public List<Cluster> executeKMeansWithTopN(String dataPath, int k, double alpha,
			int largerK,int n,String initMethod) {
		// TODO Auto-generated method stub
	 	//执行top-n算法，簇集稳定后继续执行
		List<Cluster> clusterList = executeKMeans(dataPath,largerK,alpha,initMethod);
		//执行top-n算法
		mergeCount = clusterList.size() - k;
		List<Cluster> oldClusterList = new ArrayList<Cluster>();
		while(mergeCount > 0){
		//	System.out.print(mergeCount+"\t");
			clusterList = topN(clusterList, n);
			oldClusterList.clear();
			oldClusterList.addAll(clusterList);
		}
		clusterList = oldClusterList;
		int size = clusterList.size();
		while(size > k){
			size --;
		//	System.out.print(1+"\t");
			clusterList = topN(clusterList, 1);
		}

		return clusterList;
	}

	@Override
	public List<Cluster> executeKmeansEfficiency(String dataPath, int k,
			double alpha, String initMethod) {
		// TODO Auto-generated method stub
		List<PointBox> pointList = inputData(dataPath);
		time = System.currentTimeMillis();
		List<Cluster> clusterList = null;
		if("k-means".trim().equals(initMethod)){
			clusterList = initClusters(alpha,k,pointList);//传统方法
		}else if("k-means++".equals(initMethod)){
			clusterList = initClustersPlusPlus(k, pointList);
		}
		if(null == clusterList) return null;
		distributeAllPointToClusters(pointList,clusterList);

		//System.out.println("迭代计算中……");
		iterationCalcEfficiency(clusterList, null);

		return clusterList;
	}

	@Override
	public List<Cluster> executeKmeansEfficiencyWithTopN(String dataPath,
			int k, double alpha, int largerK, int n, String initMethod) {
		// TODO Auto-generated method stub
		if(largerK < k){
			System.out.println("K' too small.");
		}
		List<Cluster> clusterList = executeKmeansEfficiency(dataPath,largerK,alpha,initMethod);
		if(largerK > k){
			//执行top-n算法
			mergeCount = clusterList.size() - k;
			List<Cluster> oldClusterList = new ArrayList<Cluster>();
			StringBuilder m = new StringBuilder();
			while(mergeCount > 0){
				m.append(mergeCount+"\t");
				clusterList = topN(clusterList, n);
				if(null == clusterList)break;
				oldClusterList.clear();
				oldClusterList.addAll(clusterList);
			}
			clusterList = oldClusterList;
			int size = clusterList.size();
			while(size > k){
				size --;
				m.append(1+"\t");
				clusterList = topN(clusterList, 1);
			}
		}
//		deleteEmptyCluster(clusterList);
	//	time = (System.currentTimeMillis()-time);
		
		return clusterList;
	}
	
	public Double SSE(List<Cluster> clusters){
		Double sse = 0.0;
		for(Cluster cluster:clusters){
			sse+=cluster.getClusterJe();
		}
		return sse;
	}
	
	/*protected void recursionTopN(List<Cluster> clusterList,int interval){
		if(mergeCount < 0 )return;
		else{
			List<Cluster> oldClusterList = new ArrayList<Cluster>();
			oldClusterList.addAll(clusterList);
			int mergeCountTmp = mergeCount;
			while(mergeCount > 0){
				mergeCountTmp = mergeCount;
				oldClusterList.clear();
				oldClusterList.addAll(clusterList);
				clusterList = topN(clusterList,interval);
			}
			clusterList = oldClusterList;
			recursionTopN(clusterList,(int)Math.ceil(interval/2));
		}
	}*/
}
