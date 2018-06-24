/*
 * This program follows the steps below:
 * 
 * 1. read lines from csv file and store the coordinates(x, y)
 * 2. create initial route
 * 3. sort by 3-opt algorithm
 * 4. display the best distance (shortest)
 * 5. write best route to csv file
 * */
import java.io.*;
import java.util.*;

public class TSP_3opt {
	final static int N  = 64;
	final static double coordinates[][] = new double[N][2];//the coordinates(x, y)
	static int bestRoute[] = new int[N];
	static double bestDistance;
	static int route[] = new int[N];//2. create initial route
	static int newRoute[] = new int[N];
	static int routeNum = 0;
	public static void main(String[] args) {
		readFile("input_3.csv");
		/*2. create initial route*/
		for(int index = 0; index<N; index++){
			route[index]=index;
		}
		bestDistance = sumDistance(route);//initial value
		ThreeOpt();//3. sort by 3-opt algorithm
		System.out.println(bestDistance);
		writeFile("output_3.csv", route);
	}
	
	/* 1. read lines from csv file and store the coordinates(x, y) */
	public static void readFile(String fileName) {
		try {
			File file = new File(fileName);
			/* exception when the file not exit */
			if (!file.exists()) {
				System.out.print("File Not Exit");
				return;
			}
			/* read lines from csv file */
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String str;
			str = bufferedReader.readLine();// skip the first line
			int index = 0;
			while ((str = bufferedReader.readLine()) != null) {
				StringTokenizer cut = new StringTokenizer(str, ",");
				coordinates[index][0] = Double.parseDouble(cut.nextToken());
				coordinates[index][1] = Double.parseDouble(cut.nextToken());
				index++;
			}
			fileReader.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*3-opt*/
	public static void ThreeOpt(){
	    // repeat until no improvement is made 
	    int visited = 0;
	    while ( visited<N ){
	        for ( int i = 1; i < N - 2; i++ )  {
	        	for ( int j = 1; j < N - 1; j++ )  {
	        		for ( int k = 1; k < N; k++) {
	        			//System.out.println(i+", "+k);
	        			newRoute=ThreeOptSort(route, i, k );
	        			newRoute=ThreeOptSort(route, j, k );
	        			double newDistance = sumDistance(newRoute);
	        			if (newDistance < bestDistance) {
					    //System.out.println("sorted from "+i+" to "+k);
	        				route = newRoute;
	        				bestDistance = newDistance;
	        				System.out.println("updated! "+bestDistance);
	        				/*
	        				for(int x : route)
	        					System.out.print(x+"->");
	        				System.out.println("");
						*/
	        			}else{
	        				newRoute=ThreeOptSort(route, j, k );
		        			newRoute=ThreeOptSort(route, i, k );
	        			}
				}
			}
	        }
	        visited++;
	    }
	}
	//reverse order sort
	public static int[] ThreeOptSort(int[]route, int i, int j ) {
		for (int k = 0; k < (j-i)/2; k++) {
            int temp = route[j-k]; 
            route[j-k]=route[i+k];
            route[i+k]=temp;
        }
		return route;
	}
	/*this method return the distance between the two points*/
	public static double calculateDistance(int start, int goal) {
		double distance = Math.pow(coordinates[goal][0] - coordinates[start][0], 2)
				+ Math.pow(coordinates[goal][1] - coordinates[start][1], 2);
		return Math.sqrt(distance);

	}
	
	/*calculate total distance*/
	public static double sumDistance(int route[]) {
		double sumDistance = 0;
		for (int i = 0; i < route.length-1; i++) {
			sumDistance += calculateDistance(route[i], route[i+1]);
		}
		sumDistance += calculateDistance(route[0], route[route.length-1]);
		return sumDistance;
	}
	
	/*5. write best route to csv file*/
	public static void writeFile(String fileName, int bestRoute[]) {
		/* 1. read lines from csv file and store the coordinates(x, y) */
		try {
			File file = new File(fileName);
			/* exception when the file not exit */
			if (!file.exists()) {
				System.out.print("File Not Exit");
				return;
			}
			/* read lines from csv file */
			FileWriter fileWriter = new FileWriter(file);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println("index");
			for(int index = 0; index<bestRoute.length; index++){
				printWriter.println(bestRoute[index]);
			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
