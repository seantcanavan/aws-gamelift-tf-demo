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
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServer {
  private io.grpc.Server grpcServer;
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
  private final AtomicInteger playerCount = new AtomicInteger(0);

  /**
   * The global game state. Defaults to empty. TODO(Canavan): this probably needs a different
   * constructor for initial state
   */
  private GameState gameState = GameState.newBuilder().build();

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
            .executor(
                Executors.newFixedThreadPool(
                    GameServerAndGameClients
                        .MAX_PLAYERS)) // Pool size matches the number of players
            .build()
            .start();

    logger.info("[SERVER] grpcServer.start() called on port {}", PORT);

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  logger.error("*** shutting down gRPC server since JVM is shutting down");
                  GameServer.this.stop();
                  logger.error("*** server shut down");
                }));
  }

  public void stop() {
    logger.info("[SERVER] stop() grpcServer {}", grpcServer);
    if (grpcServer != null) {
      grpcServer.shutdown();
      grpcServer = null;
      logger.info("[SERVER] grpcServer.shutdown() success");
    }
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

      // Return a new StreamObserver to handle incoming PlayerState messages from the client
      return new StreamObserver<>() {
        @Override
        public void onNext(GameService.PlayerState playerState) {
          logger.info(
              "[SERVER][RECEIVE] PID {} X {} Y {} Z {}",
              playerState.getNumber(),
              playerState.getCoordinates().getX(),
              playerState.getCoordinates().getY(),
              playerState.getCoordinates().getZ());
          // TODO(Canavan): make this multi-threaded safe
          generateUpdatedGameState(playerState);
          // Then, broadcast the updated GameState to all connected players
          for (Integer x : playerStateStreams.keySet()) {
            logger.info("[SERVER][SEND] PID {} GameState {}", x, gameState);
            playerStateStreams.get(x).onNext(gameState);
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

    // Utility method to generate the updated GameState based on the received PlayerState
    // This needs to be implemented according to your specific game logic
    private void generateUpdatedGameState(GameService.PlayerState playerState) {
      if (playerState.getNumber() == 1) {
        gameState = GameState.newBuilder(gameState).setPlayer1(playerState).build();
      } else if (playerState.getNumber() == 2) {
        gameState = GameState.newBuilder(gameState).setPlayer2(playerState).build();
      } else if (playerState.getNumber() == 3) {
        gameState = GameState.newBuilder(gameState).setPlayer3(playerState).build();
      } else if (playerState.getNumber() == 4) {
        gameState = GameState.newBuilder(gameState).setPlayer4(playerState).build();
      } else if (playerState.getNumber() == 5) {
        gameState = GameState.newBuilder(gameState).setPlayer5(playerState).build();
      } else if (playerState.getNumber() == 6) {
        gameState = GameState.newBuilder(gameState).setPlayer6(playerState).build();
      } else if (playerState.getNumber() == 7) {
        gameState = GameState.newBuilder(gameState).setPlayer7(playerState).build();
      } else if (playerState.getNumber() == 8) {
        gameState = GameState.newBuilder(gameState).setPlayer8(playerState).build();
      } else if (playerState.getNumber() == 9) {
        gameState = GameState.newBuilder(gameState).setPlayer9(playerState).build();
      } else if (playerState.getNumber() == 10) {
        gameState = GameState.newBuilder(gameState).setPlayer10(playerState).build();
      }
    }
  }
}
