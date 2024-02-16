import game.GameService;
import game.GameService.GameState;
import game.GameStateServiceGrpc;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class GameServer extends LoggableState {
  public static final int PORT = 50051; // Example port number
  private static final Logger logger = LoggerFactory.getLogger(GameServer.class);

  /**
   * A map of all the streams between every individual player and the server key'd off of the
   * player's number.
   */
  private final Map<Integer, StreamObserver<GameState>> playerStateStreams =
      Collections.synchronizedMap(new HashMap<>(GameServerAndGameClients.MAX_PLAYERS));

  /**
   * A map of all of every individual player's state key'd off of the player's number.
   */
  private final Map<Integer, GameService.PlayerState> playerStates =
      Collections.synchronizedMap(new HashMap<>(GameServerAndGameClients.MAX_PLAYERS));

  /**
   * Records the number of players in the game. Increments to add new players to the game so they
   * can be tracked via their unique stream number / player state number.
   */
  private final AtomicInteger playerCount = new AtomicInteger(1);

  private io.grpc.Server grpcServer;
  private GameService.PlayerState Player1State =
      GameService.PlayerState.newBuilder().setNumber(1).setConnected(false).build();
  private GameService.PlayerState Player2State =
      GameService.PlayerState.newBuilder().setNumber(2).setConnected(false).build();
  private GameService.PlayerState Player3State =
      GameService.PlayerState.newBuilder().setNumber(3).setConnected(false).build();
  private GameService.PlayerState Player4State =
      GameService.PlayerState.newBuilder().setNumber(4).setConnected(false).build();
  private GameService.PlayerState Player5State =
      GameService.PlayerState.newBuilder().setNumber(5).setConnected(false).build();
  private GameService.PlayerState Player6State =
      GameService.PlayerState.newBuilder().setNumber(6).setConnected(false).build();
  private GameService.PlayerState Player7State =
      GameService.PlayerState.newBuilder().setNumber(7).setConnected(false).build();
  private GameService.PlayerState Player8State =
      GameService.PlayerState.newBuilder().setNumber(8).setConnected(false).build();
  private GameService.PlayerState Player9State =
      GameService.PlayerState.newBuilder().setNumber(9).setConnected(false).build();
  private GameService.PlayerState Player10State =
      GameService.PlayerState.newBuilder().setNumber(10).setConnected(false).build();

  ExecutorService executorService =
      Executors.newFixedThreadPool(GameServerAndGameClients.MAX_PLAYERS);

  public GameServer() {
    this.type = "SERVER";
    this.playerNumber = 0;
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    logger.info("[SERVER] main() called");
    final GameServer server = new GameServer();
    server.start();
    logger.info("[SERVER] server.start() called. Blocking until shutdown.");
    server.blockUntilShutdown();
    logger.info("[SERVER] server.blockUntilShutdown() success.");
  }

  public void start() throws IOException {

    logger.info("[SERVER] start() called");
    grpcServer =
        ServerBuilder.forPort(PORT)
            .addService(new GameStateServiceImpl())
            .executor(executorService) // Pool size matches the number of players
            .build()
            .start();

    logger.info("[SERVER] grpcServer.start() called on port {}", PORT);
  }

  public void stop() throws InterruptedException {
    logger.info("[SERVER] stop() grpcServer {}", grpcServer);
    if (grpcServer == null) {
      return;
    }

    logger.info("[SERVER][STOP] about to call executorService.shutdown()");
    executorService.shutdown();
    logger.info("[SERVER][STOP] about to call executorService.awaitTermination(5, SECONDS)");
    executorService.awaitTermination(5, SECONDS);
    logger.info("[SERVER][STOP] about to call executorService.shutdownNow()");
    executorService.shutdownNow();
    logger.info("[SERVER][STOP] about to call executorService.awaitTermination(5, SECONDS)");
    executorService.awaitTermination(5, SECONDS);

    logger.info("[SERVER][STOP] about to call shutdown()");
    grpcServer.shutdown();
    logger.info("[SERVER][STOP] successfully called shutdown() - about to call awaitTermination()");
    grpcServer.awaitTermination(5, SECONDS);
    logger.info("[SERVER][STOP] about to call shutdownNow()");
    grpcServer.shutdownNow();
    //    logger.info("[SERVER][STOP] listAllThreads");
    //    GameServerAndGameClients.listAllThreads();
    logger.info("[SERVER][STOP] about to call awaitTermination()");
    grpcServer.awaitTermination();
    grpcServer = null;
    logger.info("[SERVER] grpcServer.shutdown() success");
  }

  public void blockUntilShutdown() throws InterruptedException {
    logger.info("[SERVER] blockUntilShutdown grpcServer {}", grpcServer);
    if (grpcServer != null) {
      grpcServer.awaitTermination();
      logger.info("[SERVER] grpcServer.awaitTermination() success");
    }
  }

  private class GameStateServiceImpl extends GameStateServiceGrpc.GameStateServiceImplBase {
    @Override
    public StreamObserver<GameService.PlayerState> streamGameState(
        StreamObserver<GameState> responseObserver) {
      final int playerNumber = playerCount.getAndIncrement();
      logger.info("[SERVER] new PID {}", playerNumber);
      playerStateStreams.put(playerNumber, responseObserver);
      playerStates.put(playerNumber, GameService.PlayerState.newBuilder().build());
      logger.info("[SERVER] {} player state streams", playerStateStreams.keySet().size());
      logger.info("[SERVER] {} player states", playerStates.keySet().size());

      // Generate a new Server Sender
      return new StreamObserver<>() {
        @Override
        public void onNext(GameService.PlayerState playerState) {
          logger.info(
              "[SERVER][RECEIVE] PID {} X {} Y {} Z {}",
              playerState.getNumber(),
              playerState.getCoordinates().getX(),
              playerState.getCoordinates().getY(),
              playerState.getCoordinates().getZ());

          switch (playerState.getNumber()) {
            case 1:
              Player1State = playerState;
              break;
            case 2:
              Player2State = playerState;
              break;
            case 3:
              Player3State = playerState;
              break;
            case 4:
              Player4State = playerState;
              break;
            case 5:
              Player5State = playerState;
              break;
            case 6:
              Player6State = playerState;
              break;
            case 7:
              Player7State = playerState;
              break;
            case 8:
              Player8State = playerState;
              break;
            case 9:
              Player9State = playerState;
              break;
            case 10:
              Player10State = playerState;
              break;
          }

          GameState gameState =
              GameState.newBuilder()
                  .setPlayer1(Player1State)
                  .setPlayer2(Player2State)
                  .setPlayer3(Player3State)
                  .setPlayer4(Player4State)
                  .setPlayer5(Player5State)
                  .setPlayer6(Player6State)
                  .setPlayer7(Player7State)
                  .setPlayer8(Player8State)
                  .setPlayer9(Player9State)
                  .setPlayer10(Player10State)
                  .build();

          logger.info("[SERVER][SEND] PID {}", playerState.getNumber());

          StreamObserver<GameState> playerStateStream =
              playerStateStreams.get(playerState.getNumber());
          if (playerStateStream != null) {
            playerStateStream.onNext(gameState);
          }
        }

        @Override
        public void onError(Throwable t) {
          logger.error("[SERVER][RECEIVE] ERROR {}", t.toString());
          t.printStackTrace();
          playerStateStreams.remove(playerNumber);
          playerStates.put(playerNumber, null);
        }

        @Override
        public void onCompleted() {
          logger.info("[SERVER][RECEIVE] COMPLETED");
          responseObserver.onCompleted();
          playerStateStreams.remove(playerNumber);
          playerStates.put(playerNumber, null);
        }
      };
    }
  }
}
