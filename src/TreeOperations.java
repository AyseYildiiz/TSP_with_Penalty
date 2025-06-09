import java.util.*;

// Tree operations for Christofides algorithm implementation
// Includes MST generation, matching operations, and Eulerian tour conversion
class TreeOperations {
    // Find vertex with minimum key value not yet in MST
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
    } // Generate Minimum Spanning Tree using Prim's algorithm

    public static int[][] primMST(int[][] graph) {
        int n = graph.length;
        int[][] mst = new int[n][n];
        boolean[] visited = new boolean[n];
        int[] minEdge = new int[n];
        int[] parent = new int[n];
        Arrays.fill(minEdge, Integer.MAX_VALUE);
        minEdge[0] = 0;
        parent[0] = -1;

        // Build MST by selecting minimum weight edges
        for (int i = 0; i < n - 1; i++) {
            // Find unvisited vertex with minimum edge weight
            int u = -1;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && (u == -1 || minEdge[j] < minEdge[u])) {
                    u = j;
                }
            }

            visited[u] = true;

            // Update minimum edges for neighbors
            for (int v = 0; v < n; v++) {
                if (graph[u][v] != 0 && !visited[v] && graph[u][v] < minEdge[v]) {
                    minEdge[v] = graph[u][v];
                    parent[v] = u;
                }
            }
        }

        // Build MST matrix from parent relationships
        for (int i = 1; i < n; i++) {
            mst[i][parent[i]] = graph[i][parent[i]];
            mst[parent[i]][i] = graph[parent[i]][i];
        }

        return mst;
    } // Build adjacency list representation from MST edges

    public static List<List<Integer>> buildAdjacencyList(int[][] mstEdges, int n) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        // Add edges in both directions for undirected graph
        for (int[] edge : mstEdges) {
            int u = edge[0], v = edge[1];
            adj.get(u).add(v);
            adj.get(v).add(u);
        }
        return adj;
    } // Find vertices with odd degree in MST for perfect matching

    public static List<Integer> getOddDegreeVertices(int[][] mstEdges, int n) {
        int[] degree = new int[n]; // Track degree of each vertex

        // Calculate degree for each vertex
        for (int[] mstEdge : mstEdges) {
            int u = mstEdge[0];
            int v = mstEdge[1];
            degree[u]++;
            degree[v]++;
        }

        // Collect vertices with odd degree
        List<Integer> oddVertices = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (degree[i] % 2 != 0) {
                oddVertices.add(i);
            }
        }

        return oddVertices;
    } // Create complete graph from odd degree vertices for matching

    public static int[][] createOddDegreeGraph(List<Integer> oddVertices) {
        int n = oddVertices.size();
        int[][] oddGraph = new int[n][n];
        // Create complete graph with all pairwise distances
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int u = oddVertices.get(i);
                int v = oddVertices.get(j);
                oddGraph[i][j] = City.distancesMatrix[u][v];
                oddGraph[j][i] = City.distancesMatrix[u][v];
            }
        }
        return oddGraph;
    } // DFS traversal to find Eulerian tour in multigraph

    public static void dfsEulerian(int node, List<List<Integer>> adj, boolean[][] visitedEdges, List<Integer> tour) {
        // Visit all unvisited edges from current node
        for (int neighbor : adj.get(node)) {
            if (!visitedEdges[node][neighbor]) {
                visitedEdges[node][neighbor] = true;
                visitedEdges[neighbor][node] = true;
                dfsEulerian(neighbor, adj, visitedEdges, tour);
            }
        }
        tour.add(node);
    } // Convert Eulerian tour to Hamiltonian by skipping repeated vertices

    public static List<Integer> makeHamiltonianTour(List<Integer> eulerTour) {
        Collections.reverse(eulerTour);
        Set<Integer> visited = new HashSet<>();
        List<Integer> hamiltonianTour = new ArrayList<>();

        // Reverse to correct order after DFS traversal
        Collections.reverse(eulerTour);

        // Skip repeated vertices to create Hamiltonian tour
        for (int city : eulerTour) {
            if (!visited.contains(city)) {
                visited.add(city);
                hamiltonianTour.add(city);
            }
        }
        // Return to starting city to complete tour
        if (!hamiltonianTour.isEmpty()) {
            hamiltonianTour.add(hamiltonianTour.get(0));
        }

        return hamiltonianTour;
    } // Combine MST and perfect matching to create multigraph

    public static List<List<Integer>> combineTrees(List<List<Integer>> mst, List<List<Integer>> matching) {
        int n = City.distancesMatrix.length;
        List<List<Integer>> combined = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            combined.add(new ArrayList<>());
        }

        // Add edges from MST
        for (int u = 0; u < mst.size(); u++) {
            for (int v : mst.get(u)) {
                if (!combined.get(u).contains(v))
                    combined.get(u).add(v);
                if (!combined.get(v).contains(u))
                    combined.get(v).add(u);
            }
        }

        // Add edges from perfect matching
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
