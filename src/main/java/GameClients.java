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
      GameClient newClient = new GameClient("localhost", GameServer.PORT);
      newClient.startGame(x);
      logger.info("Successfully called .startGame() for GameClient {}", x);
      clientsMap.put(x, newClient);
    }
  }

  //  public void createAndStartGameClients() {
  //    int numClients = GameServerAndGameClients.MAX_PLAYERS;
  //    ExecutorService executor = Executors.newFixedThreadPool(numClients); // Create a thread pool
  //
  //    for (int x = 1; x <= numClients; x++) {
  //      final int clientId = x; // Effectively final for use in lambda
  //      executor.submit(() -> {
  //        logger.info("Attempting to create {} of {} GameClient instances", clientId, numClients);
  //        try {
  //          GameClient newClient = new GameClient("localhost", GameServer.PORT);
  //          newClient.startGame(clientId);
  //          logger.info("Successfully called .startGame() for GameClient {}", clientId);
  //          clients.add(newClient);
  //        } catch (Exception e) {
  //          logger.error("Error creating or starting GameClient {}", clientId, e);
  //        }
  //      });
  //    }
  //
  //    executor.shutdown(); // No new tasks will be accepted
  //    try {
  //      // Wait for all tasks to finish execution or timeout after a certain period
  //      if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
  //        logger.warn("Some tasks did not finish before the timeout");
  //      }
  //    } catch (InterruptedException e) {
  //      logger.error("Interrupted while waiting for game clients to start", e);
  //      Thread.currentThread().interrupt();
  //    }
  //  }

  public Map<Integer, GameClient> getClients() {
    return clientsMap;
  }
}
