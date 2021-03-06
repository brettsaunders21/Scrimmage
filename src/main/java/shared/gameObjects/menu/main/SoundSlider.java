package shared.gameObjects.menu.main;

import client.main.Client;
import client.main.Settings;
import java.util.UUID;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import shared.gameObjects.Utils.ObjectType;
import shared.gameObjects.menu.SliderObject;

public class SoundSlider extends SliderObject {

  SOUND_TYPE soundType = SOUND_TYPE.MUSIC;

  public SoundSlider(
      double x,
      double y,
      double sizeX,
      double sizeY,
      SOUND_TYPE soundType,
      String label,
      ObjectType id,
      UUID objectUUID) {
    super(x, y, sizeX, sizeY, label, id, objectUUID);
    this.soundType = soundType;
  }

  @Override
  public void initialise(Group root, Settings settings) {
    super.initialise(root, settings);
    slider
        .valueProperty()
        .addListener(
            new ChangeListener<Number>() {
              public void changed(
                  ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                onValueChange();
              }
            });
  }

  @Override
  public void initialiseAnimation() {
    super.initialiseAnimation();
    update();
  }

  public void onValueChange() {
    switch (this.soundType) {
      case MUSIC:
        Client.settings.setMusicVolume(slider.getValue() / 100f);
        Client.levelHandler.getMusicAudioHandler().updateMusicVolume();
        break;
      case SFX:
        Client.settings.setSoundEffectVolume(slider.getValue() / 100f);
        break;
    }
  }

  @Override
  public void update(){
    if (this.soundType != null) {
      switch (this.soundType) {
        case MUSIC:
          slider.setValue(Client.settings.getMusicVolume() * 100f);
          break;
        case SFX:
          slider.setValue(Client.settings.getSoundEffectVolume() * 100f);
      }
    }
  }

  public enum SOUND_TYPE {
    MUSIC,
    SFX
  }
}
