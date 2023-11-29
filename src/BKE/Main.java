package BKE;


public class Main {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(Framework::Shutdown));
        Thread thread = Framework.Start();
        try {

            // Waits until thread is resolved before exiting the program.
            thread.join();
            System.exit(0);
        } catch (InterruptedException e) {
            System.exit(1);
        }
    }
}
