import java.util.*;

// 3-opt local search optimization for TSP tours
// Removes three edges and reconnects tour segments optimally
public class ThreeOpt {
    // Improve TSP tour using 3-opt edge swapping
    // Iteratively applies best 3-opt moves until no improvement
    public static List<Integer> improveTour(List<Integer> tour, int[][] graph) {
        boolean improvement = true;

        // Continue until no improvement found
        while (improvement) {
            improvement = false;

            // Try all possible 3-opt moves
            for (int i = 1; i < tour.size() - 5; i++) {
                for (int j = i + 2; j < tour.size() - 3; j++) {
                    for (int k = j + 2; k < tour.size() - 1; k++) {

                        // Test 3-opt swap at positions i, j, k
                        List<Integer> newTour = try3OptSwap(tour, i, j, k, graph);
                        if (newTour != null) {
                            tour = newTour;
                            improvement = true;
                        }
                    }
                }
            }
        }

        return tour;
    } // Attempt 3-opt swap by reversing two segments
    // Returns improved tour if swap reduces total distance

    private static List<Integer> try3OptSwap(List<Integer> tour, int i, int j, int k, int[][] graph) {
        // Get edge endpoints for distance calculation
        int A = tour.get(i - 1), B = tour.get(i);
        int C = tour.get(j - 1), D = tour.get(j);
        int E = tour.get(k - 1), F = tour.get(k);

        // Calculate original edge distances
        int d0;
        if (graph != null) {
            d0 = graph[A][B] + graph[C][D] + graph[E][F];
        } else {
            d0 = City.getDistance(A, B) + City.getDistance(C, D) + City.getDistance(E, F);
        }

        // Create new tour by reversing two segments
        List<Integer> newTour = new ArrayList<>(tour);
        reverseSegment(newTour, i, j - 1);
        reverseSegment(newTour, j, k - 1);

        // Calculate new edge distances
        int d1;
        if (graph != null) {
            d1 = graph[newTour.get(i - 1)][newTour.get(i)] +
                    graph[newTour.get(j - 1)][newTour.get(j)] +
                    graph[newTour.get(k - 1)][newTour.get(k)];
        } else {
            d1 = City.getDistance(newTour.get(i - 1), newTour.get(i)) +
                    City.getDistance(newTour.get(j - 1), newTour.get(j)) +
                    City.getDistance(newTour.get(k - 1), newTour.get(k));
        }

        // Return improved tour or null if no improvement
        if (d1 < d0)
            return newTour;
        return null;
    } // Reverse segment of tour between start and end indices

    private static void reverseSegment(List<Integer> tour, int start, int end) {
        // Swap elements from both ends moving inward
        while (start < end) {
            int temp = tour.get(start);
            tour.set(start, tour.get(end));
            tour.set(end, temp);
            start++;
            end--;
        }
    }

}