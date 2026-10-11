import java.util.Locale;
import java.util.function.Predicate;

public record VehicleFilter(Predicate<Vehicle> predicate, String description) {

    public static VehicleFilter none() {
        return new VehicleFilter(vehicle -> true, "");
    }

    public static VehicleFilter nameIncludes(String text) {
        String needle = text.toLowerCase(Locale.ROOT);
        return new VehicleFilter(
                vehicle -> vehicle.getName().toLowerCase(Locale.ROOT).contains(needle),
                "name includes \"" + text + "\"");
    }

    public static VehicleFilter speedMoreThan(float value) {
        return new VehicleFilter(vehicle -> vehicle.getSpeed() > value,
                "speed > " + formatNumber(value));
    }

    public static VehicleFilter speedLessThan(float value) {
        return new VehicleFilter(vehicle -> vehicle.getSpeed() < value,
                "speed < " + formatNumber(value));
    }

    public boolean isActive() {
        return !description.isEmpty();
    }

    private static String formatNumber(float value) {
        return new java.math.BigDecimal(Float.toString(value)).stripTrailingZeros().toPlainString();
    }
}
