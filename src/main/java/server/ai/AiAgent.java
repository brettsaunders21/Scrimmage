package server.ai;

import shared.gameObjects.Utils.ObjectID;

/** @author Harry Levick (hxl799) */
public class AiAgent {

  Bot player;
  FiniteStateAutomata state;
  boolean active;

  public AiAgent(double xPos, double yPos, ObjectID id) {
    player = new Bot(xPos, yPos, id);
    state = FiniteStateAutomata.INITIAL_STATE;
    active = false;
  }

  /**
   * The method that runs the agent.
   */
  public void startAgent() {
    active = true;

    while (active) {
      state = state.next();
      /**
       * The ai can be in one of 6 states at any one time.
       * The state it is in determines the actions that it takes.
       */
      switch (state) {
        case STILL:
          break;

        case CHASING:

        case FLEEING:

        case ATTACKING:

        case CHASING_ATTACKING:

        case FLEEING_ATTACKING:

        default:
      }
    }

  }
}
