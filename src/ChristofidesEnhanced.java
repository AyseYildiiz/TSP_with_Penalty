import java.util.*;

class ChristofidesEnhanced {
    /**
     * Enhanced Christofides algorithm that works for large instances
     * with 2-opt/3-opt optimization and penalty-aware processing
     */
    public static List<Integer> getOptimizedChristofidesTour(boolean isLargeInstance) {
        System.out.println("Running Enhanced Christofides algorithm...");
        long startTime = System.currentTimeMillis();

        try {
            // For very large instances, use high-quality heuristic instead of full
            // Christofides
            List<Integer> initialTour;
            if (City.cities.size() > 5000) {
                System.out.println("├─ Using high-quality heuristic for very large instance");
                initialTour = getHighQualityInitialTour();
            } else {
                // Step 1: Get basic Christofides tour for smaller instances
                initialTour = getChristofidesTour();
            }

            long initialTime = System.currentTimeMillis() - startTime;
            System.out.println("├─ Initial tour completed in " + (initialTime / 1000.0) + "s");

            if (initialTour == null || initialTour.size() < 3) {
                System.out.println("├─ Initial tour failed, falling back to nearest neighbor");
                return NearestNeighbour.approximateTSPTour(City.distancesMatrix);
            }

            // Step 2: Apply local optimization based on instance size
            List<Integer> optimizedTour = initialTour;
            if (isLargeInstance) {
                // For large instances, use lighter optimization
                System.out.println("├─ Applying limited 2-opt optimization...");
                optimizedTour = TwoOpt.improveTour(optimizedTour, City.distancesMatrix);
                long optTime = System.currentTimeMillis() - startTime - initialTime;
                System.out.println("├─ 2-opt completed in " + (optTime / 1000.0) + "s");
            } else {
                // For smaller instances, use full optimization
                System.out.println("├─ Applying 2-opt optimization...");
                optimizedTour = TwoOpt.improveTour(optimizedTour, City.distancesMatrix);
                long twoOptTime = System.currentTimeMillis() - startTime - initialTime;
                System.out.println("├─ 2-opt completed in " + (twoOptTime / 1000.0) + "s");

                if (City.cities.size() <= 1000) { // Only for smaller instances
                    System.out.println("├─ Applying 3-opt optimization...");
                    optimizedTour = ThreeOpt.improveTour(optimizedTour, City.distancesMatrix);
                    long threeOptTime = System.currentTimeMillis() - startTime - initialTime - twoOptTime;
                    System.out.println("├─ 3-opt completed in " + (threeOptTime / 1000.0) + "s");
                }
            }

            // Step 3: Apply penalty-aware pruning
            System.out.println("├─ Applying penalty-aware optimization...");
            List<Integer> finalTour;
            if (isLargeInstance) {
                // Use lighter pruning for large instances
                finalTour = TourUtils.pruneTourWithPenalty(optimizedTour, City.distancesMatrix);
            } else {
                finalTour = TourUtils.advancedPruning(optimizedTour, City.distancesMatrix);
            }

            long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("└─ Enhanced Christofides completed in " + (totalTime / 1000.0) + "s total");

            return finalTour;

        } catch (OutOfMemoryError e) {
            System.out.println("├─ Out of memory in Christofides, using nearest neighbor heuristic");
            return NearestNeighbour.approximateTSPTour(City.distancesMatrix);
        } catch (Exception e) {
            System.out.println("├─ Christofides failed: " + e.getMessage());
            System.out.println("├─ Falling back to nearest neighbor");
            return NearestNeighbour.approximateTSPTour(City.distancesMatrix);
        }
    }

    /**
     * Enhanced Christofides algorithm that can handle null distance matrix
     */
    public static List<Integer> getChristofidesTour() {
        // Check if we can use the matrix-based approach
        if (City.distancesMatrix != null) {
            return getMatrixBasedChristofidesTour();
        } else {
            return getOnDemandChristofidesTour();
        }
    }

    /**
     * Traditional Christofides using distance matrix
     */
    private static List<Integer> getMatrixBasedChristofidesTour() {
        int[][] mst = TreeOperations.primMST();
        List<List<Integer>> adjacencyList = TreeOperations.buildAdjacencyList(mst, City.distancesMatrix.length);
        List<Integer> odd = TreeOperations.getOddDegreeVertices(mst, City.distancesMatrix.length);
        List<List<Integer>> blossom = Blossom.minimumWeightPerfectMatching(odd, City.cities);
        List<List<Integer>> multigraph = TreeOperations.combineTrees(adjacencyList, blossom);
        boolean[][] visitedEdges = new boolean[City.distancesMatrix.length][City.distancesMatrix.length];
        List<Integer> eulerianTour = new ArrayList<>();
        TreeOperations.dfsEulerian(0, multigraph, visitedEdges, eulerianTour);
        return TreeOperations.makeHamiltonianTour(eulerianTour);
    }

    /**
     * Christofides using on-demand distance calculation for large instances
     */
    private static List<Integer> getOnDemandChristofidesTour() {
        int numCities = City.cities.size();

        // For very large instances, create a simplified MST using sampling
        if (numCities > 5000) {
            return getApproximateChristofidesTour(numCities);
        }
        // Create a virtual distance matrix for medium-large instances
        // We'll compute distances on-demand
        int[][] virtualMatrix = new int[numCities][numCities];

        // Fill matrix on-demand with caching
        for (int i = 0; i < numCities; i++) {
            for (int j = i; j < numCities; j++) {
                if (i == j) {
                    virtualMatrix[i][j] = 0;
                } else {
                    int dist = City.getDistance(i, j);
                    virtualMatrix[i][j] = dist;
                    virtualMatrix[j][i] = dist;
                }
            }

            // Progress update for large computations
            if (numCities > 1000 && i % 500 == 0) {
                System.out.println("├─ MST preparation: " + (i * 100 / numCities) + "% complete");
            }
        }

        // Temporarily set the matrix for tree operations
        int[][] originalMatrix = City.distancesMatrix;
        City.distancesMatrix = virtualMatrix;

        try {
            // Run standard Christofides
            return getMatrixBasedChristofidesTour();
        } finally {
            // Restore original matrix state
            City.distancesMatrix = originalMatrix;
        }
    }

    /**
     * Approximate Christofides for very large instances using sampling
     */
    private static List<Integer> getApproximateChristofidesTour(int numCities) {
        System.out.println("├─ Using approximate Christofides for very large instance");

        List<Integer> tour = NearestNeighbour.approximateTSPTour(null);

        // Apply limited 2-opt to improve tour quality (Christofides-like quality)
        if (tour != null && tour.size() > 3) {
            tour = applyLimited2Opt(tour, Math.min(1000, numCities / 10));
        }

        return tour;
    }

    /**
     * Limited 2-opt optimization for large instances
     */
    private static List<Integer> applyLimited2Opt(List<Integer> tour, int maxIterations) {
        if (tour.size() < 4)
            return tour;

        List<Integer> bestTour = new ArrayList<>(tour);
        int bestCost = City.calculateTourCost(bestTour, null);
        boolean improved = true;
        int iterations = 0;

        while (improved && iterations < maxIterations) {
            improved = false;

            // Sample edges instead of checking all combinations
            Random random = new Random(42 + iterations);
            for (int attempt = 0; attempt < Math.min(100, tour.size()); attempt++) {
                int i = random.nextInt(tour.size() - 1);
                int j = random.nextInt(tour.size() - 1);
                if (Math.abs(i - j) < 2)
                    continue; // Skip adjacent edges

                if (i > j) {
                    int temp = i;
                    i = j;
                    j = temp;
                }

                // Try 2-opt swap
                List<Integer> newTour = perform2OptSwap(bestTour, i, j);
                int newCost = City.calculateTourCost(newTour, null);

                if (newCost < bestCost) {
                    bestTour = newTour;
                    bestCost = newCost;
                    improved = true;
                }
            }

            iterations++;
            if (iterations % 100 == 0) {
                System.out.println("├─ 2-opt progress: " + iterations + "/" + maxIterations + " iterations");
            }
        }

        return bestTour;
    }

    /**
     * Perform 2-opt swap between positions i and j
     */
    private static List<Integer> perform2OptSwap(List<Integer> tour, int i, int j) {
        List<Integer> newTour = new ArrayList<>();

        // Add cities from start to i
        for (int k = 0; k <= i; k++) {
            newTour.add(tour.get(k));
        }

        // Add cities from j to i+1 in reverse order
        for (int k = j; k > i; k--) {
            newTour.add(tour.get(k));
        }

        // Add remaining cities
        for (int k = j + 1; k < tour.size(); k++) {
            newTour.add(tour.get(k));
        }

        return newTour;
    }

    /**
     * Generate a high-quality initial tour for very large instances
     * Combines multiple heuristics to get Christofides-like quality
     */
    private static List<Integer> getHighQualityInitialTour() {
        System.out.println("├─ Building high-quality initial tour...");

        // Try multiple approaches and pick the best
        List<Integer> bestTour = null;
        int bestCost = Integer.MAX_VALUE;

        // Approach 1: Multiple nearest neighbor starts
        for (int start = 0; start < Math.min(5, City.cities.size()); start++) {
            List<Integer> tour = NearestNeighbour.approximateTSPTourFromStart(start, City.distancesMatrix);
            if (tour != null) {
                int cost = City.calculateTourCost(tour, City.distancesMatrix);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestTour = tour;
                }
            }
        } // Approach 2: Nearest neighbor heuristic
        List<Integer> nnTour = NearestNeighbour.approximateTSPTour(City.distancesMatrix);
        if (nnTour != null) {
            int cost = City.calculateTourCost(nnTour, City.distancesMatrix);
            if (cost < bestCost) {
                bestCost = cost;
                bestTour = nnTour;
            }
        }

        System.out.println("├─ Best initial tour cost: " + bestCost);
        return bestTour;
    }
}
