import java.util.*;


public class Tsp  {
    private static final double T0 = 1000;
    private static final double Tmin = 1;
    private static final double COOLING_RATE = 0.995;

    List<City> solve(List<City> cities) {
        List<City> tatSol = TwiceAroundTheTree.twiceAroundTree(cities);
        List<City> chSol = City.generateTour(cities);

        List<City> current = City.calculateTourDistance(tatSol) < City.calculateTourDistance(chSol) ? tatSol : chSol;
        List<City> best = new ArrayList<>(current);

        double temperature = T0;
        List<List<City>> tabuList = new LinkedList<>();

        Random rand = new Random();

        while (temperature > Tmin) {
            List<City> neighbor = random2Opt(current);

            double delta = City.calculateTourDistance(neighbor) - City.calculateTourDistance(current);

            if (delta < 0 || rand.nextDouble() < Math.exp(-delta / temperature)) {
                current = tabuSearchImprove(neighbor, tabuList);

                if (City.calculateTourDistance(current) < City.calculateTourDistance(best)) {
                    best = new ArrayList<>(current);
                }

                tabuList.add(current);

                if (tabuList.size() > 50) {
                    tabuList.removeFirst();
                }
            }

            temperature *= COOLING_RATE;
        }

        return best;
    }

    double totalDistance(List<City> tour) {
        return City.calculateTourDistance(tour);
    }

    private List<City> random2Opt(List<City> tour) {
        List<City> newTour = new ArrayList<>(tour);
        Random rand = new Random();
        int size = newTour.size();

        int i = rand.nextInt(size - 1);
        int j = i + 1 + rand.nextInt(size - i - 1);

        while (i < j) {
            Collections.swap(newTour, i, j);
            i++;
            j--;
        }

        return newTour;
    }

    private List<City> tabuSearchImprove(List<City> startTour, List<List<City>> tabuList) {
        List<City> bestLocal = new ArrayList<>(startTour);
        double bestDist = City.calculateTourDistance(bestLocal);

        for (int i = 0; i < startTour.size() - 1; i++) {
            for (int j = i + 1; j < startTour.size(); j++) {
                List<City> candidate = new ArrayList<>(startTour);
                Collections.swap(candidate, i, j);

                if (tabuListContains(tabuList, candidate)) continue;

                double candidateDist = City.calculateTourDistance(candidate);
                if (candidateDist < bestDist) {
                    bestDist = candidateDist;
                    bestLocal = candidate;
                }
            }
        }

        return bestLocal;
    }

    private boolean tabuListContains(List<List<City>> tabuList, List<City> candidate) {
        for (List<City> tour : tabuList) {
            if (areToursEqual(tour, candidate)) return true;
        }
        return false;
    }

    private boolean areToursEqual(List<City> t1, List<City> t2) {
        if (t1.size() != t2.size()) return false;
        for (int i = 0; i < t1.size(); i++) {
            if (!t1.get(i).equals(t2.get(i))) return false;
        }
        return true;
    }

    //Main
    public static void main(String[] args) {
//        List<City> cities = generateRandomCities(20, 100, 100);

        List<City> cities = Arrays.asList(
                new City(0, 0, 0),
                new City(1, 1, 3),
                new City(2, 4, 3),
                new City(3, 6, 1),
                new City(4, 3, 0)
        );
        Tsp solver = new Tsp();

        long start = System.currentTimeMillis();
        List<City> solution = solver.solve(cities);
        long end = System.currentTimeMillis();

        System.out.println("Tour Distance: " + solver.totalDistance(solution));
        System.out.println("Tour order:");
        for (City c : solution) {
            System.out.print(c.id + " ");
        }
        System.out.println("\nTime elapsed: " + (end - start) + " ms");
    }

//    private static List<City> generateRandomCities(int n, int maxX, int maxY) {
//        Random rand = new Random(42);
//        List<City> cities = new ArrayList<>();
//        for (int i = 0; i < n; i++) {
//            double x = rand.nextDouble() * maxX;
//            double y = rand.nextDouble() * maxY;
//            cities.add(new City(i, x, y));
//        }
//        return cities;
//    }
}
