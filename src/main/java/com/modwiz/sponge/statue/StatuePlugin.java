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
            logger.error("An error occured while setting the profile resolver service.", e);
        }

        logger.info("Registering skin resolver service.");
        this.skinResolver = new SkinResolverService(new File("skinCache"));
        try {
            game.getServiceManager().setProvider(this, SkinResolverService.class, skinResolver);
            logger.info("Registered skin resolver service successfully.");
        } catch (ProviderExistsException e) {
            logger.error("An error occured while setting the skin resolver service.", e);
        }

        CommandSpec testResolve = CommandSpec.builder()
                .arguments(GenericArguments.string(Texts.of("username")))
                .executor(new TestResolveCommand(game)).build();
        game.getCommandDispatcher().register(this, testResolve, "resolve");
        logger.info("Registered profile resolver command.");
    }
}
