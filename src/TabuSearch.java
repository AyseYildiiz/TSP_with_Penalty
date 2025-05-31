import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabuSearch {
    static int tabuTenure = 7;

    public static List<Integer> solve(List<Integer> initialTour, int [][] distanceMatrix, int maxIterations) {
        int n = initialTour.size();
        List<Integer> bestTour = new ArrayList<>(initialTour);
        List<Integer> currentTour = new ArrayList<>(initialTour);
        double bestCost = City.calculateTourCost(bestTour, distanceMatrix);

        int[][] tabuList = new int[n][n];
        int iteration = 0;

        while (iteration < maxIterations) {
            double bestNeighborCost = Double.MAX_VALUE;
            int swapI = -1, swapJ = -1;

            for (int i = 1; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    List<Integer> neighbor = new ArrayList<>(currentTour);
                    Collections.swap(neighbor, i, j);
                    double cost = City.calculateTourCost(neighbor, distanceMatrix);

                    boolean isTabu = tabuList[currentTour.get(i)][currentTour.get(j)] > iteration;

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
                tabuList[currentTour.get(swapI)][currentTour.get(swapJ)] = iteration + tabuTenure;
                tabuList[currentTour.get(swapJ)][currentTour.get(swapI)] = iteration + tabuTenure;

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