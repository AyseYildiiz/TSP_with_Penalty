import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// ADD PRIVATE FİELDS AND GETTER SETTER FOR ENCAPSULATION MAYBE

public class City {
    double x, y;
    int id;
    static int penalty;
    static LinkedList<City> cities = new LinkedList<City>();
    static int[][] distancesMatrix;
    // Cache for on-demand distance calculations (for large instances)
    static Map<Long, Integer> distanceCache = new HashMap<>();

    public City(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        cities.add(this);

    }

    public int distanceTo(City other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
    }

    public static void createDistancesMatrix() {
        int n = cities.size();

        try {
            distancesMatrix = new int[n][n];
            System.out.println(" Distance matrix allocated successfully");
        } catch (OutOfMemoryError e) {
            System.err.println("  Out of memory! Cannot create " + n + "x" + n + " matrix");
            throw e;
        }

        // Fill matrix with progress indication
        System.out.println(" Computing distances...");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < n; i++) {
            // Show progress every 1000 cities
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
        System.out.println(" Distance matrix completed in " + (totalTime / 1000.0) + " seconds");
    }

    public static int calculateTourCost(List<Integer> tour, int[][] graph) {
        int n = City.cities.size();

        int visitedCount;
        if (tour.size() > 1 && tour.get(0).equals(tour.get(tour.size() - 1))) {
            visitedCount = tour.size() - 1;
        } else {
            visitedCount = tour.size();
        }

        int cost = 0;
        for (int i = 0; i < visitedCount; i++) {
            int u = tour.get(i);
            int v = (i == visitedCount - 1) ? tour.get(0) : tour.get(i + 1);

            // Use on-demand calculation if matrix is null
            if (graph == null) {
                cost += getDistance(u, v);
            } else {
                cost += graph[u][v];
            }
        }

        int skipped = n - visitedCount;
        cost += skipped * City.penalty;

        return cost;
    }
    // Caching Mechanism for large instances

    public static int getDistance(int cityI, int cityJ) {
        if (distancesMatrix != null) {
            return distancesMatrix[cityI][cityJ];
        } else {
            // For large instances, use cache to avoid recalculating same distances
            long key = ((long) Math.min(cityI, cityJ) << 32) | Math.max(cityI, cityJ);

            Integer cachedDistance = distanceCache.get(key);
            if (cachedDistance != null) {
                return cachedDistance;
            }

            // Calculate and cache the distance
            int distance = cities.get(cityI).distanceTo(cities.get(cityJ));
            distanceCache.put(key, distance);
            return distance;
        }
    }

    public static boolean isLargeInstance() {
        return cities.size() > 5000;
    }

    public static void createDistancesMatrixOptimized() {
        // Clear cache for fresh start
        distanceCache.clear();

        if (isLargeInstance()) {
            distancesMatrix = null;
            return;
        } else {
            createDistancesMatrix();
        }
    }

}