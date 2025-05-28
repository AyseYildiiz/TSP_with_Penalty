import java.util.*;


public class Blossom {
    private int n;
    private double[][] graph;
    private int[] match, parent, base;
    private boolean[] used, blossom;
    private Queue<Integer> queue;

    public Blossom(double[][] graph) {
        this.n = graph.length;
        this.graph = graph;
        this.match = new int[n];
        Arrays.fill(match, -1);
    }

    private int lca(int a, int b) {
        boolean[] visited = new boolean[n];
        while (true) {
            a = base[a];
            visited[a] = true;
            if (match[a] == -1) break;
            a = parent[match[a]];
        }
        while (true) {
            b = base[b];
            if (visited[b]) return b;
            if (match[b] == -1) break;
            b = parent[match[b]];
        }
        return -1;
    }

    private void markPath(int v, int b, int child) {
        while (base[v] != b) {
            blossom[base[v]] = blossom[base[match[v]]] = true;
            parent[v] = child;
            child = match[v];
            v = parent[match[v]];
        }
    }

    private int findPath(int root) {
        used = new boolean[n];
        parent = new int[n];
        base = new int[n];
        for (int i = 0; i < n; i++) base[i] = i;

        queue = new LinkedList<>();
        queue.add(root);
        used[root] = true;

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int u = 0; u < n; u++) {
                if (graph[v][u] >= 1e9 || base[v] == base[u] || match[v] == u) continue;
                if (u == root || (match[u] != -1 && parent[match[u]] != -1)) {
                    int curBase = lca(v, u);
                    blossom = new boolean[n];
                    markPath(v, curBase, u);
                    markPath(u, curBase, v);
                    for (int i = 0; i < n; i++) {
                        if (blossom[base[i]]) {
                            base[i] = curBase;
                            if (!used[i]) {
                                used[i] = true;
                                queue.add(i);
                            }
                        }
                    }
                } else if (parent[u] == -1) {
                    parent[u] = v;
                    if (match[u] == -1) return u;
                    u = match[u];
                    used[u] = true;
                    queue.add(u);
                }
            }
        }
        return -1;
    }

    private boolean augment(int start) {
        int finish = findPath(start);
        if (finish == -1) return false;

        int u = finish;
        while (u != -1) {
            int pv = parent[u];
            int ppv = match[pv];
            match[u] = pv;
            match[pv] = u;
            u = ppv;
        }
        return true;
    }

    public int[] getMatching() {
        for (int i = 0; i < n; i++) {
            if (match[i] == -1) augment(i);
        }
        return match;
    }

    public static List<List<Integer>> minimumWeightPerfectMatching(List<Integer> odds, List<City> cities) {
        int n = odds.size();
        double[][] weights = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j)
                    weights[i][j] = cities.get(odds.get(i)).distanceTo(cities.get(odds.get(j)));
                else
                    weights[i][j] = 1e9;
            }
        }
        Blossom bm = new Blossom(weights);
        int[] matching = bm.getMatching();
        List<List<Integer>> matchEdges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i < matching[i]) {
                matchEdges.add(Arrays.asList(odds.get(i), odds.get(matching[i])));
            }
        }
        return matchEdges;
    }
}


