import game.GameService;
import game.GameService.GameState;
import game.GameStateServiceGrpc;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
  private static final int PORT = 50051; // Example port number
  // A list of all of the player's GRPC streams
  private List<StreamObserver<GameState>> playerStateStreams = Collections.synchronizedList(new ArrayList<>(10));
  private List<GameService.PlayerState> playerStates = Collections.synchronizedList(new ArrayList<>(10));
  private io.grpc.Server grpcServer;
  private AtomicInteger playerCount = new AtomicInteger(0);
  private GameState gameState = GameState.newBuilder().build();

  public static void main(String[] args) throws IOException, InterruptedException {
    final Server server = new Server();
    server.start();
    server.blockUntilShutdown();
  }

  private void start() throws IOException {
    grpcServer = ServerBuilder.forPort(PORT)
        .addService(new GameStateServiceImpl())
        .executor(Executors.newFixedThreadPool(10)) // Pool size matches the number of players
        .build()
        .start();

    System.out.println("Server started, listening on " + PORT);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("*** shutting down gRPC server since JVM is shutting down");
      Server.this.stop();
      System.err.println("*** server shut down");
    }));
  }

  private void stop() {
    if (grpcServer != null) {
      grpcServer.shutdown();
    }
  }


  private void blockUntilShutdown() throws InterruptedException {
    if (grpcServer != null) {
      grpcServer.awaitTermination();
    }
  }

  private class GameStateServiceImpl extends GameStateServiceGrpc.GameStateServiceImplBase {


    @Override
    public StreamObserver<GameService.PlayerState> streamGameState(StreamObserver<GameState> responseObserver) {
      // Register the new player and assign them a unique player number
      final int playerNumber = playerCount.getAndIncrement();
      playerStateStreams.set(playerNumber, responseObserver);
      playerStates.set(playerNumber, GameService.PlayerState.newBuilder().build());


      // Return a new StreamObserver to handle incoming PlayerState messages from the client
      return new StreamObserver<>() {
        @Override
        public void onNext(GameService.PlayerState playerState) {
          // Here, process the incoming playerState and update the game state accordingly
          // For example, update the player's location, status, etc. in your game logic

          // Then, broadcast the updated GameState to all connected players
          GameState updatedGameState = generateUpdatedGameState(playerState); // Implement this based on your game logic
          for (StreamObserver<GameState> gameStateStream : playerStateStreams) {
            gameStateStream.onNext(updatedGameState);
          }
        }

        @Override
        public void onError(Throwable t) {
          // Handle the error, such as logging it and removing the player's stream
          System.err.println("Error in player stream: " + t.getMessage());
          playerStateStreams.remove(responseObserver);
          playerStates.set(playerNumber, null);
        }

        @Override
        public void onCompleted() {
          // Handle stream completion, such as by removing the player's stream
          playerStateStreams.remove(responseObserver);
          responseObserver.onCompleted();
          playerStates.set(playerNumber, null);
        }
      };
    }

    // Utility method to generate the updated GameState based on the received PlayerState
    // This needs to be implemented according to your specific game logic
    private GameState generateUpdatedGameState(GameService.PlayerState playerState) {
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

      return gameState;
    }
  }
}
