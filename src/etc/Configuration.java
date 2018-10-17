package etc;

public class Configuration {
	/*
	 * D:/CNAE-9/CNAE-9.data
	 * F:/Study/Major/k-means/dataset/D1.txt
	 * 
	 */
	public static int total = 0;
	public static int k = 20;
	public static int t_max = 500;
	public static double limit = 10e-6;
	public static String path ="data/data20groups_500d.txt";
	public static final int dmStart =0;//1st postion of 1st attribute
	public static final int dm =499;//dimensions
	public static final int classPos =499;//label postion
	public static String separated = ","; 
	public static String fileName = path.substring(path.lastIndexOf("/")+1);
}
