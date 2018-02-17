import java.util.*;
import java.io.*;
class knn{
	public static ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();
	public static ArrayList<ArrayList<Double>> clusterData = new ArrayList<ArrayList<Double>>();
	public static ArrayList<Double> innerData;
	public static ArrayList<ArrayList<Double>> means = new ArrayList<ArrayList<Double>>();
	public static ArrayList<Double> innerClusterData = new ArrayList<Double>();
	public static int counter;
	public static void readData(String fileName) throws IOException{
    	BufferedReader dataBR = new BufferedReader(new FileReader(new File(fileName)));
    	String line = "";
    	line = dataBR.readLine();
	    while ((line = dataBR.readLine()) != null) {
	    	innerData = new ArrayList<Double>(); 
	        StringTokenizer st = new StringTokenizer(line,"\t");
    		while(st.hasMoreTokens()){
    			innerData.add(Double.parseDouble(st.nextToken()));
	   		}
	   		data.add(innerData);
		}
	    // System.out.println(data);
	}

	public static void clusters(int k, String outpath) throws IOException{
		counter = 1;
		for (int i=0; i<k ; i++ ) {
			Double random =(Double) (Math.random()*data.size());
			random = Math.floor(random);
			// Double random = (double)i;
			if (innerClusterData.indexOf(random) == -1 || i==0) {
				innerClusterData.add(random);
			}
			else {
				i--;
			}
		}
		addData(k);
		calculateData();
		for (int i=1;i<25;i++) {
			iterations(k);
		}
		printData(k, outpath);
		sse();
	}

	public static void sse(){
		double dist = 0;
		for (int i=0;i<clusterData.size();i++) {
			for (int j=0;j<clusterData.get(i).size();j++) {
				double a = (double) clusterData.get(i).get(j);
				int pos = (int) a;
				double temp = calculateDistanceIter(pos, i);
				dist += Math.pow(temp,2);
			}
		}
		System.out.println("SSE: "+dist);
	}

	public static void addData(int k){
		for (int i=0;i<k;i++) {
			ArrayList<Double> temp = new ArrayList<Double>();
			temp.add(innerClusterData.get(i));
			clusterData.add(temp);
		}
	}

	public static void calculateData(){
		means = new ArrayList<ArrayList<Double>>();
		for (int i=0;i<data.size();i++) {
			int type = minDistance(i);
			if (!clusterData.get(type).contains((double)i)) {
				clusterData.get(type).add((double)i);	
			}
		}
		for (int i=0;i<clusterData.size() ;i++ ) {
			calculateMean(clusterData.get(i));
		}
	}

	public static int minDistance(int pt){
		ArrayList<Double> distances = new ArrayList<Double>();
		for (int i=0;i<innerClusterData.size();i++) {
			double dist = calculateDistance(pt, innerClusterData.get(i));
			distances.add(dist);
		}
		int pos = distances.indexOf(Collections.min(distances));
		return pos;
	}

	public static void calculateMean(ArrayList<Double> a){
		ArrayList<Double> temp = new ArrayList<Double>();
		double meanX=0, meanY=0;
		double tempX=0, tempY=0;
		for (int i = 0; i < a.size() ; i++ ) {
			double pos = a.get(i);
			tempX += data.get((int)pos).get(1);
			tempY += data.get((int)pos).get(2);
		}
		meanX = tempX / a.size();
		meanY = tempY / a.size();
		temp.add(meanX);
		temp.add(meanY);
		means.add(temp);
	}

	public static double calculateDistance(int pt1, double pt2){
		double x1 = data.get(pt1).get(1);
		double y1 = data.get(pt1).get(2);
		double x2 = data.get((int)pt2).get(1);
		double y2 = data.get((int)pt2).get(2);
		double dist = Math.sqrt(Math.pow((x1-x2),2)+Math.pow((y1-y2),2));
		return dist;
	}

	public static void iterations(int k){
		clusterData.clear();
		for (int i=0;i<k;i++) {
			ArrayList<Double> temp = new ArrayList<Double>();
			temp.add(0.0);
			clusterData.add(temp);
		}
		ArrayList<Double> distancesIter = new ArrayList<Double>();
		for (int i=0;i<data.size();i++) {
			int pos;
			distancesIter.clear();
			for (int j=0;j<k;j++) {
				double distIter = calculateDistanceIter(i, j);	
				distancesIter.add(distIter);
			}
			pos = distancesIter.indexOf(Collections.min(distancesIter));
			clusterData.get(pos).add((double)i);
		}
		for (int i=0;i<k;i++) {
			clusterData.get(i).remove(0);
		}
		means.clear();
		for (int i=0;i<clusterData.size() ;i++ ) {
			calculateMean(clusterData.get(i));
		}
	}

	public static double calculateDistanceIter(int pt1, int pt2){
		double x1 = data.get(pt1).get(1);
		double y1 = data.get(pt1).get(2);
		double x2 = means.get(pt2).get(0);
		double y2 = means.get(pt2).get(1);
		double dist = Math.sqrt(Math.pow((x1-x2),2)+Math.pow((y1-y2),2));
		return dist;
	}

	public static void printData(int k, String outpath) throws IOException{
		// System.out.print(outpath);
		PrintStream o = new PrintStream(new File(outpath)); 
        PrintStream console = System.out;
        System.setOut(o);
		for (int i=0;i<k;i++) {
			System.out.print((i+1)+" ");
			for (int j=0;j<clusterData.get(i).size();j++) {
				double temp = (double) clusterData.get(i).get(j);
				int a = (int) temp;
				System.out.print(a+",");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) throws IOException{
		readData(args[1]);
		// readData("eg.txt");
		int k = Integer.parseInt(args[0]);
		clusters(k, args[2]);
	}
}