import java.util.*;

class TreeOperations {
    public static int getMin(int[] key, boolean[] inMST) {
        int min_index = -1;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < inMST.length; i++) {
            if (!inMST[i] && min > key[i]) {
                min = key[i];
                min_index = i;
            }
        }
        return min_index;
    }

    public static int[][] primMST(int[][] graph) {
        int n = graph.length;
        int[][] mst = new int[n][n];
        boolean[] visited = new boolean[n];
        int[] minEdge = new int[n];
        int[] parent = new int[n];
        Arrays.fill(minEdge, Integer.MAX_VALUE);
        minEdge[0] = 0;
        parent[0] = -1;

        for (int i = 0; i < n - 1; i++) {
            int u = -1;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && (u == -1 || minEdge[j] < minEdge[u])) {
                    u = j;
                }
            }

            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (graph[u][v] != 0 && !visited[v] && graph[u][v] < minEdge[v]) {
                    minEdge[v] = graph[u][v];
                    parent[v] = u;
                }
            }
        }

        for (int i = 1; i < n; i++) {
            mst[i][parent[i]] = graph[i][parent[i]];
            mst[parent[i]][i] = graph[parent[i]][i];
        }

        return mst;
    }


    public static List<List<Integer>> buildAdjacencyList(int[][] mstEdges, int n) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : mstEdges) {
            int u = edge[0], v = edge[1];
            adj.get(u).add(v);
            adj.get(v).add(u);
        }
        return adj;
    }


    public static List<Integer> getOddDegreeVertices(int[][] mstEdges, int n) {
        int[] degree = new int[n];  // Her düğümün derecesini tutar

        for (int[] mstEdge : mstEdges) {
            int u = mstEdge[0];
            int v = mstEdge[1];
            degree[u]++;
            degree[v]++;
        }

        List<Integer> oddVertices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (degree[i] % 2 != 0) {
                oddVertices.add(i);
            }
        }

        return oddVertices;
    }

    public static int[][] createOddDegreeGraph(List<Integer> oddVertices) {
        int n = oddVertices.size();
        int[][] oddGraph = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int u = oddVertices.get(i);
                int v = oddVertices.get(j);
                oddGraph[i][j] = City.distancesMatrix[u][v];
                oddGraph[j][i] = City.distancesMatrix[u][v];
            }
        }
        return oddGraph;
    }

    public static void dfsEulerian(int node, List<List<Integer>> adj, boolean[][] visitedEdges, List<Integer> tour) {
        for (int neighbor : adj.get(node)) {
            if (!visitedEdges[node][neighbor]) {
                visitedEdges[node][neighbor] = true;
                visitedEdges[neighbor][node] = true;
                dfsEulerian(neighbor, adj, visitedEdges, tour);
            }
        }
        tour.add(node);
    }

    public static List<Integer> makeHamiltonianTour(List<Integer> eulerTour) {
        Collections.reverse(eulerTour);
        Set<Integer> visited = new HashSet<>();
        List<Integer> hamiltonianTour = new ArrayList<>();

        // Tersten eklenmiş olduğu için tersten gezip tekrar normale çevirebiliriz ya da direkt tersini alabiliriz
        Collections.reverse(eulerTour);

        for (int city : eulerTour) {
            if (!visited.contains(city)) {
                visited.add(city);
                hamiltonianTour.add(city);
            }
        }
        // Başlangıç noktasına dönmek için turun sonuna ilk şehri ekle
        if (!hamiltonianTour.isEmpty()) {
            hamiltonianTour.add(hamiltonianTour.get(0));
        }

        return hamiltonianTour;
    }

    public static List<List<Integer>> combineTrees(List<List<Integer>> mst, List<List<Integer>> matching) {
        int n = City.distancesMatrix.length;
        List<List<Integer>> combined = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            combined.add(new ArrayList<>());
        }

        // MST'den kenarları ekle
        for (int u = 0; u < mst.size(); u++) {
            for (int v : mst.get(u)) {
                if (!combined.get(u).contains(v))
                    combined.get(u).add(v);
                if (!combined.get(v).contains(u))
                    combined.get(v).add(u);
            }
        }

        // Blossom eşleşmelerini ekle
        for (List<Integer> edge : matching) {
            int u = edge.get(0);
            int v = edge.get(1);
            if (!combined.get(u).contains(v))
                combined.get(u).add(v);
            if (!combined.get(v).contains(u))
                combined.get(v).add(u);
        }

        return combined;
    }
}






//    public static void preorderDFS(int u, List<List<Integer>> tree, boolean[] visited, List<Integer> tour) {
//        visited[u] = true;
//        tour.add(u);
//        for (int v : tree.get(u)) {
//            if (!visited[v]) {
//                preorderDFS(v, tree, visited, tour);
//            }
//        }
//    }
//    public static void preorderBFS(int u, List<List<Integer>> tree, boolean[] visited, List<Integer> tour) {
//        int n = tree.size();
//        boolean[] visitBefore = new boolean[n];
//        Queue<Integer> queue = new LinkedList<>();
//
//        for (int i = 0; i < n; i++) {
//            if (!visitBefore[i]) {
//                queue.add(i);
//                visitBefore[i] = true;
//
//                while (!queue.isEmpty()) {
//                    int current = queue.poll();
//                    System.out.print(current + " ");
//
//                    for (int neighbor : tree.get(current)) {
//                        if (!visitBefore[neighbor]) {
//                            visitBefore[neighbor] = true;
//                            queue.add(neighbor);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//
//


