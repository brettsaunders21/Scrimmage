package shared.gameObjects.menu.main;

import client.main.Client;
import java.util.UUID;
import javafx.scene.input.MouseEvent;
import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.menu.ButtonObject;
import shared.handlers.levelHandler.Map;
import shared.util.Path;

public class ButtonQuit extends ButtonObject {

  public ButtonQuit(
      double x, double y, double sizeX, double sizeY, ObjectType id, UUID objectUUID) {
    super(x, y, sizeX, sizeY, "QUIT", id, objectUUID);
  }

  public void doOnClick(MouseEvent e) {
    super.doOnClick(e);

    //action
    // SINGLEPLAYER -> MAIN MENU
    // MULTIPLAYER -> MAIN MENU
    // MAIN MENU -> CLOSE
    switch (settings.getLevelHandler().getMap().getGameState()) {
      case IN_GAME:
        // clear bots
        Client.endGame();
      case LOBBY:
      case START_CONNECTION:
      case MULTIPLAYER:
      case ACCOUNT:
        settings.getLevelHandler().changeMap(
            new Map("menus/main_menu.map", Path.convert("src/main/resources/menus/main_menu.map")),
            true, false);
        break;
      case MAIN_MENU:
        System.exit(0);
        break;
      default:
        break;
    }
  }
}
