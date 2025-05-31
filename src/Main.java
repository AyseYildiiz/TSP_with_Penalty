import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Main  {
    public static void main(String[] args) {
        String fileName = "C:\\Users\\z005067k\\IdeaProjects\\TSPwP\\src\\example-input-1.txt";

        File file = new File(fileName);

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (scanner.hasNextInt()) {
            City.penalty = scanner.nextInt();
        }
        while (scanner.hasNextInt()) {
            City x = new City(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
        }

        City.createDistancesMatrix();
        int [][] mst = TreeOperations.primMST();
        System.out.println(Arrays.deepToString(mst));
        System.out.println();
        List<List<Integer>> adjacencyList = TreeOperations.buildAdjacencyList(mst, City.distancesMatrix.length);
        System.out.println(adjacencyList);
        System.out.println();
        List<Integer> tour = TwiceAroundTheTree.approximateTSPTour(City.distancesMatrix);
        System.out.println( City.calculateTourCost(tour, City.distancesMatrix));
        List<Integer> tour2 = NearestNeighbour.approximateTSPTour(City.distancesMatrix);
        System.out.println( City.calculateTourCost(tour2, City.distancesMatrix));
        List<Integer> tour3 = TwoOpt.improveTour(tour2, City.distancesMatrix);
        System.out.println( City.calculateTourCost(tour3, City.distancesMatrix));
        List<Integer> tour4 = ThreeOpt.improveTour(tour3, City.distancesMatrix);
        System.out.println( City.calculateTourCost(tour4, City.distancesMatrix));
        List<Integer> tour5 = Christofides.getChristofidesTour();
        System.out.println(City.calculateTourCost(tour5, City.distancesMatrix));
        List<Integer> tour6 = TabuSearch.solve(tour5,City.distancesMatrix,1000);
        System.out.println(City.calculateTourCost(tour3, City.distancesMatrix));
    }}