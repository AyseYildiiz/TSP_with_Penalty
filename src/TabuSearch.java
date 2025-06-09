import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Tabu search metaheuristic for TSP optimization
// Uses memory to avoid cycling through previously explored solutions
public class TabuSearch {
    // Number of iterations a move remains forbidden
    static int tabuTenure = 7;

    // Optimize TSP tour using tabu search algorithm
    // Explores neighborhood by swapping cities while maintaining tabu list
    public static List<Integer> solve(List<Integer> initialTour, int[][] distanceMatrix, int maxIterations) { // Initialize
                                                                                                              // search
                                                                                                              // variables
        int n = initialTour.size();
        List<Integer> bestTour = new ArrayList<>(initialTour);
        List<Integer> currentTour = new ArrayList<>(initialTour);
        double bestCost = City.calculateTourCost(bestTour, distanceMatrix);

        // Create tabu list to track forbidden moves
        int matrixSize = (distanceMatrix != null) ? distanceMatrix.length : City.cities.size();
        int[][] tabuList = new int[matrixSize][matrixSize];
        int iteration = 0;
        while (iteration < maxIterations) {
            // Find best non-tabu move in current neighborhood
            double bestNeighborCost = Double.MAX_VALUE;
            int swapI = -1, swapJ = -1;

            // Explore all possible city swaps
            for (int i = 1; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    // Generate neighbor by swapping cities
                    List<Integer> neighbor = new ArrayList<>(currentTour);
                    Collections.swap(neighbor, i, j);
                    double cost = City.calculateTourCost(neighbor, distanceMatrix); // Check if move is forbidden by
                                                                                    // tabu list
                    int cityI = currentTour.get(i);
                    int cityJ = currentTour.get(j);

                    // Safety check to avoid array index out of bounds
                    boolean isTabu = false;
                    if (cityI < matrixSize && cityJ < matrixSize) {
                        isTabu = tabuList[cityI][cityJ] > iteration;
                    }

                    // Accept move if not tabu or if it improves best solution
                    if (!isTabu || cost < bestCost) {
                        if (cost < bestNeighborCost) {
                            bestNeighborCost = cost;
                            swapI = i;
                            swapJ = j;
                        }
                    }
                }
            } // Apply best move if found
            if (swapI != -1 && swapJ != -1) {
                Collections.swap(currentTour, swapI, swapJ);

                // Add move to tabu list to prevent cycling
                int cityI = currentTour.get(swapI);
                int cityJ = currentTour.get(swapJ);

                // Safety check to avoid array index out of bounds
                if (cityI < matrixSize && cityJ < matrixSize) {
                    tabuList[cityI][cityJ] = iteration + tabuTenure;
                    tabuList[cityJ][cityI] = iteration + tabuTenure;
                }

                // Update best solution if improvement found
                double currentCost = City.calculateTourCost(currentTour, distanceMatrix);
                if (currentCost < bestCost) {
                    bestTour = new ArrayList<>(currentTour);
                    bestCost = currentCost;
                }
            }

            iteration++;
        }

        return bestTour;
    }
}