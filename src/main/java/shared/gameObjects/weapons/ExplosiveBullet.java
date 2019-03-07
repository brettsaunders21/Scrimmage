package shared.gameObjects.weapons;

import java.util.UUID;
import shared.gameObjects.players.Player;
import shared.util.Path;

public class ExplosiveBullet extends Bullet {

  private static String imagePath = "images/weapons/explosiveBullet.png";
  private static final int width = 15;
  private static final int damage = 5;
  private static final int speed = 25;

  public ExplosiveBullet (
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
        "default", this.getWidth(), this.getWidth(), true, Path.convert(this.imagePath));
  }

}