import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class City {
    public double x, y;
    public int id;
    public static int penalty;

    // Şehir listesi: sabit boyutlu erişim için ArrayList
    public static final List<City> cities = new ArrayList<>();

    // Sabit: büyük örneklerde matrix kullanılmaz
    public static final int LARGE_INSTANCE_THRESHOLD = 5000;

    // Mesafe matrisi (küçük örnekler için)
    public static int[][] distancesMatrix;

    // Dinamik mesafe önbelleği
    public static final Map<Long, Integer> distanceCache = new HashMap<>();
    public static final int MAX_CACHE_SIZE = 10_000_000;

    // Yapıcı
    public City(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        cities.add(this);
    }

    public int getId() {
        return id;
    }

    public static void setPenalty(int value) {
        penalty = value;
    }

    public static List<City> getCities() {
        return cities;
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
            System.out.println("✅ Distance matrix allocated successfully.");
        } catch (OutOfMemoryError e) {
            System.err.println("❌ Out of memory! Cannot create " + n + "x" + n + " matrix.");
            distancesMatrix = null;
            return;
        }

        System.out.println("🚀 Computing distances...");
        long startTime = System.currentTimeMillis();

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

    public static void createDistancesMatrixOptimized() {
        distanceCache.clear();
        if (cities.size() > LARGE_INSTANCE_THRESHOLD) {
            distancesMatrix = null;
            System.out.println("⚠️ Large instance detected, skipping matrix creation.");
        } else {
            createDistancesMatrix();
        }
    }

    public static int getDistance(int i, int j) {
        if (distancesMatrix != null) {
            return distancesMatrix[i][j];
        } else {
            long key = ((long) Math.min(i, j) << 32) | Math.max(i, j);
            Integer cached = distanceCache.get(key);
            if (cached != null) return cached;

            int d = cities.get(i).distanceTo(cities.get(j));

            if (distanceCache.size() > MAX_CACHE_SIZE) {
                System.out.println("♻️ Distance cache too large, clearing...");
                distanceCache.clear();
            }

            distanceCache.put(key, d);
            return d;
        }
    }

    public static int calculateTourCost(List<Integer> tour, int[][] graph) {
        int n = cities.size();

        int visitedCount = (tour.size() > 1 && tour.get(0).equals(tour.get(tour.size() - 1)))
                ? tour.size() - 1
                : tour.size();

        int cost = 0;
        for (int i = 0; i < visitedCount; i++) {
            int u = tour.get(i);
            int v = (i == visitedCount - 1) ? tour.get(0) : tour.get(i + 1);
            cost += (graph == null) ? getDistance(u, v) : graph[u][v];
        }

        int skipped = n - visitedCount;
        cost += skipped * penalty;

        return cost;
    }
}
