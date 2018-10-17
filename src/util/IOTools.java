package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class IOTools {
	public static List<List<Integer>> loadData(String path){
		List<List<Integer>> clusters = new ArrayList<List<Integer>>();
		//1.读取文件内容
		try{
			String encoding = "utf-8";
			File file = new File(path);
			if(file.isFile() && file.exists()){
				FileInputStream fis = new FileInputStream(file);
				byte[] content = new byte[fis.available()];  
				fis.read(content);
				String text = new String(content);
				String[] clustersText = text.split("\\], \\[");
				clustersText[0] = clustersText[0].replaceFirst("\\[\\[", "");
				clustersText[clustersText.length - 1] = clustersText[clustersText.length - 1].replaceFirst("\\]\\]", "");
				for(String clusterText : clustersText){
					clusters.add(new ArrayList<Integer>());
					String[] cls = clusterText.split(", ");
					for(String e:cls){
						clusters.get(clusters.size()-1).add(Integer.valueOf(e));
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{			
		}
		return clusters;
		
	}
	public static void write(String text,String path) throws IOException{
		File f  = new File(path);
        FileWriter fw =null; //声明流对象 
        if(!f.exists())f.createNewFile();
        fw = new FileWriter(f,true);
        fw.write(text);
        fw.flush();
        fw.close();
        
	}
	public static List<Double> read(String path) throws IOException{
		List<Double> values = new ArrayList<Double>();
		//1.读取文本文件
		String encoding = "utf-8";
		File file = new File(path);
		if(file.isFile() && file.exists()){
			InputStreamReader read = 
					new InputStreamReader(new FileInputStream(file),encoding);
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineText = null;
			while((lineText = bufferedReader.readLine()) != null){
				Double value = Double.valueOf(lineText);
				values.add(value);
			}
		}
		return values;
	}
}
