import java.util.*;

public class TwiceAroundTheTree {
    // Generate TSP tour using twice-around-the-tree algorithm
    // Provides 2-approximation by traversing MST and skipping repeated vertices
    public static List<Integer> approximateTSPTour(int[][] graph) {
        int n = graph.length;

        // Generate MST using Prim's algorithm
        int[][] mstEdges = TreeOperations.primMST(graph);

        // Convert MST to adjacency list for traversal
        List<List<Integer>> adj = TreeOperations.buildAdjacencyList(mstEdges, n);

        // Perform DFS to create Eulerian tour
        boolean[][] visitedEdges = new boolean[n][n];
        List<Integer> eulerianTour = new ArrayList<>();
        TreeOperations.dfsEulerian(0, adj, visitedEdges, eulerianTour);
        Collections.reverse(eulerianTour);

        // Convert Eulerian tour to Hamiltonian by skipping repeated vertices
        Set<Integer> visited = new HashSet<>();
        List<Integer> tspTour = new ArrayList<>();

        for (int node : eulerianTour) {
            if (!visited.contains(node)) {
                visited.add(node);
                tspTour.add(node);
            }
        }

        // Complete tour by returning to start
        tspTour.add(tspTour.get(0));

        return tspTour;
    }

}