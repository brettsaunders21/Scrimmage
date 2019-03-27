package shared.gameObjects.menu.main.account.skins;

import java.util.UUID;
import javafx.scene.input.MouseEvent;
import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.menu.ButtonObject;

public class ButtonBasic extends ButtonObject {

  private String updateText;
  public ButtonBasic(double x, double y, double sizeX, double sizeY, boolean right, ObjectType id, UUID uuid) {
    super(x, y, sizeX, sizeY, "" , id, uuid);
    updateText = right ? ">" : "<";
  }

  public ButtonBasic(double x, double y, double sizeX, double sizeY, String text, ObjectType id, UUID uuid) {
    super(x, y, sizeX, sizeY, "" , id, uuid);
    updateText = text;
  }

  public void doOnClick(MouseEvent e) {
    super.doOnClick(e);
    ((CycleManager)parent).triggerClick(this);
  }

  @Override
  public void initialiseAnimation() {
    if(updateText == null) return;
    if(updateText.equals(">")) {
      this.animation.supplyAnimation("default", "images/buttons/blue_sliderRight.png");
      this.animation.supplyAnimation("clicked", "images/buttons/blue_sliderRight.png");
    } else {
      this.animation.supplyAnimation("default", "images/buttons/blue_sliderLeft.png");
      this.animation.supplyAnimation("clicked", "images/buttons/blue_sliderLeft.png");
    }
  }

  @Override
  public void update() {
    super.update();
    button.setText(updateText);
  }
}
