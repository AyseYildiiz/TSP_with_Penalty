import java.util.*;

// 2-opt local search optimization for TSP tours
// Removes two edges and reconnects tour segments if improvement found
public class TwoOpt {
    // Improve TSP tour using 2-opt edge swapping
    // Iteratively applies best 2-opt moves until no improvement
    public static List<Integer> improveTour(List<Integer> tour, int[][] graph) {
        boolean improvement = true;
        int n = tour.size();

        // Continue until no improvement found
        while (improvement) {
            improvement = false;

            // Try all possible 2-opt swaps
            for (int i = 1; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    // Get edge endpoints for distance calculation
                    int a = tour.get(i - 1);
                    int b = tour.get(i);
                    int c = tour.get(j);
                    int d = tour.get(j + 1);

                    // Calculate current and new edge distances
                    int currentDistance, newDistance;
                    if (graph != null) {
                        currentDistance = graph[a][b] + graph[c][d];
                        newDistance = graph[a][c] + graph[b][d];
                    } else {
                        currentDistance = City.getDistance(a, b) + City.getDistance(c, d);
                        newDistance = City.getDistance(a, c) + City.getDistance(b, d);
                    }

                    // Apply swap if it improves tour
                    if (newDistance < currentDistance) {
                        reverseSegment(tour, i, j);
                        improvement = true;
                    }
                }
            }
        }

        return tour;
    } // Reverse segment of tour between indices i and j

    private static void reverseSegment(List<Integer> tour, int i, int j) {
        // Swap elements from both ends moving inward
        while (i < j) {
            int temp = tour.get(i);
            tour.set(i, tour.get(j));
            tour.set(j, temp);
            i++;
            j--;
        }
    }

}