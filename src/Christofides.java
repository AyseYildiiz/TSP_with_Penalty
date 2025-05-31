import java.util.*;

class Christofides {
    public static List<Integer> getChristofidesTour() {
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
}