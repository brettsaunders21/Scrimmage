package shared.gameObjects.weapons;

import java.util.UUID;
import shared.gameObjects.Utils.ObjectID;

/**
 * @author hlf764 The Handgun class.
 */
public class Handgun extends Gun {

  private static String imagePath = "images/weapons/handgun.jpg";

  /**
   * Constructor of the Handgun class
   *
   * @param x The x position of the gun
   * @param y The y position of the gun
   * @param id The ObjectID of the gun
   * @param damage Damage of the gun
   * @param weight Weight of the gun
   * @param name Name of the gun
   * @param ammo Total amount of ammo
   * @param bulletSpeed Speed of the bullets
   * @param fireRate Fire rate of the gun (bullets per minute)
   * @param bulletWidth Width of the bullet
   */
  public Handgun(
      double x,
      double y,
      ObjectID id,
      double damage,
      double weight,
      String name,
      int ammo,
      double bulletSpeed,
      double fireRate,
      double bulletWidth,
      UUID uuid) {
    super(x, y, id, imagePath, damage, weight, name, ammo, bulletSpeed, fireRate, bulletWidth,
        false, true, uuid);
  }

  @Override
  public void update() {
  }

  @Override
  public void render() {
  }

  @Override
  public void interpolatePosition(float alpha) {

  }

  @Override
  public String getState() {
    return null;
  }
}
