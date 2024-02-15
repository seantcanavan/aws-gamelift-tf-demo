import game.GameService;

public abstract class GameState {
  private GameService.GameState gameState;

  public synchronized void setGameState(GameService.GameState gameState) {
    this.gameState = gameState;
  }

  public synchronized GameService.GameState getGameState() {
    return gameState;
  }
}
