package com.modwiz.sponge.statue.utils.skins;

import java.awt.image.BufferedImage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Starbuck on 6/5/2015.
 */
public final class HeadModel {
    private final MinecraftSkin parent;

    public HeadModel(MinecraftSkin skin) {
        checkNotNull(skin);
        this.parent = skin;
    }

    public BufferedImage getFace() {
        return parent.texture.getSubimage(8, 8, 8, 8);
    }


}
