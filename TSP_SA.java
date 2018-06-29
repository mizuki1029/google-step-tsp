import java.io.*;
import java.util.*;
import java.lang.Math;

public class TSP_SA {
  final static int N = 2048;
  final static double points[][] = new double[N][2];
  static int bestRoute[] = new int[N];
  public static void main(String[] args) {
    for(int i = 0; i < N; i ++) {
      bestRoute[i] = i;
    }
    readFile("input_6.csv");
    System.out.println("first distance: " + totalDistance(points));
    sa(points);
    System.out.println("final distance: " + totalDistance(points));
    writeFile("output_6_SA.csv", bestRoute);
  }

  /* 1. read lines from the csv file and store in points[x][y]*/
  public static void readFile(String fileName) {
    try {
      File file = new File(fileName);
      // exception when the file does not exist
      if(!file.exists()) {
        System.out.println("File Does Not Exist");
        return;
      }
      // read lines from csv file
      FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String str;
      str = bufferedReader.readLine(); // skip the first line
      int index = 0;
      while((str = bufferedReader.readLine()) != null) {
        String[] strSplit = str.split(",");
        points[index][0] = Double.parseDouble(strSplit[0]);
        points[index][1] = Double.parseDouble(strSplit[1]);
        index ++;
      }
      fileReader.close();
      bufferedReader.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  /* 2. calculate the total distance */
  public static double totalDistance(double points[][]) {
    double total = 0;
    for(int i = 1; i < N; i ++) {
      total += distance(points[i], points[i-1]);
    }
    total += distance(points[0], points[N-1]);
    return total;
  }
  // calculate the distance between currentPoint and previousPoint
  public static double distance(double currentPoint[], double previousPoint[]) {
    double dx = currentPoint[0] - previousPoint[0];
    double dy = currentPoint[1] - previousPoint[1];
    return Math.sqrt(dx*dx + dy*dy);
  }

  /* 3. Optimization of the route by SA */
  public static void sa(double points[][]) {
    double currentT; // current temperature
    double finalT; // the temperature which ends a repeat
    int annealingN; // the number of times tried at each temperature
    double coolingRate;
    double currenTotalDistance, newTotalDistance;
    int randomIndex1, randomIndex2;

    currentT = 100;
    finalT = 0.1;
    annealingN = 20*N;
    coolingRate = 0.999;

    currenTotalDistance = totalDistance(points);
    for(; currentT > finalT; currentT *= coolingRate) {
      for(int i = 0; i < annealingN; i ++) {
        randomIndex1 = (int)(Math.random() * N);
        do {
          randomIndex2 = (int)(Math.random() * N);
        } while(randomIndex1 == randomIndex2);
        swap(points, bestRoute, randomIndex1, randomIndex2);
        newTotalDistance = totalDistance(points);
        // judge whether the route should be changed or not
        if(shouldChange(newTotalDistance - currenTotalDistance, currentT)) {
          currenTotalDistance = newTotalDistance;
        } else {
          swap(points, bestRoute, randomIndex1, randomIndex2); // return the route to one before
        }
      }
    }
  }
  // replace two indexes
  public static void swap(double points[][], int bestRoute[], int index1, int index2) {
    double xTmpPoints, yTmpPoints;
    int tmpRoute;
    xTmpPoints = points[index1][0]; yTmpPoints = points[index1][1];
    points[index1][0] = points[index2][0]; points[index1][1] = points[index2][1];
    points[index2][0] = xTmpPoints; points[index2][1] = yTmpPoints;
    tmpRoute = bestRoute[index1];
    bestRoute[index1] = bestRoute[index2];
    bestRoute[index2] = tmpRoute;
  }
  // whether the route should be changed or not
  // deltaDistance: new totalDistance - old totalDistance, t: temperature
  public static boolean shouldChange(double deltaDistance, double t) {
    if(deltaDistance <= 0) return true; // it should be changed
    if(Math.random() < Math.exp(-(deltaDistance / t))) return true; // new route is accepted by a probability of exp(-deltaDistance/t)
    return false;
  }


  /* 4. write best route to csv file*/
  public static void writeFile(String fileName, int bestRoute[]) {
    try {
      File file = new File(fileName);
      FileWriter fileWriter = new FileWriter(file);
      PrintWriter printWriter = new PrintWriter(fileWriter);
      printWriter.println("index");
      for(int i = 0; i < N; i ++) {
        printWriter.println(bestRoute[i]);
      }
      fileWriter.close();
      printWriter.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }


}
