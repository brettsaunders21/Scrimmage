package shared.gameObjects.weapons;

import client.main.Settings;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import shared.gameObjects.Destructable;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.components.Rigidbody;
import shared.gameObjects.players.Player;
import shared.physics.Physics;
import shared.physics.data.Collision;
import shared.util.maths.Vector2;

/**
 * Abstract class of all Melee weapons
 */
public abstract class Melee extends Weapon {

  /**
   * Damage of the melee
   */
  protected int damage;
  /**
   * Limit of attack allowed
   */
  protected int ammo;
  /**
   * Range of the melee (radius)
   */
  protected double range;
  /**
   * Origin position of swing when attacking (relative to arm)
   */
  protected double beginAngle;
  /**
   * Destination position of swing when attacking (relative to arm)
   */
  protected double endAngle;
  /**
   * Rigidbody of this melee
   */
  protected Rigidbody rb;
  /**
   * True if this melee is attacking (in the process of swing)
   */
  protected boolean attacking;
  /**
   * Angles to travel when attacking
   */
  protected double[] angles;
  /**
   * Index indicating which part the swing is in now during attack
   */
  protected int currentAngleIndex;
  /**
   * -1 if aiming Left, 1 if aiming Right
   */
  protected double attackAngleSign;
  /**
   * Hash set to record collided object in 1 attack
   */
  protected HashSet<Destructable> collidedSet;
  /**
   * Rotate for swinging the sword on attack
   */
  protected transient Rotate rotateAttack;


  /**
   * Default constructor of all Melee weapon
   *
   * @param x X position of this melee
   * @param y Y position of this melee
   * @param sizeX Width of this image
   * @param sizeY Height of this image
   * @param id Object type of this melee (default: Weapon)
   * @param damage Damage of this melee
   * @param weight Weight of this melee
   * @param name Name of the melee
   * @param ammo Limit of attack allowed of this melee
   * @param fireRate Fire rate of this melee
   * @param pivotX X pivot of image rotation
   * @param pivotY Y pivot of image rotation
   * @param holder Player who holds this melee
   * @param range Range of this melee
   * @param beginAngle Begin position of the attack
   * @param endAngle End position of the attack
   * @param singleHanded True if this melee is hold with one hand only
   * @param uuid UUID of this melee
   */
  public Melee(
      double x,
      double y,
      double sizeX,
      double sizeY,
      ObjectType id,
      int damage,
      double weight,
      String name,
      int ammo,
      int fireRate,
      double pivotX,
      double pivotY,
      Player holder,
      double range,
      double beginAngle,
      double endAngle,
      boolean singleHanded,
      UUID uuid) {

    super(
        x,
        y,
        sizeX,
        sizeY,
        id,
        weight,
        name,
        false,
        true,
        ammo,
        fireRate,
        pivotX,
        pivotY,
        holder,
        uuid);
    this.damage = damage;
    this.ammo = ammo;
    this.range = range;
    this.beginAngle = beginAngle;
    this.endAngle = endAngle;
    this.angles = generateAngles();
    this.attacking = false;
    this.currentAngleIndex = 0;
    this.singleHanded = singleHanded;
    this.collidedSet = new HashSet<>();
    rotateAttack = new Rotate();
  }

  @Override
  public void update() {
    super.update();
  }

  /**
   * Returns the width of image
   */
  public abstract double getSizeX();

  /**
   * Returns the height of image
   */
  public abstract double getSizeY();

  /**
   * Used to reconstruct object after serialization
   *
   * @param root Javafx root node
   * @param settings Settings for game
   */
  public void initialise(Group root, Settings settings) {
    super.initialise(root, settings);
    rotateAttack = new Rotate();
  }

  @Override
  public void fire(double mouseX, double mouseY) {
    if (canFire()) {
      this.attacking = true;
      this.collidedSet.clear();

      // Box cast on beginning of swing
      ArrayList<Collision> collisions =
          Physics.boxcastAll(
              new Vector2((float) (holderHandPos[0] + (range * attackAngleSign)),
                  (float) (holderHandPos[1] - (getSizeY() / 2))),
              new Vector2((float) getSizeY() / 2, (float) getSizeY() / 2),
              false,
              false
          );
      // Box cast at end of swing
      /*collisions.addAll(
          Physics.boxcastAll(
              new Vector2((float) (holderHandPos[0] + (range * attackAngleSign)), (float) (holderHandPos[1]+(getSizeY()/2))),
              new Vector2((float) getSizeY()/2, (float) getSizeY()/2),
              true, false
          )
      );*/
      ArrayList<Destructable> objectsBeingHit = new ArrayList<>();

      for (Collision c : collisions) {
        GameObject g = c.getCollidedObject();

        if (g instanceof Destructable && !isHolder(g) && !collidedSet.contains(g)) {
          objectsBeingHit.add((Destructable) g);
          collidedSet.add((Destructable) g);
        }
      }

      this.currentCooldown = getDefaultCoolDown();

      for (Destructable obj : objectsBeingHit) {
        obj.deductHp(this.damage, holder);
      }

      deductAmmo();
    }
  }

  /**
   * Generate an array of angle for attacking
   *
   * @return An array of angles, difference by 1
   */
  private double[] generateAngles() {
    double[] angle = new double[(int) (beginAngle + endAngle) + 1];
    int k = 0;
    for (double i = beginAngle; i >= (endAngle * -1); i--) {
      angle[k] = i;
      ++k;
    }

    return angle;
  }

  // -------------------
  // Setters and Getters
  // -------------------

  /**
   * Get the current angle of attack
   */
  public double getAngle(int index) {
    if (index < (int) (beginAngle + endAngle + 1)) {
      return angles[index];
    }
    return 0;
  }

  /**
   * Get the range of this melee
   */
  public double getRange() {
    return this.range;
  }

  /**
   * Set a new range to this melee
   */
  public void setRange(double newRange) {
    if (newRange > 0 && newRange < 100.0) {
      this.range = newRange;
    }
  }

  /**
   * Get the begin angle of attack
   */
  public double getBeginAngle() {
    return this.beginAngle;
  }

  /**
   * Set a new begin angle of attack
   */
  public void setBeginAngle(double newBeginAngle) {
    if (newBeginAngle > 0 && newBeginAngle < 90.0) {
      this.beginAngle = newBeginAngle;
    }
  }

  /**
   * Get the end angle of attack
   */
  public double getEndAngle() {
    return this.endAngle;
  }

  /**
   * Set a new end angle of attack
   */
  public void setEndAngle(double newEndAngle) {
    if (newEndAngle > 0 && newEndAngle < 90.0) {
      this.endAngle = newEndAngle;
    }
  }
}
