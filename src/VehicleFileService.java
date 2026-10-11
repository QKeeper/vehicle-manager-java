import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VehicleFileService {

    public record LoadResult(List<Vehicle> vehicles, List<String> errors) {
    }

    public LoadResult load(Path path) throws IOException {
        List<Vehicle> vehicles = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.startsWith("\uFEFF")) {
                    line = line.substring(1);
                }
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                try {
                    vehicles.add(VehicleParser.parseLine(line));
                } catch (ValidationException e) {
                    errors.add("line " + lineNumber + ": " + e.getMessage());
                }
            }
        }
        return new LoadResult(vehicles, errors);
    }

    public void save(Path path, List<Vehicle> vehicles) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Vehicle vehicle : vehicles) {
                writer.write(VehicleParser.toLine(vehicle));
                writer.newLine();
            }
        }
    }
}
