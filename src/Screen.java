public abstract class Screen {
    protected void printAndClear(String text) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println(text);
    }

    protected void sleep(int ms) {
        Utils.trySleep(ms);
    }
}