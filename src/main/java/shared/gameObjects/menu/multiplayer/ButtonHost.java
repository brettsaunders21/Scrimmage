package shared.gameObjects.menu.multiplayer;

import client.Menu;
import client.main.Client;
import java.util.UUID;
import javafx.scene.input.MouseEvent;
import shared.gameObjects.Utils.ObjectID;
import shared.gameObjects.menu.ButtonObject;
import shared.handlers.levelHandler.GameState;
import shared.handlers.levelHandler.Map;

public class ButtonHost extends ButtonObject {

  /**
   * Base class used to create an object in game. This is used on both the client and server side to
   * ensure actions are calculated the same
   *
   * @param x X coordinate of object in game world
   * @param y Y coordinate of object in game world
   * @param id Unique Identifier of every game object
   */
  public ButtonHost(double x, double y, ObjectID id, UUID objectUUID) {
    super(x, y, 50, 50, id, objectUUID);
  }

  @Override
  public void initialiseAnimation() {
    super.initialiseAnimation("images/buttons/multiplayer_unpressed.png",
        "images/buttons/multiplayer_pressed.png");
  }

  public void doOnClick(MouseEvent e) {
    super.doOnClick(e);
    Client.levelHandler.changeMap(new Map("Host", Menu.HOST.getMenuPath(), GameState.MAIN_MENU));
  }
}