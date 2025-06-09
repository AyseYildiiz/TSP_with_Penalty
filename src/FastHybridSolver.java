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
        System.out.println("├─ Phase 3: Smart perturbation...");
        bestSolution = smartPerturbation(bestSolution, distanceMatrix, 5, 20);

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
            System.out.println("├─ Running Nearest Neighbour from city 0...");
            List<Integer> nn1 = fastNearestNeighbor(0, distanceMatrix, n);
            solutions.add(nn1);
            System.out.println("├─ ✅ Nearest Neighbour from 0 completed");
        } catch (Exception e) {
            System.out.println("├─ ❌ Nearest Neighbour from 0 failed: " + e.getMessage());
        }

        // Strategy 2: Fast NN from 2 strategic points only
        try {
            System.out.println("├─ Running Nearest Neighbour from strategic points...");
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
        else {
            try {
                System.out.println("├─ Running Twice Around the Tree...");
                List<Integer> twiceTour = TwiceAroundTheTree.approximateTSPTour(distanceMatrix);
                solutions.add(twiceTour);
                System.out.println("├─ ✅ Twice Around completed");
            } catch (Exception e) {
                System.out.println("├─ ❌ Twice Around failed: " + e.getMessage());
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
        List<Integer> tour = new ArrayList<>();
        boolean[] inTour = new boolean[n];

        // Start from city 0
        int current = 0;
        tour.add(current);
        inTour[current] = true;

        // Greedily add closest cities
        while (tour.size() < n) {
            int nextCity = -1;
            int minDistance = Integer.MAX_VALUE;

            for (int city = 0; city < n; city++) {
                if (!inTour[city]) {
                    int distance = getDistanceValue(current, city, distanceMatrix);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nextCity = city;
                    }
                }
            }

            if (nextCity != -1) {
                tour.add(nextCity);
                inTour[nextCity] = true;
                current = nextCity;

                if (tour.size() % 1000 == 0) {
                    System.out.printf("   Fast greedy: %d/%d\r", tour.size(), n);
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
        Random rand = new Random();

        int n = result.size();
        int successfulSwaps = 0;

        for (int iter = 0; iter < maxIterations; iter++) {
            int i = rand.nextInt(n - 3) + 1;
            int j = i + rand.nextInt(Math.min(n - i - 2, 20)); // j: i'den sonra, ama çok uzak değil

            if (try2OptSwap(result, i, j, distanceMatrix)) {
                successfulSwaps++;
            }
        }

        System.out.println("   Randomized 2-opt: " + successfulSwaps + " successful swaps");
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
    /**
     * Smart perturbation: Shuffle a random small segment and reinsert optimally
     */
    private static List<Integer> smartPerturbation(List<Integer> tour, int[][] distanceMatrix, int segmentSize, int iterations) {
        Random rand = new Random();
        List<Integer> bestTour = new ArrayList<>(tour);
        int bestCost = City.calculateTourCost(bestTour, distanceMatrix);

        for (int it = 0; it < iterations; it++) {
            int n = bestTour.size();

            // Choose a random segment of 'segmentSize' to shuffle
            if (n <= segmentSize + 2) break; // not enough cities

            int start = 1 + rand.nextInt(n - segmentSize - 1); // avoid start/end
            List<Integer> segment = new ArrayList<>(bestTour.subList(start, start + segmentSize));
            Collections.shuffle(segment);

            // Create a new tour by replacing segment
            List<Integer> newTour = new ArrayList<>(bestTour.subList(0, start));
            newTour.addAll(segment);
            newTour.addAll(bestTour.subList(start + segmentSize, n));

            // Local 2-opt improvement (just a bit)
            newTour = applyLimited2Opt(newTour, distanceMatrix, 10);

            int newCost = City.calculateTourCost(newTour, distanceMatrix);
            if (newCost < bestCost) {
                bestTour = newTour;
                bestCost = newCost;
            }
        }

        return bestTour;
    }

}
