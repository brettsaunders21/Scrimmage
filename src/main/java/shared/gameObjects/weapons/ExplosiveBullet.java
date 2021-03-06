package shared.gameObjects.weapons;

import client.handlers.audioHandler.AudioHandler;
import client.handlers.effectsHandler.ServerParticle;
import client.main.Client;
import java.util.ArrayList;
import java.util.UUID;
import shared.gameObjects.Destructable;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.components.ComponentType;
import shared.gameObjects.components.Rigidbody;
import shared.gameObjects.players.Player;
import shared.physics.Physics;
import shared.physics.data.Collision;
import shared.physics.types.RigidbodyType;
import shared.util.Path;
import shared.util.maths.Vector2;

/**
 * Bullet that deals explosion damage on contact
 */
public class ExplosiveBullet extends Bullet {

  /**
   * Width of the bullet
   */
  private static final int width = 15;
  /**
   * Damage of the explosion
   */
  private static final int damage = 35;
  /**
   * Speed of travel of the bullet
   */
  private static final int speed = 25;
  /**
   * Radius of explosion on impact
   */
  private static final float radius = 30f;
  /**
   * Power of pushing on Objects in explosion
   */
  private static final float pushPower = 90f;
  /**
   * Path to image
   */
  private static String imagePath = "images/weapons/explosiveBullet.png";

  /**
   * Constructor of Explosive Bullet
   *
   * @param gunX X position of the gun when fired
   * @param gunY Y position of the gun when fired
   * @param mouseX X position of the mouse when fired
   * @param mouseY Y poistion of the mouse when fired
   * @param holder The player who fired this bullet
   * @param uuid UUID of this bullet
   */
  public ExplosiveBullet(
      double gunX,
      double gunY,
      double mouseX,
      double mouseY,
      Player holder,
      UUID uuid) {

    super(gunX, gunY, mouseX, mouseY, width, speed, damage, holder, uuid);
  }

  @Override
  public void initialiseAnimation() {
    // this.animation.supplyAnimation("default", this.imagePath);
    this.animation.supplyAnimationWithSize(
        "default", this.getWidth(), this.getWidth(), true, Path.convert(imagePath));
  }

  @Override
  /**
   * Holder: Not going to take hazard, Will be knocked back by explosion but not on direct impact
   * In explosion area, every player will take half the hazard this bullet dealt
   * Player on direct impact will take half the hazard on collision, then half the hazard on
   *   explosion
   *
   * @param col The collision on direct impact
   *
   */
  public void OnCollisionEnter(Collision col) {
    ArrayList<Collision> collision = Physics.circlecastAll(this.bc.getCentre(), radius);
    GameObject gCol = col.getCollidedObject();
    boolean remove = true;

    new AudioHandler(settings, Client.musicActive).playSFX("FART");

    settings.getLevelHandler().addGameObject(new ServerParticle(
        bc.getCentre(), Vector2.Zero(), Vector2.Zero(), new Vector2(192,192), "explosion", 0.4f
    ));

    if (gCol.getId() == ObjectType.Player || gCol instanceof Destructable) {
      if (gCol.equals(holder)) {
        hitHolder = true;
        remove = false;
      } else {
        // Player on direct impact takes full hazard (another half dealt in circleCasting down there)
        ((Destructable) gCol).deductHp(damage / 2, holder);
      }
    }

    for (Collision c : collision) {
      GameObject g = c.getCollidedObject();

      // Skip if getCollidedObject gets removed accidentally
      if (g == null) {
        continue;
      }

      // Not going to push holder if he is the first collided object (i.e. the impact)
      if (g.equals(gCol) && hitHolder) {
        continue;
      }

      // Not going to deal hazard to holder
      if (g instanceof Destructable && !g.equals(holder)) {
        // Every player in the explosion area deals half the hazard
        ((Destructable) g).deductHp(damage / 2, holder);
        settings.getLevelHandler().addGameObject(new ServerParticle(
            bc.getCentre(), Vector2.Zero(), Vector2.Zero(), new Vector2(64,64), "explosion", 0.35f
        ));
      }

      // Knockback here
      Rigidbody body = (Rigidbody) g.getComponent(ComponentType.RIGIDBODY);

      if (body != null && body.getBodyType() == RigidbodyType.DYNAMIC) {
        Vector2 dir = g.getTransform().getPos().sub(bc.getCentre()).normalize();
        body.move(dir.mult(pushPower), 0.1f);
      }

    }

    if (remove) {
      settings.getLevelHandler().removeGameObject(this);
    }
  }

}
