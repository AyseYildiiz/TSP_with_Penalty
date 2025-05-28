import java.util.*;

interface TSPSolver {
    List<City> solve(List<City> cities);
    double totalDistance(List<City> tour);
}

//class Utils {
//    public static double calculateTourDistance(List<City> tour) {
//        double total = 0;
//        for (int i = 0; i < tour.size(); i++) {
//            City from = tour.get(i);
//            City to = tour.get((i + 1) % tour.size());
//            total += from.distanceTo(to);
//        }
//        return total;
//    }
//}

//class Blossom {
//    static class Edge implements Comparable<Edge> {
//        int u, v;
//        double weight;
//
//        Edge(int u, int v, double weight) {
//            this.u = u;
//            this.v = v;
//            this.weight = weight;
//        }
//
//        public int compareTo(Edge other) {
//            return Double.compare(this.weight, other.weight);
//        }
//    }
//
//    public static List<int[]> minimumWeightPerfectMatching(double[][] cost, List<Integer> nodes) {
//        int n = cost.length;
//        int[] match = new int[n];
//        Arrays.fill(match, -1);
//
//        PriorityQueue<Edge> pq = new PriorityQueue<>();
//        for (int i = 0; i < nodes.size(); i++) {
//            for (int j = i + 1; j < nodes.size(); j++) {
//                int u = nodes.get(i);
//                int v = nodes.get(j);
//                pq.add(new Edge(u, v, cost[u][v]));
//            }
//        }
//
//        List<int[]> matching = new ArrayList<>();
//        while (!pq.isEmpty()) {
//            Edge e = pq.poll();
//            if (match[e.u] == -1 && match[e.v] == -1) {
//                match[e.u] = e.v;
//                match[e.v] = e.u;
//                matching.add(new int[]{e.u, e.v});
//            }
//        }
//
//        return matching;
//    }
//}



class ChristofidesSolver implements TSPSolver {
    @Override
    public List<City> solve(List<City> cities) {
        return Christofides.generateTour(cities);
    }

    @Override
    public double totalDistance(List<City> tour) {
        return City.calculateTourDistance(tour);
    }
}

class TwiceAroundTreeSolver implements TSPSolver {
    @Override
    public List<City> solve(List<City> cities) {
        int n = cities.size();
        double[][] dist = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                dist[i][j] = cities.get(i).distanceTo(cities.get(j));

        boolean[] inMST = new boolean[n];
        double[] key = new double[n];
        int[] parent = new int[n];
        Arrays.fill(key, Double.MAX_VALUE);
        key[0] = 0;

        for (int count = 0; count < n - 1; count++) {
            double min = Double.MAX_VALUE;
            int u = -1;
            for (int v = 0; v < n; v++)
                if (!inMST[v] && key[v] < min) {
                    min = key[v];
                    u = v;
                }

            inMST[u] = true;
            for (int v = 0; v < n; v++)
                if (!inMST[v] && dist[u][v] < key[v]) {
                    key[v] = dist[u][v];
                    parent[v] = u;
                }
        }

        Map<Integer, List<Integer>> tree = new HashMap<>();
        for (int i = 1; i < n; i++) {
            tree.computeIfAbsent(parent[i], k -> new ArrayList<>()).add(i);
            tree.computeIfAbsent(i, k -> new ArrayList<>()).add(parent[i]);
        }

        List<Integer> preorder = new ArrayList<>();
        boolean[] visited = new boolean[n];
        dfs(0, tree, visited, preorder);

        List<City> tour = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        for (int id : preorder) {
            if (seen.add(id)) tour.add(cities.get(id));
        }
        tour.add(cities.getFirst());
        return tour;
    }

    private void dfs(int node, Map<Integer, List<Integer>> tree, boolean[] visited, List<Integer> result) {
        visited[node] = true;
        result.add(node);
        for (int neighbor : tree.getOrDefault(node, new ArrayList<>())) {
            if (!visited[neighbor]) dfs(neighbor, tree, visited, result);
        }
    }

    @Override
    public double totalDistance(List<City> tour) {
        return City.calculateTourDistance(tour);
    }
}

class Hybrid2Solver implements TSPSolver {
    @Override
    public List<City> solve(List<City> cities) {
        TSPSolver twiceAroundTree = new TwiceAroundTreeSolver();
        TSPSolver christofides = new ChristofidesSolver();

        List<City> tour = twiceAroundTree.solve(cities);
        tour = christofides.solve(tour);
        return tour;
    }

    @Override
    public double totalDistance(List<City> tour) {
        return City.calculateTourDistance(tour);
    }
}

public class AlternativeTsp {
    public static void main(String[] args) {
        List<City> cities = Arrays.asList(
                new City(0, 0, 0),
                new City(1, 1, 3),
                new City(2, 4, 3),
                new City(3, 6, 1),
                new City(4, 3, 0)
        );

        List<TSPSolver> solvers = Arrays.asList(
                new ChristofidesSolver(),
                new TwiceAroundTreeSolver(),
                new Hybrid2Solver()
        );

        for (TSPSolver solver : solvers) {
            List<City> tour = solver.solve(cities);
            double distance = solver.totalDistance(tour);
            System.out.println("\n" + solver.getClass().getSimpleName() + " Tour:");
            for (City city2 : tour) {
                System.out.print(city2.id + " ");
            }
            System.out.println("\nTotal Distance: " + distance);
        }
    }
}