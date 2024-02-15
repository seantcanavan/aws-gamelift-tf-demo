import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameClients {
  private static final int MAX_CLIENT_INSTANCES = 1;
  //  private static final AtomicInteger firstClientPort = new AtomicInteger(GameServer.PORT);
  private static final List<GameClient> clients = Collections.synchronizedList(new ArrayList<>());

  GameClients() throws InterruptedException {
    for (int x = 0; x < MAX_CLIENT_INSTANCES; x++) {
//      GameClient newClient = new GameClient("localhost", firstClientPort.addAndGet(1));
      GameClient newClient = new GameClient("localhost", GameServer.PORT);
      newClient.startGame();
      clients.add(newClient);
    }
  }

  public List<GameClient> getClients() {
    return clients;
  }
}
