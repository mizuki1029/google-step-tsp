/*
 * This program follows the steps below:
 * 
 * 1. read lines from csv file and store the coordinates(x, y)
 * 2. create routes((n-1)! versions)
 * 3. calculate each total distance
 * 4. find best route
 * 5. display the best distance (shortest)
 * 6. write best route to csv file
 * */
import java.io.*;
import java.util.*;

public class TSP_8 {
	final static int N = 8;
	final static int M = 5040;
	final static double coordinates[][] = new double[N][2];//the coordinates(x, y)
	static int bestRoute[] = new int[N];
	static double bestDistance;
	static int routes[][] = new int[M][N];//(n-1)! versions
	static int routeNum = 0;
	public static void main(String[] args) {
		readFile("input_1.csv");
		createRoute(0, new int[N-1], new boolean[N]);
		bestDistance = sumDistance(routes[0]);//initial value
		checkBestRoute();
		System.out.println(bestDistance);
		writeFile("output_1.csv", bestRoute);
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

	/*2. create routes((n-1)! versions)*/
	public static void createRoute(int pointer, int perm[], boolean flag[]) {
		if (pointer == perm.length) {
			routes[routeNum][0] = 0;
			int j = 1;
			for (int x:perm) {
				routes[routeNum][j]=x;
				j++;
			}
			routeNum++;
		} else {
			for (int i = 1; i <= perm.length; i++) {
				if (flag[i])
					continue;
				perm[pointer] = i;
				flag[i] = true;
				createRoute(pointer + 1, perm, flag);
				flag[i] = false;
			}
		}
	}
	
	/*this method return the distance between the two points*/
	public static double calculateDistance(int start, int goal) {
		double distance = Math.pow(coordinates[goal][0] - coordinates[start][0], 2)
				+ Math.pow(coordinates[goal][1] - coordinates[start][1], 2);
		return Math.sqrt(distance);

	}
	
	/*3. calculate each total distance*/
	public static double sumDistance(int route[]) {
		double sumDistance = 0;
		for (int i = 0; i < route.length-1; i++) {
			sumDistance += calculateDistance(route[i], route[i+1]);
		}
		sumDistance += calculateDistance(route[0], route[route.length-1]);
		return sumDistance;
	}
	
	/* 4. find best route*/
	public static void checkBestRoute(){
		 for(int i = 0; i<M; i++){ 
			 double distance = sumDistance(routes[i]);
			 if(bestDistance>distance){//better than bestDistance
				 bestDistance = distance;//update value
				 bestRoute = routes[i].clone();//deep copy
			 }
		 }
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
