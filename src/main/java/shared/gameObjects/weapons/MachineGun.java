package shared.gameObjects.weapons;

import client.handlers.audioHandler.AudioHandler;
import client.main.Client;
import client.main.Settings;
import java.util.UUID;
import javafx.scene.Group;
import shared.gameObjects.components.BoxCollider;
import shared.gameObjects.components.Rigidbody;
import shared.gameObjects.players.Player;
import shared.physics.data.AngularData;
import shared.physics.data.Collision;
import shared.physics.data.MaterialProperty;
import shared.physics.types.ColliderLayer;
import shared.physics.types.RigidbodyType;
import shared.util.Path;
import shared.util.maths.Vector2;

public class MachineGun extends Gun {

  private static String imagePath = "images/weapons/machinegun.png"; // path to Machine Gun image
  private static String audioPath = "audio/sound-effects/laser_gun.wav"; // path to Machine Gun sfx
  private static double sizeX = 84, sizeY = 35;

  public MachineGun(double x, double y, String name, Player holder, UUID uuid) {

    super(
        x,
        y,
        sizeX, // sizeX
        sizeY, // sizeY
        10, // weight
        name,
        50, // ammo
        70, // fireRate
        holder,
        true, // fullAutoFire
        false, // singleHanded
        uuid);

    // pivot = position of the grip
    // If changing the value of this, change the value in all getGrip() methods
    rotate.setPivotX(20);
    rotate.setPivotY(10);
  }

  public MachineGun(MachineGun that) {
    this(that.getX(), that.getY(), that.name, that.holder, UUID.randomUUID());
  }

  @Override
  public void initialise(Group root, Settings settings) {
    super.initialise(root, settings);
  }

  @Override
  public void fire(double mouseX, double mouseY) {
    if (canFire()) {
      UUID uuid = UUID.randomUUID();
      Vector2 playerCentre = new Vector2(holderHandPos[0], holderHandPos[1]); // centre = main hand
      double playerRadius = 55 + 65; // Player.sizeY / 2 + bias

      double bulletX = playerCentre.getX() + playerRadius * Math.cos(-angleRadian);
      double bulletY = playerCentre.getY() - playerRadius * Math.sin(-angleRadian);
      double bulletFlipX = playerCentre.getX() - playerRadius * Math.cos(angleRadian);
      double bulletFlipY = playerCentre.getY() - playerRadius * Math.sin(angleRadian);
      Bullet bullet =
          new FireBullet(
              (holder.isAimingLeft() ? bulletFlipX : bulletX),
              (holder.isAimingLeft() ? bulletFlipY : bulletY),
              mouseX,
              mouseY,
              this.holder,
              uuid);
      settings.getLevelHandler().addGameObject(bullet);
      this.currentCooldown = getDefaultCoolDown();
      new AudioHandler(settings, Client.musicActive).playSFX("MACHINEGUN");
      deductAmmo();
    }
  }

  public void startThrowing() {
    holder.usePunch();
    float radius = this.getTransform().getSize().getX() / 2;
    this.bcCol = new BoxCollider(this, ColliderLayer.DEFAULT, false);
    this.rb = new Rigidbody(
        RigidbodyType.DYNAMIC,
        1f,
        1f,
        1f,
        new MaterialProperty(0.1f, 1, 1),
        new AngularData(0, 0, 0, 0),
        this); // TODO FIX
    addComponent(bcCol);
    addComponent(rb);
    this.throwVector = getDeltaThrowVecNorm();
    this.startedThrowing = true;
  }

  // Only handle collision when throwing weapon
  @Override
  public void OnCollisionEnter(Collision col) {
    // Leave if this gun is a spawn gun
    if (this.holder == null) {
      return;
    }

/*    boolean remove = true;
    GameObject g = col.getCollidedObject();
    BoxCollider holderBC = (BoxCollider) holder.getComponent(ComponentType.COLLIDER);

    if (g.getId() == ObjectType.Player) {
      Player p = (Player) g;
      if (p.getUUID() == holder.getUUID()) {
        holderBC.setLayer(ColliderLayer.COLLECTABLE);
        remove = false;
      }
      else
        p.deductHp(30);
    }

    if (remove)
      settings.getLevelHandler().removeGameObject(this);*/
  }

  @Override
  public void OnCollisionExit(Collision col) {
    // Leave if this gun is a spawn gun
    if (this.holder == null) {
      return;
    }

//    ((BoxCollider) holder.getComponent(ComponentType.COLLIDER)).setLayer(ColliderLayer.DEFAULT);
  }

  @Override
  public void initialiseAnimation() {
    this.animation.supplyAnimation("default", Path.convert(this.imagePath));
  }

  // =============================
  // Get Grip and Muzzle positions
  // =============================
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
    return holderHandPos[0] - 55;
  }

  @Override
  public double getGripFlipY() {
    return holderHandPos[1] - 10;
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

  public Vector2 getDeltaThrowVecNorm() {
    return new Vector2(holder.mouseX - this.getX(), holder.mouseY - this.getY()).normalize();
  }
}
