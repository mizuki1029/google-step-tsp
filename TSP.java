import java.io.*;
import java.util.*;
//import org.uncommons.maths.combinatorics.PermutationGenerator;

public class TSP {
	final static double coordinates[][] = new double[5][2];
	static int bestRoute[] = new int[5];
	static int routes[][] = new int[24][5];
	static double bestDistance;
	public static void main(String[] args) {
		readFile("input_0.csv");
		createRoute(0, new int[4], new boolean[5]);
		bestDistance = sumDistance(routes[0]);//initial value
		 for(int i = 0; i<24; i++){ 
			 double distance = sumDistance(routes[i]);
			 if(bestDistance>distance){
				 bestDistance = distance;
				 bestRoute = routes[i].clone();//deep copy
			 }
		 }
		 System.out.println(bestDistance);
		 writeFile("output_0.csv", bestRoute);

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
	static int routeNum = 0;
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

	public static double calculateDistance(int start, int goal) {
		double distance = Math.pow(coordinates[goal][0] - coordinates[start][0], 2)
				+ Math.pow(coordinates[goal][1] - coordinates[start][1], 2);
		return Math.sqrt(distance);

	}

	public static double sumDistance(int route[]) {
		double sumDistance = 0;
		for (int i = 0; i < route.length-1; i++) {
			sumDistance += calculateDistance(route[i], route[i+1]);
		}
		sumDistance += calculateDistance(route[0], route[route.length-1]);
		return sumDistance;
	}
}