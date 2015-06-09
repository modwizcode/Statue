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
package com.modwiz.sponge.statue.utils.images;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Created by Starbuck on 6/5/2015.
 */
public enum ColorMapping {
    WHITE_WOOL(new Color(219, 219, 219), BlockTypes.WOOL, DyeColors.WHITE),
    ORANGE_WOOL(new Color(216, 122, 60), BlockTypes.WOOL, DyeColors.ORANGE),
    MAGENTA_WOOL(new Color(177, 78, 186), BlockTypes.WOOL, DyeColors.MAGENTA),
    LIGHT_BLUE_WOOL(new Color(104, 135, 198), BlockTypes.WOOL, DyeColors.LIGHT_BLUE),
    YELLOW_WOOL(new Color(174, 163, 36), BlockTypes.WOOL, DyeColors.YELLOW),
    LIME_WOOL(new Color(63, 171, 53), BlockTypes.WOOL, DyeColors.LIME),
    PINK_WOOL(new Color(205, 129, 150), BlockTypes.WOOL, DyeColors.PINK),
    GRAY_WOOL(new Color(61, 61, 61), BlockTypes.WOOL, DyeColors.GRAY),
    SILVER_WOOL(new Color(152, 158, 158), BlockTypes.WOOL, DyeColors.SILVER),
    CYAN_WOOL(new Color(43, 107, 134), BlockTypes.WOOL, DyeColors.CYAN),
    PURPLE_WOOL(new Color(124, 59, 179), BlockTypes.WOOL, DyeColors.PURPLE),
    BLUE_WOOL(new Color(43, 54, 139), BlockTypes.WOOL, DyeColors.BLUE),
    BROWN_WOOL(new Color(76, 47, 28), BlockTypes.WOOL, DyeColors.BROWN),
    GREEN_WOOL(new Color(50, 68, 24), BlockTypes.WOOL, DyeColors.GREEN),
    RED_WOOL(new Color(148, 49, 46), BlockTypes.WOOL, DyeColors.RED),
    BLACK_WOOL(new Color(23, 19, 19), BlockTypes.WOOL, DyeColors.BLACK),

    // CLAYS
    HARDENED_CLAY(new Color(147, 90, 64), BlockTypes.HARDENED_CLAY),
    WHITE_CLAY(new Color(207, 175, 158), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.WHITE),
    ORANGE_CLAY(new Color(159, 81, 35), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.ORANGE),
    MAGENTA_CLAY(new Color(147, 85, 106), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.MAGENTA),
    LIGHT_BLUE_CLAY(new Color(110, 105, 135), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.LIGHT_BLUE),
    YELLOW_CLAY(new Color(183, 130, 32), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.YELLOW),
    LIME_CLAY(new Color(100, 114, 50), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.LIME),
    PINK_CLAY(new Color(159, 75, 76), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.PINK),
    GRAY_CLAY(new Color(55, 39, 32), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.GRAY),
    SILVER_CLAY(new Color(132, 104, 94), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.SILVER),
    CYAN_CLAY(new Color(84, 88, 88), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.CYAN),
    PURPLE_CLAY(new Color(115, 67, 83), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.PURPLE),
    BLUE_CLAY(new Color(71, 57, 88), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.BLUE),
    BROWN_CLAY(new Color(74, 48, 32), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.BROWN),
    GREEN_CLAY(new Color(73, 80, 39), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.GREEN),
    RED_CLAY(new Color(140, 58, 44), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.RED),
    BLACK_CLAY(new Color(34, 19, 13), BlockTypes.STAINED_HARDENED_CLAY, DyeColors.BLACK),

    // OTHER
    RED_SANDSTONE(new Color(167, 85, 30), BlockTypes.RED_SANDSTONE),
    SANDSTONE(new Color(215, 207, 156), BlockTypes.SANDSTONE),
    QUARTZ(new Color(234, 231, 224), BlockTypes.QUARTZ_BLOCK)
    ;

    public final Color matchColor;
    public final BlockType equivalentBlock;
    public final Optional<DyeColor> dyeColor;

    private static final Map<Color, ColorMapping> mappings = Maps.newHashMap();


    ColorMapping(Color matchColor, BlockType equivalentBlock) {
        this(matchColor, equivalentBlock, null);
    }

    ColorMapping(Color matchColor, BlockType equivalentBlock, DyeColor dyeColor) {
        this.matchColor = matchColor;
        this.equivalentBlock = equivalentBlock;
        this.dyeColor = Optional.fromNullable(dyeColor);
    }

    public static final ColorMapping matchColor(Color toMatch) {
        // Weight green higher because human vision likes green
        // 2x green difference will make similar green more valuable
        checkNotNull(toMatch);
        if (mappings.containsKey(toMatch)) {
            return mappings.get(toMatch);
        }

        double lowestAverage = Double.MAX_VALUE;
        ColorMapping lowestMapping = null;

        int toMatchRed = toMatch.getRed();
        int toMatchGreen = toMatch.getGreen();
        int toMatchBlue = toMatch.getBlue();

        for (ColorMapping mapping : ColorMapping.values()) {
            int mapRed = mapping.matchColor.getRed();
            int mapGreen = mapping.matchColor.getGreen();
            int mapBlue = mapping.matchColor.getBlue();

            int diffRed = Math.abs(toMatchRed - mapRed);

            // Weighting
            int diffGreen = 2 * Math.abs(toMatchGreen - mapGreen);

            int diffBlue = Math.abs(toMatchBlue - mapBlue);

            double averageDiff = (diffRed + diffGreen + diffBlue) / 3.0;

            if (averageDiff < lowestAverage) {
                lowestAverage = averageDiff;
                lowestMapping = mapping;
            }
        }

        return lowestMapping;
    }

    static {
        initColors();
    }

    private static final void initColors() {
        for (ColorMapping mapping : ColorMapping.values()) {
            mappings.put(mapping.matchColor, mapping);
        }
    }
}
