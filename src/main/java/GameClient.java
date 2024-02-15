import game.GameService;
import game.GameStateServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class GameClient {
  private static final Logger logger = LoggerFactory.getLogger(GameClient.class);

  private final Random random = new Random();
  private final ManagedChannel channel;
  private final GameStateServiceGrpc.GameStateServiceStub asyncStub;
  private GameService.GameState gameState = GameService.GameState.newBuilder().build();

  // Constructor to initialize the channel and stub
  public GameClient(String host, int port) {
    this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
    this.asyncStub = GameStateServiceGrpc.newStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
  }

  // Method to start the bidirectional streaming
  public void startGame(int playerNumber) {
    StreamObserver<GameService.PlayerState> requestObserver =
        asyncStub.streamGameState(
            new StreamObserver<>() {
              @Override
              public void onNext(GameService.GameState newGameState) {
                logger.info("[CLIENT][RECEIVE][{}] newGameState {}", playerNumber, newGameState);
                gameState = newGameState;
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
            });

    GameService.PlayerState state = GameServerAndGameClients.getDefaultState(playerNumber);

    for (int moveNum = 1; moveNum <= GameServerAndGameClients.MAX_MOVES; moveNum++) {
      logger.info(
          "[CLIENT][{}] startGame() Generated {} of {} newRandomState on the Client Side {}",
          playerNumber,
          moveNum,
          GameServerAndGameClients.MAX_MOVES,
          state);

      requestObserver.onNext(state);
      logger.info("[CLIENT][SEND][{}] playerState {}", playerNumber, state);
      state = GameServerAndGameClients.doRandomAction(state);
      long millis = 1500;
      logger.info("[CLIENT][{}] sleeping for {}ms", playerNumber, millis);
      try {
        Thread.sleep(millis);
      } catch (InterruptedException e) {
        e.printStackTrace();
        logger.error("[CLIENT][{}] ERROR {}", playerNumber, e.toString());
      }
    }
  }
}
