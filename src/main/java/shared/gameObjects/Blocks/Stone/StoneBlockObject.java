package shared.gameObjects.Blocks.Stone;

import java.util.UUID;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.ObjectID;
import shared.gameObjects.components.BoxCollider;
import shared.gameObjects.components.Rigidbody;
import shared.physics.data.AngularData;
import shared.physics.data.MaterialProperty;
import shared.physics.types.RigidbodyType;

public class StoneBlockObject extends GameObject {

  private int health;

  /**
   * Base class used to create an object in game. This is used on both the client and server side to
   * ensure actions are calculated the same
   *
   * @param x X coordinate of object in game world
   * @param y Y coordinate of object in game world
   * @param id Unique Identifier of every game object
   */
  public StoneBlockObject(
      double x, double y, double sizeX, double sizeY, ObjectID id, UUID exampleUUID) {
    super(x, y, sizeX, sizeY, id, exampleUUID);
    health = 100;
    addComponent(
        new Rigidbody(
            RigidbodyType.DYNAMIC,
            200,
            1.85f,
            0,
            new MaterialProperty(1f, 0.2f, 0.1f),
            new AngularData(0, 0, 0, 0),
            this));
    addComponent(new BoxCollider(this, false));
  }

  // Initialise the animation
  public void initialiseAnimation() {
    this.animation.supplyAnimation("default", "images/platforms/stone/elementStone011.png");
  }


  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
    if (this.health < 0) {
      this.health = 0;
    }
    if (this.health > 100) {
      this.health = 100;
    }
  }


  @Override
  public String getState() {
    return null;
  }
}
