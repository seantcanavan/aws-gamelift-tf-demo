import game.GameService;
import game.GameStateServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GameClient {
  private final ManagedChannel channel;
  private final GameStateServiceGrpc.GameStateServiceStub asyncStub;
  private GameService.GameState gameState = GameService.GameState.newBuilder().build();
  private GameService.PlayerState playerState = GameService.PlayerState.newBuilder().build();

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
    StreamObserver<GameService.PlayerState> requestObserver = asyncStub.streamGameState(new StreamObserver<>() {
      @Override
      public void onNext(GameService.GameState newGameState) { // Handle incoming GameState updates from the server
        System.out.println("startGame().onNext() - called with newGameState " + newGameState.toString());
        gameState = newGameState;
        System.out.println("startGame().onNext() - local gameState is now " + gameState);
      }

      @Override
      public void onError(Throwable t) { // Handle errors for the client game stream
        System.out.println("startGame().onError() - called with Throwable " + t);
        t.printStackTrace();
      }

      @Override
      public void onCompleted() { // Handle the stream closing for the client game stream
        System.out.println("startGame().onCompleted() - called");
        try {
          shutdown();
          System.out.println("startGame().onCompleted() - shutdown success");
        } catch (InterruptedException e) {
          System.out.println("startGame().onCompleted() - shutdown failed");
          e.printStackTrace();
        }
      }
    });

//    try {
    // Example of sending a PlayerState message
    System.out.println("Initializing default state");
    GameService.PlayerState defaultState = GameServerAndGameClients.getDefaultState();
    System.out.println("Sending default state " + defaultState);
    requestObserver.onNext(defaultState);
    System.out.println("Default state sent " + defaultState);

    GameService.PlayerState randomState;

    for (int x = 0; x < 100; x++) {
      randomState = GameServerAndGameClients.doRandomAction(defaultState);
      System.out.println("Generated [" + x + "] of [" + GameServerAndGameClients.MAX_MOVES + "] newRandomState on the Client Side " + randomState);
      playerState = randomState;
      requestObserver.onNext(playerState);
      System.out.println("Sent playerState " + playerState);
    }


    // Mark the end of requests
//      requestObserver.onCompleted();

    // Wait a bit for responses before shutting down
//      Thread.sleep(10000);
//    } catch (RuntimeException | InterruptedException e) {
//    } catch (RuntimeException e) {
//      requestObserver.onError(e);
//      throw e;
//    }
  }
}
