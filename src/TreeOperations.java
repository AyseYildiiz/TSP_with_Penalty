import java.util.*;

class TreeOperations {
    public static LinkedList<City> deleteMin(LinkedList<City> cities){
        LinkedList<City> min = new LinkedList<>();


    }

    public static List<List<Integer>> getMST() {
        LinkedList<City> tour = City.cities;
        int n = tour.size();
        double[] distances = new double[n];
        boolean[] visited = new boolean[n];
        int[] from = new int[n];

        Arrays.fill(distances, Double.MAX_VALUE);
        distances[0] = 0;
        Arrays.fill(from, -1);


        for (int i = 0; i < n; i++) {
            int u = -1;
            double minDistance = Double.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                if (!visited[j] && distances[j] < minDistance) {
                    minDistance = distances[j];
                    u = j;
                }
            }

            if (u == -1) {
                throw new IllegalArgumentException("Graph is not connected!");
            }

            visited[u] = true;

            for (int v = 0; v < n; v++) {
                if (!visited[v]) {
                    double d = tour.get(u).distanceTo(tour.get(v));
                    if (d < distances[v]) {
                        distances[v] = d;
                        from[v] = u;
                    }
                }
            }
        }






        return tree;
    }


    public static void preorderDFS(int u, List<List<Integer>> tree, boolean[] visited, List<Integer> tour) {
        visited[u] = true;
        tour.add(u);
        for (int v : tree.get(u)) {
            if (!visited[v]) {
                preorderDFS(v, tree, visited, tour);
            }
        }
    }
    public static void preorderBFS(int u, List<List<Integer>> tree, boolean[] visited, List<Integer> tour) {
        int n = tree.size();
        boolean[] visitBefore = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();

        for (int i = 0; i < n; i++) {
            if (!visitBefore[i]) {
                queue.add(i);
                visitBefore[i] = true;

                while (!queue.isEmpty()) {
                    int current = queue.poll();
                    System.out.print(current + " ");

                    for (int neighbor : tree.get(current)) {
                        if (!visitBefore[neighbor]) {
                            visitBefore[neighbor] = true;
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
    }


    public static List<Integer> getOddDegreeVertices(List<List<Integer>> tree) {
        List<Integer> oddVertices = new ArrayList<>();

        for (int i = 0; i < tree.size(); i++) {
            if (tree.get(i).size() % 2 != 0) {
                oddVertices.add(i);
            }
        }

        return oddVertices;
    }

    public static List<List<Integer>> combineTrees(List<List<Integer>> mst, List<List<Integer>> matching) {
        int n = mst.size();
        List<List<Integer>> combined = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            combined.add(new ArrayList<>());
            combined.get(i).addAll(mst.get(i)); // MST kenarlarını ekle
        }

        for (int u = 0; u < n; u++) {
            for (int v : matching.get(u)) {
                if (!combined.get(u).contains(v)) {
                    combined.get(u).add(v);
                }
            }
        }

        return combined;
    }

}