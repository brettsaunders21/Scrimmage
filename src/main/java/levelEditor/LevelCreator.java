package levelEditor;

import client.handlers.effectsHandler.ServerParticle;
import client.handlers.effectsHandler.emitters.LineEmitter;
import client.main.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.stage.Stage;
import shared.gameObjects.Blocks.Stone.StoneFloorObject;
import shared.gameObjects.Blocks.Stone.StoneWallObject;
import shared.gameObjects.Blocks.Wood.WoodBlockLargeObject;
import shared.gameObjects.Blocks.Wood.WoodBlockSmallObject;
import shared.gameObjects.GameObject;
import shared.gameObjects.MapDataObject;
import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.background.Background1;
import shared.gameObjects.background.Background2;
import shared.gameObjects.background.Background3;
import shared.gameObjects.background.Background4;
import shared.gameObjects.background.Background5;
import shared.gameObjects.background.Background6;
import shared.gameObjects.background.Background7;
import shared.gameObjects.background.Background8;
import shared.gameObjects.menu.LabelObject;
import shared.gameObjects.menu.Title;
import shared.gameObjects.menu.main.ButtonBack;
import shared.gameObjects.menu.main.ButtonCredits;
import shared.gameObjects.menu.main.ButtonMultiplayer;
import shared.gameObjects.menu.main.ButtonQuit;
import shared.gameObjects.menu.main.ButtonSettings;
import shared.gameObjects.menu.main.ButtonSingleplayer;
import shared.gameObjects.menu.main.MaxPlayerSlider;
import shared.gameObjects.menu.main.SoundSlider;
import shared.gameObjects.menu.main.SoundSlider.SOUND_TYPE;
import shared.gameObjects.menu.main.account.AccountPageHandler;
import shared.gameObjects.menu.main.account.ButtonAccount;
import shared.gameObjects.menu.main.controls.ButtonInputJump;
import shared.gameObjects.menu.main.controls.ButtonInputLeft;
import shared.gameObjects.menu.main.controls.ButtonInputMenu;
import shared.gameObjects.menu.main.controls.ButtonInputRight;
import shared.gameObjects.menu.main.controls.ButtonInputThrow;
import shared.gameObjects.menu.multiplayer.ButtonJoin;
import shared.gameObjects.menu.multiplayer.ButtonReady;
import shared.gameObjects.objects.utility.BlueBlock;
import shared.gameObjects.objects.utility.GreenBlock;
import shared.gameObjects.objects.utility.JumpPad;
import shared.gameObjects.objects.utility.RedBlock;
import shared.gameObjects.objects.utility.YellowBlock;
import shared.gameObjects.players.Player;
import shared.gameObjects.score.Podium1;
import shared.gameObjects.weapons.WeaponSpawner;
import shared.handlers.levelHandler.GameState;
import shared.handlers.levelHandler.MapLoader;
import shared.handlers.levelHandler.PlaylistHandler;
import shared.util.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import shared.util.maths.Vector2;

/**
 * Script class for regenerating the main maps/levels used in the game
 */
public class LevelCreator extends Application {

  private static Settings settings = new Settings(null, null); // WARNING be careful using this

  private static int stageSizeX = settings.getMapWidth(); // todo autofetch
  private static int stageSizeY = settings.getMapHeight();
  private static int gridSizePX = settings.getGridSize();
  private static int gridSizeX = stageSizeX / gridSizePX; // 40 px blocks
  private static int gridSizeY = stageSizeY / gridSizePX; // 48 x 27
  private static ConcurrentLinkedHashMap<UUID, GameObject> gameObjects;
  private static ArrayList<Player> playerSpawns;
  private static MapDataObject mapDataObject;
  private ArrayList<Vector2> spawnPoints;
  private UUID uuid = UUID.randomUUID();

  private static int getAbs(int gridPos) {
    return gridPos * gridSizePX;
  }

  /**
   * Script class for regenerating the main maps/levels used in the game
   *
   * @param primaryStage From JavaFX Application, not used in this class
   */
  @Override
  public void start(Stage primaryStage) {
    Group root = new Group();
    // CLASS TO AUTO RECREATE MAPS
    String filename = "";
    String filepath = settings.getMenuPath() + File.separator;

    String filepathMaps = settings.getMapsPath() + File.separator;

    spawnPoints = new ArrayList<>();
    spawnPoints.add(new Vector2(360, 150));
    spawnPoints.add(new Vector2(600, 150));
    spawnPoints.add(new Vector2(1200, 150));
    spawnPoints.add(new Vector2(1700, 150));


    ////////////////////////////////////////
    // MAIN MENU
    ////////////////////////////////////////
    System.out.println("Generating Main Menu");
    filename = "main_menu";
    gameObjects = new ConcurrentLinkedHashMap.Builder<UUID, GameObject>()
        .maximumWeightedCapacity(500).build();
    playerSpawns = new ArrayList<Player>();
    mapDataObject = new MapDataObject(UUID.randomUUID(), GameState.MAIN_MENU);
    mapDataObject.setSpawnPoints(spawnPoints);
    mapDataObject.setBackground(
        new Background1(uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid, new Title(
        getAbs(15), getAbs(3), getAbs(19), getAbs(5), uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid, new ButtonSingleplayer(
        getAbs(20), getAbs(9), getAbs(8), getAbs(2), ObjectType.Button, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid, new ButtonMultiplayer(
        getAbs(20), getAbs(12), getAbs(8), getAbs(2), ObjectType.Button, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid, new ButtonSettings(
        getAbs(20), getAbs(15), getAbs(8), getAbs(2), ObjectType.Button, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new ButtonAccount(
            getAbs(20), getAbs(18), getAbs(8), getAbs(2), ObjectType.Button, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid, new ButtonQuit(
        getAbs(20), getAbs(21), getAbs(8), getAbs(2), ObjectType.Button, uuid));
    uuid = UUID.randomUUID();

    //ColouredBlocks todo remove form mm
    gameObjects
        .put(uuid, new RedBlock(getAbs(4), getAbs(11), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects
        .put(uuid, new RedBlock(getAbs(5), getAbs(11), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects
        .put(uuid, new RedBlock(getAbs(6), getAbs(11), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects
        .put(uuid, new RedBlock(getAbs(7), getAbs(11), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    //ColouredBlocks
    gameObjects.put(uuid,
        new BlueBlock(getAbs(29), getAbs(9), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new BlueBlock(getAbs(30), getAbs(9), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new BlueBlock(getAbs(31), getAbs(9), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new BlueBlock(getAbs(32), getAbs(9), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    //ColouredBlocks
    gameObjects.put(uuid,
        new GreenBlock(getAbs(7), getAbs(16), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new GreenBlock(getAbs(8), getAbs(16), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new GreenBlock(getAbs(9), getAbs(16), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new GreenBlock(getAbs(10), getAbs(16), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    //ColouredBlocks
    gameObjects.put(uuid,
        new YellowBlock(getAbs(33), getAbs(9), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new YellowBlock(getAbs(34), getAbs(9), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new YellowBlock(getAbs(35), getAbs(9), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new YellowBlock(getAbs(36), getAbs(9), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();

    //JumpPad
    gameObjects.put(uuid, new JumpPad(getAbs(1), getAbs(25), uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid, new JumpPad(getAbs(2), getAbs(25), uuid));
    uuid = UUID.randomUUID();

    //Middle platforms
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(4), getAbs(6), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(8), getAbs(6), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(14), getAbs(12), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(4), getAbs(20), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(12), getAbs(20), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(31), getAbs(18), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(35), getAbs(5), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(37), getAbs(13), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();

    // left side blocks
    GameObject object = new WoodBlockLargeObject(
        getAbs(5), getAbs(4), getAbs(2), getAbs(2), ObjectType.Bot, uuid);
    //object.addComponent(new Crushing(object)); // no death in mm
    gameObjects.put(uuid, object);
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(6), getAbs(3), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();

//        remove weapons from main menu
//        gameObjects.put(uuid, new WeaponSpawner(
//            getAbs(8), getAbs(4), getAbs(1), getAbs(1), uuid
//        ));
//        uuid = UUID.randomUUID();

    // right side blocks
    gameObjects.put(uuid,
        new WoodBlockLargeObject(
            getAbs(38), getAbs(25), getAbs(2), getAbs(2), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockLargeObject(
            getAbs(40), getAbs(25), getAbs(2), getAbs(2), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockLargeObject(
            getAbs(40), getAbs(23), getAbs(2), getAbs(2), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(37), getAbs(26), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(38), getAbs(24), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(42), getAbs(24), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(42), getAbs(25), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(42), getAbs(26), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(43), getAbs(26), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();

    for (int i = 0; i < 12; i++) {
      // top row wall
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(0), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 5; i++) {
      // side col walls
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(0),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              UUID.randomUUID()));
      uuid = UUID.randomUUID();
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(47),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              UUID.randomUUID()));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 12; i++) {
      // bottom row floor
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(26), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }
    MapLoader.saveMap(gameObjects, mapDataObject, filepath + filename + ".map");

    ////////////////////////////////////////
    // SINGLEPLAYER MAP
    ////////////////////////////////////////
    System.out.println("Generating Single Player Map");
    filename = "menu";
    gameObjects = new ConcurrentLinkedHashMap.Builder<UUID, GameObject>()
        .maximumWeightedCapacity(500).build();
    playerSpawns = new ArrayList<>();
    mapDataObject = new MapDataObject(UUID.randomUUID(), GameState.IN_GAME);

    //mapDataObject.setSpawnPoints(spawnPoints);
    // manual set instead
    // player spawns

    ArrayList<Vector2> singleplayerSpawns = new ArrayList<>();
    singleplayerSpawns.add(new Vector2(getAbs(2), getAbs(14)));
    singleplayerSpawns.add(new Vector2(getAbs(2), getAbs(23)));
    singleplayerSpawns.add(new Vector2(getAbs(6), getAbs(14)));
    singleplayerSpawns.add(new Vector2(getAbs(11), getAbs(23)));
    singleplayerSpawns.add(new Vector2(getAbs(14), getAbs(13)));
    singleplayerSpawns.add(new Vector2(getAbs(15), getAbs(3)));
    singleplayerSpawns.add(new Vector2(getAbs(23), getAbs(9)));
    singleplayerSpawns.add(new Vector2(getAbs(24), getAbs(23)));
    singleplayerSpawns.add(new Vector2(getAbs(29), getAbs(32)));
    singleplayerSpawns.add(new Vector2(getAbs(30), getAbs(2)));
    singleplayerSpawns.add(new Vector2(getAbs(33), getAbs(14)));
    singleplayerSpawns.add(new Vector2(getAbs(34), getAbs(2)));
    singleplayerSpawns.add(new Vector2(getAbs(37), getAbs(9)));
    singleplayerSpawns.add(new Vector2(getAbs(37), getAbs(23)));
    singleplayerSpawns.add(new Vector2(getAbs(41), getAbs(14)));
    singleplayerSpawns.add(new Vector2(getAbs(44), getAbs(23)));

    mapDataObject.setSpawnPoints(singleplayerSpawns);

    mapDataObject.setBackground(
        new Background1(UUID.randomUUID()));
    uuid = UUID.randomUUID();
    for (int i = 0; i < 12; i++) {
      // bottom row floor
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(26), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }

    //Middle platforms
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(0), getAbs(17), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(4), getAbs(17), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(13), getAbs(16), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(14), getAbs(6), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(22), getAbs(12), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(29), getAbs(5), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(32), getAbs(19), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(33), getAbs(5), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(36), getAbs(19), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneFloorObject(
            getAbs(40), getAbs(19), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();

    // walls
    gameObjects.put(uuid,
        new StoneWallObject(
            getAbs(0), getAbs(18), getAbs(1), getAbs(4), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneWallObject(
            getAbs(0), getAbs(22), getAbs(1), getAbs(4), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new StoneWallObject(
            getAbs(27), getAbs(22), getAbs(1), getAbs(4), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();

    // weapon spawn points
    for (Vector2 spawnVector : singleplayerSpawns) {
      gameObjects.put(uuid,
          new WeaponSpawner(spawnVector.getX(), spawnVector.getY() - getAbs(2), getAbs(1),
              getAbs(1), uuid));
      uuid = UUID.randomUUID();
    }

    //wood block pyramid
    gameObjects.put(uuid,
        new WoodBlockLargeObject(
            getAbs(36), getAbs(17), getAbs(2), getAbs(2), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockLargeObject(
            getAbs(37), getAbs(15), getAbs(2), getAbs(2), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockLargeObject(
            getAbs(38), getAbs(17), getAbs(2), getAbs(2), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(33), getAbs(18), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(34), getAbs(17), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(34), getAbs(18), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(35), getAbs(15), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(35), getAbs(16), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(35), getAbs(17), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(35), getAbs(18), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(36), getAbs(14), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(36), getAbs(15), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(36), getAbs(16), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(37), getAbs(12), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(37), getAbs(13), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(37), getAbs(14), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(38), getAbs(12), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(38), getAbs(13), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(38), getAbs(14), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(39), getAbs(14), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(39), getAbs(15), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(39), getAbs(16), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(40), getAbs(15), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(40), getAbs(16), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(40), getAbs(17), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(40), getAbs(18), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(41), getAbs(17), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(41), getAbs(18), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WoodBlockSmallObject(
            getAbs(42), getAbs(18), getAbs(1), getAbs(1), ObjectType.Bot, uuid));
    uuid = UUID.randomUUID();

    Collections.shuffle(singleplayerSpawns);
    MapLoader.saveMap(gameObjects, mapDataObject, filepathMaps + "playlist1/" + "map1" + ".map");
    mapDataObject.setBackground(new Background2(UUID.randomUUID()));
    Collections.shuffle(singleplayerSpawns);
    MapLoader.saveMap(gameObjects, mapDataObject, filepathMaps + "playlist1/" + "map2" + ".map");
    mapDataObject.setBackground(new Background3(UUID.randomUUID()));
    Collections.shuffle(singleplayerSpawns);
    MapLoader.saveMap(gameObjects, mapDataObject, filepathMaps + "playlist1/" + "map3" + ".map");
    mapDataObject.setBackground(new Background4(UUID.randomUUID()));
    Collections.shuffle(singleplayerSpawns);
    MapLoader.saveMap(gameObjects, mapDataObject, filepathMaps + "playlist1/" + "map4" + ".map");
    mapDataObject.setBackground(new Background5(UUID.randomUUID()));
    Collections.shuffle(singleplayerSpawns);
    MapLoader.saveMap(gameObjects, mapDataObject, filepathMaps + "playlist1/" + "map5" + ".map");
    mapDataObject.setBackground(new Background6(UUID.randomUUID()));
    Collections.shuffle(singleplayerSpawns);
    MapLoader.saveMap(gameObjects, mapDataObject, filepathMaps + "playlist1/" + "map6" + ".map");
    mapDataObject.setBackground(new Background7(UUID.randomUUID()));
    Collections.shuffle(singleplayerSpawns);
    MapLoader.saveMap(gameObjects, mapDataObject, filepathMaps + "playlist1/" + "map7" + ".map");
    mapDataObject.setBackground(new Background8(UUID.randomUUID()));
    Collections.shuffle(singleplayerSpawns);
    MapLoader.saveMap(gameObjects, mapDataObject, filepathMaps + "playlist1/" + "map8" + ".map");
    mapDataObject.setBackground(new Background1(UUID.randomUUID()));
    Collections.shuffle(singleplayerSpawns);
    MapLoader.saveMap(gameObjects, mapDataObject, filepathMaps + "playlist1/" + "map9" + ".map");
    mapDataObject.setBackground(new Background2(UUID.randomUUID()));
    Collections.shuffle(singleplayerSpawns);
    MapLoader.saveMap(gameObjects, mapDataObject, filepathMaps + "playlist1/" + "map10" + ".map");

    ////////////////////////////////////////
    // MULTIPLAYER
    ////////////////////////////////////////
    System.out.println("Generating MULTIPLAYER Map");
    filename = "multiplayer";
    gameObjects = new ConcurrentLinkedHashMap.Builder<UUID, GameObject>()
        .maximumWeightedCapacity(500).build();
    playerSpawns = new ArrayList<Player>();
    mapDataObject = new MapDataObject(UUID.randomUUID(), GameState.MULTIPLAYER);
    mapDataObject.setSpawnPoints(spawnPoints);
    mapDataObject.setBackground(
        new Background1(UUID.randomUUID()));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new ButtonJoin(
            getAbs(20), getAbs(7), getAbs(8), getAbs(2), ObjectType.Button, uuid));
    uuid = UUID.randomUUID();
    for (int i = 0; i < 12; i++) {
      // top row wall
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(0), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 5; i++) {
      // side col walls
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(0),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              uuid));
      uuid = UUID.randomUUID();
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(47),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 12; i++) {
      // bottom row floor
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(26), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }
    MapLoader.saveMap(gameObjects, mapDataObject, filepath + filename + ".map");

    ////////////////////////////////////////
    // MULTIPLAYER LOBBY
    ////////////////////////////////////////
    System.out.println("Generating LOBBY");
    filename = "lobby";
    gameObjects = new ConcurrentLinkedHashMap.Builder<UUID, GameObject>()
        .maximumWeightedCapacity(500).build();
    playerSpawns = new ArrayList<Player>();
    mapDataObject = new MapDataObject(UUID.randomUUID(), GameState.LOBBY);
    mapDataObject.setSpawnPoints(spawnPoints);
    uuid = UUID.randomUUID();
    mapDataObject.setBackground(
        new Background1(uuid));
    uuid = UUID.randomUUID();
    for (int i = 0; i < 12; i++) {
      // top row wall
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(0), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 5; i++) {
      // side col walls
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(0),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              uuid));
      uuid = UUID.randomUUID();
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(47),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 12; i++) {
      // bottom row floor
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(26), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }

    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new WeaponSpawner(200, 350, 40, 40, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new ButtonReady(getAbs(14), getAbs(3), getAbs(18), getAbs(2), ObjectType.Button, uuid));

    MapLoader.saveMap(gameObjects, mapDataObject, filepath + filename + ".map");

    ////////////////////////////////////////
    // SCORE SCREEN
    ////////////////////////////////////////
    System.out.println("Generating Score Screen");
    filename = "score";
    gameObjects = new ConcurrentLinkedHashMap.Builder<UUID, GameObject>()
        .maximumWeightedCapacity(500).build();
    playerSpawns = new ArrayList<Player>();
    mapDataObject = new MapDataObject(UUID.randomUUID(), GameState.LOBBY);
    mapDataObject.setSpawnPoints(spawnPoints);
    uuid = UUID.randomUUID();
    mapDataObject.setBackground(
        new Background5(uuid));
    uuid = UUID.randomUUID();
    for (int i = 0; i < 12; i++) {
      // top row wall
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(0), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 5; i++) {
      // side col walls
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(0),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              uuid));
      uuid = UUID.randomUUID();
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(47),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 12; i++) {
      // bottom row floor
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(26), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }

    uuid = UUID.randomUUID();
    gameObjects.put(uuid, new Podium1(getAbs(17), getAbs(14), getAbs(6), getAbs(12)));

    MapLoader.saveMap(gameObjects, mapDataObject, filepath + filename + ".map");

    ////////////////////////////////////////
    // SETTINGS
    ////////////////////////////////////////
    System.out.println("Generating Settings");
    filename = "settings";
    gameObjects = new ConcurrentLinkedHashMap.Builder<UUID, GameObject>()
        .maximumWeightedCapacity(500).build();
    playerSpawns = new ArrayList<Player>();
    mapDataObject = new MapDataObject(UUID.randomUUID(), GameState.SETTINGS);
    mapDataObject.setSpawnPoints(spawnPoints);
    uuid = UUID.randomUUID();
    mapDataObject.setBackground(
        new Background1(UUID.randomUUID()));
    uuid = UUID.randomUUID();

    //sliders
    gameObjects
        .put(uuid, new LabelObject(getAbs(12), getAbs(5), "Game Settings:", ObjectType.Button,
            uuid)); //todo Button type?
    uuid = UUID.randomUUID();
    gameObjects
        .put(uuid, new SoundSlider(getAbs(12), getAbs(7), getAbs(8), getAbs(1), SOUND_TYPE.MUSIC,
            "Music", ObjectType.Button, uuid));
    uuid = UUID.randomUUID();
    gameObjects
        .put(uuid, new SoundSlider(getAbs(12), getAbs(11), getAbs(8), getAbs(1), SOUND_TYPE.SFX,
            "Sound Effects", ObjectType.Button, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new MaxPlayerSlider(getAbs(12), getAbs(15), getAbs(8), getAbs(1), ObjectType.Button, uuid));
    uuid = UUID.randomUUID();

    // input controls
    gameObjects.put(uuid, new LabelObject(getAbs(22), getAbs(5), "Controls:", ObjectType.Button,
        uuid)); //todo Button type?
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new ButtonInputJump(getAbs(22), getAbs(7), getAbs(6), getAbs(2), ObjectType.Button, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new ButtonInputLeft(getAbs(22), getAbs(10), getAbs(6), getAbs(2), ObjectType.Button, uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new ButtonInputRight(getAbs(22), getAbs(13), getAbs(6), getAbs(2), ObjectType.Button,
            uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new ButtonInputThrow(getAbs(22), getAbs(16), getAbs(6), getAbs(2), ObjectType.Button,
            uuid));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid,
        new ButtonInputMenu(getAbs(30), getAbs(7), getAbs(6), getAbs(2), ObjectType.Button, uuid));
    uuid = UUID.randomUUID();

    // main buttons
    gameObjects
        .put(uuid,
            new ButtonCredits(getAbs(15), getAbs(20), getAbs(8), getAbs(2), ObjectType.Button,
                uuid));
    uuid = UUID.randomUUID();
    gameObjects
        .put(uuid, new ButtonBack(getAbs(25), getAbs(20), getAbs(8), getAbs(2), ObjectType.Button,
            uuid));
    uuid = UUID.randomUUID();

    for (int i = 0; i < 12; i++) {
      // top row wall
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(0), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 5; i++) {
      // side col walls
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(0),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              uuid));
      uuid = UUID.randomUUID();
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(47),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 12; i++) {
      // bottom row floor
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(26), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }
    MapLoader.saveMap(gameObjects, mapDataObject, filepath + filename + ".map");
    ////////////////////////////////////////
    // Account Map
    ////////////////////////////////////////
    System.out.println("Generating Account Map");
    filename = "account";
    gameObjects = new ConcurrentLinkedHashMap.Builder<UUID, GameObject>()
        .maximumWeightedCapacity(500).build();
    playerSpawns = new ArrayList<Player>();
    mapDataObject = new MapDataObject(UUID.randomUUID(), GameState.ACCOUNT);
    mapDataObject.setSpawnPoints(spawnPoints);
    mapDataObject.setBackground(
        new Background1(UUID.randomUUID()));
    uuid = UUID.randomUUID();
    gameObjects.put(uuid, new AccountPageHandler(uuid));
    uuid = UUID.randomUUID();

    for (int i = 0; i < 12; i++) {
      // top row wall
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(0), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 5; i++) {
      // side col walls
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(0),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              uuid));
      uuid = UUID.randomUUID();
      gameObjects.put(uuid,
          new StoneWallObject(
              getAbs(47),
              getAbs((i * 5) + 1),
              getAbs(1),
              getAbs(5),
              ObjectType.Bot,
              uuid));
      uuid = UUID.randomUUID();
    }
    for (int i = 0; i < 12; i++) {
      // bottom row floor
      gameObjects.put(uuid,
          new StoneFloorObject(
              getAbs(i * 4), getAbs(26), getAbs(4), getAbs(1), ObjectType.Bot, uuid));
      uuid = UUID.randomUUID();
    }
    MapLoader.saveMap(gameObjects, mapDataObject, filepath + filename + ".map");

    System.out.println("RECREATED MAP FILES");
    Platform.exit();
  }
}
