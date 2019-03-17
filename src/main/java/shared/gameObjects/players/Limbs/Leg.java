package shared.gameObjects.players.Limbs;

import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.players.Limb;
import shared.gameObjects.players.Player;
import shared.handlers.levelHandler.LevelHandler;
import shared.gameObjects.players.Behaviour;

public class Leg extends Limb {

  /**
   * Base class used to create an object in game. This is used on both the client and server side to
   * ensure actions are calculated the same
   */
  public Leg(Boolean isLeft, Player parent, LevelHandler levelHandler) {
    super(19, 87, 43, 87, 21, 23, ObjectType.Limb, isLeft, parent, parent, 0, 0, levelHandler);
  }

  @Override
  public void initialiseAnimation() {
    this.animation.supplyAnimation("default", "images/player/Standard_Male/leg.png");
  }
  

  @Override
  protected void rotateAnimate() {
   
    interval = 7;
    segments = 3;
    localTime = this.player.getAnimationTimer() % (interval * segments);
    
    // Control to switch the leg animations depending on movement direction.
    boolean control = isLeft;
    int inverse = 1;
    if(this.behaviour == Behaviour.WALK_LEFT) {
      control =!control;
      inverse = -1;
    }
    
    if(this.behaviour == Behaviour.WALK_LEFT || this.behaviour == Behaviour.WALK_RIGHT) {     

      if(localTime < interval*1) {
        if(control) {
          imageView.setRotate(45*inverse);
        }
        else {
          imageView.setRotate(-45*inverse);
        }
      }
      else if(localTime < interval*2) {
        if(control) {
          imageView.setRotate(-40*inverse);
        }
        else {
          imageView.setRotate(0*inverse);
          int offset = 5;
          if(this.behaviour == Behaviour.WALK_RIGHT) {
            offset = -15;
          }
          imageView.setX(imageView.getX()-offset);
          resetOffsetX = offset;
        }
      }
      
      else if(localTime < interval*3) {
        if(control) {
          imageView.setRotate(0);
        }
        else {
          imageView.setRotate(0);
        }
      }
      

        
    }
  }
  
  
  
}
