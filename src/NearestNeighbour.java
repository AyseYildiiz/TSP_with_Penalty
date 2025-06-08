import java.util.*;

public class NearestNeighbour {
    public static List<Integer> approximateTSPTour(int[][] graph) {
        return approximateTSPTourFromStart(0, graph);
    }

    public static List<Integer> approximateTSPTourFromStart(int startCity, int[][] graph) {
        // Get number of cities from graph or City class
        int n = (graph != null) ? graph.length : City.cities.size();
        boolean[] visited = new boolean[n];
        List<Integer> tour = new ArrayList<>();
        int current = startCity;
        tour.add(current);
        visited[current] = true;

        for (int i = 1; i < n; i++) {
            int nextCity = -1;
            int minDistance = Integer.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                if (!visited[j]) {
                    // Use matrix if available, otherwise calculate on-demand
                    int distance = (graph != null) ? graph[current][j] : City.getDistance(current, j);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nextCity = j;
                    }
                }
            }

            if (nextCity == -1) {
                break; // No more unvisited cities reachable
            }

            visited[nextCity] = true;
            tour.add(nextCity);
            current = nextCity;
        }

        // Return to the starting point
        tour.add(startCity);

        return tour;
    }

}
