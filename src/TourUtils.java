import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TourUtils {
    public static List<Integer> pruneTourWithPenalty(List<Integer> fullTour, int[][] graph) {
        List<Integer> currentTour = new ArrayList<>(fullTour);
        Set<Integer> skippedCities = new HashSet<>();

        boolean improved = true;
        while (improved) {
            improved = false;
            int limit = currentTour.size() - 1;
            for (int idx = 1; idx < limit; idx++) {
                int cityToSkip = currentTour.get(idx);
                if (skippedCities.contains(cityToSkip))
                    continue;

                List<Integer> testTour = new ArrayList<>(currentTour);
                testTour.remove(idx);

                int currentCost = City.calculateTourCost(currentTour, graph);
                int testCost = City.calculateTourCost(testTour, graph);
                if (testCost < currentCost) {
                    currentTour = testTour;
                    skippedCities.add(cityToSkip);
                    improved = true;
                    break;
                }
            }
        }
        return currentTour;
    }

}
