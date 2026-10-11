import java.util.Comparator;
import java.util.List;

public class StreamSort<T> implements SortAlgorithm<T> {

    @Override
    public String getName() {
        return "Stream API (built-in)";
    }

    @Override
    public void sort(List<T> items, Comparator<? super T> comparator) {
        List<T> sorted = items.stream().sorted(comparator).toList();
        items.clear();
        items.addAll(sorted);
    }
}
