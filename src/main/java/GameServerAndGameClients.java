import game.GameService;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class GameServerAndGameClients {
  public static final int MAX_PLAYERS = 10;
  public static final int MAX_POS = 1000;
  public static final int MAX_MOVES = 100;
  private static final Random random = new Random();

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
      List<GameClient> clientsList = gameClients.getClients();
      // shutdown the game first
      clientsList.get(0).shutdown();
      gameServer.stop();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      System.err.println(e);
    }

    System.out.println("end of main reached");
  }

  public static GameService.PlayerState getDefaultState() {
    return GameService.PlayerState.newBuilder()
        .setConnected(true)
        .setNumber(0)
        .setCoordinates(GameService.Coordinates.newBuilder().setX(0).setY(0).setZ(0).build())
        .build();
  }

  public static GameService.PlayerState doRandomAction(GameService.PlayerState playerState) {
    return GameService.PlayerState.newBuilder(playerState)
        .setCoordinates(doRandomMovement(playerState.getCoordinates()))
        .build();
  }

  public static GameService.Coordinates doRandomMovement(GameService.Coordinates coordinates) {
    int x = coordinates.getX();
    int y = coordinates.getY();
    int z = coordinates.getZ();

    // handle the default state where the player starts at 0, 0, 0
    if (x == 0 && y == 0 && z == 0) {
      System.out.println("Detected 0, 0, 0. Returning middle-ish coordinates");
      return GameService.Coordinates.newBuilder()
          .setX(getRandomMiddleCoordinates())
          .setY(getRandomMiddleCoordinates())
          .setZ(getRandomMiddleCoordinates())
          .build();
    }

    // Otherwise, randomly nudge the player along the X, Y, and Z axis by 5
    // Unless that would put the player out of bounds, then correct by moving 100 in the other
    // direction
    return GameService.Coordinates.newBuilder()
        .setX(playerMoves(x))
        .setY(playerMoves(y))
        .setZ(playerMoves(z))
        .build();
  }

  public static int getRandomMiddleCoordinates() {
    // Starting value
    int base = 500;
    // Generate either 0 (subtract) or 1 (add)
    boolean add = random.nextBoolean();
    // Random value between 1 and 100
    int change = random.nextInt(100) + 1;
    // Apply the change based on the add flag
    return add ? base + change : base - change;
  }

  public static int playerMoves(int pos) {
    // 50% chance to either add or subtract 5
    boolean add = random.nextBoolean();
    if (add) {
      // If adding exceeds MAX_POS, subtract 100 instead
      if (pos + 5 > MAX_POS) {
        return pos - 100;
      } else {
        return pos + 5;
      }
    } else {
      // If subtracting goes below 0, add 100 instead
      if (pos - 5 < 0) {
        return pos + 100;
      } else {
        return pos - 5;
      }
    }
  }
}
