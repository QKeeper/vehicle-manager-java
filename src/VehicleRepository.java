import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class VehicleRepository {
    private final List<Vehicle> vehicles = new ArrayList<>();

    public void add(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void addAll(Collection<Vehicle> newVehicles) {
        vehicles.addAll(newVehicles);
    }

    public Vehicle get(int index) {
        return vehicles.get(index);
    }

    public Vehicle removeAt(int index) {
        return vehicles.remove(index);
    }

    public int removeByName(String name) {
        int before = vehicles.size();
        vehicles.removeIf(vehicle -> vehicle.getName().equalsIgnoreCase(name));
        return before - vehicles.size();
    }

    public int size() {
        return vehicles.size();
    }

    public boolean isEmpty() {
        return vehicles.isEmpty();
    }

    public List<Vehicle> getAll() {
        return List.copyOf(vehicles);
    }

    public List<Integer> indicesMatching(Predicate<Vehicle> predicate) {
        return IntStream.range(0, vehicles.size())
                .filter(i -> predicate.test(vehicles.get(i)))
                .boxed()
                .toList();
    }

    public void sort(SortAlgorithm<Vehicle> algorithm, Comparator<Vehicle> comparator) {
        algorithm.sort(vehicles, comparator);
    }
}
