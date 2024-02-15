import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameClients {
  private static final Logger logger = LoggerFactory.getLogger(GameClients.class);
  private static final List<GameClient> clients = Collections.synchronizedList(new ArrayList<>());

  GameClients() throws InterruptedException {
    for (int x = 0; x < GameServerAndGameClients.MAX_PLAYERS; x++) {
      logger.info(
          "Attempting to create {} of {} GameClient instances",
          x,
          GameServerAndGameClients.MAX_PLAYERS);
      GameClient newClient = new GameClient("localhost", GameServer.PORT);
      newClient.startGame();
      logger.info("Successfully called .startGame() for GameClient {}", x);
      clients.add(newClient);
    }
  }

  public List<GameClient> getClients() {
    return clients;
  }
}
