import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabuSearch {
    static int tabuTenure = 7;

    public static List<Integer> solve(List<Integer> initialTour, int[][] distanceMatrix, int maxIterations) {
        int n = initialTour.size();
        List<Integer> bestTour = new ArrayList<>(initialTour);
        List<Integer> currentTour = new ArrayList<>(initialTour);
        double bestCost = City.calculateTourCost(bestTour, distanceMatrix);

        // Use the size of the distance matrix for tabu list, not the tour size
        int matrixSize = (distanceMatrix != null) ? distanceMatrix.length : City.cities.size();
        int[][] tabuList = new int[matrixSize][matrixSize];
        int iteration = 0;

        while (iteration < maxIterations) {
            double bestNeighborCost = Double.MAX_VALUE;
            int swapI = -1, swapJ = -1;

            for (int i = 1; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    List<Integer> neighbor = new ArrayList<>(currentTour);
                    Collections.swap(neighbor, i, j);
                    double cost = City.calculateTourCost(neighbor, distanceMatrix);

                    // Get the actual city indices safely
                    int cityI = currentTour.get(i);
                    int cityJ = currentTour.get(j);

                    // Safety check to avoid array index out of bounds
                    boolean isTabu = false;
                    if (cityI < matrixSize && cityJ < matrixSize) {
                        isTabu = tabuList[cityI][cityJ] > iteration;
                    }

                    if (!isTabu || cost < bestCost) {
                        if (cost < bestNeighborCost) {
                            bestNeighborCost = cost;
                            swapI = i;
                            swapJ = j;
                        }
                    }
                }
            }

            if (swapI != -1 && swapJ != -1) {
                Collections.swap(currentTour, swapI, swapJ);

                // Get the actual city indices safely
                int cityI = currentTour.get(swapI);
                int cityJ = currentTour.get(swapJ);

                // Safety check to avoid array index out of bounds
                if (cityI < matrixSize && cityJ < matrixSize) {
                    tabuList[cityI][cityJ] = iteration + tabuTenure;
                    tabuList[cityJ][cityI] = iteration + tabuTenure;
                }

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