import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BubbleSort<T> implements SortAlgorithm<T> {

    @Override
    public String getName() {
        return "Bubble Sort";
    }

    @Override
    public void sort(List<T> items, Comparator<? super T> comparator) {
        int size = items.size();
        for (int pass = 0; pass < size - 1; pass++) {
            boolean swapped = false;
            for (int i = 0; i < size - 1 - pass; i++) {
                if (comparator.compare(items.get(i), items.get(i + 1)) > 0) {
                    Collections.swap(items, i, i + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                return;
            }
        }
    }
}
