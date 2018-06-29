
/*
 * This program follows the steps below:
 * 
 * 1. read lines from csv file and store the coordinates(x, y)
 * 2. make random multiple route and sort by 2-opt
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
	final static int N = 16;
	final static int M = 5000; // first choice the number of random routes
	final static double coordinates[][] = new double[N][2];// the coordinates(x,y)
	final static int parent = 500; // the number of parent routes
	static int bestRoute[] = new int[N];
	static double allDistances[] = new double[M];
	// static double bestDistance;
	static int routes[][] = new int[M][N];
	static int parentRoutes[][] = new int[parent][N];
	static double rate[] = new double[parent];// good parent has high rate
	// static int routeNum = 0;
	// final static int mutation = 10; // the number of mutation突然変異

	public static void main(String[] args) {
		readFile("input_2.csv");
		makeMultipleRoutes(routes);// create random routes
		multipleTwoOpt(routes);//sort
		allRoutesDistance(routes);/* 3. calculate each total distance */
		sortRoute(allDistances, 0, M - 1);/* 4. sort route */

		System.out.println("1000th Generations's best route");
		howGenerations(1000);

		for (int i = 0; i < routes[0].length; i++) {
			System.out.print(routes[0][i] + " ");// elite
		}
		System.out.println("");
		System.out.println("best distance : " + allDistances[0]);
		for (int i = 0; i < routes[0].length; i++) {
			bestRoute[i] = routes[0][i];// elite
		}
		//TwoOpt(bestRoute);
		writeFile("output_2.csv", bestRoute);
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

	/* 4. sort random routes in ascending order */
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
				for (int i = 0; i < routes[0].length; i++) {
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
		int[] choosedParent = new int[parentRoutes.length];// next parents
		double[] rateOfAll = new double[allDistances.length];
		for (int i = 0; i < allDistances.length; i++) {
			rateOfAll[i] = (allDistances.length - i) * (10 / allDistances[i]);
			// System.out.print(rateOfAll[i]+" ");
		}
		// System.out.println("");
		choosedParent[0] = 0;// elite
		for (int i = 1; i < parentRoutes.length; i++) {
			choosedParent[i] = rouletteWheelSelection(rateOfAll);
			// System.out.print(choosedParent[i]+" ");
		}
		// System.out.println("");
		int index = 0;
		for (int i : choosedParent) {
			for (int j = 0; j < parentRoutes[0].length; j++) {
				parentRoutes[index][j] = routes[i][j];
			}
			rate[index] = (parentRoutes.length - index) * (100 / sumDistance(parentRoutes[index]));
			// System.out.print(rate[index]+" ");
			index++;
		}
		// System.out.println("");
	}
	public static int[][] multipleTwoOpt(int route[][]){
		for(int i=0; i<route.length; i++){
			route[i] = TwoOpt(route[i]);
		}
		return route;
	}
	/*2-opt*/
	public static int[] TwoOpt(int route[]){
		int [] newRoute = new int[route.length];
		double preDistance = sumDistance(route);
	    // repeat until no improvement is made 
	    int visited = 0;
	    while ( visited<1 ){
	        for ( int i = 1; i < N - 1; i++ )  {
	        	for ( int k = 1; k < N; k++) {
	        		//System.out.println(i+", "+k);
	        		newRoute=TwoOptSort(route, i, k );
	        		double newDistance = sumDistance(newRoute);
	        		if (newDistance < preDistance) {
	        			//System.out.println("sorted from "+i+" to "+k);
	        			route = newRoute;
	        		}else{
		        		//route=TwoOptSort(route, i, k );
	                }
	            }
	        }
	        visited++;
	    }
	    return route;
	}
	//reverse order sort
	public static int[] TwoOptSort(int[]route, int i, int j ) {
		for (int k = 0; k < (j-i)/2; k++) {
            int temp = route[j-k]; 
            route[j-k]=route[i+k];
            route[i+k]=temp;
        }
		return route;
	}
	/*
	 * 6. mutation by the rate of 5%(copy parent route into choosed routes &
	 * swap its order randomly)
	 */
	public static void mutantRoute() {
		int pivot = 0;
		Vector<Integer> choosedRoute = chooseFactors(parent, 0.005, 0);// choose
																		// factors
																		// from
																		// parent
																		// by
																		// the
																		// rate
																		// of
																		// 0.5%
																		// (skip
																		// routes[0](elite))
		/* copy parent route into choosed routes */
		/*
		 * for (int i : choosedRoute) { for (int j = 0; j < routes[i].length;
		 * j++) { routes[i][j] = parentRoutes[i][j]; } }
		 */
		/* swap order randomly to choosed routes */
		for (int i : choosedRoute) {
			int rnd1 = (int) (Math.random() * (double) routes[i].length);
			int rnd2 = (int) (Math.random() * (double) routes[i].length);
			/* swap */
			pivot = routes[i][rnd1];
			routes[i][rnd1] = routes[i][rnd2];
			routes[i][rnd2] = pivot;
		}
	}

	/* this method combine parent routes */
	public static int[] combine(int route[]) {
		int[][] parentRoutes2 = parentRoutes;
		int[][] proposedRoutes = new int[2][route.length];
		int rnd1 = (int) rouletteWheelSelection(rate);
		int rnd2 = (int) rouletteWheelSelection(rate);
		int start = (int) (Math.random() * (double) parentRoutes2[0].length);
		int goal = (int) (Math.random() * (double) parentRoutes2[0].length);
		int pivot = 0;
		if (start > goal) {// always start < goal
			pivot = start;
			start = goal;
			goal = pivot;
		}
		/*try crossover for 2 pattern*/
		for (int test = 0; test < 2; test++) {
			int flag = 0;
			int index1 = 0;
			int index2 = 0;
			int index3 = goal + 1;
			for (int i = 0; i < route.length; i++) {
				proposedRoutes[test][i] = -1;
			}
			// select one parent route randomly & change a part of route into
			// the parent route
			for (int i = start; i < goal + 1; i++) {
				proposedRoutes[test][i] = parentRoutes2[rnd1][i];
			}

			while (index1 < start) {// part before start (the changed part)
				flag = 0;
				for (int i = 0; i < route.length; i++) {
					if (parentRoutes2[rnd2][index2] == proposedRoutes[test][i]) {
						flag = 1;
					}
				}
				// if randomly selected city doesn't exist in route[], copy it
				// into route(the part before start)
				if (flag == 0) {
					proposedRoutes[test][index1] = parentRoutes2[rnd2][index2];
					index1++;
				}
				index2++;
			}

			while (index3 < route.length) {// part after goal (the changed part)
				flag = 0;
				for (int i = 0; i < route.length; i++) {
					if (parentRoutes[rnd2][index2] == proposedRoutes[test][i]) {
						flag = 1;
					}
				}
				// if randomly selected city doesn't exist in route[], copy it
				// into route(the part after goal)
				if (flag == 0) {
					proposedRoutes[test][index3] = parentRoutes[rnd2][index2];
					index3++;
				}
				index2++;
			}
		}
		/*select better route than the other*/
		if(sumDistance(proposedRoutes[0])<sumDistance(proposedRoutes[1])){
			return proposedRoutes[0];
		}else{
			return proposedRoutes[1];
		}
	}

	/* this method is roulette wheel selection */
	public static int rouletteWheelSelection(double[] rate) {
		Random rnd = new Random();
		double sum = 0;
		for (int i = 0; i < rate.length; i++) {
			sum += rate[i];
		}
		double temp = rnd.nextDouble() * sum;
		/* P(i)=(sum-rate[i])/sum */
		for (int i = 0; i < rate.length; i++) {
			temp -= rate[i];
			if (temp < 0) {
				return i;
			}
		}
		return -1;
	}

	/* this method choose factors in specific proportions */
	public static Vector<Integer> chooseFactors(int arrayLength, double rate, int ban) {
		int maximumValue = (int) (rate * arrayLength);
		int factorsNum = 0;
		Vector<Integer> choosedFactors = new Vector<Integer>(maximumValue);
		while (factorsNum < maximumValue) {
			int random = (int) (Math.random() * arrayLength);
			if (!choosedFactors.contains(random) && random != ban) {
				choosedFactors.add(random);
				factorsNum++;
			}
		}
		return choosedFactors;
	}

	/* 7. make multiple combined parent routes by the rate of 85% */
	public static void multipleCombine() {
		Vector<Integer> choosedRoutes = chooseFactors(parent, 0.85, 0);
		for (int i : choosedRoutes) {
			routes[i] = combine(routes[i]);
		}
	}

	/* 8. make next generations */
	public static void nextGeneration() {
		parentRoutes();
		//multipleTwoOpt(parentRoutes);
		multipleCombine();// crossing
		mutantRoute();// mutation
		allRoutesDistance(routes);
		sortRoute(allDistances, 0, M - 1);
	}

	/* 9. designate number of generations */
	public static void howGenerations(int number) {
		double distance = 0;// for check
		for (int i = 0; i < number; i++) {
			nextGeneration();
			// for check
			if (distance != allDistances[0]) {
				System.out.println("updated! " + allDistances[0] + " (" + i + ")");
			}
			distance = allDistances[0];
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
