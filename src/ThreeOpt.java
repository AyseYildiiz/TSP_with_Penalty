import java.util.*;
public class ThreeOpt {
    public static List<Integer> improveTour(List<Integer> tour, int[][] graph) {
        boolean improvement = true;

        while (improvement) {
            improvement = false;

            for (int i = 1; i < tour.size() - 5; i++) {
                for (int j = i + 2; j < tour.size() - 3; j++) {
                    for (int k = j + 2; k < tour.size() - 1; k++) {

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
    }

    private static List<Integer> try3OptSwap(List<Integer> tour, int i, int j, int k, int[][] graph) {
        int A = tour.get(i - 1), B = tour.get(i);
        int C = tour.get(j - 1), D = tour.get(j);
        int E = tour.get(k - 1), F = tour.get(k);

        int d0 = graph[A][B] + graph[C][D] + graph[E][F];
        List<Integer> newTour = new ArrayList<>(tour);
        reverseSegment(newTour, i, j - 1);
        reverseSegment(newTour, j, k - 1);

        int d1 = graph[newTour.get(i - 1)][newTour.get(i)] +
                graph[newTour.get(j - 1)][newTour.get(j)] +
                graph[newTour.get(k - 1)][newTour.get(k)];

        if (d1 < d0) return newTour;
        return null;
    }

    private static void reverseSegment(List<Integer> tour, int start, int end) {
        while (start < end) {
            int temp = tour.get(start);
            tour.set(start, tour.get(end));
            tour.set(end, temp);
            start++;
            end--;
        }
    }

}