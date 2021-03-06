package shared.gameObjects.Blocks;

import java.util.UUID;
import shared.gameObjects.Destructable;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.ObjectType;

/**
 * General class for a Block that can explore into smaller dynamic objects
 */
public abstract class DestructableBlock extends GameObject implements Destructable {

  int hp;

  public DestructableBlock(
      double x, double y, double sizeX, double sizeY, ObjectType id, UUID exampleUUID) {
    super(x, y, sizeX, sizeY, id, exampleUUID);
  }

  @Override
  public void initialiseAnimation() {

  }

  @Override
  public void deductHp(int damage, GameObject source) {

  }
}
