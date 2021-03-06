package server.ai;

import shared.gameObjects.Blocks.Wood.WoodBlockLargeObject;
import shared.gameObjects.Blocks.Wood.WoodBlockSmallObject;
import shared.gameObjects.Blocks.Wood.WoodFloorObject;
import shared.gameObjects.components.ComponentType;
import shared.gameObjects.components.Rigidbody;
import shared.gameObjects.players.Player;
import shared.gameObjects.weapons.Gun;
import shared.gameObjects.weapons.Melee;
import shared.physics.Physics;
import shared.physics.data.Collision;
import shared.physics.types.RigidbodyType;
import shared.util.maths.Vector2;

/**
 * @author Harry Levick (hxl799)
 */
public enum FSA {
  /**
   * The attacking state
   */
  ATTACKING() {
    /**
     * Finds the next state based on the current scenario in the world
     * @param targetPlayer The bots target.
     * @param bot The bot that this FSA is associated with.
     * @param targetDistance The distance to the target.
     * @return The next state
     */
    public FSA next(Player targetPlayer, Player bot, double targetDistance, int prevHealth) {
      if (targetPlayer == null) {
        return IDLE;
      }

      StateInfo.setInfo(targetPlayer, bot);

      double weaponRange = StateInfo.weaponRange;
      int ammoLeft = StateInfo.ammoLeft;
      int botHealth = StateInfo.botHealth;
      boolean inSight;

      Vector2 botPos = bot.getTransform().getPos();
      Vector2 botPosCenter = botPos.add(bot.getTransform().getSize().mult(0.5f));
      Vector2 enemyPos = targetPlayer.getTransform().getPos();
      Vector2 enemyPosCenter = enemyPos.add(bot.getTransform().getSize().mult(0.5f));

      // Use the worldScene of the path finding to raycast, instead of the actual gameObjects list.
      Collision rayCast = Physics.raycastAi(botPosCenter,
          enemyPosCenter.sub(botPosCenter),
          null,
          (Bot) bot,
          false);

      inSight = ((Rigidbody) rayCast.getCollidedObject()
          .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC;
      /*
      if (bot.getHolding().isGun() && ((Gun) bot.getHolding()).firesExplosive()) {
        inSight = ((Rigidbody) rayCast.getCollidedObject()
            .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC;
      } else {
        inSight = ((Rigidbody) rayCast.getCollidedObject()
            .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC &&
            !(rayCast.getCollidedObject() instanceof WoodBlockSmallObject ||
                rayCast.getCollidedObject() instanceof WoodBlockLargeObject ||
                rayCast.getCollidedObject() instanceof WoodFloorObject);
      }

       */

      if (((targetDistance > weaponRange) || !inSight)
          && ((ammoLeft > 0) && bot.getHolding().isGun() || bot.getHolding().isMelee())) {
        return CHASING;

      } else if ((prevHealth > botHealth)
          || ((ammoLeft == 0) && bot.getHolding().isGun())) {
        return FLEEING;

      } else if (inSight) {
        return ATTACKING;

      } else {
        return IDLE;
      }
    }
  },
  /**
   * The chasing state
   */
  CHASING() {
    /**
     * Finds the next state based on the current scenario in the world
     * @param targetPlayer The bots target.
     * @param bot The bot that this FSA is associated with.
     * @param targetDistance The distance to the target.
     * @return The next state
     */
    public FSA next(Player targetPlayer, Player bot, double targetDistance, int prevHealth) {
      if (targetPlayer == null) {
        return IDLE;
      }

      StateInfo.setInfo(targetPlayer, bot);

      double weaponRange = StateInfo.weaponRange;
      int ammoLeft = StateInfo.ammoLeft;
      int botHealth = StateInfo.botHealth;
      boolean inSight;

      Vector2 botPos = bot.getTransform().getPos();
      Vector2 botPosCenter = botPos.add(bot.getTransform().getSize().mult(0.5f));
      Vector2 enemyPos = targetPlayer.getTransform().getPos();
      Vector2 enemyPosCenter = enemyPos.add(bot.getTransform().getSize().mult(0.5f));

      // Use the worldScene of the path finding to raycast, instead of the actual gameObjects list.
      Collision rayCast = Physics.raycastAi(botPosCenter,
          enemyPosCenter.sub(botPosCenter),
          null,
          (Bot) bot,
          false);

      inSight = ((Rigidbody) rayCast.getCollidedObject()
          .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC;
      /*

      if (bot.getHolding().isGun() && ((Gun) bot.getHolding()).firesExplosive()) {
        inSight = ((Rigidbody) rayCast.getCollidedObject()
            .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC;
      } else {
        inSight = ((Rigidbody) rayCast.getCollidedObject()
            .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC &&
            !(rayCast.getCollidedObject() instanceof WoodBlockSmallObject ||
                rayCast.getCollidedObject() instanceof WoodBlockLargeObject ||
                rayCast.getCollidedObject() instanceof WoodFloorObject);
      }
       */

      if ((targetDistance <= weaponRange)
          && inSight
          && ((ammoLeft > 0) && bot.getHolding().isGun() || bot.getHolding().isMelee())) {
        return ATTACKING;

      } else if ((prevHealth > botHealth)
          || ((bot.getHolding().isGun()) && (ammoLeft == 0))) {
        return FLEEING;

      } else {
        return CHASING;
      }
    }
  },
  /**
   * The fleeing state
   */
  FLEEING() {
    /**
     * Finds the next state based on the current scenario in the world
     * @param targetPlayer The bots target.
     * @param bot The bot that this FSA is associated with.
     * @param targetDistance The distance to the target.
     * @return The next state
     */
    public FSA next(Player targetPlayer, Player bot, double targetDistance, int prevHealth) {

      if (targetPlayer == null) {
        return IDLE;
      }

      StateInfo.setInfo(targetPlayer, bot);

      double weaponRange = StateInfo.weaponRange;
      int ammoLeft = StateInfo.ammoLeft;
      int botHealth = StateInfo.botHealth;
      boolean inSight;

      Vector2 botPos = bot.getTransform().getPos();
      Vector2 botPosCenter = botPos.add(bot.getTransform().getSize().mult(0.5f));
      Vector2 enemyPos = targetPlayer.getTransform().getPos();
      Vector2 enemyPosCenter = enemyPos.add(bot.getTransform().getSize().mult(0.5f));

      // Use the worldScene of the path finding to raycast, instead of the actual gameObjects list.
      Collision rayCast = Physics.raycastAi(botPosCenter,
          enemyPosCenter.sub(botPosCenter),
          null,
          (Bot) bot,
          false);

      inSight = ((Rigidbody) rayCast.getCollidedObject()
          .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC;

      /*

      if (bot.getHolding().isGun() && ((Gun) bot.getHolding()).firesExplosive()) {
        inSight = ((Rigidbody) rayCast.getCollidedObject()
            .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC;
      } else {
        inSight = ((Rigidbody) rayCast.getCollidedObject()
            .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC &&
            !(rayCast.getCollidedObject() instanceof WoodBlockSmallObject ||
                rayCast.getCollidedObject() instanceof WoodBlockLargeObject ||
                rayCast.getCollidedObject() instanceof WoodFloorObject);
      }
       */

      Melee temp;

      double enemyWeaponRange =
          (targetPlayer.getHolding().isGun())
              ? Double.POSITIVE_INFINITY
              : (temp = (Melee) targetPlayer.getHolding()).getRange();

      if ((targetDistance <= weaponRange)
          && inSight
          && ((ammoLeft > 0) && bot.getHolding().isGun() || bot.getHolding().isMelee())) {
        return ATTACKING;

      } else if (targetDistance > weaponRange || !inSight) {
        return CHASING;

      } else {
        return FLEEING;
      }
    }
  },
  /**
   * The idle state
   */
  IDLE() {
    /**
     * Finds the next state based on the current scenario in the world
     * @param targetPlayer The bots target.
     * @param bot The bot that this FSA is associated with.
     * @param targetDistance The distance to the target.
     * @return the next state
     */
    public FSA next(Player targetPlayer, Player bot, double targetDistance, int prevHealth) {
      if (targetPlayer == null) {
        return IDLE;
      }

      StateInfo.setInfo(targetPlayer, bot);

      double weaponRange = StateInfo.weaponRange;
      int ammoLeft = StateInfo.ammoLeft;
      int botHealth = StateInfo.botHealth;
      boolean inSight;

      Vector2 botPos = bot.getTransform().getPos();
      Vector2 botPosCenter = botPos.add(bot.getTransform().getSize().mult(0.5f));
      Vector2 enemyPos = targetPlayer.getTransform().getPos();
      Vector2 enemyPosCenter = enemyPos.add(bot.getTransform().getSize().mult(0.5f));

      // Use the worldScene of the path finding to raycast, instead of the actual gameObjects list.
      Collision rayCast = Physics.raycastAi(botPosCenter,
          enemyPosCenter.sub(botPosCenter),
          null,
          (Bot) bot,
          false);

      inSight = ((Rigidbody) rayCast.getCollidedObject()
          .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC;
      /*

      if (bot.getHolding().isGun() && ((Gun) bot.getHolding()).firesExplosive()) {
        inSight = ((Rigidbody) rayCast.getCollidedObject()
            .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC;
      } else {
        inSight = ((Rigidbody) rayCast.getCollidedObject()
            .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC &&
            !(rayCast.getCollidedObject() instanceof WoodBlockSmallObject ||
                rayCast.getCollidedObject() instanceof WoodBlockLargeObject ||
                rayCast.getCollidedObject() instanceof WoodFloorObject);
      }
       */

      if ((botHealth >= this.HIGH_HEALTH || botHealth < this.MEDIUM_HEALTH)
          && inSight
          && (targetDistance <= weaponRange)
          && ((ammoLeft > 0) && bot.getHolding().isGun() || bot.getHolding().isMelee())) {
        return ATTACKING;

      } else if (((botHealth >= this.HIGH_HEALTH)) && (targetDistance > weaponRange || !inSight)) {
        return CHASING;

      } else if ((botHealth < this.MEDIUM_HEALTH)) {
        return FLEEING;

      } else {
        return IDLE;
      }
    }
  },
  /**
   * The initial state
   */
  INITIAL_STATE() {
    /**
     * Finds the next state based on the current scenario in the world
     * @param targetPlayer The bots target.
     * @param bot The bot that this FSA is associated with.
     * @param targetDistance The distance to the target.
     * @return The next state
     */
    public FSA next(Player targetPlayer, Player bot, double targetDistance, int prevHealth) {
      // The initial state just acts as an entry point, and so directs straight to the IDLE state.
      return FSA.IDLE;
    }
  };

  /**
   * The border between medium - high health
   */
  final int HIGH_HEALTH = 66;
  /**
   * The border between low - medium health
   */
  final int MEDIUM_HEALTH = 33;

  /**
   * Determines the state to update to.
   *
   * @param targetPlayer The bots target.
   * @param bot The bot that this FSA is associated with.
   * @param targetDistance The distance to the target.
   * @return The state that we should update to.
   */
  public abstract FSA next(Player targetPlayer, Player bot, double targetDistance, int prevHealth);
}
