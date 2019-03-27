package client.handlers.networkHandlers;

import client.main.Client;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.TimePosition;
import shared.gameObjects.players.Limbs.Arm;
import shared.gameObjects.players.Player;
import shared.handlers.levelHandler.Map;
import shared.packets.PacketDelete;
import shared.packets.PacketGameState;
import shared.packets.PacketInput;
import shared.util.Path;
import shared.util.maths.Vector2;

public class ClientNetworkManager {

  private static final boolean prediction = false; //Broken
  private static final boolean reconciliation = true;
  private static final boolean setStateSnap = true; //Broken
  private static final boolean entity_interpolation = true;

  public static void interpolateEntities() {
    Timestamp now = new Timestamp(System.currentTimeMillis() - (1000 / 60));
    //Need to calculate render timestamp

    Client.levelHandler.getGameObjects().forEach((key, gameObject) -> {
      //Find the two authoritative positions surrounding the rendering timestamp
      ArrayList<TimePosition> buffer = gameObject.getPositionBuffer();

      if (gameObject.getUUID() != Client.levelHandler.getClientPlayer().getUUID() && buffer != null
          && buffer.size() > 0) {

        //Drop older positions
        while (buffer.size() >= 2 && buffer.get(1).getTimestamp().before(now)) {
          buffer.remove(0);
        }

        //Interpolate between the two surrounding  authoritative  positions to smooth motion
        if (buffer.size() >= 2 && buffer.get(0).getTimestamp().before(now) && now
            .before(buffer.get(1).getTimestamp())) {
          Vector2 pos0 = buffer.get(0).getPosition();
          Vector2 pos1 = buffer.get(1).getPosition();
          Long t0 = buffer.get(0).getTimestamp().getTime();
          Long t1 = buffer.get(1).getTimestamp().getTime();
          Long tnow = now.getTime();

          gameObject
              .setX(pos0.getX() + (pos1.getX() - pos0.getX()) * (tnow - t0) / (t1 - t0));
          gameObject
              .setY(pos0.getY() + (pos1.getY() - pos0.getY()) * (tnow - t0) / (t1 - t0));
        }
      }
    });
  }

  public static void serverReconciliation(int lastProcessedInput) {
    int j = 0;
    // Server Reconciliation. Re-apply all the inputs not yet processed by
    // the server.
    while (j < Client.pendingInputs.size()) {
      if (Client.inputSequenceNumber - 1 <= lastProcessedInput) {
        // Already processed. Its effect is already taken into account into the world update
        // we just got so drop it
        Client.pendingInputs.remove(j);
      } else {
        Player player = Client.levelHandler.getClientPlayer();
        PacketInput input = Client.pendingInputs.get(j);
        // Not processed by the server yet. Re-apply it.
        player.mouseY = input.getY();
        player.mouseX = input.getX();
        player.jumpKey = input.isJumpKey();
        player.leftKey = input.isLeftKey();
        player.rightKey = input.isRightKey();
        player.click = false; // Don't want extra bullets
        player.applyInput();
        j++;
      }
    }
  }

  public static void processServerPackets() {
    if (Client.connectionHandler.received.size() != 0) {
      try {
        String message = (String) Client.connectionHandler.received.take();
        int messageID = Integer.parseInt(message.substring(0, 1));
        switch (messageID) {
          // Ends
          case 6:
            Client.connectionHandler.end();
            Client.connectionHandler = null;
            // Show score board
            Client.multiplayer = false;
            Client.levelHandler.changeMap(
                new Map(
                    "main_menu",
                    Path.convert(
                        Client.settings.getMenuPath() + File.separator + "menus/main_menu.map")),
                false, false);

            break;
          case 8:
            PacketDelete delete = new PacketDelete(message);
            GameObject deleteObject = Client.levelHandler.getGameObjects()
                .get(delete.getGameobject());
            if (deleteObject == null) {
              System.out.println("Can't delete " + delete.getGameobject());
            } else {
              Client.levelHandler.removeGameObject(deleteObject);
            }
            break;
          case 7:
            PacketGameState gameState = new PacketGameState(message);
            HashMap<UUID, String> data = gameState.getGameObjects();
            data.forEach((key, value) -> {
              if (!value.split(";")[1].equals("Fist")
                  && !value.split(";")[1].equals("WeaponSpawner")) {
                GameObject gameObject = Client.levelHandler.getGameObjects().get(key);
                if (gameObject == null) {
                  System.out.println("HMMM I've never seen this before " + value);
                } else {
                  if (!entity_interpolation || gameObject.getUUID() == Client.levelHandler
                      .getClientPlayer().getUUID()) {
                    gameObject.setState(value, setStateSnap);
                  } else {
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    String[] unpackedData = value.split(";");
                    Vector2 statePos = new Vector2(Double.parseDouble(unpackedData[2]),
                        Double.parseDouble(unpackedData[3]));
                    gameObject.getPositionBuffer().add(new TimePosition(now, statePos));
                  }
                }
              }
            });
            if (reconciliation) {
              serverReconciliation(Client.levelHandler.getClientPlayer().getLastInputCount());
            }
            break;
          default:
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }


  public static void createGameObjects(byte[] data) {
    ObjectInputStream objectInputStream = null;
    try {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
      objectInputStream = new ObjectInputStream(byteArrayInputStream);
      GameObject gameObjects = (GameObject) objectInputStream
          .readObject();
      Client.levelHandler.getToCreate().put(gameObjects.getUUID(), gameObjects);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      try {
        objectInputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void sendInput() {
    Player player = Client.levelHandler.getClientPlayer();
    PacketInput input =
        new PacketInput(
            player.mouseX,
            player.mouseY,
            player.leftKey,
            player.rightKey,
            player.jumpKey,
            player.click,
            player.throwHoldingKey,
            player.getUUID(),
            Client.inputSequenceNumber);
    Client.connectionHandler.send(input.getString());
    input.setInputSequenceNumber(Client.inputSequenceNumber);
    Client.pendingInputs.add(input);
    Client.inputSequenceNumber++;
  }

  public static void update() {
    if (prediction) {
      Client.levelHandler.getClientPlayer().update();
    }
    Client.levelHandler.getPlayers().forEach((key, player) ->
        player.getChildren().forEach(child -> {
          child.update();
          if (child instanceof Arm) {
            child.getChildren().forEach(childChild -> childChild.update());
          }
        })
    );

    //Interpolate Networked Entities
    if (Client.multiplayer && entity_interpolation) {
      interpolateEntities();
    }
  }

}
