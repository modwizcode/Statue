/*
 * This file is part of Statues, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015, Starbuck Johnson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
