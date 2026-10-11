import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StalinSort<T> implements SortAlgorithm<T> {

    @Override
    public String getName() {
        return "Stalin Sort";
    }

    @Override
    public void sort(List<T> items, Comparator<? super T> comparator) {
        List<T> survivors = new ArrayList<>();
        for (T item : items) {
            boolean inOrder = survivors.isEmpty()
                    || comparator.compare(survivors.get(survivors.size() - 1), item) <= 0;
            if (inOrder) {
                survivors.add(item);
            }
        }
        items.clear();
        items.addAll(survivors);
    }
}
