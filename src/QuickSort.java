import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuickSort<T> implements SortAlgorithm<T> {

    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public void sort(List<T> items, Comparator<? super T> comparator) {
        if (items.size() > 1) {
            quickSort(items, 0, items.size() - 1, comparator);
        }
    }

    private void quickSort(List<T> items, int low, int high, Comparator<? super T> comparator) {
        int left = low;
        int right = high;
        T pivot = items.get(low + (high - low) / 2);

        while (left <= right) {
            while (comparator.compare(items.get(left), pivot) < 0) {
                left++;
            }
            while (comparator.compare(items.get(right), pivot) > 0) {
                right--;
            }
            if (left <= right) {
                Collections.swap(items, left, right);
                left++;
                right--;
            }
        }

        if (low < right) {
            quickSort(items, low, right, comparator);
        }
        if (left < high) {
            quickSort(items, left, high, comparator);
        }
    }
}
