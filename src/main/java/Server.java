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
  private List<StreamObserver<GameService.PlayerState>> playerStateStreams = Collections.synchronizedList(new ArrayList<>(10));
  private List<GameService.PlayerState> playerStates = Collections.synchronizedList(new ArrayList<>(10));
  private io.grpc.Server grpcServer;
  private AtomicInteger playerCount = new AtomicInteger(0);

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
    public StreamObserver<GameState> streamGameState(
        StreamObserver<GameService.PlayerState> responseObserver) {

      int playerNumber = playerCount.addAndGet(1);

      // Add the player's stream to the list
      playerStateStreams.set(playerNumber, responseObserver);


      return new StreamObserver<>() {
        @Override
        public void onNext(GameState gameState) {
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onCompleted() {
        }
      };
    }
  }
}
