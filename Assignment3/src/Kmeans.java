import java.util.*;
import java.io.*;

public class Kmeans {
	private int k = 10;	
	private int repeat;  //��������
	private ArrayList<ArrayList<Double>> dataSet;
	private ArrayList<ArrayList<Double>> center;
	private ArrayList<ArrayList<ArrayList<Double>>> cluster;
	private ArrayList<Double> jc;           // ���ƽ���ͣ�kԽ�ӽ�dataSetLength�����ԽС  
	private int dataSetLength = 10000;
	private Random rd;
	
	public static void main(String[] args) throws IOException{
		
//		List<ArrayList<Double>> centers = new ArrayList<ArrayList<Double>>();  
//      List<ArrayList<Double>> newCenters = new ArrayList<ArrayList<Double>>();  
//      List<ArrayList<ArrayList<Double>>> helpCenterList = new ArrayList<ArrayList<ArrayList<Double>>>();        
		Kmeans k = new Kmeans();
		k.execute();
		
		ArrayList<ArrayList<ArrayList<Double>>> clusters = k.getCluster();  
        //�鿴���  
        for(int i = 0;i < clusters.size(); i++)  
        {  
            k.printDataArray(clusters.get(i), "cluster["+i+"]");  
        }  
	}
	
	private ArrayList<ArrayList<ArrayList<Double>>> getCluster() {
		// TODO Auto-generated method stub
		return cluster;
	}

	private void init() throws IOException {  
        repeat = 0;  
        rd = new Random();  
        if (dataSet == null || dataSet.size() == 0) {  
            dataSet = getDataSet("src/mnist.txt");  
        }  
        //dataSetLength = dataSet.size();  
        if (k > dataSetLength) {  
            k = dataSetLength;  
        }  
        center = initCenters();  
        cluster = initCluster();  
        jc = new ArrayList<Double>();  
    }
	//��ȡ�ļ����ض�ά����
	public ArrayList<ArrayList<Double>> getDataSet(String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
		ArrayList<ArrayList<Double>> dataSet = new ArrayList<ArrayList<Double>>();
		String ln = null;
		while((ln = br.readLine()) != null){
			String []data = ln.split(",");
			List<Double> tmpList = new ArrayList<Double>();
			for(int i = 0;i < data.length;i++){
				tmpList.add(Double.parseDouble(data[i])); //tmpList����ÿ�е�����
			}
			//System.out.println(tmpList);
			dataSet.add((ArrayList<Double>) tmpList);
		}
		br.close();
		//System.out.println(dataSet);
		return dataSet;     //dataSet��һ����ά���� �������е�����
	}
	
	
	//����dataSet ʹ���������ʼ������ ͬ���ö�ά���鱣��
	public ArrayList<ArrayList<Double>> initCenters(){
		ArrayList<ArrayList<Double>> centers = new ArrayList<ArrayList<Double>>();
		int [] randoms = new int[k];
		boolean flag;
		int temp = rd.nextInt(dataSetLength);
		randoms[0] = temp;
		for(int i = 1;i < k;i++){
			flag = true;
			while(flag){
				temp = rd.nextInt(dataSetLength);
				int j = 0;
				while(j < i){
					if(temp == randoms[j]){
						break;
					}
					j++;
				}
				if(j == i){
					flag = false;
				}
			}
			randoms[i] = temp;
		}
		for(int i = 0;i < k;i++){
			centers.add(dataSet.get(randoms[i]));
		}
		return centers;
	}
	
	//��ʼ��k��Ϊ�յ����ݼ��� �����ö�ά���鱣�� 
	public ArrayList<ArrayList<ArrayList<Double>>> initCluster(){
		ArrayList<ArrayList<ArrayList<Double>>> cluster = new ArrayList<ArrayList<ArrayList<Double>>>();
		for(int i = 0;i < k;i++){
			cluster.add(new ArrayList<ArrayList<Double>>());
			//cluster.add(new ArrayList<Double>());
		}
		return cluster;
	}
	
	//����ŷʽ���룻 
	//ע�⣡���˴�����Ĳ���Ϊһά������ Ҳ����centers.get(index)��centers�����е�ĳһ����dataSet.get(index)�ľ���
	public double distance(ArrayList<Double> element, ArrayList<Double> center){
		double distance = 0;
		double x = 0;
		for(int i = 0;i < center.size() - 1;i++){
			x += (center.get(i) - element.get(i)) * (center.get(i) - element.get(i));
		}
		distance = Math.sqrt(x);
		return distance;		
	}
	
	//return ��С�����ھ��������е�λ��
	public int minDistance(double[] distance){
		double minDistance = distance[0];
		int minLocation = 0;
		for(int i = 0;i < distance.length;i++){
			if(distance[i] < minDistance){
				minDistance = distance[i];
				minLocation = i;
			}else if(distance[i] == minDistance) //���������������һ��λ�á� Ϊʲô��������
			{
				if(rd.nextInt(10) < 5){ //��һ���ǣ�
					minLocation = i;
				}
			}
		}
		return minLocation;
	}
	
	//���Ĳ��֣�������ǰԪ�طŵ���С����������صĴ�
	public void clusterSet(){
		double[] distance = new double[k]; //k��Ԥ�ֵĴ���
		for(int i = 0;i < dataSetLength;i++){
			for(int j = 0;j < k;j++){
				distance[j] = distance(dataSet.get(i), center.get(j));
			}
			int minLocation = minDistance(distance);
			
			cluster.get(minLocation).add(dataSet.get(i));   //���ģ�����ǰԪ�طŵ���С����������صĴ���
		}
	}
	
	
	//�������ƽ����
	//ע�⣡���˴�����Ĳ���Ϊһά������ Ҳ����centers.get(index)��centers�����е�ĳһ����dataSet.get(index)�ľ���
	public double errorSquare(ArrayList<Double> element, ArrayList<Double> center){
		double errSquare = 0;
		for(int i = 0;i < center.size() - 1;i++){
			errSquare += (center.get(i) - element.get(i)) * (center.get(i) - element.get(i));
		}
		return errSquare;		
	}
	
	//�������ƽ����׼�����ķ�����
	public void countRule(){
		double jcF = 0;
		for(int i = 0;i < cluster.size(); i++){
			for(int j = 0;j < cluster.get(i).size();j++){
				jcF += errorSquare(cluster.get(i).get(j), center.get(i));
			}			
		}
		jc.add(jcF);
	}
	
	//�����µĴ�
	public void setNewCenter(){
		for(int i = 0; i < k ; i++){
			//int n = cluster.get(i).size();
			ArrayList<Double> newCenter = new ArrayList<Double>();
			for(int j = 0; j < center.get(0).size() ;j++){
				double sum = 0;
				for(int t = 0; t < cluster.get(i).size(); t++ ){
					sum += cluster.get(i).get(t).get(j);
				}
				newCenter.add(sum / cluster.get(i).size());
			}
		center.set(i, newCenter);
		}
	}
	

	
	public void kmeans() throws IOException{
		init();
		while(true){			
			clusterSet();
			countRule();
			
			if (repeat != 0) {  
                if (jc.get(repeat) - jc.get(repeat - 1) == 0) {  
                    break;  
                }  
            }  
  
            setNewCenter();  
            //printDataArray(center,"newCenter");  
            repeat++;  
            cluster.clear();  
            cluster = initCluster();
			
		}
	}
	
	/** 
     * ִ���㷨 
	 * @throws IOException 
     */  
    public void execute() throws IOException {  
        long startTime = System.currentTimeMillis();  
        System.out.println("kmeans begins");  
        kmeans();  
        long endTime = System.currentTimeMillis();  
        System.out.println("kmeans running time=" + (endTime - startTime)  
                + "ms");  
        System.out.println("kmeans ends");  
        System.out.println();  
    } 
    
	//��ӡ ����
	public void printDataArray(ArrayList<ArrayList<Double>> dataArray, String dataArrayName) {  
        for (int i = 0; i < dataArray.size(); i++) {  
            System.out.println("print:" + dataArrayName + "[" + i + "]={"  
                    + dataArray.get(i) + "}");  
        }  
        System.out.println("������������������������������������������������������������������������");  
    } 
	
    
/*    public void getMatrix(ArrayList<ArrayList<ArrayList<Double>>> cluster){
    	
    	int [][] matrix;
    	int count0 = 0;
    	int count1 = 0;
    	for(int i = 0; i < cluster.size(); i++){
    		for(int j = 0; j < cluster.get(i).size();j++){
    			System.out.println(cluster.get(i).get(j).get(25));
    			if(cluster.get(i).get(j).get(24) == (double)-1){
    				count0++;
    			}
    			else{
    				count1++;
    			}
    		}
    	}
    	
    	System.out.println(count0 + "   " + count1);
		//return null;    	
    }*/

}
