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
package com.modwiz.sponge.statue;

import com.google.inject.Inject;
import com.modwiz.sponge.statue.commands.TestResolveCommand;
import com.modwiz.sponge.statue.utils.skins.SkinResolverService;
import com.sk89q.squirrelid.cache.HashMapCache;
import com.sk89q.squirrelid.resolver.CacheForwardingService;
import com.sk89q.squirrelid.resolver.HttpRepositoryService;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;

import java.io.File;

/**
 * Created by Starbuck on 6/5/2015.
 */
@Plugin(id = "statue", name = "Statue")
public class StatuePlugin {
    @Inject
    private Logger logger;
    private CacheForwardingService profileResolver;
    private SkinResolverService skinResolver;

    @Subscribe
    public void onPreInit(PreInitializationEvent event) {
        Game game = event.getGame();

        logger.info("Registering profile resolver service.");
        this.profileResolver = new CacheForwardingService(HttpRepositoryService.forMinecraft(), new HashMapCache());
        try {
            game.getServiceManager().setProvider(this, CacheForwardingService.class, profileResolver);
            logger.info("Registered profile resolver service successfully.");
        } catch (ProviderExistsException e) {
            logger.error("An error occurred while setting the profile resolver service.", e);
        }

        logger.info("Registering skin resolver service.");
        this.skinResolver = new SkinResolverService(new File("skinCache"));
        try {
            game.getServiceManager().setProvider(this, SkinResolverService.class, skinResolver);
            logger.info("Registered skin resolver service successfully.");
        } catch (ProviderExistsException e) {
            logger.error("An error occurred while setting the skin resolver service.", e);
        }

        CommandSpec testResolve = CommandSpec.builder()
                .arguments(GenericArguments.string(Texts.of("username")))
                .executor(new TestResolveCommand(game)).build();
        game.getCommandDispatcher().register(this, testResolve, "resolve");
        logger.info("Registered profile resolver command.");
    }
}
