import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public class VehicleApp {
    private static final int PAGE_SIZE = 10;
    private static final int MAX_SHOWN_ERRORS = 5;

    private final ConsoleInput input = new ConsoleInput();
    private final VehicleRepository repository = new VehicleRepository();
    private final VehicleFileService fileService = new VehicleFileService();

    private final List<SortAlgorithm<Vehicle>> algorithms = List.of(
            new StreamSort<>(), new QuickSort<>(), new BubbleSort<>(), new InsertionSort<>(),
            new StalinSort<>());

    private final Menu<Runnable> mainMenu;
    private final Menu<Runnable> addMenu;
    private final Menu<Runnable> removeMenu;
    private final Menu<Runnable> filterMenu;
    private final Menu<SortAlgorithm<Vehicle>> algorithmMenu;
    private final Menu<SortField> fieldMenu;
    private final Menu<SortDirection> directionMenu;

    private VehicleFilter filter = VehicleFilter.none();
    private int page = 0;
    private String status = "";
    private boolean running = true;

    public VehicleApp() {
        mainMenu = new Menu<Runnable>("Vehicles Manager", "Exit")
                .add("Add", this::addVehicle)
                .add("Remove", this::removeVehicle)
                .add("Sort", this::sortVehicles)
                .add("Filter", this::filterVehicles)
                .add("Save", this::saveVehicles)
                .add("Show names", this::showNames);

        addMenu = new Menu<Runnable>("Add vehicle", "Back")
                .add("Manually", this::addManually)
                .add("From file", this::addFromFile);

        removeMenu = new Menu<Runnable>("Remove vehicle", "Back")
                .add("By index", this::removeByIndex)
                .add("By name", this::removeByName);

        filterMenu = new Menu<Runnable>("Filter", "Back")
                .add("By name (includes)", this::filterByName)
                .add("Speed: more than", this::filterSpeedMoreThan)
                .add("Speed: less than", this::filterSpeedLessThan)
                .add("Reset filter", this::resetFilter);

        algorithmMenu = Menu.of("Sort (1/3): algorithm", "Cancel", algorithms, SortAlgorithm::getName);
        fieldMenu = Menu.of("Sort (2/3): field", "Cancel",
                Arrays.asList(SortField.values()), SortField::getLabel);
        directionMenu = Menu.of("Sort (3/3): direction", "Cancel",
                Arrays.asList(SortDirection.values()), SortDirection::getLabel);
    }

    public void run() {
        try {
            while (running) {
                mainMenu.render(buildView(true));
                handleMainInput(input.ask(buildPrompt()));
            }
            System.out.println("Goodbye!");
        } catch (NoSuchElementException e) {
            System.out.println("\nInput closed. Goodbye!");
        }
    }

    private void handleMainInput(String command) {
        if (command.isEmpty()) {
            return;
        }
        if (mainMenu.isZero(command)) {
            running = false;
            return;
        }
        switch (command.toLowerCase()) {
            case "n" -> nextPage();
            case "p" -> previousPage();
            default -> mainMenu.choose(command).ifPresentOrElse(
                    Runnable::run, () -> status = "Unknown command: " + command);
        }
    }


    private String buildView(boolean withStatus) {
        List<Integer> visible = repository.indicesMatching(filter.predicate());
        int totalPages = totalPages(visible.size());
        page = Math.min(page, totalPages - 1);

        List<VehicleRow> rows = visible.stream()
                .skip((long) page * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .map(index -> new VehicleRow(index + 1, repository.get(index)))
                .toList();

        StringBuilder view = new StringBuilder();
        if (filter.isActive()) {
            view.append("Filter: ").append(filter.description())
                    .append(" (shown ").append(visible.size())
                    .append(" of ").append(repository.size()).append(")\n");
        }
        view.append(VehicleTableRenderer.render(rows));
        if (totalPages > 1) {
            view.append("Page ").append(page + 1).append('/').append(totalPages)
                    .append("  (n - next, p - previous)\n");
        }
        if (withStatus && !status.isEmpty()) {
            view.append("\n>> ").append(status).append('\n');
            status = "";
        }
        return view.toString();
    }

    private String buildPrompt() {
        int visibleCount = repository.indicesMatching(filter.predicate()).size();
        return totalPages(visibleCount) > 1 ? "Choose option (or n/p): " : "Choose option: ";
    }

    private int totalPages(int itemCount) {
        return Math.max(1, (itemCount + PAGE_SIZE - 1) / PAGE_SIZE);
    }

    private void nextPage() {
        int totalPages = totalPages(repository.indicesMatching(filter.predicate()).size());
        if (page + 1 < totalPages) {
            page++;
        } else {
            status = "This is the last page.";
        }
    }

    private void previousPage() {
        if (page > 0) {
            page--;
        } else {
            status = "This is the first page.";
        }
    }


    private void addVehicle() {
        runSubmenu(addMenu);
    }

    private void addManually() {
        System.out.println("(an empty line cancels)");
        Optional<String> name = input.askValid("Name: ", VehicleParser::parseName);
        if (name.isEmpty()) {
            status = "Cancelled.";
            return;
        }
        Optional<Float> speed = input.askValid("Speed (km/h): ", VehicleParser::parseSpeed);
        if (speed.isEmpty()) {
            status = "Cancelled.";
            return;
        }
        Optional<Integer> year = input.askValid("Year: ", VehicleParser::parseYear);
        if (year.isEmpty()) {
            status = "Cancelled.";
            return;
        }
        Vehicle vehicle = new Vehicle(name.get(), speed.get(), year.get());
        repository.add(vehicle);
        page = Integer.MAX_VALUE;
        status = "Added: " + vehicle;
    }

    private void addFromFile() {
        String text = input.ask("File path (format '" + VehicleParser.LINE_FORMAT
                + "' per line, e.g. data/vehicles.txt; empty line cancels): ");
        if (text.isEmpty()) {
            status = "Cancelled.";
            return;
        }
        try {
            VehicleFileService.LoadResult result = fileService.load(Path.of(text));
            repository.addAll(result.vehicles());
            page = Integer.MAX_VALUE;
            status = describeLoad(result, text);
        } catch (NoSuchFileException e) {
            status = "Error: file not found: " + text;
        } catch (IOException | InvalidPathException e) {
            status = "Error: cannot read file: " + e.getMessage();
        }
    }

    private String describeLoad(VehicleFileService.LoadResult result, String fileName) {
        StringBuilder message = new StringBuilder("Loaded " + result.vehicles().size()
                + " vehicle(s) from " + fileName + ".");
        List<String> errors = result.errors();
        if (!errors.isEmpty()) {
            message.append("\n   Skipped ").append(errors.size()).append(" invalid line(s):");
            errors.stream().limit(MAX_SHOWN_ERRORS)
                    .forEach(error -> message.append("\n   - ").append(error));
            if (errors.size() > MAX_SHOWN_ERRORS) {
                message.append("\n   ... and ").append(errors.size() - MAX_SHOWN_ERRORS).append(" more");
            }
        }
        return message.toString();
    }


    private void removeVehicle() {
        if (repository.isEmpty()) {
            status = "Nothing to remove.";
            return;
        }
        runSubmenu(removeMenu);
    }

    private void removeByIndex() {
        Optional<Integer> number = input.askValid("Index (#) to remove (empty line cancels): ",
                text -> VehicleParser.parseIndex(text, repository.size()));
        if (number.isEmpty()) {
            status = "Cancelled.";
            return;
        }
        Vehicle removed = repository.removeAt(number.get() - 1);
        status = "Removed: " + removed;
    }

    private void removeByName() {
        Optional<String> name = input.askValid("Name to remove (empty line cancels): ",
                VehicleParser::parseName);
        if (name.isEmpty()) {
            status = "Cancelled.";
            return;
        }
        int removed = repository.removeByName(name.get());
        status = removed == 0
                ? "No vehicles named \"" + name.get() + "\"."
                : "Removed " + removed + " vehicle(s) named \"" + name.get() + "\".";
    }


    private void sortVehicles() {
        if (repository.size() < 2) {
            status = "Nothing to sort (need at least 2 vehicles).";
            return;
        }
        Optional<SortAlgorithm<Vehicle>> algorithm = algorithmMenu.select(input, buildView(false));
        if (algorithm.isEmpty()) {
            status = "Sorting cancelled.";
            return;
        }
        Optional<SortField> field = fieldMenu.select(input, buildView(false));
        if (field.isEmpty()) {
            status = "Sorting cancelled.";
            return;
        }
        Optional<SortDirection> direction = directionMenu.select(input, buildView(false));
        if (direction.isEmpty()) {
            status = "Sorting cancelled.";
            return;
        }

        repository.sort(algorithm.get(), direction.get().apply(field.get().getComparator()));
        page = 0;
        status = "Sorted by " + field.get().getLabel() + " (" + direction.get().getLabel()
                + ") using " + algorithm.get().getName() + ".";
    }


    private void filterVehicles() {
        runSubmenu(filterMenu);
    }

    private void filterByName() {
        Optional<String> text = input.askValid("Name includes (empty line cancels): ",
                VehicleParser::parseName);
        text.ifPresentOrElse(
                value -> applyFilter(VehicleFilter.nameIncludes(value)),
                () -> status = "Cancelled.");
    }

    private void filterSpeedMoreThan() {
        Optional<Float> value = input.askValid("Speed more than (km/h, empty line cancels): ",
                VehicleParser::parseThreshold);
        value.ifPresentOrElse(
                number -> applyFilter(VehicleFilter.speedMoreThan(number)),
                () -> status = "Cancelled.");
    }

    private void filterSpeedLessThan() {
        Optional<Float> value = input.askValid("Speed less than (km/h, empty line cancels): ",
                VehicleParser::parseThreshold);
        value.ifPresentOrElse(
                number -> applyFilter(VehicleFilter.speedLessThan(number)),
                () -> status = "Cancelled.");
    }

    private void applyFilter(VehicleFilter newFilter) {
        filter = newFilter;
        page = 0;
        status = "Filter applied: " + newFilter.description();
    }

    private void resetFilter() {
        filter = VehicleFilter.none();
        page = 0;
        status = "Filter reset.";
    }


    private void saveVehicles() {
        if (repository.isEmpty()) {
            status = "Nothing to save.";
            return;
        }
        String text = input.ask("File name to save to (empty line cancels): ");
        if (text.isEmpty()) {
            status = "Cancelled.";
            return;
        }
        try {
            Path path = Path.of(text);
            if (Files.exists(path) && !input.confirm("File exists. Overwrite?")) {
                status = "Cancelled.";
                return;
            }
            fileService.save(path, repository.getAll());
            status = "Saved " + repository.size() + " vehicle(s) to " + text
                    + (filter.isActive() ? " (all vehicles, the filter is ignored)." : ".");
        } catch (IOException | InvalidPathException e) {
            status = "Error: cannot write file: " + e.getMessage();
        }
    }


    private void showNames() {
        String names = repository.getAll().stream()
                .filter(filter.predicate())
                .map(Vehicle::getName)
                .collect(Collectors.joining(", "));
        status = names.isEmpty() ? "No vehicles to show." : "Names: " + names;
    }


    private void runSubmenu(Menu<Runnable> menu) {
        menu.select(input, buildView(false)).ifPresentOrElse(
                Runnable::run, () -> status = "Cancelled.");
    }
}
