import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Menu<T> extends Screen {

    private record Item<T>(String label, T value) {
    }

    private final String title;
    private final String zeroLabel;
    private final List<Item<T>> items = new ArrayList<>();

    public Menu(String title, String zeroLabel) {
        this.title = title;
        this.zeroLabel = zeroLabel;
    }

    public static <T> Menu<T> of(String title, String zeroLabel, List<T> values,
                                 Function<T, String> labelProvider) {
        Menu<T> menu = new Menu<>(title, zeroLabel);
        values.forEach(value -> menu.add(labelProvider.apply(value), value));
        return menu;
    }

    public Menu<T> add(String label, T value) {
        items.add(new Item<>(label, value));
        return this;
    }

    public void render(String content) {
        StringBuilder text = new StringBuilder();
        text.append("===== ").append(title).append(" =====\n\n");
        text.append(content).append('\n');
        for (int i = 0; i < items.size(); i++) {
            text.append(i + 1).append(". ").append(items.get(i).label()).append('\n');
        }
        text.append("0. ").append(zeroLabel).append('\n');
        printAndClear(text.toString());
    }

    public boolean isZero(String input) {
        return input.equals("0");
    }

    public Optional<T> choose(String input) {
        try {
            int number = Integer.parseInt(input);
            if (number >= 1 && number <= items.size()) {
                return Optional.of(items.get(number - 1).value());
            }
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public Optional<T> select(ConsoleInput input, String content) {
        render(content);
        return choose(input.ask("Choose option: "));
    }
}
