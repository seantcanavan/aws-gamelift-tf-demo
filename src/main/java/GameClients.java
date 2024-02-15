import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameClients {
  private static final Logger logger = LoggerFactory.getLogger(GameClients.class);
  private static final Map<Integer, GameClient> clientsMap =
      Collections.synchronizedMap(new HashMap<>());

  GameClients() throws InterruptedException {
    for (int x = 1; x <= GameServerAndGameClients.MAX_PLAYERS; x++) {
      logger.info(
          "Attempting to create {} of {} GameClient instances",
          x,
          GameServerAndGameClients.MAX_PLAYERS);
      GameClient newClient = new GameClient("localhost", GameServer.PORT, x);
      newClient.startPrinting();
      newClient.startGame(x);
      logger.info("Successfully called .startGame() for GameClient {}", x);
      clientsMap.put(x, newClient);
    }
  }

  public Map<Integer, GameClient> getClients() {
    return clientsMap;
  }
}
