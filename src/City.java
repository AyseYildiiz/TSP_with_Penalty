import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class City {
    double x, y;
    int id;
    int penalty;
    static LinkedList<City> cities = new LinkedList<City>();

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
    public static double calculateTourDistance(LinkedList<City> tour) {
        double total = 0;
        for (int i = 0; i < tour.size(); i++) {
            City from = tour.get(i);
            City to = tour.get((i + 1) % tour.size());
            total += from.distanceTo(to);
        }
        return total;
    }
}
