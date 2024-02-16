import game.GameService;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServerAndGameClients {
  public static final int MAX_PLAYERS = 10;
  public static final int MAX_POS = 1000;
  private static final Logger logger = LoggerFactory.getLogger(GameServerAndGameClients.class);
  private static final Random random = new Random();
  private static final Map<Integer, GameClient> clientsMap =
      Collections.synchronizedMap(new HashMap<>());

  public static void main(String[] args) {
    GameServer gameServer = new GameServer();

    try {
      gameServer.start();
      gameServer.startLogging();
      for (int playerNumber = 1;
          playerNumber <= GameServerAndGameClients.MAX_PLAYERS;
          playerNumber++) {
        logger.info(
            "Attempting to create {} of {} GameClient instances",
            playerNumber,
            GameServerAndGameClients.MAX_PLAYERS);
        GameClient newClient = new GameClient("localhost", GameServer.PORT, playerNumber);
        //         initialize logging the game state
        newClient.startLogging();
        //         initialize sending of randomized local player state to the server
        newClient.start();
        clientsMap.put(playerNumber, newClient);
      }

      long millis = 5000;
      logger.info("main is sleeping for {} millis", millis);
      Thread.sleep(millis); // sleep to let clients send messages
      // Stop sending on all game clients and stop logging
      clientsMap
          .keySet()
          .forEach(
              key -> {
                GameClient currentClient = clientsMap.get(key);
                currentClient.stop();
                currentClient.stopLogging();
              });
      Thread.sleep(3000);
      gameServer.stopLogging();
      gameServer.stop();
    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      System.err.println(e);
    }
  }

  public static GameService.PlayerState getDefaultState(int playerNumber) {
    return GameService.PlayerState.newBuilder()
        .setConnected(true)
        .setNumber(playerNumber)
        .setCoordinates(
            GameService.Coordinates.newBuilder()
                .setX(getInitialCoordinates())
                .setY(getInitialCoordinates())
                .setZ(getInitialCoordinates())
                .build())
        .build();
  }

  public static GameService.PlayerState doRandomAction(GameService.PlayerState playerState) {
    return GameService.PlayerState.newBuilder(playerState)
        .setCoordinates(doRandomXYZMovement(playerState.getCoordinates()))
        .build();
  }

  public static GameService.Coordinates doRandomXYZMovement(GameService.Coordinates coordinates) {
    // Randomly nudge the player along the X, Y, and Z axis by 5
    // Unless that would put the player out of bounds, then correct by moving 100 in the other
    // direction
    return GameService.Coordinates.newBuilder()
        .setX(doRandomMove(coordinates.getX()))
        .setY(doRandomMove(coordinates.getY()))
        .setZ(doRandomMove(coordinates.getZ()))
        .build();
  }

  public static int getInitialCoordinates() {
    // Starting value
    int base = 500;
    // Generate either 0 (subtract) or 1 (add)
    boolean add = random.nextBoolean();
    // Random value between 1 and 100
    int change = random.nextInt(100) + 1;
    // Apply the change based on the add flag
    return add ? base + change : base - change;
  }

  public static int doRandomMove(int pos) {
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
