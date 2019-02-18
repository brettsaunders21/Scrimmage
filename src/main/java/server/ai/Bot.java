package server.ai;

import client.handlers.connectionHandler.ConnectionHandler;
import client.handlers.inputHandler.InputHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import server.ai.pathFind.AStar;
import shared.gameObjects.GameObject;
import shared.gameObjects.players.Player;
import java.util.UUID;
import shared.packets.PacketInput;
import shared.physics.Physics;
import shared.util.maths.Vector2;


/** @author Harry Levick (hxl799) */
public class Bot extends Player {

  public static final int KEY_JUMP = 0;
  public static final int KEY_LEFT = 1;
  public static final int KEY_RIGHT = 2;
  public static final int KEY_CLICK = 3;
  public float jumpTime;
  boolean jumpKey, leftKey, rightKey, click;
  double mouseX, mouseY;
  boolean mayJump = true;

  FSA state;
  Player targetPlayer;
  AStar pathFinder;
  List<Player> allPlayers;

  /**
   * @param x
   * @param y
   * @param sizeX
   * @param sizeY
   * @param playerUUID
   * @param allObjs Contains a list of all game objects in the world, including players.
   */
  public Bot(double x, double y, double sizeX, double sizeY, UUID playerUUID,
      List<GameObject> allObjs) {
    super(x, y, sizeX, sizeY, playerUUID);
    allPlayers = new ArrayList<>();
    this.state = FSA.INITIAL_STATE;

    // Collect all players (other than bots) from the world
    allPlayers = allObjs.stream()
            .filter(p -> p instanceof Player)
            .map(Player.class::cast)
            .collect(Collectors.toList());

    targetPlayer = findTarget(allPlayers);
    this.pathFinder = new AStar(allObjs, this);
  }

  public boolean mayJump() {
    return mayJump;
  }

  @Override
  public void applyInput(boolean multiplayer, ConnectionHandler connectionHandler) {
    if (this.rightKey) {
      rb.moveX(speed);
      animation.switchAnimation("walk");
      imageView.setScaleX(1);
    }
    if (this.leftKey) {
      rb.moveX(speed * -1);
      animation.switchAnimation("walk");
      imageView.setScaleX(-1);
    }

    if (!this.rightKey && !this.leftKey) {
      vx = 0;
      animation.switchDefault();

    }
    if (this.jumpKey && !jumped) {
      rb.moveY(jumpForce, 0.33333f);
      jumped = true;
    }
    if (jumped) {
      animation.switchAnimation("jump");
    }
    if(grounded) {
      jumped = false;
    }
    if (this.click && holding != null) {
      holding.fire(this.mouseX, this.mouseY);
    } //else punch
    //setX(getX() + (vx * 0.0166));

    if (this.getHolding() != null) {
      this.getHolding().setX(this.getX() + 60);
      this.getHolding().setY(this.getY() + 70);
    }

    // If multiplayer then send input to server
    if (multiplayer) {
      PacketInput input =
          new PacketInput(
              InputHandler.x,
              InputHandler.y,
              InputHandler.leftKey,
              InputHandler.rightKey,
              InputHandler.jumpKey,
              InputHandler.click);
      connectionHandler.send(input.getData());
    }
  }


  @Override
  public void update() {
    double prevDist, newDist;
    // Calculate the distance to the target from the previous loop
    prevDist = calcDist();
    // Update the target player
    targetPlayer = findTarget(allPlayers);
    // Calculate the distance to the updated target
    newDist = calcDist();

    state = state.next(targetPlayer, this, prevDist, newDist);

    switch (state) {
      case IDLE:
        System.out.println("IDLE");
        // TODO what to do in the idle state?
        executeAction(new boolean[] {false, false, false, false, false});
        break;
      case CHASING:
        System.out.println("CHASING");
        // Find the next best move to take, and execute this move.
        executeAction(pathFinder.optimise(targetPlayer));
        // TODO calculate and execute the best path to the target.
        break;
      case FLEEING:
        System.out.println("FLEEING");
        executeAction(new boolean[] {false, false, false, false, false});
        // TODO calculate and execute the best path away from the target.
        break;
      case ATTACKING:
        System.out.println("ATTACKING");
        //executeAction(pathFinder.optimise(targetPlayer));
        // TODO think about how an attacking script would work.
        break;
      case CHASING_ATTACKING:
        System.out.println("CHASING-ATTACKING");
        //executeAction(pathFinder.optimise(targetPlayer));
        // TODO calculate and execute the best path to the target whilst attacking.
        break;
      case FLEEING_ATTACKING:
        System.out.println("CHASING-ATTACKING");
        executeAction(new boolean[] {false, false, false, false, false});
        // TODO calculate and execute the best path away from the target whilst attacking.
        break;
    }

    super.update();
  }

  /**
   * Calculates the distance from the bot to the current target player
   * @return The distance to the target player
   */
  private double calcDist() {
    Vector2 botPos = new Vector2((float) this.getX(), (float) this.getY());
    Vector2 targetPos = new Vector2((float) targetPlayer.getX(), (float) targetPlayer.getY());
    // Calculate the distance to the target
    return botPos.exactMagnitude(targetPos);
  }

  /**
   * Finds the closest player
   *
   * @param allPlayers A list of all players in the world
   * @return The player who is the closest to the bot
   */
  private Player findTarget(List<Player> allPlayers) {
    Player target = null;
    double targetDistance = Double.POSITIVE_INFINITY;
    Vector2 botPos = new Vector2((float) this.getX(), (float) this.getY());

    for (Player p : allPlayers) {
      Vector2 playerPos = new Vector2((float) p.getX(), (float) p.getY());
      double distance = botPos.exactMagnitude(playerPos);
      // Update the target if another player is closer
      if (distance < targetDistance && p.isActive()) {
        targetDistance = distance;
        target = p;
      }
    }
    // Returns null if no active player is found.
    return target;
  }

  /**
   * Receives an action and then executes this action. This method will only execute one action at a
   * time (the first action in the list). Since the method will be called inside of the agent loop
   *
   * @param action: an action to exacute.
   */
  private void executeAction(boolean[] action) {
    this.jumpKey = action[Bot.KEY_JUMP];
    this.leftKey = action[Bot.KEY_LEFT];
    this.rightKey = action[Bot.KEY_RIGHT];
    this.click = action[Bot.KEY_CLICK];
  }

}

