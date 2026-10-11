import java.util.logging.Level;
import java.util.logging.Logger;

public final class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    public static void trySleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Ожидание потока было прервано", e);
            Thread.currentThread().interrupt();
        }
    }
}
