package shared.gameObjects;

import org.junit.BeforeClass;
import org.junit.Test;
import shared.gameObjects.Utils.ObjectID;
import shared.gameObjects.weapons.Handgun;
import shared.gameObjects.weapons.Weapon;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HandgunTest {

  private static Weapon handgun;

  @BeforeClass
  public static void initHandgun() {
    handgun =
        new Handgun(
            10, 10, 100, 100, "HandGun", UUID.randomUUID());
  }

  @Test
  public void test1() {
    System.out.println(handgun.toString());
    assertTrue(true);
  }

  @Test
  public void test2() {
    assertEquals(10, handgun.getDamage(), 0.0001f);
  }
}
