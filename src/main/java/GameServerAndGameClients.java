import game.GameService;

import java.io.IOException;

public class GameServerAndGameClients {

  public static void main(String[] args) {
    System.out.println("Initializing GameServer");
    GameServer gameServer = new GameServer();


    try {
      gameServer.start();
      System.out.println("Sleeping for 3s before creating GameClients");
      Thread.sleep(3000);
      System.out.println("Initializing GameClients");
      GameClients gameClients = new GameClients();
      Thread.sleep(10000);
      gameServer.blockUntilShutdown();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      System.err.println(e);
    }
  }

  public static GameService.PlayerState getDefaultState() {
    return GameService.PlayerState.
        newBuilder().
        setConnected(true).
        setNumber(0).
        setCoordinates(GameService.Coordinates.
            newBuilder().
            setX(0).
            setY(0).
            setZ(0).
            build()).
        build();
  }
}
