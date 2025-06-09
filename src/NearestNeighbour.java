import java.util.*;

// Nearest neighbor heuristic for TSP approximation
// Builds tour by visiting closest unvisited cities
public class NearestNeighbour {
    // Generate TSP tour starting from city 0
    public static List<Integer> approximateTSPTour(int[][] graph) {
        return approximateTSPTourFromStart(0, graph);
    }
    // Uses greedy approach: always visit nearest unvisited city

    public static List<Integer> approximateTSPTourFromStart(int startCity, int[][] graph) {
        // Initialize tour data structures
        int n = (graph != null) ? graph.length : City.cities.size();
        boolean[] visited = new boolean[n];
        List<Integer> tour = new ArrayList<>();

        // Start tour from specified city
        int current = startCity;
        tour.add(current);
        visited[current] = true;

        // Visit each remaining city by choosing nearest unvisited neighbor
        for (int i = 1; i < n; i++) {
            int nextCity = -1;
            int minDistance = Integer.MAX_VALUE;

            // Find closest unvisited city
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
                break; // Safety check: no unvisited cities found
            }

            // Move to next city
            visited[nextCity] = true;
            tour.add(nextCity);
            current = nextCity;
        } // Complete tour by returning to start
        tour.add(startCity);

        return tour;
    }

}
