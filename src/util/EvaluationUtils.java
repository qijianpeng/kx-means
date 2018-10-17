package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class EvaluationUtils {

	//NMI,SSE,轮廓系数,time
	
	
	
/**
 * 保存评估结果message到filePath
 * @param filePath
 * @param message
 * @throws IOException 
 */
	public static void saveEvaluation(String filePath,String message) throws IOException{
		File file = new File(filePath);
		FileWriter fw = new FileWriter(file, true);
		fw.write(message.toCharArray());
		fw.flush();
		fw.close();
	}
	public static void clear(String filePath) throws IOException{
		File file = new File(filePath);
		FileWriter fw = new FileWriter(file);
		fw.write("");
		fw.flush();
		fw.close();
	}
}
