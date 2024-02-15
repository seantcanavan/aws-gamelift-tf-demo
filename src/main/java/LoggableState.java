import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggableState extends GameState {
  private static final Logger logger = LoggerFactory.getLogger(LoggableState.class);
  String type;
  int playerNumber;

  private Thread printThread = null;
  private boolean printingEnabled;

  public synchronized void setPrintingEnabled(boolean printingEnabled) {
    this.printingEnabled = printingEnabled;
  }

  public synchronized boolean isPrintingEnabled() {
    return this.printingEnabled;
  }

  public synchronized void startPrinting() {
    if (printThread == null || !printThread.isAlive()) {
      setPrintingEnabled(true);
      printThread =
          new Thread(
              () -> {
                while (isPrintingEnabled()) {
                  try {
                    logger.info("[{}][{}] GameState {}", type, playerNumber, getGameState());
                    Thread.sleep(1000); // Sleep for 1 second
                  } catch (InterruptedException e) {
                    logger.error("[{}][{}] Printing thread interrupted", type, playerNumber, e);
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    break; // Exit the loop if the thread is interrupted
                  }
                }
              });
      printThread.start();
    }
  }

  public synchronized void stopPrinting() {
    setPrintingEnabled(false);
    // Optionally join the thread to ensure it has finished
    if (printThread != null) {
      try {
        printThread.join(); // Wait for the printing thread to finish
      } catch (InterruptedException e) {
        logger.error("Interrupted while waiting for the printing thread to finish", e);
        Thread.currentThread().interrupt(); // Restore interrupted status
      }
    }
  }
}
