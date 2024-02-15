import game.GameService;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerAndGameClients {
  public static final int MAX_PLAYERS = 2;
  public static final int MAX_POS = 1000;
  public static final int MAX_MOVES = 2;
  private static final Logger logger = LoggerFactory.getLogger(GameServerAndGameClients.class);
  private static final Random random = new Random();

  public static void main(String[] args) {
    logger.info("Initializing GameServer");
    GameServer gameServer = new GameServer();

    try {
      gameServer.start();
      logger.info("Sleeping for 3s before creating GameClients");
      Thread.sleep(3000);
      logger.info("Initializing GameClients");
      GameClients gameClients = new GameClients();
      Thread.sleep(10000);
      Map<Integer, GameClient> clientsMap = gameClients.getClients();
      // shutdown the game first
      clientsMap.get(1).shutdown();
      gameServer.stop();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      System.err.println(e);
    }

    logger.info("end of main reached");
  }

  public static GameService.PlayerState getDefaultState(int playerNumber) {
    return GameService.PlayerState.newBuilder()
        .setConnected(true)
        .setNumber(playerNumber)
        .setCoordinates(
            GameService.Coordinates.newBuilder()
                .setX(getRandomMiddleCoordinates())
                .setY(getRandomMiddleCoordinates())
                .setZ(getRandomMiddleCoordinates())
                .build())
        .build();
  }

  public static GameService.PlayerState doRandomAction(GameService.PlayerState playerState) {
    return GameService.PlayerState.newBuilder(playerState)
        .setCoordinates(doRandomMovement(playerState.getCoordinates()))
        .build();
  }

  public static GameService.Coordinates doRandomMovement(GameService.Coordinates coordinates) {
    // Otherwise, randomly nudge the player along the X, Y, and Z axis by 5
    // Unless that would put the player out of bounds, then correct by moving 100 in the other
    // direction
    return GameService.Coordinates.newBuilder()
        .setX(playerMoves(coordinates.getX()))
        .setY(playerMoves(coordinates.getY()))
        .setZ(playerMoves(coordinates.getZ()))
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
