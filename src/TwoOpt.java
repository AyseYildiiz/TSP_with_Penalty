import java.util.*;
public class TwoOpt {
    public static List<Integer> improveTour(List<Integer> tour, int[][] graph) {
        boolean improvement = true;
        int n = tour.size();

        while (improvement) {
            improvement = false;

            for (int i = 1; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    int a = tour.get(i - 1);
                    int b = tour.get(i);
                    int c = tour.get(j);
                    int d = tour.get(j + 1);

                    int currentDistance = graph[a][b] + graph[c][d];
                    int newDistance = graph[a][c] + graph[b][d];

                    if (newDistance < currentDistance) {
                        reverseSegment(tour, i, j);
                        improvement = true;
                    }
                }
            }
        }

        return tour;
    }

    private static void reverseSegment(List<Integer> tour, int i, int j) {
        while (i < j) {
            int temp = tour.get(i);
            tour.set(i, tour.get(j));
            tour.set(j, temp);
            i++;
            j--;
        }
    }


}