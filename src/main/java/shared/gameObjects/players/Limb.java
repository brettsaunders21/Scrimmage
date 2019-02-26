package shared.gameObjects.players;

import client.main.Settings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.ObjectID;
import shared.gameObjects.components.BoxCollider;
import shared.gameObjects.components.Rigidbody;
import shared.physics.data.MaterialProperty;
import shared.physics.types.RigidbodyType;

public abstract class Limb extends GameObject {

  protected boolean isLeft;
  protected Rotate rotate;
  protected boolean limbAttached;
  protected boolean lastAttachedCheck;

  protected final double pivotX;
  protected final double pivotY;
  protected Behaviour behaviour;
  protected Behaviour lastBehaviour;
  protected int action;
  protected HashMap<Behaviour, ArrayList<Integer>> actions;

  protected double xLeft;
  protected double yLeft;
  protected double xRight;
  protected double yRight;

  protected Rigidbody rb;
  protected BoxCollider bc;


  /**
   * Base class used to create an object in game. This is used on both the client and server side to
   * ensure actions are calculated the same
   *
   * @param id Unique Identifier of every game object
   */
  public Limb(double xLeft, double yLeft, double xRight, double yRight, double sizeX, double sizeY,
      ObjectID id, Boolean isLeft, GameObject parent, double pivotX, double pivotY) {
    super(0, 0, sizeX, sizeY, id, UUID.randomUUID());
    this.limbAttached = true;
    this.lastAttachedCheck = true;
    this.isLeft = isLeft;
    this.xLeft = xLeft;
    this.yLeft = yLeft;
    this.xRight = xRight;
    this.yRight = yRight;
    this.parent = parent;
    this.rotate = new Rotate();
    this.behaviour = Behaviour.IDLE;
    this.actions = new HashMap<>();
    this.lastBehaviour = Behaviour.IDLE;
    this.action = 0;
    this.pivotX = pivotX;
    this.pivotY = pivotY;

    //Physics
    //bc = new BoxCollider(this, false);
    //addComponent(bc);
    rb =
        new Rigidbody(
            RigidbodyType.DYNAMIC, 80, 8, 0.2f, new MaterialProperty(0.005f, 0.1f, 0.05f), null,
            this);
    rotate.setPivotX(pivotX);
    rotate.setPivotY(pivotY);
  }

  public abstract void initialiseAnimation();

  public void setRelativePosition() {
    if (isLeft) {
      setX(parent.getX() + xLeft);
      setY(parent.getY() + yLeft);
    } else {
      setX(parent.getX() + xRight);
      setY(parent.getY() + yRight);
    }
  }

  @Override
  public void initialise(Group root) {
    super.initialise(root);
    if (isLeft) {
      imageView.setScaleX(-1);
    }
  }

  @Override
  public void update() {
    super.update();
    if (limbAttached) {
      setRelativePosition();
      if (!lastAttachedCheck) {
        removeComponent(rb);
      }
    } else {
      if (lastAttachedCheck) {
        addComponent(rb);
      }
    }
    updateAction();
    imageView.getTransforms().remove(rotate);
    lastAttachedCheck = limbAttached;

  }

  public void reset() {
    Settings.levelHandler.addGameObject(this);
    children.forEach(child -> {
      Limb limb = (Limb) child;
      limb.reset();
    });
  }

  public void updateAction() {
    if (behaviour != lastBehaviour) {
      action = 0;
      lastBehaviour = behaviour;
    }
  }

  public boolean isLimbAttached() {
    return limbAttached;
  }

  public void detachLimb() {
    this.limbAttached = false;
  }

  public void reattachLimb() {
    this.limbAttached = true;
  }
}

