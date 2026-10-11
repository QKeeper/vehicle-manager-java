import java.util.List;
import java.util.stream.Collectors;

public final class VehicleTableRenderer {

    private VehicleTableRenderer() {
    }

    public static String render(List<VehicleRow> rows) {
        int numberWidth = maxWidth("#", rows.stream().map(row -> String.valueOf(row.number())).toList());
        int nameWidth = maxWidth("Name", rows.stream().map(row -> row.vehicle().getName()).toList());
        int speedWidth = maxWidth("Speed", rows.stream().map(row -> row.vehicle().getSpeedText()).toList());
        int yearWidth = 4;

        String separator = "+" + "-".repeat(numberWidth + 2)
                + "+" + "-".repeat(nameWidth + 2)
                + "+" + "-".repeat(speedWidth + 2)
                + "+" + "-".repeat(yearWidth + 2) + "+";

        String rowFormat = "| %-" + numberWidth + "s | %-" + nameWidth + "s | %"
                + speedWidth + "s | %" + yearWidth + "s |";

        String header = String.format(rowFormat, "#", "Name", "Speed", "Year");

        String body = rows.stream()
                .map(row -> String.format(rowFormat,
                        row.number(),
                        row.vehicle().getName(),
                        row.vehicle().getSpeedText(),
                        row.vehicle().getYear()))
                .collect(Collectors.joining("\n"));

        StringBuilder table = new StringBuilder();
        table.append(separator).append('\n').append(header).append('\n').append(separator).append('\n');
        if (rows.isEmpty()) {
            table.append("  (no vehicles to show)\n");
        } else {
            table.append(body).append('\n').append(separator).append('\n');
        }
        return table.toString();
    }

    private static int maxWidth(String title, List<String> values) {
        int longestValue = values.stream().mapToInt(String::length).max().orElse(0);
        return Math.max(title.length(), longestValue);
    }
}
