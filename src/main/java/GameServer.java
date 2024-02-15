import game.GameService;
import game.GameService.GameState;
import game.GameStateServiceGrpc;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class GameServer {
  public static final int PORT = 50051; // Example port number
  // A list of all of the player's GRPC streams
  private Map<Integer, StreamObserver<GameState>> playerStateStreams = Collections.synchronizedMap(new HashMap<>(10));
  private Map<Integer, GameService.PlayerState> playerStates = Collections.synchronizedMap(new HashMap<>(10));
  private io.grpc.Server grpcServer;
  private AtomicInteger playerCount = new AtomicInteger(0);
  private GameState gameState = GameState.newBuilder().build();

  public static void main(String[] args) throws IOException, InterruptedException {
    final GameServer server = new GameServer();
    server.start();
    server.blockUntilShutdown();
  }

  public void start() throws IOException {
    grpcServer = ServerBuilder.forPort(PORT)
        .addService(new GameStateServiceImpl())
        .executor(Executors.newFixedThreadPool(10)) // Pool size matches the number of players
        .build()
        .start();

    System.out.println("Server started, listening on " + PORT);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("*** shutting down gRPC server since JVM is shutting down");
      GameServer.this.stop();
      System.err.println("*** server shut down");
    }));
  }

  public void stop() {
    if (grpcServer != null) {
      grpcServer.shutdown();
    }
  }


  public void blockUntilShutdown() throws InterruptedException {
    if (grpcServer != null) {
      grpcServer.awaitTermination();
    }
  }

  private class GameStateServiceImpl extends GameStateServiceGrpc.GameStateServiceImplBase {
    @Override
    public StreamObserver<GameService.PlayerState> streamGameState(StreamObserver<GameState> responseObserver) {
      // Register the new player and assign them a unique player number
      final int playerNumber = playerCount.getAndIncrement();
      playerStateStreams.put(playerNumber, responseObserver);
      playerStates.put(playerNumber, GameService.PlayerState.newBuilder().build());


      // Return a new StreamObserver to handle incoming PlayerState messages from the client
      return new StreamObserver<>() {
        @Override
        public void onNext(GameService.PlayerState playerState) {
          // Here, process the incoming playerState and update the game state accordingly
          // For example, update the player's location, status, etc. in your game logic
          GameState updatedGameState = generateUpdatedGameState(playerState);

          // Then, broadcast the updated GameState to all connected players
          for (Integer x : playerStateStreams.keySet()) {
            playerStateStreams.get(x).onNext(updatedGameState);
          }
        }

        @Override
        public void onError(Throwable t) {
          t.printStackTrace();
          // Handle the error, such as logging it and removing the player's stream
          System.err.println("Error in player stream: " + t.getMessage());
          playerStateStreams.remove(responseObserver);
          playerStates.put(playerNumber, null);
        }

        @Override
        public void onCompleted() {
          // Handle stream completion, such as by removing the player's stream
          playerStateStreams.remove(responseObserver);
          responseObserver.onCompleted();
          playerStates.put(playerNumber, null);
        }
      };
    }

    // Utility method to generate the updated GameState based on the received PlayerState
    // This needs to be implemented according to your specific game logic
    private GameState generateUpdatedGameState(GameService.PlayerState playerState) {
      System.out.println("Received PlayerState " + playerState);

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

      System.out.println("GameState is now " + gameState);

      return gameState;
    }
  }
}
