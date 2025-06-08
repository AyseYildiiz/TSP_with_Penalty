import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TourUtils {
    /**
     * Basic pruning that removes cities one by one when beneficial
     */
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

    /**
     * Advanced pruning that considers sequences of cities and their impact on tour
     * cost. Enhanced version that intelligently prunes cities based on penalty and
     * distance analysis.
     */
    public static List<Integer> advancedPruning(List<Integer> tour, int[][] graph) {
        List<Integer> prunedTour = new ArrayList<>(tour);
        boolean improved = true;
        Set<Integer> skippedCities = new HashSet<>();

        // First pass: individual city removal
        while (improved) {
            improved = false;

            // Skip first and last cities if they're the same (closed tour)
            int startIdx = 1;
            int endIdx = prunedTour.size() - 1;
            if (prunedTour.size() > 1 && prunedTour.get(0).equals(prunedTour.get(prunedTour.size() - 1))) {
                endIdx = prunedTour.size() - 2; // Don't remove the last city if it's the same as first
            }

            // Try removing each city
            for (int i = startIdx; i < endIdx; i++) {
                int cityToRemove = prunedTour.get(i);
                if (skippedCities.contains(cityToRemove))
                    continue; // Calculate detour cost vs penalty
                int prev = prunedTour.get(i - 1);
                int next = prunedTour.get(i + 1);
                int detourCost;
                if (graph != null) {
                    detourCost = graph[prev][cityToRemove] + graph[cityToRemove][next] - graph[prev][next];
                } else {
                    detourCost = City.getDistance(prev, cityToRemove) + City.getDistance(cityToRemove, next)
                            - City.getDistance(prev, next);
                }

                // If penalty is less than the detour, remove the city
                if (City.penalty < detourCost) {
                    List<Integer> candidateTour = new ArrayList<>(prunedTour);
                    candidateTour.remove(i);

                    int originalCost = City.calculateTourCost(prunedTour, graph);
                    int newCost = City.calculateTourCost(candidateTour, graph);

                    if (newCost < originalCost) {
                        prunedTour = candidateTour;
                        skippedCities.add(cityToRemove);
                        improved = true;
                        break;
                    }
                }
            }
        }

        // Second pass: try removing sequences of cities
        improved = true;
        while (improved) {
            improved = false;

            int maxSequenceLength = Math.min(5, prunedTour.size() / 3); // Don't remove more than 1/3 of tour at once

            for (int len = 2; len <= maxSequenceLength; len++) {
                // Only consider sequences that don't include the first or last city
                for (int start = 1; start < prunedTour.size() - len - 1; start++) {
                    // Calculate detour cost of skipping the sequence
                    int beforeSeq = prunedTour.get(start - 1);
                    int afterSeq = prunedTour.get(start + len);
                    int detourSavings = 0;
                    for (int i = start; i < start + len; i++) {
                        int current = prunedTour.get(i);
                        int next = prunedTour.get(i + 1);
                        if (graph != null) {
                            detourSavings += graph[current][next];
                        } else {
                            detourSavings += City.getDistance(current, next);
                        }
                    }
                    if (graph != null) {
                        detourSavings -= graph[beforeSeq][afterSeq];
                    } else {
                        detourSavings -= City.getDistance(beforeSeq, afterSeq);
                    }

                    // If the penalty for skipping these cities is less than the saved detour
                    int sequencePenalty = len * City.penalty;
                    if (sequencePenalty < detourSavings) {
                        // Create candidate tour without the sequence
                        List<Integer> candidateTour = new ArrayList<>();
                        for (int j = 0; j < start; j++) {
                            candidateTour.add(prunedTour.get(j));
                        }
                        for (int j = start + len; j < prunedTour.size(); j++) {
                            candidateTour.add(prunedTour.get(j));
                        }

                        int originalCost = City.calculateTourCost(prunedTour, graph);
                        int newCost = City.calculateTourCost(candidateTour, graph);

                        if (newCost < originalCost) {
                            prunedTour = candidateTour;
                            improved = true;
                            break;
                        }
                    }
                }
                if (improved)
                    break;
            }
        }

        return prunedTour;
    }

    /**
     * Builds a tour that's penalty-aware from the beginning
     */
    public static List<Integer> buildPenaltyAwareTour(int[][] distanceMatrix) {
        int n = (distanceMatrix != null) ? distanceMatrix.length : City.cities.size();
        List<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[n];

        // Calculate value-to-cost ratio for each city (penalty avoided vs distance to
        // visit)
        // Start from city 0
        int current = 0;
        tour.add(current);
        visited[current] = true;

        while (tour.size() < n) {
            int next = -1;
            double bestRatio = 0;

            for (int candidate = 0; candidate < n; candidate++) {
                if (!visited[candidate]) {
                    // Calculate detour cost to visit this city
                    int detourCost = calculateDetourCost(tour, candidate, distanceMatrix);

                    // If detour cost is less than penalty, consider adding this city
                    if (detourCost < City.penalty) {
                        double ratio = (double) City.penalty / detourCost;
                        if (ratio > bestRatio) {
                            bestRatio = ratio;
                            next = candidate;
                        }
                    }
                }
            }

            // If no city worth visiting was found, break
            if (next == -1)
                break;

            // Add the best city to the tour
            tour.add(next);
            visited[next] = true;
            current = next;
        }

        // Complete the tour by returning to the start
        if (tour.size() > 0 && tour.get(0) != tour.get(tour.size() - 1)) {
            tour.add(tour.get(0));
        }

        return tour;
    }

    /**
     * Calculate the detour cost of adding a city to an existing tour
     */
    private static int calculateDetourCost(List<Integer> tour, int newCity, int[][] distanceMatrix) {
        if (tour.isEmpty())
            return 0;

        int minDetour = Integer.MAX_VALUE;

        // Try inserting the new city at each position in the tour
        for (int i = 0; i < tour.size(); i++) {
            int prev = tour.get(i);
            int next = tour.get((i + 1) % tour.size());
            int detourCost;
            if (distanceMatrix != null) {
                detourCost = distanceMatrix[prev][newCity] + distanceMatrix[newCity][next] - distanceMatrix[prev][next];
            } else {
                detourCost = City.getDistance(prev, newCity) + City.getDistance(newCity, next)
                        - City.getDistance(prev, next);
            }
            if (detourCost < minDetour) {
                minDetour = detourCost;
            }
        }

        return minDetour;
    }
}