package shared.gameObjects.players.Limbs;

import java.util.UUID;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.components.BoxCollider;
import shared.gameObjects.components.Rigidbody;
import shared.gameObjects.players.Limb;
import shared.gameObjects.players.Player;
import shared.handlers.levelHandler.LevelHandler;

/**
 * Head limb of a player
 */
public class Head extends Limb {

  protected Rigidbody rb;
  private BoxCollider bc;

  /**
   * Base class used to create an object in game. This is used on both the client and server side to
   * ensure actions are calculated the same
   */
  public Head(Player parent, LevelHandler levelHandler, UUID uuid) {
    super(0, 0, 17, 13, 48, 58, ObjectType.Limb, false, parent, parent, 0, 0, levelHandler, uuid);
    limbMaxHealth = 99999;
    limbHealth = limbMaxHealth;
  }

  @Override
  public void initialiseAnimation() {
    this.animation.supplyAnimation("default",
        "images/player/skin" + settings.getData().getActiveSkin()[0] + "/head.png");
  }

  @Override
  public void updateSkinRender(int id) {
    this.animation.supplyAnimation("default", "images/player/skin" + id + "/head.png");
  }

  @Override
  protected void rotateAnimate() {

  }

  @Override
  public void deductHp(int damage, GameObject source) {
    super.deductHp(damage * 2, source);
  }
}
