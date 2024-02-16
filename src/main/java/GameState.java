import game.GameService;

public abstract class GameState {
  private GameService.GameState gameState;

  synchronized void setGameState(GameService.GameState gameState) {
    this.gameState = gameState;
  }

  synchronized GameService.GameState getGameState() {
    return gameState;
  }
}
