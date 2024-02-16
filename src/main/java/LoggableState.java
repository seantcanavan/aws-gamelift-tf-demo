import game.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggableState extends GameState {
  private static final Logger logger = LoggerFactory.getLogger(LoggableState.class);
  String type;
  int playerNumber;

  private Thread printThread = null;
  private boolean loggingEnabled;

  private synchronized void setLoggingEnabled(boolean loggingEnabled) {
    this.loggingEnabled = loggingEnabled;
  }

  private synchronized boolean isLoggingEnabled() {
    return this.loggingEnabled;
  }

  public synchronized void startLogging() {
    if (printThread != null && printThread.isAlive()) {
      return;
    }

    setLoggingEnabled(true);
    printThread =
        new Thread(
            () -> {
              while (isLoggingEnabled()) {
                try {
                  GameService.GameState state = getGameState();
                  if (state != null) {
                    logger.info(
                        "[{}][{}] P1X {} P1Y {} P1Z {} | P2X {} P2Y {} P2Z {} | P3X {} P3Y {} P3Z {} | P4X {} P4Y {} P4Z {} | P5X {} P5Y {} P5Z {} | P6X {} P6Y {} P6Z {} | P7X {} P7Y {} P7Z {} | P8X {} P8Y {} P8Z {} | P9X {} P9Y {} P9Z {} | P10X {} P10Y {} P10Z {}",
                        type,
                        playerNumber,
                        state.getPlayer1().getCoordinates().getX(),
                        state.getPlayer1().getCoordinates().getY(),
                        state.getPlayer1().getCoordinates().getZ(),
                        state.getPlayer2().getCoordinates().getX(),
                        state.getPlayer2().getCoordinates().getY(),
                        state.getPlayer2().getCoordinates().getZ(),
                        state.getPlayer3().getCoordinates().getX(),
                        state.getPlayer3().getCoordinates().getY(),
                        state.getPlayer3().getCoordinates().getZ(),
                        state.getPlayer4().getCoordinates().getX(),
                        state.getPlayer4().getCoordinates().getY(),
                        state.getPlayer4().getCoordinates().getZ(),
                        state.getPlayer5().getCoordinates().getX(),
                        state.getPlayer5().getCoordinates().getY(),
                        state.getPlayer5().getCoordinates().getZ(),
                        state.getPlayer6().getCoordinates().getX(),
                        state.getPlayer6().getCoordinates().getY(),
                        state.getPlayer6().getCoordinates().getZ(),
                        state.getPlayer7().getCoordinates().getX(),
                        state.getPlayer7().getCoordinates().getY(),
                        state.getPlayer7().getCoordinates().getZ(),
                        state.getPlayer8().getCoordinates().getX(),
                        state.getPlayer8().getCoordinates().getY(),
                        state.getPlayer8().getCoordinates().getZ(),
                        state.getPlayer9().getCoordinates().getX(),
                        state.getPlayer9().getCoordinates().getY(),
                        state.getPlayer9().getCoordinates().getZ(),
                        state.getPlayer10().getCoordinates().getX(),
                        state.getPlayer10().getCoordinates().getY(),
                        state.getPlayer10().getCoordinates().getZ());
                  }
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

  public synchronized void stopLogging() {
    setLoggingEnabled(false);
    logger.info("[{}][{}] setLoggingEnabled(false)", type, playerNumber);
    if (printThread == null) {
      logger.info("[{}][{}] printThread == null - returning", type, playerNumber);
      return;
    }

    try {
      logger.info("[{}][{}] about to call printThread.join(5000)", type, playerNumber);
      printThread.join(5000); // Wait for the printing thread to finish
      logger.info("[{}][{}] successfully called printThread.join(5000)", type, playerNumber);
    } catch (InterruptedException e) {
      logger.error("Interrupted while waiting for the printing thread to finish", e);
      Thread.currentThread().interrupt(); // Restore interrupted status
    }
  }
}
