package shared.gameObjects.UI;


import client.main.Settings;
import javafx.scene.Group;
import shared.gameObjects.players.Player;

/**
 * The parent UI managing class
 */
public class UI {

  private PlayerInfo playerInfo;
  private Score score;
  private TimeUI time;
  private PlayerPointer pointer;
  private Settings settings;

  /**
   * Constructs a UI pertaining to a specific player
   *
   * @param root UI root to render to
   * @param clientPlayer Player to obtain data from
   */
  public UI(Group root, Player clientPlayer, Settings settings) {

    //Load the Kenney font
    //uiFont = Font.loadFont("fonts/kenney1.ttf", 25);
    settings = settings;
    playerInfo = new PlayerInfo(root, clientPlayer,settings);
    score = new Score(root, clientPlayer, settings);
    time = new TimeUI(root,clientPlayer, settings);
    pointer = new PlayerPointer(root, clientPlayer);

  }

  /**
   * Renders the UI
   */
  public void render() {
    playerInfo.render();
    score.render();
    time.render();
    pointer.render();
  }


}
