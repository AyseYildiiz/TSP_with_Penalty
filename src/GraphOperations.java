import java.util.*;
public class GraphOperations {
    static class Edge {
        int from, to;
        double weight;

        public Edge(int from, int to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
    static class Graph {
        int n;
        double[][] adjMatrix;

        Graph(int n) {
            this.n = n;
            adjMatrix = new double[n][n];
        }

        void addEdge(int u, int v, double w) {
            adjMatrix[u][v] = w;
            adjMatrix[v][u] = w;
        }

        double getWeight(int u, int v) {
            return adjMatrix[u][v];
        }

        List<Integer>[] getAdjList(double[][] matrix) {
            List<Integer>[] adj = new List[n];
            for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (matrix[i][j] > 0)
                        adj[i].add(j);
            return adj;
        }
    }
}
