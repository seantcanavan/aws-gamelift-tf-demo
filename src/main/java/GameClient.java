import game.GameService;
import game.GameStateServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GameClient {
  private final ManagedChannel channel;
  private final GameStateServiceGrpc.GameStateServiceStub asyncStub;

  // Constructor to initialize the channel and stub
  public GameClient(String host, int port) {
    this.channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build();
    this.asyncStub = GameStateServiceGrpc.newStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
  }

  // Method to start the bidirectional streaming
  public void startGame() throws InterruptedException {
    StreamObserver<GameService.PlayerState> requestObserver = asyncStub.streamGameState(new StreamObserver<GameService.GameState>() {
      @Override
      public void onNext(GameService.GameState gameState) {
        // Handle incoming GameState updates
        System.out.println("Game state updated: " + gameState);
      }

      @Override
      public void onError(Throwable t) {
        t.printStackTrace();
        System.err.println("Error in GameState stream: " + t.getMessage());
      }

      @Override
      public void onCompleted() {
        // Server has completed the stream
        System.out.println("Stream completed by server.");
      }
    });

    try {
      // Example of sending a PlayerState message
      GameService.PlayerState playerState = GameServerAndGameClients.getDefaultState();
      requestObserver.onNext(playerState);

      // More PlayerState messages can be sent through requestObserver

      // Mark the end of requests
      requestObserver.onCompleted();

      // Wait a bit for responses before shutting down
      Thread.sleep(10000);
    } catch (RuntimeException | InterruptedException e) {
      requestObserver.onError(e);
      throw e;
    }
  }
}
