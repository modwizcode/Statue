package com.modwiz.sponge.statue.utils.skins;

import java.awt.image.BufferedImage;
import java.util.UUID;

/**
 * Created by Starbuck on 6/5/2015.
 */
public final class MinecraftSkin {
    public enum Type {
        STEVE,
        ALEX
    }

    public final Type type;
    public final UUID uuid;
    public final BufferedImage texture;
    public final long timestamp;

    MinecraftSkin(Type type, UUID uuid, BufferedImage texture, long timestamp) {
        this.type = type;
        this.uuid = uuid;
        this.texture = texture;
        this.timestamp = timestamp;
    }
}
