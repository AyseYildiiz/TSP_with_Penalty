import java.util.*;

/**
 * Enhanced optimization strategies for the TSP with Penalty problem
 * that incorporate global optimization techniques beyond local search.
 */
public class OptimizationStrategies {
    /**
     * Analyze cities to determine which ones should always be skipped based on
     * their
     * penalty-to-distance ratios and structural properties of the graph.
     * 
     * @param distanceMatrix The distance matrix between cities
     * @return A set of city indices that should be skipped
     */
    public static Set<Integer> identifySkipCities(int[][] distanceMatrix) {
        int n = (distanceMatrix != null) ? distanceMatrix.length : City.cities.size();
        Set<Integer> citiesToSkip = new HashSet<>();

        // For each city, analyze whether it's worth visiting in the optimal case
        for (int city = 0; city < n; city++) {
            // Find the two closest cities to this one
            int closest1 = -1, closest2 = -1;
            int dist1 = Integer.MAX_VALUE, dist2 = Integer.MAX_VALUE;

            for (int other = 0; other < n; other++) {
                if (other == city)
                    continue;

                int dist;
                if (distanceMatrix != null) {
                    dist = distanceMatrix[city][other];
                } else {
                    dist = City.getDistance(city, other);
                }

                if (dist < dist1) {
                    dist2 = dist1;
                    closest2 = closest1;
                    dist1 = dist;
                    closest1 = other;
                } else if (dist < dist2) {
                    dist2 = dist;
                    closest2 = other;
                }
            }

            // Calculate the detour cost in the best case - if we were to visit this city
            // optimally between its two closest neighbors
            int bestDetour = Integer.MAX_VALUE;
            if (closest1 != -1 && closest2 != -1) {
                if (distanceMatrix != null) {
                    bestDetour = distanceMatrix[closest1][city] + distanceMatrix[city][closest2]
                            - distanceMatrix[closest1][closest2];
                } else {
                    bestDetour = City.getDistance(closest1, city) + City.getDistance(city, closest2)
                            - City.getDistance(closest1, closest2);
                }
            }

            // If the penalty is less than the best possible detour, always skip this city
            if (City.penalty < bestDetour) {
                citiesToSkip.add(city);
            }
        }

        return citiesToSkip;
    }

    /**
     * Applies preprocessing to identify cities that should be skipped, then runs
     * the specified algorithm on the reduced problem.
     * 
     * @param distanceMatrix The distance matrix
     * @param maxIterations  Maximum iterations for local search
     * @return An optimized tour
     */
    public static List<Integer> optimizeWithPreprocessing(int[][] distanceMatrix, int maxIterations) {
        // Identify cities to skip
        Set<Integer> citiesToSkip = identifySkipCities(distanceMatrix);
        System.out.println("Preprocessing identified " + citiesToSkip.size() + " cities to skip");

        // Create a reduced problem without these cities
        int n = (distanceMatrix != null) ? distanceMatrix.length : City.cities.size();
        List<Integer> includedCities = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!citiesToSkip.contains(i)) {
                includedCities.add(i);
            }
        }

        // If all cities should be skipped, return an empty tour
        if (includedCities.isEmpty()) {
            return new ArrayList<>();
        }

        // Solve the reduced problem with multi-start local search
        List<Integer> bestTour = null;
        int bestCost = Integer.MAX_VALUE;

        Random random = new Random();

        // Try several starting points
        for (int start = 0; start < 3; start++) {
            // Generate an initial tour using only included cities
            List<Integer> initialTour;
            if (start == 0 && includedCities.size() > 1) {
                initialTour = new ArrayList<>(includedCities);
                Collections.shuffle(initialTour);
                initialTour.add(initialTour.get(0)); // Close the tour
            } else {
                // Use a simple nearest neighbor for other starts
                initialTour = new ArrayList<>();
                boolean[] visited = new boolean[n];

                // Mark all skip cities as already visited
                for (int city : citiesToSkip) {
                    visited[city] = true;
                }

                // Start from the first non-skipped city
                int current = includedCities.get(0);
                initialTour.add(current);
                visited[current] = true;

                // Build the rest of the tour
                for (int i = 1; i < includedCities.size(); i++) {
                    int next = -1;
                    int minDist = Integer.MAX_VALUE;
                    for (int j = 0; j < n; j++) {
                        int distance;
                        if (distanceMatrix != null) {
                            distance = distanceMatrix[current][j];
                        } else {
                            distance = City.getDistance(current, j);
                        }

                        if (!visited[j] && distance < minDist) {
                            minDist = distance;
                            next = j;
                        }
                    }

                    if (next != -1) {
                        initialTour.add(next);
                        visited[next] = true;
                        current = next;
                    }
                }

                // Close the tour
                initialTour.add(initialTour.get(0));
            }

            // Improve this tour using 2-opt and 3-opt
            List<Integer> improvedTour = TwoOpt.improveTour(initialTour, distanceMatrix);
            improvedTour = ThreeOpt.improveTour(improvedTour, distanceMatrix);

            // Check if this is the best tour so far
            int cost = City.calculateTourCost(improvedTour, distanceMatrix);
            if (cost < bestCost) {
                bestCost = cost;
                bestTour = improvedTour;
            }
        }

        return bestTour;
    }
}
