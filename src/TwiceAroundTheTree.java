import java.util.*;

public class TwiceAroundTheTree {
    public static List<Integer> approximateTSPTour(int[][] graph) {
        int n = graph.length;

        int[][] mstEdges = TreeOperations.primMST();

        List<List<Integer>> adj = TreeOperations.buildAdjacencyList(mstEdges, n);

        boolean[][] visitedEdges = new boolean[n][n];
        List<Integer> eulerianTour = new ArrayList<>();
        TreeOperations.dfsEulerian(0, adj, visitedEdges, eulerianTour);
        Collections.reverse(eulerianTour);

        Set<Integer> visited = new HashSet<>();
        List<Integer> tspTour = new ArrayList<>();

        for (int node : eulerianTour) {
            if (!visited.contains(node)) {
                visited.add(node);
                tspTour.add(node);
            }
        }

        tspTour.add(tspTour.getFirst());

        return tspTour;
    }

}