import java.util.ArrayList;
import java.util.List;

public class TwiceAroundTheTree {
public static List<City> twiceAroundTree(List<City> cities) {
    List<List<Integer>> mstTree = TreeOperations.getMST(cities);
    List<Integer> preorderTour = new ArrayList<>();
    boolean[] visited = new boolean[cities.size()];
    TreeOperations.preorderDFS(0, mstTree, visited, preorderTour);
    List<City> tour = new ArrayList<>();
    for (int i : preorderTour) {
        tour.add(cities.get(i));
    }
    tour.add(cities.get(preorderTour.getFirst()));
    return tour;
}
}