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
package com.modwiz.sponge.statue.commands;

import com.flowpowered.math.vector.Vector3d;
import com.modwiz.sponge.statue.utils.images.ColorMapping;
import com.modwiz.sponge.statue.utils.skins.MinecraftSkin;
import com.modwiz.sponge.statue.utils.skins.SkinResolverService;
import com.sk89q.squirrelid.Profile;
import com.sk89q.squirrelid.resolver.CacheForwardingService;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Axis;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import java.awt.Color;

/**
 * Created by Starbuck on 6/5/2015.
 */
public final class TestResolveCommand implements CommandExecutor {
    private final Game game;
    private final CacheForwardingService profileResolver;

    // Make sure to construct after profile service is registered
    public TestResolveCommand(final Game game) {
        this.game = game;
        this.profileResolver = game.getServiceManager().provideUnchecked(CacheForwardingService.class);
    }

    @Override
    public final CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            if (args.hasAny("username")) {
                String username = args.<String>getOne("username").get();
                Profile profile = profileResolver.findByName(username);
                if (profile == null) {
                    src.sendMessage(Texts.of(TextColors.RED,"Sorry failed to find a profile for that username."));
                    return CommandResult.empty();
                }
                src.sendMessage(Texts.of(TextColors.GREEN, "Found a profile for the provided username."));
                ClickAction.SuggestCommand clickAction = new ClickAction.SuggestCommand(profile.getUniqueId().toString());
                Text clickableUUID = Texts.builder()
                        .append(Texts.of(TextColors.GREEN, profile.getUniqueId().toString()))
                        .onClick(clickAction).build();
                src.sendMessage(Texts.of(TextColors.BLUE, profile.getName(), TextColors.GRAY, ": ", clickableUUID));

                MinecraftSkin skin = game.getServiceManager().provideUnchecked(SkinResolverService.class).getSkin(profile.getUniqueId());
                if (skin == null) {
                    src.sendMessage(Texts.of(TextColors.RED, "Failed to resolve skin for the profile."));
                } else {
                    src.sendMessage(Texts.of(TextColors.GREEN, "Skin type: ", skin.type));
                    ColorMapping mapping = ColorMapping.matchColor(new Color(skin.texture.getRGB(12, 12)));
                    String message = String.format("The matching block was %s and the color was %s", mapping.equivalentBlock.toString(), mapping.dyeColor.orNull() == null ? "null" : mapping.dyeColor.toString());

                    if (src instanceof Player) {
                        Player player = (Player) src;
                        Vector3d rot = player.getRotation();
                        int value = floor_double((double)(rot.getX() * 4.0F / 360.0F) + 0.5D) & 3;
                        System.out.println(value);
                        player.sendMessage(Texts.of((rot.getX() * 4.0F / 360.0F) + 0.5D));
                        player.sendMessage(Texts.of(rot.getX() * 4.0F / 360.0F));
                        player.sendMessage(Texts.of(value));
                        int pos = ((value < 0 ? -value : value) % 4);
                        Direction buildDirection = null;
                        switch (pos) {
                            case 0:
                                // South
                                System.out.println("SOUTH");
                                player.sendMessage(Texts.of("SOUTH"));
                                buildDirection = Direction.SOUTH;
                                break;
                            case 1:
                                // WEST
                                System.out.println("WEST");
                                player.sendMessage(Texts.of("WEST"));
                                buildDirection = Direction.WEST;
                                break;
                            case 2:
                                // NORTH
                                System.out.println("NORTH");
                                player.sendMessage(Texts.of("NORTH"));
                                buildDirection = Direction.NORTH;
                                break;
                            case 3:
                                // EAST
                                System.out.println("EAST");
                                player.sendMessage(Texts.of("EAST"));
                                buildDirection = Direction.EAST;
                                break;
                        }

                    }
                }
                return CommandResult.success();
            }
            return CommandResult.empty();
        } catch (Throwable th) {
            th.printStackTrace();
            return CommandResult.empty();
        }
    }

    public static int floor_double(double p_76128_0_)
    {
        int i = (int)p_76128_0_;
        return p_76128_0_ < (double)i ? i - 1 : i;
    }
}
