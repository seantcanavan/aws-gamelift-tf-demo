import game.GameService;
import game.GameStateServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

public class GameClient extends LoggableState implements StreamObserver<GameService.GameState> {
  private static final Logger logger = LoggerFactory.getLogger(GameClient.class);
  private final ManagedChannel channel;
  StreamObserver<GameService.PlayerState> requestObserver;

  // Constructor to initialize the channel and stub
  public GameClient(String host, int port, int playerNumber) {
    this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    GameStateServiceGrpc.GameStateServiceStub asyncStub = GameStateServiceGrpc.newStub(channel);
    this.requestObserver = asyncStub.streamGameState(this);
    this.type = "Client";
    this.playerNumber = playerNumber;
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
      stop();
    } catch (InterruptedException e) {
      logger.error("[CLIENT][EXCEPTION][{}] ERROR {}", playerNumber, e.toString());
    }
  }

  @Override
  public void onCompleted() {
    logger.info("[CLIENT][RECEIVE][{}] COMPLETED", playerNumber);
    try {
      stop();
      logger.info("[CLIENT][{}] shutdown() success", playerNumber);
    } catch (InterruptedException e) {
      logger.error("[CLIENT][EXCEPTION][{}] ERROR {}", playerNumber, e.toString());
      e.printStackTrace();
    }
  }

  public void stop() throws InterruptedException {
    logger.info("[CLIENT][STOP][{}] about to call shutdown()", playerNumber);
    channel.shutdown();
    logger.info(
        "[CLIENT][STOP][{}] successfully called shutdown(). about to call awaitTermination",
        playerNumber);
    channel.awaitTermination(10, SECONDS);
    logger.info("[CLIENT][STOP][{}] successfully called awaitTermination()", playerNumber);
  }

  public void start(int playerNumber) {
    GameService.PlayerState state = GameServerAndGameClients.getDefaultState(playerNumber);

    for (int moveNum = 1; moveNum <= GameServerAndGameClients.MAX_MOVES; moveNum++) {
      logger.info(
          "[CLIENT][SEND][{}] startGame() Generated {} of {}",
          playerNumber,
          moveNum,
          GameServerAndGameClients.MAX_MOVES);

      this.requestObserver.onNext(state);
      logger.info("[CLIENT][SEND][{}]", playerNumber);
      state = GameServerAndGameClients.doRandomAction(state);
      long millis = 1500;
      logger.info("[CLIENT][SEND][{}] sleeping for {}ms", playerNumber, millis);
      try {
        Thread.sleep(millis);
      } catch (InterruptedException e) {
        e.printStackTrace();
        logger.error("[CLIENT][SEND][{}] ERROR {}", playerNumber, e.toString());
      }
    }
  }
}
