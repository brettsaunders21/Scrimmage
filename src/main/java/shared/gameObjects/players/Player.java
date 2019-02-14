package shared.gameObjects.players;

import client.handlers.connectionHandler.ConnectionHandler;
import client.handlers.inputHandler.InputHandler;
import client.main.Client;
import java.util.UUID;
import shared.gameObjects.GameObject;
import shared.gameObjects.Utils.ObjectID;
import shared.gameObjects.weapons.Sword;
import shared.gameObjects.weapons.Weapon;
import shared.packets.PacketInput;

public class Player extends GameObject {

  protected int health;
  protected Weapon holding;
  protected final int speed = 500;
  private double vx;

  public Player(double x, double y, double sizeX, double sizeY, UUID playerUUID) {
    super(x, y, 100, 100, ObjectID.Player, playerUUID);
    this.health = 100;
    holding = null;
  }

  // Initialise the animation 
  public void initialiseAnimation() {
    this.animation.supplyAnimation("default", "images/player/player_idle.png");
    this.animation.supplyAnimation("moveLeft",
        "images/player/player_left_walk1.png",
        "images/player/player_left_walk2.png");
    this.animation.supplyAnimation("moveRight",
        "images/player/player_right_walk1.png",
        "images/player/player_right_walk2.png");
  }

  @Override
  public void update() {
    // Check if the current holding is valid
    // Change the weapon to Punch if it is not
    badWeapon();
    super.update();
  }

  @Override
  public void render() {
    if (!isActive()) {
      return;
    }
    super.render();
    imageView.setTranslateX(getX());
    imageView.setTranslateY(getY());

  }

  @Override
  public String getState() {
    return null;
  }

  public void applyInput(boolean multiplayer, ConnectionHandler connectionHandler) {
    if (InputHandler.rightKey) {
      vx = speed;
      animation.switchAnimation("moveRight");
    }
    if (InputHandler.leftKey) {
      vx = -speed;
      animation.switchAnimation("moveLeft");
    }

    if (!InputHandler.rightKey && !InputHandler.leftKey) {
      vx = 0;
      animation.switchDefault();
    }
    if (InputHandler.click && holding != null) {
      holding.fire(InputHandler.x, InputHandler.y);
    } //else punch
    setX(getX() + (vx * 0.0166));
    
    if (this.getHolding() != null) {
      this.getHolding().setX(this.getX());
      this.getHolding().setY(this.getY());
    }
    
    /** If multiplayer then send input to server */
    if (multiplayer) {
      PacketInput input = new PacketInput(InputHandler.x, InputHandler.y, InputHandler.leftKey,
          InputHandler.rightKey, InputHandler.jumpKey, InputHandler.click);
      connectionHandler.send(input.getData());
    }
  }
  
  /**
   * Check if the current holding weapon is valid or not
   * 
   * @return True if the weapon is a bad weapon (out of ammo)
   * @return False if the weapon is a good weapon, or there is no weapon
   */
  public boolean badWeapon() {
     if (this.holding == null) return false;
     if (this.holding.getAmmo() == 0) {
       this.holding.destroyWeapon();
       this.setHolding(null);
       
       Weapon sword = new Sword(this.getX(), this.getY(), 50, 50, "newSword@Player", 10, 50, 20, UUID.randomUUID());
       Client.levelHandler.addGameObject(sword);
       this.setHolding(sword);
       return true;
     }
     return false;
  }

  public int getHealth() {
    return health;
  }

  public Weapon getHolding() {
    return holding;
  }

  public void setHolding(Weapon holding) {
    this.holding = holding;
  }
}
