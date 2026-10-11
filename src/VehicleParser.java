import java.time.Year;
import java.util.regex.Pattern;

public final class VehicleParser {
    public static final int MAX_NAME_LENGTH = 30;
    public static final float MAX_SPEED = 2000f;
    public static final int MIN_YEAR = 1886;
    public static final String LINE_FORMAT = "name;speed;year";

    private static final String SEPARATOR = ";";
    private static final Pattern NUMBER = Pattern.compile("\\d+([.,]\\d+)?");
    private static final Pattern YEAR = Pattern.compile("\\d{4}");

    private VehicleParser() {
    }

    public static String parseName(String text) throws ValidationException {
        String name = text.trim();
        if (name.isEmpty()) {
            throw new ValidationException("Name must not be empty.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Name must be at most " + MAX_NAME_LENGTH + " characters.");
        }
        if (name.contains(SEPARATOR) || name.chars().anyMatch(Character::isISOControl)) {
            throw new ValidationException("Name must not contain ';' or control characters.");
        }
        return name;
    }

    public static float parseSpeed(String text) throws ValidationException {
        float speed = parseNumber(text, "Speed");
        if (speed <= 0 || speed > MAX_SPEED) {
            throw new ValidationException("Speed must be greater than 0 and at most "
                    + (int) MAX_SPEED + " km/h.");
        }
        return speed;
    }

    public static float parseThreshold(String text) throws ValidationException {
        return parseNumber(text, "Value");
    }

    public static int parseYear(String text) throws ValidationException {
        String trimmed = text.trim();
        int maxYear = Year.now().getValue();
        if (!YEAR.matcher(trimmed).matches()) {
            throw new ValidationException("Year must be a 4-digit number.");
        }
        int year = Integer.parseInt(trimmed);
        if (year < MIN_YEAR || year > maxYear) {
            throw new ValidationException("Year must be between " + MIN_YEAR + " and " + maxYear + ".");
        }
        return year;
    }

    public static int parseIndex(String text, int size) throws ValidationException {
        String trimmed = text.trim();
        if (!trimmed.matches("\\d{1,9}")) {
            throw new ValidationException("Index must be a whole number.");
        }
        int index = Integer.parseInt(trimmed);
        if (index < 1 || index > size) {
            throw new ValidationException("Index must be between 1 and " + size + ".");
        }
        return index;
    }

    public static Vehicle parseLine(String line) throws ValidationException {
        String[] parts = line.split(SEPARATOR, -1);
        if (parts.length != 3) {
            throw new ValidationException("expected format '" + LINE_FORMAT + "'");
        }
        return new Vehicle(parseName(parts[0]), parseSpeed(parts[1]), parseYear(parts[2]));
    }

    public static String toLine(Vehicle vehicle) {
        return vehicle.getName() + SEPARATOR + vehicle.getSpeedText() + SEPARATOR + vehicle.getYear();
    }

    private static float parseNumber(String text, String label) throws ValidationException {
        String trimmed = text.trim();
        if (!NUMBER.matcher(trimmed).matches()) {
            throw new ValidationException(label + " must be a non-negative number (e.g. 20 or 20.5).");
        }
        return Float.parseFloat(trimmed.replace(',', '.'));
    }
}
