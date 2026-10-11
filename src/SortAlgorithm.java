import java.util.Comparator;
import java.util.List;

public interface SortAlgorithm<T> {

    String getName();

    void sort(List<T> items, Comparator<? super T> comparator);
}
