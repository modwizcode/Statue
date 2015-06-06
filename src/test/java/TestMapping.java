import com.modwiz.sponge.statue.utils.images.ColorMapping;
import org.junit.Test;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Starbuck on 6/6/2015.
 */
public class TestMapping {
    public static class MyDyeColor implements DyeColor {

        @Override
        public Color getColor() {
            return Color.MAGENTA;
        }

        @Override
        public String getId() {
            return "minecraft:" + getName();
        }

        @Override
        public String getName() {
            return "magenta";
        }

        @Override
        public String toString() {
            return String.format("%s[color=%s, id=%s, name=%s]", getClass().getName(), getColor().toString(), getId().toString(), getName().toString());
        }
    }

    @Test
    public void testEnumMagic() {
        System.out.println(DyeColors.WHITE);
        for (Field f : DyeColors.class.getDeclaredFields()) {
            if (f.getName().equals("WHITE")) {
                f.setAccessible(true);

                Field modifiersField = null;
                try {
                    modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(f, modifiersField.getModifiers() ^ ~Modifier.FINAL);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                try {
                    f.set(null, new MyDyeColor());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(DyeColors.WHITE);
    }
}
