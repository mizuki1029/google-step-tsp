
/*
 * This program follows the steps below:
 * 
 * 1. read lines from csv file and store the coordinates(x, y)
 * 2. make random multiple route 
 * 3. calculate each total distance of all routes
 * 4. sort route in distance order(quick sort)
 * 5. copy Top 50(int parent) routes into parent routes
 * 6. mutation (copy parent route into routes[1]~[10] & swap its order randomly) *
 * 7. make multiple combined parent routes[11]~[N-1]
 * 8. sort routes in distance order
 * 9. repeat step5 ~ step8 10000 times 
 * 10. write best route to csv file and display the best distance
 * */
import java.io.*;
import java.util.*;

public class TSP_GA {
	final static int N = 128;
	final static int M = 5000; // first choice the number of random routes
	final static double coordinates[][] = new double[N][2];// the coordinates(x,y)
	final static int parent = 100; // the number of parent routes
	static int bestRoute[] = new int[N];
	static double allDistances[] = new double[M];
	//static double bestDistance;
	static int routes[][] = new int[M][N];
	static int parentRoutes[][] = new int[parent][N];
	static double rate[] = new double[parent];//good parent has high rate
	//static int routeNum = 0;
	//final static int mutation = 10; // the number of mutation突然変異

	public static void main(String[] args) {
		readFile("input_4.csv");
		makeMultipleRoutes(routes);//create 500 random routes
		allRoutesDistance(routes);/* 3. calculate each total distance*/
		sortRoute(allDistances, 0, M - 1);/*4. sort route*/

		System.out.println("10000th Generations's best route");
		howGenerations(10000);

		for (int i = 0; i < routes[0].length; i++) {
			System.out.print(routes[0][i] + " ");//elite
		}
		System.out.println("");
		System.out.println("best distance : " + allDistances[0]);
		for (int i = 0; i < routes[0].length; i++) {
			bestRoute[i] = routes[0][i];//elite
		}
		writeFile("output_4.csv", bestRoute);
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
	/* this method make random route */
	public static void makeRoute(int route[]) {
		/* make array according to turn */
		for (int i = 0; i < route.length; i++) {
			route[i] = i;
		}
		for (int i = 0; i < route.length; ++i) {
			/* shuffle array */
			int rnd = (int) (Math.random() * (double) route.length);
			int copy = route[i];
			route[i] = route[rnd];
			route[rnd] = copy;
		}
	}

	/* 2. make random multiple route */
	public static void makeMultipleRoutes(int routes[][]) {
		for (int i = 0; i < routes.length; i++) {
			/* do an i line at random */
			makeRoute(routes[i]);
		}
	}

	
	/* this method return the distance between the two points */
	public static double calculateDistance(int start, int goal) {
		double distance = Math.pow(coordinates[goal][0] - coordinates[start][0], 2)
				+ Math.pow(coordinates[goal][1] - coordinates[start][1], 2);
		return Math.sqrt(distance);

	}

	/* this method calculate each total distance */
	public static double sumDistance(int route[]) {
		double sumDistance = 0;
		for (int i = 0; i < route.length - 1; i++) {
			sumDistance += calculateDistance(route[i], route[i + 1]);
		}
		sumDistance += calculateDistance(route[0], route[route.length - 1]);
		return sumDistance;
	}

	/* 3. calculate each total distance of all routes */
	public static void allRoutesDistance(int routes[][]) {
		for (int i = 0; i < routes.length; i++) {
			allDistances[i] = sumDistance(routes[i]);
		}
	}

	/* 4. sort random routes in ascending order*/
	static void sortRoute(double distances[], int left, int right) {
		if (left >= right) {
			return;
		}
		double p = distances[(left + right) / 2];
		int pivot = 0;
		int l = left;
		int r = right;
		double tmp;
		while (l <= r) {
			while (distances[l] < p) {
				l++;
			}
			while (distances[r] > p) {
				r--;
			}
			if (l <= r) {
				tmp = distances[l];
				distances[l] = distances[r];
				distances[r] = tmp;
				for (int i = 0; i < routes[i].length; i++) {
					pivot = routes[l][i];
					routes[l][i] = routes[r][i];
					routes[r][i] = pivot;
				}
				l++;
				r--;
			}
		}
		sortRoute(distances, left, r);
		sortRoute(distances, l, right);
	}

	/* 5. choice parent route */
	public static void parentRoutes() {
		for (int i = 0; i < parentRoutes.length; i++) {
			for (int j = 0; j < parentRoutes[i].length; j++) {
				parentRoutes[i][j] = routes[i][j];
				rate[i] = 10000/sumDistance(parentRoutes[i]);
			}
		}
	}

	/* 6. mutation by the rate of 5%(copy parent route into choosed routes & swap its order randomly) */
	public static void mutantRoute() {
		int pivot = 0;
		Vector<Integer>choosedRoute = chooseFactors(parent, 0.05);
		/* copy parent route into choosed routes */
		for (int i : choosedRoute) {
			for (int j = 0; j < routes[i].length; j++) {
				if(i != 0){
					routes[i][j] = parentRoutes[i][j];
				}
			}
		}
		/* swap order randomly to routes[1]~[10] */
		for (int i : choosedRoute) {
			int rnd1 = (int) (Math.random() * (double) routes[i].length);
			int rnd2 = (int) (Math.random() * (double) routes[i].length);
			/*swap*/
			pivot = routes[i][rnd1];
			routes[i][rnd1] = routes[i][rnd2];
			routes[i][rnd2] = pivot;
		}
	}

	/* this method combine parent routes */
	public static int[] combine(int route[]) {
		int[][] parentRoutes2 = parentRoutes;
		int rnd1 = (int) rouletteWheelSelection(rate);
		int rnd2 = (int) rouletteWheelSelection(rate);
		int start = (int) (Math.random() * (double) parentRoutes2[0].length);
		int goal = (int) (Math.random() * (double) parentRoutes2[0].length);
		int pivot = 0;
		int flag = 0;
		int index1 = 0;
		int index2 = 0;
		//System.out.println(rnd1+" "+rnd2);
		if (start > goal) {//always start < goal
			pivot = start;
			start = goal;
			goal = pivot;
		}
		int index3 = goal + 1;
		for (int i = 0; i < route.length; i++) {
			route[i] = -1;
		}
		//select one parent route randomly & change a part of route into the parent route
		for (int i = start; i < goal + 1; i++) {
			route[i] = parentRoutes2[rnd1][i];
		}

		while (index1 < start) {//part before start (the changed part)
			flag = 0;
			for (int i = 0; i < route.length; i++) {
				if (parentRoutes2[rnd2][index2] == route[i]) {
					flag = 1;
				}
			}
			//if randomly selected city doesn't exist in route[], copy it into route(the part before start)
			if (flag == 0) {
				route[index1] = parentRoutes2[rnd2][index2];
				index1++;
			}
			index2++;
		}

		while (index3 < route.length) {//part after goal (the changed part)
			flag = 0;
			for (int i = 0; i < route.length; i++) {
				if (parentRoutes[rnd2][index2] == route[i]) {
					flag = 1;
				}
			}
			//if randomly selected city doesn't exist in route[], copy it into route(the part after goal)
			if (flag == 0) {
				route[index3] = parentRoutes[rnd2][index2];
				index3++;
			}
			index2++;
		}
		return route;
	}

	/*this method is roulette wheel selection*/
	public static int rouletteWheelSelection(double[] rate){
		Random rnd = new Random();
		double sum = 0;
		for(int i = 0; i < rate.length; i++ ){
			sum += rate[i];
		}
		double temp = rnd.nextDouble() * sum ;
		/* P(i)=(sum-rate[i])/sum */
		for(int i = 0;i < rate.length ;i++ ){
			temp -= rate[i];
			if(temp < 0){
				return i;	
			}
		}
		return -1;
	}
	
	/*this method choose factors in specific proportions*/
	public static Vector<Integer> chooseFactors(int arrayLength, double rate){
		int maximumValue = (int)(rate*arrayLength);
		int factorsNum = 0;
		Vector <Integer>choosedFactors = new Vector<Integer>(maximumValue);
		while(factorsNum<maximumValue){
			int random = (int) (Math.random() * arrayLength);
			if(!choosedFactors.contains(random)){
				choosedFactors.add(random);
				factorsNum++;
			}
		}
		return choosedFactors;
	}
	
	/* 7. make multiple combined parent routes by the rate of 80%*/
	public static void multipleCombine() {
		Vector<Integer>cannotChoose = chooseFactors(parent, 0.2);
		for (int i = 1; i < routes.length; i++) {
			if(!cannotChoose.contains(i)){
				routes[i]=combine(routes[i]);
			}
		}
	}

	/* 8. make next generations */
	public static void nextGeneration() {
		parentRoutes();
		multipleCombine();//crossing
		mutantRoute();//mutation
		allRoutesDistance(routes);
		sortRoute(allDistances, 0, M - 1);
	}

	/* 9. designate number of generations */
	public static void howGenerations(int number) {
		double distance = 0;//for check
		for (int i = 0; i < number; i++) {
			nextGeneration();
			//for check
			if(distance != allDistances[0]){
				System.out.println("updated! "+allDistances[0]+" ("+i+")");
			}
			distance =allDistances[0];
		}
	}

	/* 10. write best route to csv file */
	public static void writeFile(String fileName, int bestRoute[]) {
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
			for (int index = 0; index < bestRoute.length; index++) {
				printWriter.println(bestRoute[index]);
			}
			fileWriter.close();
			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
