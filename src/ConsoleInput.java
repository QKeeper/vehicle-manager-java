import java.util.Optional;
import java.util.Scanner;

public class ConsoleInput {

    @FunctionalInterface
    public interface Parser<T> {
        T parse(String text) throws ValidationException;
    }

    private final Scanner scanner = new Scanner(System.in);

    public String ask(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        return scanner.nextLine().trim();
    }

    public <T> Optional<T> askValid(String prompt, Parser<T> parser) {
        while (true) {
            String text = ask(prompt);
            if (text.isEmpty()) {
                return Optional.empty();
            }
            try {
                return Optional.of(parser.parse(text));
            } catch (ValidationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public boolean confirm(String prompt) {
        String answer = ask(prompt + " (y/n): ").toLowerCase();
        return answer.equals("y") || answer.equals("yes");
    }
}
