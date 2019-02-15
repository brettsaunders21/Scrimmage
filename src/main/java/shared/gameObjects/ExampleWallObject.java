package shared.gameObjects;

import java.util.UUID;
import javafx.scene.image.Image;
import shared.gameObjects.Utils.ObjectID;
import shared.gameObjects.components.BoxCollider;
import shared.gameObjects.components.Rigidbody;
import shared.physics.data.AngularData;
import shared.physics.data.MaterialProperty;
import shared.physics.types.RigidbodyType;

public class ExampleWallObject extends GameObject {

  private int health;

  /**
   * Base class used to create an object in game. This is used on both the client and server side to
   * ensure actions are calculated the same
   *
   * @param x X coordinate of object in game world
   * @param y Y coordinate of object in game world
   * @param id Unique Identifier of every game object
   */
  public ExampleWallObject(
      double x, double y, double sizeX, double sizeY, ObjectID id, UUID exampleUUID) {
    super(x, y, sizeX, sizeY, id, exampleUUID);
    health = 100;
    addComponent(
        new Rigidbody(
            RigidbodyType.STATIC,
            0,
            1,
            0,
            new MaterialProperty(0.1f, 1, 1),
            new AngularData(0, 0, 0, 0),
            this));
    addComponent(new BoxCollider(this, false));
  }

  // Initialise the animation
  public void initialiseAnimation() {
    this.animation.supplyAnimation("default", "images/platforms/stone/elementStone013.png");
  }

  @Override
  public void update() {
    super.update();
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
  public void render() {
    super.render();
    imageView.relocate(getX(), getY());
    // Example not best way as every frame rechecking and recreating image
    if (health > 60) {

    } else if (health < 60 && health > 40) {
      imageView.setImage(new Image("images/platforms/stone/elementStone016.png"));
    } else if (health < 40) {
      imageView.setImage(new Image("images/platforms/stone/elementStone048.png"));
    }
  }

  @Override
  public void interpolatePosition(float alpha) {
  }

  @Override
  public String getState() {
    return null;
  }
}