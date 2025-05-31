import java.util.*;

public class Blossom {

    public static List<List<Integer>> minimumWeightPerfectMatching(List<Integer> odds, List<City> cities) {
        int n = odds.size();
        double[][] graph = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                graph[i][j] = (i != j) ? cities.get(odds.get(i)).distanceTo(cities.get(odds.get(j))) : 1e9;
            }
        }

        int[] match = new int[n];
        Arrays.fill(match, -1);

        for (int i = 0; i < n; i++) {
            if (match[i] == -1) {
                augment(i, match, graph);
            }
        }

        List<List<Integer>> matchEdges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i < match[i]) {
                matchEdges.add(Arrays.asList(odds.get(i), odds.get(match[i])));
            }
        }
        return matchEdges;
    }

    private static void augment(int start, int[] match, double[][] graph) {
        int n = graph.length;
        int[] parent = new int[n];
        int[] base = new int[n];
        boolean[] used = new boolean[n];
        boolean[] blossom = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();

        Arrays.fill(parent, -1);
        for (int i = 0; i < n; i++) base[i] = i;

        queue.add(start);
        used[start] = true;

        int finish = -1;

        while (!queue.isEmpty()) {
            int v = queue.poll();
            for (int u = 0; u < n; u++) {
                if (graph[v][u] >= 1e9 || base[v] == base[u] || match[v] == u) continue;
                if (u == start || (match[u] != -1 && parent[match[u]] != -1)) {
                    int curBase = lca(v, u, match, parent, base);
                    Arrays.fill(blossom, false);
                    markPath(v, curBase, u, match, parent, base, blossom);
                    markPath(u, curBase, v, match, parent, base, blossom);
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
                    if (match[u] == -1) {
                        finish = u;
                        break;
                    }
                    int m = match[u];
                    used[m] = true;
                    queue.add(m);
                }
            }
            if (finish != -1) break;
        }

        if (finish == -1) return;

        int u = finish;
        while (u != -1) {
            int pv = parent[u];
            int ppv = match[pv];
            match[u] = pv;
            match[pv] = u;
            u = ppv;
        }
    }

    private static int lca(int a, int b, int[] match, int[] parent, int[] base) {
        int n = match.length;
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

    private static void markPath(int v, int b, int child, int[] match, int[] parent, int[] base, boolean[] blossom) {
        while (base[v] != b) {
            blossom[base[v]] = true;
            blossom[base[match[v]]] = true;
            parent[v] = child;
            child = match[v];
            v = parent[match[v]];
        }
    }
}