package shared.gameObjects.weapons;

import client.handlers.audioHandler.AudioHandler;
import client.main.Client;
import java.util.UUID;
import shared.gameObjects.players.Player;
import shared.util.Path;
import shared.util.maths.Vector2;

/**
 * Gun of type Uzi
 */
public class Uzi extends Gun {

  /**
   * Path to image
   */
  private static String imagePath = "images/weapons/uzi.png";
  /**
   * Size of image
   */
  private static double sizeX = 84, sizeY = 35;
  /** Ammo of gun */
  private static int ammo = 50;
  /** Fire rate of gun */
  private static int fireRate = 72;

  /**
   * Default constructor
   *
   * @param x X position of this Uzi
   * @param y Y position of this Uzi
   * @param name Name of this Uzi
   * @param holder Player who holds this Uzi
   * @param uuid UUID of this Uzi
   */
  public Uzi(double x, double y, String name, Player holder, UUID uuid) {
    super(
        x,
        y,
        sizeX,
        sizeY,
        10, // weight
        name,
        ammo,
        fireRate,
        20, // pivotX
        25, // pivotY
        holder,
        true, // fullAutoFire
        false, // singleHanded
        uuid
    );
    this.weaponRank = 3;
  }

  /**
   * Constructor that duplicate a Uzi with different UUID
   *
   * @param that Source of duplication
   */
  public Uzi(Uzi that) {
    this(that.getX(), that.getY(), that.getName(), that.getHolder(), UUID.randomUUID());
  }

  @Override
  public void fire(double mouseX, double mouseY) {
    if (canFire()) {
      UUID uuid = UUID.randomUUID();
      //Vector2 playerCentre = ((BoxCollider) (holder.getComponent(ComponentType.COLLIDER))).getCentre(); // centre = body.centre
      Vector2 playerCentre = new Vector2(holderHandPos[0], holderHandPos[1]-12); // centre = main hand

      double bulletX;
      double bulletY;

      try {
        if (holder.isAimingLeft()) {
          bulletX = playerCentre.getX() - playerRadius * Math.cos(angleRadian);
          bulletY = playerCentre.getY() - playerRadius * Math.sin(angleRadian);
        } else {
          bulletX = playerCentre.getX() + playerRadius * Math.cos(-angleRadian);
          bulletY = playerCentre.getY() - playerRadius * Math.sin(-angleRadian);

        }

        // Ray cast check if shooting floor
        double[] bulletStartPos =
            isShootingFloor(bulletX, bulletY, mouseX, mouseY, playerCentre);

        Bullet bullet = new CircleBullet(
            bulletStartPos[0],
            bulletStartPos[1],
            mouseX,
            mouseY,
            this.holder,
            uuid
        );

        settings.getLevelHandler().addGameObject(bullet);
      } catch (NullPointerException e) {
        System.out.println("NullPointerException in Uzi when creating bullet");
      }

      this.currentCooldown = getDefaultCoolDown();
      new AudioHandler(settings, Client.musicActive).playSFX("MACHINEGUN");
      deductAmmo();
    }
  }

  @Override
  public void initialiseAnimation() {
    this.animation.supplyAnimation("default", Path.convert(this.imagePath));
  }

  @Override
  public boolean firesExplosive() {
    return false;
  }

  @Override
  public double getGripX() {
    if (holder.isAimingLeft()) {
      return getGripFlipX();
    } else {
      return holderHandPos == null ? 0 : holderHandPos[0] - 20;
    }
  }

  @Override
  public double getGripY() {
    if (holder.isAimingLeft()) {
      return getGripFlipY();
    } else {
      return holderHandPos == null ? 0 : holderHandPos[1] - 20;
    }
  }

  @Override
  public double getGripFlipX() {
    return holderHandPos[0] - 45;
  }

  @Override
  public double getGripFlipY() {
    return holderHandPos[1] - 20;
  }

  @Override
  public double getForeGripX() {
    if (holder.isAimingLeft()) {
      return getForeGripFlipX();
    }
    return getGripX() + 50 * Math.cos(-angleRadian);
  }

  @Override
  public double getForeGripY() {
    if (holder.isAimingLeft()) {
      return getForeGripFlipY();
    }
    return getGripY() + 50 * Math.sin(angleRadian);
  }

  @Override
  public double getForeGripFlipX() {
    return getGripX() + 50 - 30 * Math.cos(angleRadian);
  }

  @Override
  public double getForeGripFlipY() {
    return getGripY() - 50 * Math.sin(angleRadian);
  }
}
