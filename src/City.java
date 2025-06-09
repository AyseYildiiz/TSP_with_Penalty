import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// City class representing TSP cities with distance calculations and caching
public class City {
    public double x, y;
    public int id;
    public static int penalty;

    // City list for fixed-size access
    public static final List<City> cities = new ArrayList<>();

    // Threshold for large instances - matrix not used above this size
    public static final int LARGE_INSTANCE_THRESHOLD = 5000;

    // Distance matrix for small instances
    public static int[][] distancesMatrix;

    // Dynamic distance cache for large instances
    public static final Map<Long, Integer> distanceCache = new HashMap<>();
    public static final int MAX_CACHE_SIZE = 10_000_000;

    // Constructor for creating new city // Constructor for creating new city
    public City(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        cities.add(this);
    }

    // Get city ID
    public int getId() {
        return id;
    }

    // Set penalty value for unvisited cities
    public static void setPenalty(int value) {
        penalty = value;
    }

    // Get list of all cities
    public static List<City> getCities() {
        return cities;
    }

    // Calculate Euclidean distance to another city // Calculate Euclidean distance
    // to another city
    public int distanceTo(City other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
    }

    // Create distance matrix for all cities // Create distance matrix for all
    // cities
    public static void createDistancesMatrix() {
        int n = cities.size();

        try {
            distancesMatrix = new int[n][n];
            System.out.println("✅ Distance matrix allocated successfully.");
        } catch (OutOfMemoryError e) {
            System.err.println("❌ Out of memory! Cannot create " + n + "x" + n + " matrix.");
            distancesMatrix = null;
            return;
        }

        System.out.println("🚀 Computing distances...");
        long startTime = System.currentTimeMillis();

        // Fill distance matrix with progress tracking
        for (int i = 0; i < n; i++) {
            if (i % 1000 == 0 && i > 0) {
                long elapsed = System.currentTimeMillis() - startTime;
                double progress = (double) i / n * 100;
                long estimated = (long) (elapsed / progress * 100);
                System.out.printf("   Progress: %.1f%% (%d/%d) - ETA: %.1f seconds\n",
                        progress, i, n, (estimated - elapsed) / 1000.0);
            }

            for (int j = 0; j < n; j++) {
                distancesMatrix[i][j] = cities.get(i).distanceTo(cities.get(j));
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("✅ Distance matrix completed in " + (totalTime / 1000.0) + " seconds");
    }

    // Create optimized distance matrix based on instance size // Create optimized
    // distance matrix based on instance size
    public static void createDistancesMatrixOptimized() {
        distanceCache.clear();
        if (cities.size() > LARGE_INSTANCE_THRESHOLD) {
            distancesMatrix = null;
            System.out.println("⚠️ Large instance detected, skipping matrix creation.");
        } else {
            createDistancesMatrix();
        }
    }

    // Get distance between two cities with caching for large instances // Get
    // distance between two cities with caching for large instances
    public static int getDistance(int i, int j) {
        if (distancesMatrix != null) {
            return distancesMatrix[i][j];
        } else {
            // Use cache for large instances
            long key = ((long) Math.min(i, j) << 32) | Math.max(i, j);
            Integer cached = distanceCache.get(key);
            if (cached != null)
                return cached;

            int d = cities.get(i).distanceTo(cities.get(j));

            // Clear cache if it gets too large
            if (distanceCache.size() > MAX_CACHE_SIZE) {
                System.out.println("♻️ Distance cache too large, clearing...");
                distanceCache.clear();
            }

            distanceCache.put(key, d);
            return d;
        }
    }

    // Calculate total cost of a tour including penalty for unvisited cities //
    // Calculate total cost of a tour including penalty for unvisited cities
    public static int calculateTourCost(List<Integer> tour, int[][] graph) {
        int n = cities.size();

        // Calculate number of visited cities
        int visitedCount = (tour.size() > 1 && tour.get(0).equals(tour.get(tour.size() - 1)))
                ? tour.size() - 1
                : tour.size();

        // Calculate distance cost
        int cost = 0;
        for (int i = 0; i < visitedCount; i++) {
            int u = tour.get(i);
            int v = (i == visitedCount - 1) ? tour.get(0) : tour.get(i + 1);
            cost += (graph == null) ? getDistance(u, v) : graph[u][v];
        }

        // Add penalty for unvisited cities
        int skipped = n - visitedCount;
        cost += skipped * penalty;

        return cost;
    }
}
