import java.util.Comparator;

public enum SortDirection {
    ASCENDING("Ascending", false),
    DESCENDING("Descending", true);

    private final String label;
    private final boolean reversed;

    SortDirection(String label, boolean reversed) {
        this.label = label;
        this.reversed = reversed;
    }

    public String getLabel() {
        return label;
    }

    public <T> Comparator<T> apply(Comparator<T> comparator) {
        return reversed ? comparator.reversed() : comparator;
    }
}
