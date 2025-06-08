import java.util.*;

/**
 * Fast Hybrid Solver for very large TSP instances (>15,000 cities)
 * Combines multiple fast heuristics for optimal performance
 */
public class FastHybridSolver {

    /**
     * Main solver for large instances using hybrid approach
     */
    public static List<Integer> solve(int[][] distanceMatrix) {
        int n = (distanceMatrix != null) ? distanceMatrix.length : City.cities.size();

        System.out.println("├─ Fast Hybrid: Starting multi-phase optimization...");

        // Phase 1: Create multiple initial solutions
        System.out.println("├─ Phase 1: Generating initial solutions...");
        List<List<Integer>> initialSolutions = generateInitialSolutions(distanceMatrix, n);

        // Phase 2: Select best initial solution
        List<Integer> bestSolution = selectBestSolution(initialSolutions, distanceMatrix);
        int bestCost = City.calculateTourCost(bestSolution, distanceMatrix);
        System.out.println("├─ Best initial cost: " + bestCost);

        // Phase 3: Apply limited local optimization
        System.out.println("├─ Phase 2: Applying limited 2-opt optimization...");
        bestSolution = applyLimited2Opt(bestSolution, distanceMatrix, Math.min(n / 10, 1000));

        // Phase 4: Aggressive penalty-aware pruning
        System.out.println("├─ Phase 3: Aggressive penalty-aware pruning...");
        bestSolution = aggressivePruning(bestSolution, distanceMatrix);

        // Phase 5: Final local optimization
        System.out.println("├─ Phase 4: Final optimization...");
        bestSolution = applyLimited2Opt(bestSolution, distanceMatrix, Math.min(n / 20, 500));

        int finalCost = City.calculateTourCost(bestSolution, distanceMatrix);
        System.out.println("├─ Final cost: " + finalCost + " (improvement: " + (bestCost - finalCost) + ")");

        return bestSolution;
    }

    /**
     * Generate multiple initial solutions using different strategies
     */
    private static List<List<Integer>> generateInitialSolutions(int[][] distanceMatrix, int n) {
        List<List<Integer>> solutions = new ArrayList<>();

        System.out.println("├─ Generating initial solutions for " + n + " cities...");

        // Strategy 1: Fast Nearest Neighbor from city 0
        try {
            System.out.println("├─ Running NN from city 0...");
            List<Integer> nn1 = fastNearestNeighbor(0, distanceMatrix, n);
            solutions.add(nn1);
            System.out.println("├─ ✅ NN from 0 completed");
        } catch (Exception e) {
            System.out.println("├─ ❌ NN from 0 failed: " + e.getMessage());
        }

        // Strategy 2: Fast NN from 2 strategic points only
        try {
            System.out.println("├─ Running NN from strategic points...");
            List<Integer> nnMid = fastNearestNeighbor(n / 2, distanceMatrix, n);
            solutions.add(nnMid);

            List<Integer> nnLast = fastNearestNeighbor(n - 1, distanceMatrix, n);
            solutions.add(nnLast);
            System.out.println("├─ ✅ Strategic NN completed");
        } catch (Exception e) {
            System.out.println("├─ ❌ Strategic NN failed: " + e.getMessage());
        }

        // Strategy 3: Greedy sampling for very large instances
        if (n > 10000) {
            try {
                System.out.println("├─ Running greedy sampling...");
                List<Integer> samplingTour = createGreedySamplingTour(distanceMatrix, n);
                solutions.add(samplingTour);
                System.out.println("├─ ✅ Greedy sampling completed");
            } catch (Exception e) {
                System.out.println("├─ ❌ Sampling approach failed: " + e.getMessage());
            }
        }

        System.out.println("├─ Generated " + solutions.size() + " initial solutions");
        return solutions;
    }

    /**
     * Fast Nearest Neighbor algorithm optimized for large instances
     */
    private static List<Integer> fastNearestNeighbor(int startCity, int[][] distanceMatrix, int n) {
        boolean[] visited = new boolean[n];
        List<Integer> tour = new ArrayList<>();
        int current = startCity;

        tour.add(current);
        visited[current] = true;

        // Progress tracking for large instances
        int progressStep = Math.max(1, n / 20); // Show progress every 5%

        for (int step = 1; step < n; step++) {
            if (step % progressStep == 0) {
                System.out.printf("   Progress: %.1f%% (%d/%d)\r",
                        (double) step / n * 100, step, n);
            }

            int nextCity = -1;
            int minDistance = Integer.MAX_VALUE;

            // Find nearest unvisited city
            for (int j = 0; j < n; j++) {
                if (!visited[j]) {
                    int distance = (distanceMatrix != null) ? distanceMatrix[current][j] : City.getDistance(current, j);

                    if (distance < minDistance) {
                        minDistance = distance;
                        nextCity = j;
                    }
                }
            }

            if (nextCity == -1)
                break; // Safety check

            current = nextCity;
            tour.add(current);
            visited[current] = true;
        }

        System.out.println(); // New line after progress
        return tour;
    }

    /**
     * Create greedy sampling-based tour for very large instances
     */
    private static List<Integer> createGreedySamplingTour(int[][] distanceMatrix, int n) {
        // Start with a smaller subset and expand
        List<Integer> tour = new ArrayList<>();
        boolean[] inTour = new boolean[n];

        // Start from city 0
        tour.add(0);
        inTour[0] = true;

        // Greedily add closest cities
        while (tour.size() < n) {
            int bestCity = -1;
            int bestDistance = Integer.MAX_VALUE;
            int bestPosition = -1;

            // Find best city to insert
            for (int city = 0; city < n; city++) {
                if (inTour[city])
                    continue;

                // Find best position to insert this city
                for (int pos = 1; pos <= tour.size(); pos++) {
                    int prev = tour.get(pos - 1);
                    int next = (pos < tour.size()) ? tour.get(pos) : tour.get(0);

                    int insertCost = getDistanceValue(prev, city, distanceMatrix) +
                            getDistanceValue(city, next, distanceMatrix) -
                            getDistanceValue(prev, next, distanceMatrix);

                    if (insertCost < bestDistance) {
                        bestDistance = insertCost;
                        bestCity = city;
                        bestPosition = pos;
                    }
                }
            }

            if (bestCity != -1) {
                tour.add(bestPosition, bestCity);
                inTour[bestCity] = true;

                // Show progress for large instances
                if (tour.size() % 1000 == 0) {
                    System.out.printf("   Greedy insertion: %d/%d cities\r", tour.size(), n);
                }
            } else {
                break;
            }
        }

        System.out.println(); // New line after progress
        return tour;
    }

    private static int getDistanceValue(int from, int to, int[][] distanceMatrix) {
        return (distanceMatrix != null) ? distanceMatrix[from][to] : City.getDistance(from, to);
    }

    /**
     * Select the best solution from multiple initial solutions
     */
    private static List<Integer> selectBestSolution(List<List<Integer>> solutions, int[][] distanceMatrix) {
        List<Integer> bestSolution = null;
        int bestCost = Integer.MAX_VALUE;

        for (List<Integer> solution : solutions) {
            if (solution != null && !solution.isEmpty()) {
                int cost = City.calculateTourCost(solution, distanceMatrix);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestSolution = new ArrayList<>(solution);
                }
            }
        }

        return bestSolution != null ? bestSolution : new ArrayList<>();
    }

    /**
     * Apply limited 2-opt optimization for large instances
     */
    private static List<Integer> applyLimited2Opt(List<Integer> tour, int[][] distanceMatrix, int maxIterations) {
        if (tour.size() < 4)
            return tour;

        List<Integer> result = new ArrayList<>(tour);
        boolean improved = true;
        int iterations = 0;

        while (improved && iterations < maxIterations) {
            improved = false;

            for (int i = 1; i < result.size() - 2 && !improved; i++) {
                for (int j = i + 1; j < result.size() - 1 && !improved; j++) {
                    if (try2OptSwap(result, i, j, distanceMatrix)) {
                        improved = true;
                    }
                }
            }
            iterations++;
        }

        return result;
    }

    /**
     * Try 2-opt swap and apply if beneficial
     */
    private static boolean try2OptSwap(List<Integer> tour, int i, int j, int[][] distanceMatrix) {
        int a = tour.get(i - 1);
        int b = tour.get(i);
        int c = tour.get(j);
        int d = tour.get(j + 1);

        int currentCost, newCost;
        if (distanceMatrix != null) {
            currentCost = distanceMatrix[a][b] + distanceMatrix[c][d];
            newCost = distanceMatrix[a][c] + distanceMatrix[b][d];
        } else {
            currentCost = City.getDistance(a, b) + City.getDistance(c, d);
            newCost = City.getDistance(a, c) + City.getDistance(b, d);
        }

        if (newCost < currentCost) {
            // Reverse segment between i and j
            Collections.reverse(tour.subList(i, j + 1));
            return true;
        }

        return false;
    }

    /**
     * Aggressive penalty-aware pruning for large instances
     */
    private static List<Integer> aggressivePruning(List<Integer> tour, int[][] distanceMatrix) {
        List<Integer> result = new ArrayList<>(tour);
        boolean improved = true;

        while (improved) {
            improved = false;

            // Remove cities one by one if beneficial
            for (int i = 1; i < result.size() - 1; i++) {
                List<Integer> testTour = new ArrayList<>(result);
                testTour.remove(i);

                int originalCost = City.calculateTourCost(result, distanceMatrix);
                int newCost = City.calculateTourCost(testTour, distanceMatrix);

                if (newCost < originalCost) {
                    result = testTour;
                    improved = true;
                    break;
                }
            }

            // Try removing sequences of 2-3 cities
            if (!improved) {
                for (int len = 2; len <= 3 && !improved; len++) {
                    for (int start = 1; start < result.size() - len; start++) {
                        List<Integer> testTour = new ArrayList<>(result);
                        for (int k = 0; k < len; k++) {
                            testTour.remove(start);
                        }

                        int originalCost = City.calculateTourCost(result, distanceMatrix);
                        int newCost = City.calculateTourCost(testTour, distanceMatrix);

                        if (newCost < originalCost) {
                            result = testTour;
                            improved = true;
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }
}
