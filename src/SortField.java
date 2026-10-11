import java.util.Comparator;

public enum SortField {
    NAME("Name", Comparator.comparing(Vehicle::getName, String.CASE_INSENSITIVE_ORDER)),
    SPEED("Speed", Comparator.comparingDouble(Vehicle::getSpeed)),
    YEAR("Year", Comparator.comparingInt(Vehicle::getYear));

    private final String label;
    private final Comparator<Vehicle> comparator;

    SortField(String label, Comparator<Vehicle> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    public String getLabel() {
        return label;
    }

    public Comparator<Vehicle> getComparator() {
        return comparator;
    }
}
