package shared.gameObjects.Blocks.Metal;

import java.util.UUID;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.ObjectID;
import shared.gameObjects.components.BoxCollider;
import shared.gameObjects.components.Rigidbody;
import shared.physics.data.AngularData;
import shared.physics.data.MaterialProperty;
import shared.physics.types.RigidbodyType;

public class MetalBlockSmallObject extends GameObject {


  /**
   * Base class used to create an object in game. This is used on both the client and server side to
   * ensure actions are calculated the same
   *
   * @param x X coordinate of object in game world
   * @param y Y coordinate of object in game world
   * @param id Unique Identifier of every game object
   */
  public MetalBlockSmallObject(
      double x, double y, double sizeX, double sizeY, ObjectID id, UUID exampleUUID) {
    super(x, y, sizeX, sizeY, id, exampleUUID);
    addComponent(
        new Rigidbody(
            RigidbodyType.DYNAMIC,
            700,
            3,
            0,
            new MaterialProperty(0.05f, 1, 1),
            new AngularData(0, 0, 0, 0),
            this));
    addComponent(new BoxCollider(this, false));
  }

  // Initialise the animation
  public void initialiseAnimation() {
    this.animation.supplyAnimation("default", "images/platforms/metal/elementMetal011.png");
  }

  @Override
  public String getState() {
    return objectUUID + ";" + getX() + ";" + getY();
  }

  @Override
  public void setState(String data) {
    String[] unpackedData = data.split(";");
    setX(Double.parseDouble(unpackedData[1]));
    setY(Double.parseDouble(unpackedData[2]));
  }


}
