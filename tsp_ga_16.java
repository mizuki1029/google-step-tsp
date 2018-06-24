/*
 * This program follows the steps below:
 * 
 * 1. read lines from csv file and store the coordinates(x, y)
 * 2. create routes((n-1)! versions)
 * 3. calculate each total distance
 * 4. sort route(quick sort)
 * 5. make random route
 * 6. make random multiple route
 * 7. choice parent route
 * 8. make next generations
 * */
import java.io.*;
import java.util.*;

public class tsp_ga_16 {
	final static int N = 16;
	final static int M = 500; //first choice the number of random routes
	final static double coordinates[][] = new double[N][2];//the coordinates(x, y)
	final static int parent = 50; //the number of parent routes
	static int bestRoute[] = new int[N];
	static double allDistances[] = new double[M];
	static double bestDistance;
	static int routes[][] = new int[M][N];
	static int parentRoutes[][] = new int[parent][N];
	static int routeNum = 0;
	final static int mutation = 10; //the number of mutation
	public static void main(String[] args) {
		readFile("input_2.csv");
		makeMultipleRoutes(routes);
		allRoutesDistance(routes);
        sortRoute(allDistances, 0, M-1);
        
        System.out.println("10000th Generations's best route");
        howGenerations(10000);
        
        for(int i = 0; i < routes[0].length; i++){
            System.out.print(routes[0][i] + " ");
        }
        System.out.println(" : " + allDistances[0]);
        for(int i = 0; i < routes[0].length; i++){
            bestRoute[i] = routes[0][i];
        }
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

	/*2. this method return the distance between the two points*/
	public static double calculateDistance(int start, int goal) {
		double distance = Math.pow(coordinates[goal][0] - coordinates[start][0], 2)
				+ Math.pow(coordinates[goal][1] - coordinates[start][1], 2);
		return Math.sqrt(distance);

	}
	
	/*3. calculate each total distance */
	public static double sumDistance(int route[]) {
		double sumDistance = 0;
		for (int i = 0; i < route.length-1; i++) {
			sumDistance += calculateDistance(route[i], route[i+1]);
		}
		sumDistance += calculateDistance(route[0], route[route.length-1]);
		return sumDistance;
	}
	
	/* calcurate all routes's daistance */
	public static void allRoutesDistance(int routes[][]){
		for(int i = 0; i < routes.length; i++){
			allDistances[i] = sumDistance(routes[i]);
		}
	}

	/* 4. sort routes */
	static void sortRoute(double distances[], int left, int right) {
        if (left >= right) {
            return;
        }
        double p = distances[(left + right) / 2];
		int pivot = 0;
        int l = left;
		int r = right;
		double tmp;
        while(l<=r) {
            while(distances[l] < p){
				l++;
			}
            while(distances[r] > p){
				r--;
			}
            if (l<=r) {
                tmp = distances[l]; 
				distances[l] = distances[r]; 
				distances[r] = tmp;
				for(int i = 0; i < routes[i].length; i++){
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
	

	/*5. make random route */
	public static void makeRoute(int route[]){
        /* make array according to turn */
		for(int i = 0; i < route.length; i++){
			route[i] = i;
		}
        for ( int i = 0; i < route.length; ++ i ) {
            /* shuffle array */
            int rnd = (int)(Math.random() * (double)route.length);
    	    int copy = route[i];
   		    route[i] = route[rnd];
   		    route[rnd] = copy;
    	}
	}

	/*6. make random multiple route */
    public static void makeMultipleRoutes(int routes[][]){
        for(int i = 0; i < routes.length; i++){
			/* do an i line at random */
            makeRoute(routes[i]);
        }
    }

	/*7. choice parent route */
	public static void parentRoutes(){
		for(int i = 0; i < parentRoutes.length; i++){
			for(int j = 0; j < parentRoutes[i].length; j++){
				parentRoutes[i][j] = routes[i][j];
			}
		}
	}

	/* make mutant route */
	public static void mutantRoute(){
		int pivot = 0;
		
		for(int i = 0; i < mutation; i++){
			for(int j = 0; j < routes[i].length; j++){
				routes[1 + i][j] = parentRoutes[i][j];
			}
		}
		
		for(int i = 1; i < mutation + 1; i++){
			int rnd1 = (int)(Math.random() * (double)routes[i].length);
			int rnd2 = (int)(Math.random() * (double)routes[i].length);
			pivot = routes[i][rnd1];
			routes[i][rnd1] = routes[i][rnd2];
			routes[i][rnd2] = pivot;
		}
	}

	/* combine parent routes */
	public static void combine(int route[]){
		
		int[][] parentRoutes2 = parentRoutes;
		int rnd1 = (int)(Math.random() * (double)parentRoutes2.length);
		int rnd2 = (int)(Math.random() * (double)parentRoutes2.length);
		int start = (int)(Math.random() * (double)parentRoutes2[0].length);
		int goal = (int)(Math.random() * (double)parentRoutes2[0].length);
		int pivot = 0;
		int flag = 0;
		int index1 = 0;
		int index2 = 0;
		
		if(start > goal){
			pivot = start;
			start = goal;
			goal = pivot;
		}
		
		int index3 = goal + 1;
		
		for(int i = 0; i < route.length; i++){
			route[i] = -1;
		}
		
		for(int i = start; i < goal + 1; i++){
			route[i] = parentRoutes2[rnd1][i];
		}

		
		while(index1 < start){
			
			flag = 0;
			
			for(int i = 0; i < route.length; i++){
				if(parentRoutes2[rnd2][index2] == route[i]){
					flag = 1;
				}
			}
			
			if(flag == 0){
				route[index1] = parentRoutes2[rnd2][index2];
				index1++;
			}
			index2++;

		}
		
		while(index3 < route.length){
			flag = 0;
			
			for(int i = 0; i < route.length; i++){
				if(parentRoutes[rnd2][index2] == route[i]){
					flag++;
				}
			}
			
			
			if(flag == 0){
				route[index3] = parentRoutes[rnd2][index2];
				index3++;
			}
			index2++;

		}
		
	}
	
	/* make multiple combining parent routes */
	public static void multipleCombine(){
		for(int i = mutation + 1; i < routes.length; i++){
			combine(routes[i]);
		}
	}

	/*8. make next generations */
	public static void nextGeneration(){
		parentRoutes();
		mutantRoute();
		multipleCombine();
		allRoutesDistance(routes);
		sortRoute(allDistances, 0, M-1);
    }
    
    /*9. designate number of generations */
    public static void howGenerations(int number){
        for(int i = 0; i < number; i++){
            nextGeneration();
        }
    }

    /*10. write best route to csv file*/
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