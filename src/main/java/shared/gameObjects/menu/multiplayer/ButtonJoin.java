package shared.gameObjects.menu.multiplayer;

import client.handlers.networkHandlers.ConnectionHandler;
import client.main.Client;
import client.main.Settings;
import java.util.UUID;
import javafx.scene.Group;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.menu.ButtonObject;
import shared.handlers.levelHandler.Map;
import shared.util.Path;

/**
 * Button to take the client to the join menu
 */
public class ButtonJoin extends ButtonObject {

  private transient TextField addressInput;

  /**
   * Base class used to create an object in game. This is used on both the client and server side to
   * ensure actions are calculated the same
   *
   * @param x X coordinate of object in game world
   * @param y Y coordinate of object in game world
   * @param id Unique Identifier of every game object
   */
  public ButtonJoin(double x, double y, double sizeX, double sizeY, ObjectType id,
      UUID objectUUID) {
    super(x, y, sizeX, sizeY, "Join", id, objectUUID);
  }

  /**
   * Setup button and text field for address input
   *
   * @param root Game root to render to
   * @param settings Settings
   */
  public void initialise(Group root, Settings settings) {
    super.initialise(root, settings);
    addressInput = new TextField();
    addressInput.setTranslateX(getX() + 90);
    addressInput.setTranslateY(getY() + 120);
    root.getChildren().add(addressInput);
  }


  /**
   * When button clicked start multiplayer connection
   *
   * @param e Mouse event
   */
  public void doOnClick(MouseEvent e) {
    super.doOnClick(e);
    Client.connectionHandler = new ConnectionHandler(addressInput.getText());
    Client.connectionHandler.setDaemon(true);
    Client.connectionHandler.start();
    button.disarm();
    root.getChildren().remove(addressInput);
    settings.getLevelHandler().changeMap(
        new Map("LOBBY", Path.convert("src/main/resources/menus/lobby.map")),
        false, false);
  }

  @Override
  public void removeRender() {
    super.removeRender();
    root.getChildren().remove(addressInput);
  }
}
