import java.util.LinkedList;
import java.util.List;

// ADD PRIVATE FİELDS AND GETTER SETTER FOR ENCAPSULATION MAYBE

public class City {
    double x, y;
    int id;
    static int penalty;
    static LinkedList<City> cities = new LinkedList<City>();
    static int [][] distancesMatrix;


    public City(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        cities.add(this);

    }
    public int distanceTo(City other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return (int)Math.round(Math.sqrt(dx * dx + dy * dy));
    }

    public static void createDistancesMatrix(){
        int n = cities.size();
        distancesMatrix = new int[n][n];
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                distancesMatrix[i][j]= cities.get(i).distanceTo(City.cities.get(j));
            }
        }
    }

    public static int calculateTourCost(List<Integer> tour, int[][] graph) {
        int cost = 0;
        for (int i = 0; i < tour.size() - 1; i++) {
            cost += graph[tour.get(i)][tour.get(i + 1)];
        }
        return cost;
    }

}