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
  private Map<Integer, StreamObserver<GameState>> playerStateStreams = Collections.synchronizedMap(new HashMap<>(GameServerAndGameClients.MAX_PLAYERS));
  private Map<Integer, GameService.PlayerState> playerStates = Collections.synchronizedMap(new HashMap<>(GameServerAndGameClients.MAX_PLAYERS));
  private io.grpc.Server grpcServer;
  private AtomicInteger playerCount = new AtomicInteger(0);
  private GameState gameState = GameState.newBuilder().build();

  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("GameServer.main called");
    final GameServer server = new GameServer();
    server.start();
    server.blockUntilShutdown();
  }

  public void start() throws IOException {
    System.out.println("GameServer.start() called");
    grpcServer = ServerBuilder.forPort(PORT)
        .addService(new GameStateServiceImpl())
        .executor(Executors.newFixedThreadPool(GameServerAndGameClients.MAX_PLAYERS)) // Pool size matches the number of players
        .build()
        .start();

    System.out.println("Successfully started GameServer on port " + PORT);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.err.println("*** shutting down gRPC server since JVM is shutting down");
      GameServer.this.stop();
      System.err.println("*** server shut down");
    }));
  }

  public void stop() {
    System.out.println("GameServer.stop() - successfully called");
    if (grpcServer != null) {
      System.out.println("GameServer.stop() - grpcServer is not null - shutting down");
      grpcServer.shutdown();
      System.out.println("GameServer.stop() - successfully shutdown grpcServer");
      grpcServer = null;
    }
  }

  public void blockUntilShutdown() throws InterruptedException {
    System.out.println("GameServer.blockUntilShutdown - successfully called");
    if (grpcServer != null) {
      System.out.println("GameServer.blockUntilShutdown - grpcServer is not null - awaiting termination");
      grpcServer.awaitTermination();
      System.out.println("GameServer.blockUntilShutdown - termination awaited successfully");
    }
  }

  private class GameStateServiceImpl extends GameStateServiceGrpc.GameStateServiceImplBase {
    @Override
    public StreamObserver<GameService.PlayerState> streamGameState(StreamObserver<GameState> responseObserver) {
      // Register the new player and assign them a unique player number
      final int playerNumber = playerCount.getAndIncrement();
      System.out.println("public StreamObserver<GameService.PlayerState> streamGameState - retrieved player number " + playerNumber);
      playerStateStreams.put(playerNumber, responseObserver);
      playerStates.put(playerNumber, GameService.PlayerState.newBuilder().build());


      // Return a new StreamObserver to handle incoming PlayerState messages from the client
      return new StreamObserver<>() {
        @Override
        public void onNext(GameService.PlayerState playerState) {
          // Receive the new player state and update the internal game state appropriately
          System.out.println("StreamObserver<>().onNext() - received new playerState " + playerState);
          // TODO(Canavan): make this multi-threaded safe
          gameState = generateUpdatedGameState(playerState);
          System.out.println("StreamObserver<>().onNext() - updated game state with new playerState [" + gameState + "]");

          // Then, broadcast the updated GameState to all connected players
          for (Integer x : playerStateStreams.keySet()) {
            playerStateStreams.get(x).onNext(gameState);
          }
        }

        @Override
        public void onError(Throwable t) {
          System.out.println("StreamObserver<>().onError() - stream error on the server side");
          t.printStackTrace();
          // Handle the error, such as logging it and removing the player's stream
          System.err.println("Error in player stream: " + t.getMessage());
          playerStateStreams.remove(playerNumber);
          playerStates.put(playerNumber, null);
        }

        @Override
        public void onCompleted() {
          System.out.println("StreamObserver<>().onCompleted() - function was called");
          // Handle stream completion, such as by removing the player's stream
          playerStateStreams.remove(playerNumber);
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
