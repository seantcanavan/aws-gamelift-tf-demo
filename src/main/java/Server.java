import game.GameService.PlayerActionUpdate;
import game.GameService.PlayerActionUpdates;
import game.GameStateServiceGrpc;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class Server {

  private final int port = 50051; // Example port number
  // List to hold and manage player streams
  private final List<StreamObserver<PlayerActionUpdate>> playerStreams = new ArrayList<>();
  private io.grpc.Server grpcServer;

  public static void main(String[] args) throws IOException, InterruptedException {
    final Server server = new Server();
    server.start();
    server.blockUntilShutdown();
  }

  private void start() throws IOException {
    grpcServer = ServerBuilder.forPort(port)
        .addService(new GameStateServiceImpl())
        .executor(Executors.newFixedThreadPool(10)) // Pool size matches the number of players
        .build()
        .start();

    System.out.println("Server started, listening on " + port);

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
    public StreamObserver<PlayerActionUpdates> streamGameState(
        StreamObserver<PlayerActionUpdate> responseObserver) {

      // Add the player's stream to the list
      playerStreams.add(responseObserver);

      return new StreamObserver<PlayerActionUpdates>() {
        @Override
        public void onNext(PlayerActionUpdates updates) {
          // Handle incoming player action updates
          // For simplicity, echoing back the received updates to all players
          updates.getPlayerActionUpdatesList().forEach(update -> {
            playerStreams.forEach(stream -> stream.onNext(update));
          });
        }

        @Override
        public void onError(Throwable t) {
          System.err.println("Error in stream: " + t.getMessage());
          // Remove the stream on error
          playerStreams.remove(responseObserver);
        }

        @Override
        public void onCompleted() {
          // Close the stream for this player
          responseObserver.onCompleted();
          playerStreams.remove(responseObserver);
        }
      };
    }
  }
}
