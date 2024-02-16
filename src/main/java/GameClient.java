import game.GameService;
import game.GameStateServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameClient extends LoggableState implements StreamObserver<GameService.GameState> {
  private static final Logger logger = LoggerFactory.getLogger(GameClient.class);
  private final ManagedChannel channel;
  StreamObserver<GameService.PlayerState> requestObserver;
  private Thread sendingThread = null;
  private volatile boolean sendingMessages;
  private GameService.PlayerState playerState;

  // Constructor to initialize the channel and stub
  public GameClient(String host, int port, int playerNumber) {
    this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    GameStateServiceGrpc.GameStateServiceStub asyncStub = GameStateServiceGrpc.newStub(channel);
    this.requestObserver = asyncStub.streamGameState(this);
    this.type = "CLIENT";
    this.playerNumber = playerNumber;
    this.playerState = GameServerAndGameClients.getDefaultState(playerNumber);
  }

  @Override
  public void onNext(GameService.GameState newGameState) {
    logger.info("[CLIENT][RECEIVE][{}]", playerNumber);
    setGameState(newGameState);
  }

  @Override
  public void onError(Throwable t) {
    logger.error("[CLIENT][RECEIVE][{}] ERROR {}", playerNumber, t.toString());
    t.printStackTrace();
    try {
      shutdown();
    } catch (InterruptedException e) {
      logger.error("[CLIENT][EXCEPTION][{}] ERROR {}", playerNumber, e.toString());
    }
  }

  @Override
  public void onCompleted() {
    logger.info("[CLIENT][RECEIVE][{}] COMPLETED", playerNumber);
    try {
      shutdown();
      logger.info("[CLIENT][{}] shutdown() success", playerNumber);
    } catch (InterruptedException e) {
      logger.error("[CLIENT][EXCEPTION][{}] ERROR {}", playerNumber, e.toString());
      e.printStackTrace();
    }
  }

  private synchronized void setSendingMessages(boolean setSendingMessages) {
    this.sendingMessages = setSendingMessages;
  }

  private synchronized boolean isSendingMessages() {
    return this.sendingMessages;
  }

  private synchronized GameService.PlayerState getPlayerState() {
    return this.playerState;
  }

  private synchronized void setPlayerState(GameService.PlayerState playerState) {
    this.playerState = playerState;
  }

  public void start() {
    if (sendingThread != null && sendingThread.isAlive()) {
      return;
    }

    setSendingMessages(true);
    sendingThread =
        new Thread(
            () -> {
              while (isSendingMessages()) {
                try {
                  long millis = 1500;
                  logger.info("[CLIENT][SEND][{}] sleeping for {} millis", playerNumber, millis);
                  Thread.sleep(millis);
                  setPlayerState(GameServerAndGameClients.doRandomAction(getPlayerState()));
                  this.requestObserver.onNext(playerState);
                  logger.info("[CLIENT][SEND][{}] sent updated state", playerNumber);
                } catch (InterruptedException e) {
                  logger.info(
                      "[CLIENT][SEND][{}] interrupted exception {}", playerNumber, e.toString());
                  e.printStackTrace();
                  logger.error("[CLIENT][{}] ERROR {}", playerNumber, e.toString());
                  break; // Exit the loop if the thread is interrupted
                }
              }
            });
    sendingThread.start();
  }

  public void stop() {
    setSendingMessages(false);
    if (sendingThread == null) {
      return;
    }

    // Optionally join the thread to ensure it has finished
    try {
      sendingThread.join(); // Wait for the printing thread to finish
    } catch (InterruptedException e) {
      logger.error("Interrupted while waiting for the printing thread to finish", e);
      Thread.currentThread().interrupt(); // Restore interrupted status
    }
  }

  public synchronized void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
  }
}
