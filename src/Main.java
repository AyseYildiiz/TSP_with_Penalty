import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Working Dir = " + System.getProperty("user.dir"));

        String inputFileName = "src/example-input-4.txt";
        String outputFileName = inputFileName.replace("input", "output");

        System.out.println("TSP with Penalty Solver");
        System.out.println("Input file: " + inputFileName);

        Result result = processFile(inputFileName);
        List<Integer> bestTour = result.tour;

        // Create output tour without duplicates
        List<Integer> outputTour = new ArrayList<>(bestTour);
        if (outputTour.size() > 1 && outputTour.get(0).equals(outputTour.get(outputTour.size() - 1))) {
            outputTour.remove(outputTour.size() - 1);
        }

        int totalCost = City.calculateTourCost(bestTour, City.distancesMatrix);
        int citiesVisited = outputTour.size();

        writeOutputFile(outputFileName, outputTour, totalCost, citiesVisited);

        System.out.println("Output file: " + outputFileName);
        System.out.println("Total cost: " + totalCost);
        System.out.println("Cities visited: " + citiesVisited + "/" + City.cities.size());
        System.out.println("Best algorithm: " + result.algorithmName);
    }

    private static Result processFile(String fileName) {
        // Reset and read input
        City.cities.clear();
        City.distancesMatrix = null;

        try (Scanner scanner = new Scanner(new File(fileName))) {
            if (scanner.hasNextInt()) {
                City.penalty = scanner.nextInt();
            }
            while (scanner.hasNextInt()) {
                new City(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
            throw new RuntimeException(e);
        }

        System.out.println("Cities: " + City.cities.size() + ", Penalty: " + City.penalty);

        // Create distance matrix
        City.createDistancesMatrixOptimized();

        List<Integer> bestTour;
        String bestAlgorithm; // If more than 15,000 cities, use Fast Hybrid approach
        if (City.cities.size() > 15000) {
            System.out.println("Large instance - using Fast Hybrid approach");
            bestTour = FastHybridSolver.solve(City.distancesMatrix);
            bestAlgorithm = "Fast Hybrid Solver";
        } else {
            // For smaller instances, try multiple algorithms
            System.out.println("Standard instance - testing multiple algorithms");

            List<Integer> bestSolution = null;
            int bestCost = Integer.MAX_VALUE;
            bestAlgorithm = "None";

            // Nearest Neighbor
            List<Integer> nnTour = NearestNeighbour.approximateTSPTour(City.distancesMatrix);
            nnTour = TourUtils.pruneTourWithPenalty(nnTour, City.distancesMatrix);
            int nnCost = City.calculateTourCost(nnTour, City.distancesMatrix);
            if (nnCost < bestCost) {
                bestCost = nnCost;
                bestSolution = nnTour;
                bestAlgorithm = "Nearest Neighbor";
            }

            // Nearest Neighbor + 2-opt + 3-opt
            List<Integer> optimizedTour = TwoOpt.improveTour(nnTour, City.distancesMatrix);
            optimizedTour = ThreeOpt.improveTour(optimizedTour, City.distancesMatrix);
            optimizedTour = TourUtils.advancedPruning(optimizedTour, City.distancesMatrix);
            int optimizedCost = City.calculateTourCost(optimizedTour, City.distancesMatrix);
            if (optimizedCost < bestCost) {
                bestCost = optimizedCost;
                bestSolution = optimizedTour;
                bestAlgorithm = "Nearest Neighbor + 2-opt + 3-opt";
            } // Christofides Enhanced
            try {
                List<Integer> christofidesTour = ChristofidesEnhanced.getOptimizedChristofidesTour(false);
                int christofidesCost = City.calculateTourCost(christofidesTour, City.distancesMatrix);
                if (christofidesCost < bestCost) {
                    bestCost = christofidesCost;
                    bestSolution = christofidesTour;
                    bestAlgorithm = "Christofides Enhanced";
                }
            } catch (Exception e) {
                System.out.println("Christofides failed: " + e.getMessage());
            }

            // TabuSearch
            try {
                List<Integer> tabuTour = TabuSearch.solve(bestSolution, City.distancesMatrix, 1000);
                tabuTour = TourUtils.advancedPruning(tabuTour, City.distancesMatrix);
                int tabuCost = City.calculateTourCost(tabuTour, City.distancesMatrix);
                if (tabuCost < bestCost) {
                    bestCost = tabuCost;
                    bestSolution = tabuTour;
                    bestAlgorithm = "TabuSearch";
                }
            } catch (Exception e) {
                System.out.println("TabuSearch failed: " + e.getMessage());
            } // OptimizationStrategies
            try {
                List<Integer> optimizationTour = OptimizationStrategies.optimizeWithPreprocessing(City.distancesMatrix,
                        5000);
                int optimizationCost = City.calculateTourCost(optimizationTour, City.distancesMatrix);
                if (optimizationCost < bestCost) {
                    bestCost = optimizationCost;
                    bestSolution = optimizationTour;
                    bestAlgorithm = "OptimizationStrategies";
                }
            } catch (Exception e) {
                System.out.println("OptimizationStrategies failed: " + e.getMessage());
            }

            bestTour = bestSolution;
        }

        return new Result(bestTour, bestAlgorithm);
    }

    private static void writeOutputFile(String fileName, List<Integer> tour, int totalCost, int citiesVisited) {
        try {
            FileWriter writer = new FileWriter(fileName);

            // First, check if the tour is closed (first city equals last city)
            List<Integer> outputTour = new ArrayList<>(tour);
            if (outputTour.size() > 1 && outputTour.get(0).equals(outputTour.get(outputTour.size() - 1))) {
                // Remove the duplicate last city
            }

            // First line: total cost and number of cities visited
            writer.write(totalCost + " " + outputTour.size() + "\n");

            // Write each city ID on a separate line
            for (Integer cityId : outputTour) {
                writer.write(cityId + "\n");
            }

            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to output file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}