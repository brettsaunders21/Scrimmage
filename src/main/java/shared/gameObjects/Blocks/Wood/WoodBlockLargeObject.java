package shared.gameObjects.Blocks.Wood;

import java.util.UUID;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.components.BoxCollider;
import shared.gameObjects.components.Rigidbody;
import shared.physics.data.AngularData;
import shared.physics.data.MaterialProperty;
import shared.physics.types.RigidbodyType;

public class WoodBlockLargeObject extends GameObject {

  /**
   * Base class used to create an object in game. This is used on both the client and server side to
   * ensure actions are calculated the same
   *
   * @param x X coordinate of object in game world
   * @param y Y coordinate of object in game world
   * @param id Unique Identifier of every game object
   */
  public WoodBlockLargeObject(
      double x, double y, double sizeX, double sizeY, ObjectType id, UUID exampleUUID) {
    super(x, y, sizeX, sizeY, id, exampleUUID);
    addComponent(
        new Rigidbody(
            RigidbodyType.DYNAMIC,
            300,
            3,
            0,
            new MaterialProperty(0.55f, 1, 1),
            new AngularData(0, 0, 0, 0),
            this));
    addComponent(new BoxCollider(this, false));
  }

  @Override
  public void update() {
    super.update();
    transform.rotate(1);
  }

  // Initialise the animation
  public void initialiseAnimation() {
    this.animation.supplyAnimation("default", "images/platforms/wood/elementWood017.png");
  }

}
