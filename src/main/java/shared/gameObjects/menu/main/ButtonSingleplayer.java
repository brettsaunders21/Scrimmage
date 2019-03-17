package shared.gameObjects.menu.main;

import client.main.Client;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import javafx.scene.input.MouseEvent;
import server.ai.Bot;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.menu.ButtonObject;
import shared.gameObjects.weapons.MachineGun;
import shared.handlers.levelHandler.Map;
import shared.util.Path;

public class ButtonSingleplayer extends ButtonObject {

  private final int maxPlayers = 2;

  /**
   * Base class used to create an object in game. This is used on both the client and server side to
   * ensure actions are calculated the same
   *
   * @param x X coordinate of object in game world
   * @param y Y coordinate of object in game world
   * @param id Unique Identifier of every game object
   */
  public ButtonSingleplayer(
      double x, double y, double sizeX, double sizeY, ObjectType id, UUID objectUUID) {
    super(x, y, sizeX, sizeY, "Singleplayer", id, objectUUID);
  }

  public void doOnClick(MouseEvent e) {
    super.doOnClick(e);

    int botsToAdd = maxPlayers - settings.getLevelHandler().getPlayers().size();
    for (int b = 0; b < botsToAdd; b++) {
      //TODO Change physics to LinkedHashMaps
      Collection<GameObject> values = settings.getLevelHandler().getGameObjects().values();
      ArrayList<GameObject> physicsGameObjects = new ArrayList<>(values);
      Bot botPlayer = new Bot(200, 600, UUID.randomUUID(), settings.getLevelHandler());
      botPlayer.initialise(settings.getGameRoot(), settings);
      settings.getLevelHandler().getPlayers().put(botPlayer.getUUID(), botPlayer);
      settings.getLevelHandler().getBotPlayerList().put(botPlayer.getUUID(), botPlayer);
      settings.getLevelHandler().getGameObjects().put(botPlayer.getUUID(), botPlayer);
      botPlayer.startThread();
    }
    settings.getLevelHandler().changeMap(
        new Map("map1", Path.convert("src/main/resources/maps/map1.map")),
        true, false);
    settings.getLevelHandler().getPlayers().forEach((uuid, player) -> {
      player.setHolding(
          new MachineGun(500, 600, "MachineGun@ButtonSinglePlayer", player, UUID.randomUUID()));
      player.getHolding().initialise(settings.getGameRoot(), settings);
      settings.getLevelHandler().getGameObjects()
          .put(player.getHolding().getUUID(), player.getHolding());
    });
    Client.singleplayerGame = true;
    //Client.timer.schedule(Client.task, 30000L);

  }
}
