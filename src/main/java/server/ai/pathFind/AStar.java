package server.ai.pathFind;

/**
 * @author Harry Levick (hxl799)
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import server.ai.Bot;
import server.ai.FSA;
import shared.gameObjects.GameObject;
import shared.gameObjects.components.ComponentType;
import shared.gameObjects.components.Rigidbody;
import shared.gameObjects.players.Player;
import shared.gameObjects.weapons.Handgun;
import shared.gameObjects.weapons.MachineGun;
import shared.gameObjects.weapons.Melee;
import shared.gameObjects.weapons.Punch;
import shared.gameObjects.weapons.Sword;
import shared.gameObjects.weapons.Weapon;
import shared.handlers.levelHandler.LevelHandler;
import shared.physics.Physics;
import shared.physics.data.Collision;
import shared.physics.types.RigidbodyType;
import shared.util.maths.Vector2;
/**
 * The main file for the A* planner. - search(): This function is the core search algorithm,
 * searching for an optimal path. - optimise(): Function controlling the search and extracting plans
 * to return to the Application Programming Interface.
 */
public class AStar {

  /** The levelhandler used to retrieve game objects */
  LevelHandler levelHandler;
  /** The list of game objects in the current search world */
  ArrayList<GameObject> worldScene;
  /** penalty for being in the visited-states list */
  public static final int visitedListPenalty = 100;
  /** The current best position fouond by the planner */
  public SearchNode bestPosition;
  /** The furthest position found by the planner (sometimes different to the best) */
  public SearchNode furthestPosition;
  /** The open list of A*, contains all the unexplored search nodes */
  ArrayList<SearchNode> openList;
  /** The list of closed (visited) nodes */
  ArrayList<SearchNode> closedList;
  /** The respective bot */
  Bot bot;
  /** The enemy x-coord that we are path-finding to */
  double enemyX;
  /** The enemy y-coord that we are path-finding to */
  double enemyY;
  /** The size of the enemy that we are path-finding to */
  Vector2 enemySize;
  /** The plan generated by the planner */
  private List<boolean[]> currentPlan;
  /** clone of the bot to be used by search nodes */
  Bot replicaBot;

  /**
   * A SearchNode is a node in the A* search, consisting of an action (that got to the current
   * node), the world state after this action was used, and information about the parent node.
   */
  public class SearchNode {

    /** The distance from the start of the search to this node (g) */
    double distanceElapsed;
    /** The distance to reach the goal node AFTER simulating with the selected action (h) */
    double remainingDistance;
    /** The F value (f = g + h) of the node */
    double fValue;
    /** The parent node */
    SearchNode parentNode;
    /** The enemy x-coord that we are path-finding to, local to the search node */
    double botX;
    /** The enemy y-coord that we are path-finding to, local to the search node */
    double botY;
    /** Flag to show if the node has been visited already */
    boolean visited = false;
    /** The action used to get to this node */
    boolean[] action;

    /**
     * Create a new node
     * @param action The action to get to this node
     * @param parent The parent node
     */
    public SearchNode(boolean[] action, SearchNode parent) {
      this.parentNode = parent;
      if (parentNode != null) {
        replicaBot.setX(parentNode.botX);
        replicaBot.setY(parentNode.botY);
        // Simulate the bot with the action, using the game physics
        simulateBot(action);

        botX = replicaBot.getX();
        botY = replicaBot.getY();

        Vector2 thisPos = new Vector2(botX, botY);
        Vector2 parentPos = new Vector2(parent.botX, parent.botX);

        double distChange = thisPos.exactMagnitude(parentPos);
        this.remainingDistance = calcH(getItems(worldScene));
        this.distanceElapsed = parent.distanceElapsed + distChange;

        this.fValue = /*distanceElapsed +*/ remainingDistance;
      } else {
        distanceElapsed = 0;
        replicaBot = new Bot(bot);
        botX = replicaBot.getX();
        botY = replicaBot.getY();

        // Create a copy of the bots weapon
        Weapon botWeapon = bot.getHolding();
        Weapon cloneWeapon = null;

        if (botWeapon instanceof Handgun) {
          cloneWeapon = new Handgun((Handgun) botWeapon);
        } else if (botWeapon instanceof MachineGun) {
          cloneWeapon = new MachineGun((MachineGun) botWeapon);
        } else if (botWeapon instanceof Punch) {
          cloneWeapon = new Punch((Punch) botWeapon);
        } else if (botWeapon instanceof Sword) {
          cloneWeapon = new Sword((Sword) botWeapon);
        }
        replicaBot.setHolding(cloneWeapon);

        // Calculate the heuristic value of the node.
        this.remainingDistance = calcH(getItems(worldScene));
        this.fValue = remainingDistance + distanceElapsed;
      }

      this.action = action;
    }

    /**
     * Simulate the bot with its action
     * @param action the action that the simulation will use
     */
    private void simulateBot(boolean[] action) {
      replicaBot.simulateAction(action);
      replicaBot.simulateApplyInput();
      replicaBot.simulateUpdate();
      replicaBot.simulateUpdateCollision();
    }

    /**
     * Calculate the heuristic value for the node.
     * @return the distance to the enemy
     */
    public double calcH(List<Weapon> allItems) {
      Vector2 botPos = new Vector2((float) botX, (float) botY);
      Vector2 enemyPos = new Vector2((float) enemyX, (float) enemyY);
      double totalH = botPos.exactMagnitude(enemyPos);

      GameObject weaponToCollect = findClosestItem(allItems);
      if (weaponToCollect != null) {
        Vector2 itemPos = new Vector2((float) weaponToCollect.getX(),
            (float) weaponToCollect.getY());
        double distanceToItem = botPos.exactMagnitude(itemPos);
        // The heuristic value is the combined distance of the bot->enemy + bot->item
        // The heuristic value for the item is weighted to add preference to pick the items up.
        totalH += (distanceToItem * 5);
      }

      return totalH;
    }

    /**
     * Find all items in the world, currently only finds weapons because no such item yet
     * implemented.
     * @param allObjects all objects in the world
     * @return list of weapons
     */
    private List<Weapon> getItems(List<GameObject> allObjects) {
      // Collect all weapons from the world
      List<Weapon> allWeapons =
          allObjects.stream()
              .filter(w -> w instanceof Weapon && (((Weapon) w).getHolder() == null))
              .map(Weapon.class::cast)
              .collect(Collectors.toList());

      return allWeapons;
    }

    /**
     * Generate all the possible children of the node by calculating the result of all possible
     * actions.
     * @return The list of children nodes.
     */
    public ArrayList<SearchNode> generateChildren() {
      ArrayList<SearchNode> list = new ArrayList<>();
      ArrayList<boolean[]> possibleActions = createPossibleActions(this);

      for (boolean[] action : possibleActions) {
        SearchNode neighbour = new SearchNode(action, this);
        if (!isInClosed(neighbour) && !isInOpen(neighbour) && isInWorld(neighbour)) {
          list.add(neighbour);
          //Physics.drawCast(neighbour.botX, neighbour.botY, neighbour.botX, neighbour.botY, "ff0000");
        }
      }

      return list;
    }

    /**
     * Finds the closest pick-upable item
     * @param allItems A list of all the items in the world.
     * @return The item that is the closest to the bot
     */
    public Weapon findClosestItem(List<Weapon> allItems) {
      Weapon closestWeap = null;
      Vector2 botPos = new Vector2((float) bot.getX(), (float) bot.getY());
      double targetDistance = Double.POSITIVE_INFINITY;

      for (Weapon weap : allItems) {
        Vector2 itemPos = new Vector2((float) weap.getX(), (float) weap.getY());
        double distance = botPos.exactMagnitude(itemPos);

        boolean betterWeap = weap.getWeaponRank() > replicaBot.getHolding().getWeaponRank();

        if (distance < targetDistance &&
            (weap.getWeaponRank() > replicaBot.getHolding().getWeaponRank())) {
          targetDistance = distance;
          closestWeap = weap;
        }
      }

      return closestWeap;
    }
  }

  /**
   * Boolean method to check if a SearchNode is within the world bounds.
   * @param neighbour
   * @return returns true if the SearchNode is in the world.
   */
  private boolean isInWorld(SearchNode neighbour) {
    int xMax = 1880;
    int yMax = 1040;
    Vector2 botSize = replicaBot.getTransform().getSize();

    boolean outHorizontal = (neighbour.botX + botSize.getX() <= 30) ||
        (neighbour.botX >= xMax);

    boolean outVertical = (neighbour.botY + botSize.getY() <= 30) ||
        (neighbour.botY >= yMax);

    if (outHorizontal || outVertical) {
      return false;
    } else return true;
  }

  /**
   * The main search function
   */
  private void search() {
    int searchCount = 0, seachCutoff = 1300;
    SearchNode current = bestPosition;
    closedList.add(current);
    boolean currentGood = false;

    while (openList.size() != 0 && !atEnemy(current.botX, current.botY, replicaBot)) {
      current = pickBestNode(openList);
      currentGood = false;

      if (!current.visited && isInClosed(current)) {
        /**
         * Closed List -> Nodes too close to a node in the closed list are considered visited, even
         * though they are a bit different.
         */
        current.fValue += visitedListPenalty;
        current.visited = true;
        openList.add(current);

      } else {
        currentGood = true;
        closedList.add(current);
        openList.addAll(current.generateChildren());
        searchCount++;
        //Physics.drawCast(current.botX, current.botY, current.botX, current.botY, "#00ff00");
      }

      if (currentGood) {
        bestPosition = current;
      }
      if (searchCount >= seachCutoff) {
        break;
      }

    }

  }

  /**
   * Create new AStar
   * @param bot the bot that this path-finding is concerned with.
   * @param levelHandler the levelhandler of the game.
   */
  public AStar(Bot bot, LevelHandler levelHandler) {
    this.levelHandler = levelHandler;
    this.bot = bot;
    currentPlan = new ArrayList<>();
  }

  /**
   * The api of the AStar search
   * @param enemy the enemy to search towards.
   * @param mode whether we are chasing or fleeing
   * @return The action to take.
   */
  public List<boolean[]> optimise(Player enemy, FSA mode) {
    initEnemyData(enemy);
    initSearch();

    switch (mode) {
      case CHASING:
        search();

        break;
      case FLEEING:
        flee(enemy);
        break;
    }

    currentPlan = extractPlan();

    return currentPlan;
  }

  /**
   * Initialise the planner
   */
  private void initSearch() {
    // Take the game objects from the level handler on each search
    this.worldScene = new ArrayList<>(this.levelHandler.getGameObjects().values());
    SearchNode startPosition = new SearchNode(null, null);

    openList = new ArrayList<>();
    closedList = new ArrayList<>();
    openList.addAll(startPosition.generateChildren());

    bestPosition = startPosition;
    furthestPosition = startPosition;
  }

  /**
   * The main fleeing function
   * @param enemy the enemy we are fleeing from
   */
  private void flee(Player enemy) {
    int searchCount = 0, seachCutoff = 200;
    SearchNode current = bestPosition;
    closedList.add(current);
    boolean currentGood = false;

    while (openList.size() != 0 && atEnemy(current.botX, current.botY, enemy)) {
      current = pickWorstNode(openList);
      currentGood = false;

      if (!current.visited && isInClosed(current)) {
        /**
         * Closed List -> Nodes too close to a node in the closed list are considered visited, even
         * though they are a bit different.
         */
        current.fValue += visitedListPenalty;
        current.visited = true;
        openList.add(current);

      } else {

        if (!isInWorld(current))
          break;

        currentGood = true;
        closedList.add(current);
        openList.addAll(current.generateChildren());
        searchCount++;
      }

      if (currentGood)
        bestPosition = current;
      if (searchCount >= seachCutoff)
        break;

    }

  }

  /**
   * Check if the current position of the bot is close enough to the enemy to attack them, and
   * if they are in sight of the enemy.
   * @param botX the bots x-coord
   * @param botY the bots y-coord
   * @param weaponHolder the player whose gun we are concerned with (enemy if we are fleeing, ours
   * if we are chasing)
   * @return true if the bot is close enough to the enemy.
   */
  private boolean atEnemy(double botX, double botY, Player weaponHolder) {
    Vector2 botPos = new Vector2(botX, botY);
    Vector2 botPosCenter = botPos.add(bot.getTransform().getSize().mult(0.5f));
    Vector2 enemyPos = new Vector2(enemyX, enemyY);
    Vector2 enemyPosCenter = enemyPos.add(bot.getTransform().getSize().mult(0.5f));

    double dist = botPos.exactMagnitude(enemyPos);
    Melee tempMelee;

    // Use the worldScene of the path finding to raycast, instead of the actual gameObjects list.
    Collision rayCast = Physics.raycastAi(botPosCenter,
        enemyPosCenter.sub(botPosCenter),
        worldScene,
        bot,
        false);

    // If the cast is null or returns a Static RigidBody
    boolean inSight = rayCast == null || (((Rigidbody) rayCast.getCollidedObject()
        .getComponent(ComponentType.RIGIDBODY)).getBodyType() != RigidbodyType.STATIC);

    if (weaponHolder.getHolding().isGun()) {

      if (inSight)
        return true;
      else {
        return false;
      }

    } else { // melee weapon
      tempMelee = (Melee) weaponHolder.getHolding();
      if (dist <= tempMelee.getRange() && inSight) {
        return true;
      } else {
        return false;
      }
    }

  }

  /**
   * Find the actions that the bot can take, given its current position
   * @param currentPos The current position of the bot
   * @return A list of actions that the bot could take
   */
  private ArrayList<boolean[]> createPossibleActions(SearchNode currentPos) {
    boolean[] parentAction = currentPos.action;
    System.out.print("");
    Bot nodeBot = replicaBot;
    nodeBot.setX(currentPos.botX);
    nodeBot.setY(currentPos.botY);
    boolean left = false, right = false;
    ArrayList<boolean[]> possibleActions = new ArrayList<>();
    Vector2 botPosition = new Vector2((float) currentPos.botX, (float) currentPos.botY);
    Vector2 botSize = nodeBot.getTransform().getSize();

    // Box cast to the left
    if (!Arrays.equals(parentAction, new boolean[]{false, false, true})) {

      ArrayList<Collision> viscinityLeft = Physics.boxcastAll(
          botPosition.add(Vector2.Left().mult(botSize.mult(new Vector2(0.6, 0.9))))
              .add(Vector2.Down().mult(3)),
          botSize.mult(new Vector2(0.6, 0.85)), false);

      if (viscinityLeft.size() == 0 ||
          (viscinityLeft.stream().allMatch(o -> ((Rigidbody) o.getCollidedObject()
              .getComponent(ComponentType.RIGIDBODY))
              .getBodyType() != RigidbodyType.STATIC))) {
        // If no collision
        possibleActions.add(createAction(false, true, false));
        left = true;
      }
    } else {
      left = true; // Cant move LEFT because the parent action was a RIGHT
    }


    // Box cast to the right
    if (!Arrays.equals(parentAction, new boolean[]{false, true, false})) {

      ArrayList<Collision> viscinityRight = Physics.boxcastAll(
          botPosition.add(Vector2.Right().mult(botSize)).add(Vector2.Down().mult(3)),
          botSize.mult(new Vector2(0.6, 0.85)), false);

      if (viscinityRight.size() == 0 ||
          (viscinityRight.stream().allMatch(o -> ((Rigidbody) o.getCollidedObject()
              .getComponent(ComponentType.RIGIDBODY))
              .getBodyType() != RigidbodyType.STATIC))) {

        possibleActions.add(createAction(false, false, true));
        right = true;
      }
    } else {
      right = true; // Cant move RIGHT because the parent action was a LEFT
    }


    // Box cast upwards
    if (nodeBot.mayJump()) { // If the bot cant jump, theres no point casting upwards

      ArrayList<Collision> viscinityUp = Physics.boxcastAll(
          botPosition.add(Vector2.Up().mult(botSize.mult(new Vector2(1, 0.5)))),
          botSize.mult(new Vector2(1, 0.5)), false);

      // If no collision
      if (viscinityUp.size() == 0 ||
          (viscinityUp.stream().allMatch(o -> ((Rigidbody) o.getCollidedObject()
              .getComponent(ComponentType.RIGIDBODY))
              .getBodyType() != RigidbodyType.STATIC))) {

        ArrayList<Collision> viscinityUpLeft = Physics.boxcastAll(
            botPosition.add(Vector2.Up().mult(botSize)).add(Vector2.Left()
                .mult(new Vector2(0.5, 1))),
            botSize.mult(new Vector2(0.5, 1)), false);

        ArrayList<Collision> viscinityUpRight = Physics.boxcastAll(
            botPosition.add(Vector2.Up().mult(botSize)).add(Vector2.Right()
                .mult(botSize.mult(new Vector2(0.5, 1)))),
            botSize.mult(new Vector2(0.5, 1)), false);

        // Just jump
        possibleActions.add(createAction(true, false, false));

        if (viscinityUpRight.size() == 0 ||
            (viscinityUpRight.stream().allMatch(o -> ((Rigidbody) o.getCollidedObject()
                .getComponent(ComponentType.RIGIDBODY))
                .getBodyType() != RigidbodyType.STATIC))) {
          // Jump to the right
          possibleActions.add(createAction(true, false, true));
        }

        if (viscinityUpLeft.size() == 0 ||
            (viscinityUpLeft.stream().allMatch(o -> ((Rigidbody) o.getCollidedObject()
                .getComponent(ComponentType.RIGIDBODY))
                .getBodyType() != RigidbodyType.STATIC))) {
          // Jump to the left
          possibleActions.add(createAction(true, true, false));
        }

      }

    } else {
      possibleActions.add(createAction(false, false, false));
      if (!left)
        possibleActions.add(createAction(false, true, false));
      if (!right)
        possibleActions.add(createAction(false, false, true));
    }

    return possibleActions;
  }

  /**
   * Returns if a node is already in the closed list
   * @param node The node in question
   */
  private boolean isInClosed(SearchNode node) {
    // Is the x and y coords of the given node too close the the coords of a node in the visited
    // list?
    double nodeX = node.botX;
    double nodeY = node.botY;
    double xDiff = 5.0;
    double yDiff = 5.0;

    for (SearchNode n : closedList) {

      if ((Math.abs(n.botX - nodeX) < xDiff) &&
          (Math.abs(n.botY - nodeY) < yDiff)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns if a node is already in the open list
   * @param node the node in question
   */
  private boolean isInOpen(SearchNode node) {
    double nodeX = node.botX;
    double nodeY = node.botY;
    double xDiff = 5.0;
    double yDiff = 5.0;

    for (SearchNode n : openList) {

      if ((Math.abs(n.botX - nodeX) < xDiff) &&
          (Math.abs(n.botY - nodeY) < yDiff)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Extract the plan by taking the best node and going back to the root, recording the actions
   * taken at each step.
   */
  private ArrayList<boolean[]> extractPlan() {
    ArrayList<boolean[]> actions = new ArrayList<>();

    // Just do nothing if no best position exists
    if (bestPosition == null) {
      return actions;
    }

    SearchNode current = bestPosition;
    while (current.parentNode != null) {
      // Add the same action twice because the physics simulation in the path finding is less
      // than the actual physics in the main game loop, so one acton in path finding ~= two actions
      // in the main game loop.
      actions.add(0, current.action);
      current = current.parentNode;
    }

    return actions;
  }


  /**
   * Sets the enemies position and size
   * @param enemy the enemy
   */
  private void initEnemyData(Player enemy) {
    enemyX = enemy.getX();
    enemyY = enemy.getY();
    enemySize = enemy.getTransform().getSize();
  }

  /**
   * Create an action given multiple flags
   * @param jump The jump flag
   * @param left Move left flag
   * @param right Move right flag
   * @return An array of booleans, with the flags packed into the array
   */
  private boolean[] createAction(boolean jump, boolean left, boolean right) {
    boolean[] action = new boolean[3];
    action[Bot.KEY_JUMP] = jump;
    action[Bot.KEY_LEFT] = left;
    action[Bot.KEY_RIGHT] = right;

    return action;
  }

  /**
   * Picks the node in the open list with the best f (f = h + g) value
   * @param openList the list of open nodes
   * @return the best node, from the open list
   */
  private SearchNode pickBestNode(ArrayList<SearchNode> openList) {
    SearchNode bestNode = null;
    double bestNodeF = Double.POSITIVE_INFINITY;

    for (SearchNode current : openList) {
      double f = current.fValue;
      if (f < bestNodeF) {
        bestNode = current;
        bestNodeF = f;
      }
    }
    openList.remove(bestNode);

    return bestNode;
  }

  /**
   * Picks the node in the open list with the worst f (f = h + g) value
   * @param openList the list of open nodes
   * @return the worst node, from the open list
   */
  private SearchNode pickWorstNode(ArrayList<SearchNode> openList) {
    SearchNode worstNode = null;
    double worstNodeF = Double.NEGATIVE_INFINITY;

    for (SearchNode current : openList) {
      double f = current.fValue;
      if (f > worstNodeF) {
        worstNode = current;
        worstNodeF = f;
      }
    }
    openList.remove(worstNode);

    return worstNode;
  }


}
