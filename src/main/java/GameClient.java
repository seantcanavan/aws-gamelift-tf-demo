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
  public void startGame() throws InterruptedException {
    StreamObserver<GameService.PlayerState> requestObserver =
        asyncStub.streamGameState(
            new StreamObserver<>() {
              @Override
              public void onNext(GameService.GameState newGameState) {
                logger.info("[CLIENT][RECEIVE] newGameState {}", newGameState);
                gameState = newGameState;
                logger.info("[CLIENT] local gameState is now {}", gameState);
              }

              @Override
              public void onError(Throwable t) {
                logger.error("[CLIENT][RECEIVE] ERROR {}", t.toString());
                t.printStackTrace();
                try {
                  shutdown();
                } catch (InterruptedException e) {
                  logger.error("[CLIENT][EXCEPTION] ERROR {}", e.toString());
                }
              }

              @Override
              public void onCompleted() {
                logger.info("[CLIENT][RECEIVE] COMPLETED");
                try {
                  shutdown();
                  logger.info("[CLIENT] shutdown() success");
                } catch (InterruptedException e) {
                  logger.error("[CLIENT][EXCEPTION] ERROR {}", e.toString());
                  e.printStackTrace();
                }
              }
            });

    GameService.PlayerState defaultState = GameServerAndGameClients.getDefaultState();
    requestObserver.onNext(defaultState);
    logger.info("[CLIENT] startGame() Sent default state {}", defaultState);

    GameService.PlayerState randomState;

    for (int moveNum = 1; moveNum <= GameServerAndGameClients.MAX_MOVES; moveNum++) {
      randomState = GameServerAndGameClients.doRandomAction(defaultState);
      logger.info(
          "[CLIENT] startGame() Generated {} of {} newRandomState on the Client Side {}",
          moveNum,
          GameServerAndGameClients.MAX_MOVES,
          randomState);
      GameService.PlayerState playerState = randomState;
      requestObserver.onNext(playerState);
      logger.info("[CLIENT][SEND] playerState {}", playerState);
      int delayMs = random.nextInt(0, 3000) + 1000;
      logger.info("[CLIENT] startGame() Sleeping for {} milliseconds", delayMs);
      Thread.sleep(delayMs);
    }
  }
}
