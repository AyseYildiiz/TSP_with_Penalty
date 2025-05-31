import java.util.*;

public class NearestNeighbour {
    public static List<Integer> approximateTSPTour(int[][] graph) {
        int n = graph.length;
        boolean[] visited = new boolean[n];
        List<Integer> tour = new ArrayList<>();
        int current = 0;
        tour.add(current);
        visited[current] = true;

        for (int i = 1; i < n; i++) {
            int nextCity = -1;
            int minDistance = Integer.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                if (!visited[j] && graph[current][j] < minDistance) {
                    minDistance = graph[current][j];
                    nextCity = j;
                }
            }

            visited[nextCity] = true;
            tour.add(nextCity);
            current = nextCity;
        }

        // Başlangıç noktasına dön
        tour.add(tour.get(0));

        return tour;
    }


}


