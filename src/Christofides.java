import java.util.*;

class Christofides {

    public static List<Integer> christofides(List<City> tour){


        // Step 1: Minimum Spanning Tree (MST)
        List<List<Integer>> mst = (TreeOperations.getMST(tour));

        // Step 2: Find odd degree vertices
        List<Integer> oddVertices = TreeOperations.getOddDegreeVertices(mst);

        // Step 3: Minimum Weight Perfect Matching (Greedy)
        List<List<Integer>> matching = Blossom.minimumWeightPerfectMatching(oddVertices,tour);

        // Step 4: Combine MST and Matching
        List<List<Integer>> multigraph = TreeOperations.combineTrees(mst,matching);

        // Step 5: Eulerian Tour
        List<Integer> eulerTour = findEulerianTour(multigraph);

        // Step 6: Make Hamiltonian circuit (TSP)
        return makeHamiltonian(eulerTour);
    }




// --------------------------------------------------------------------------------------------------------------
//    public static List<City> generateTour(List<City> cities) {
//        int n = cities.size();
//        double[][] dist = new double[n][n];
//        for (int i = 0; i < n; i++)
//            for (int j = 0; j < n; j++)
//                dist[i][j] = cities.get(i).distanceTo(cities.get(j));
//
//        boolean[] inMST = new boolean[n];
//        double[] key = new double[n];
//        int[] parent = new int[n];
//        Arrays.fill(key, Double.MAX_VALUE);
//        key[0] = 0;
//
//        for (int count = 0; count < n - 1; count++) {
//            double min = Double.MAX_VALUE;
//            int u = -1;
//            for (int v = 0; v < n; v++)
//                if (!inMST[v] && key[v] < min) {
//                    min = key[v];
//                    u = v;
//                }
//
//            if (u == -1) throw new RuntimeException("MST oluşturulurken bağlantısız düğüm bulundu.");
//            inMST[u] = true;
//            for (int v = 0; v < n; v++)
//                if (!inMST[v] && dist[u][v] < key[v]) {
//                    parent[v] = u;
//                    key[v] = dist[u][v];
//                }
//        }
//
//        Map<Integer, List<Integer>> mst = new HashMap<>();
//        for (int i = 1; i < n; i++) {
//            mst.computeIfAbsent(parent[i], k -> new ArrayList<>()).add(i);
//            mst.computeIfAbsent(i, k -> new ArrayList<>()).add(parent[i]);
//        }
//
//        int[] degree = new int[n];
//        for (Map.Entry<Integer, List<Integer>> entry : mst.entrySet()) {
//            for (int v : entry.getValue()) {
//                degree[entry.getKey()]++;
//                degree[v]++;
//            }
//        }
//
//        List<Integer> oddVertices = new ArrayList<>();
//        for (int i = 0; i < n; i++)
//            if (degree[i] % 2 == 1) oddVertices.add(i);
//
//        List<int[]> matching = Blossom.minimumWeightPerfectMatching(dist, oddVertices);
//        for (int[] edge : matching) {
//            int u = edge[0], v = edge[1];
//            mst.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
//            mst.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
//        }
//
//        Stack<Integer> stack = new Stack<>();
//        List<Integer> eulerTour = new ArrayList<>();
//        Map<Integer, Deque<Integer>> localGraph = new HashMap<>();
//        for (Map.Entry<Integer, List<Integer>> entry : mst.entrySet()) {
//            localGraph.put(entry.getKey(), new ArrayDeque<>(entry.getValue()));
//        }
//
//        stack.push(0);
//        while (!stack.isEmpty()) {
//            int v = stack.peek();
//            if (localGraph.containsKey(v) && !localGraph.get(v).isEmpty()) {
//                int u = localGraph.get(v).poll();
//                localGraph.get(u).remove(v);
//                stack.push(u);
//            } else {
//                eulerTour.add(stack.pop());
//            }
//        }
//
//        List<City> tour = new ArrayList<>();
//        Set<Integer> visited = new HashSet<>();
//        for (int id : eulerTour) {
//            if (visited.add(id)) {
//                tour.add(cities.get(id));
//            }
//        }
//
//        return tour;
//    }
}