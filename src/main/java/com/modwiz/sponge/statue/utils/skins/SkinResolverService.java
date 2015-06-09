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
package com.modwiz.sponge.statue.utils.skins;

import com.google.common.cache.CacheLoader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.google.common.base.Preconditions.*;

/**
 * Created by Starbuck on 6/5/2015.
 */
public final class SkinResolverService {
    private static final String REQUEST_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private HashMap<UUID, MinecraftSkin> skinCache;
    private SkinLoader skinLoader;
    private final File cacheDir;

    public SkinResolverService(final File cacheDir) {
        checkNotNull(cacheDir);

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        } else {
            File[] cachedSkins = cacheDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".json");
                }
            });
            JsonParser parser = new JsonParser();
            for (File file : cachedSkins) {
                try {
                    JsonObject element = parser.parse(new FileReader(file)).getAsJsonObject();
                    long now = new Date().getTime();
                    long timestamp = element.get("timestamp").getAsLong();
                    if (new Date(now - timestamp).getMinutes() <= 45) {
                        // Less than 45 minutes old
                        UUID uuid = UUID.fromString(element.get("uuid").getAsString());
                        BufferedImage texture = ImageIO.read(new File(String.format("%s.png", uuid.toString())));
                        MinecraftSkin.Type skinType = MinecraftSkin.Type.valueOf(element.get("type").getAsString());
                        MinecraftSkin skin = new MinecraftSkin(skinType, uuid, texture, timestamp);
                        skinCache.put(uuid, skin);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.cacheDir = cacheDir;

        skinCache = new HashMap<UUID, MinecraftSkin>(100);
        skinLoader = new SkinLoader();
    }

    public MinecraftSkin getSkin(UUID profileID) {
        return getSkin(profileID, false);
    }

    public MinecraftSkin getSkin(UUID profileID, boolean forceRefresh) {
        return getSkin0(profileID, forceRefresh);
    }

    private MinecraftSkin getSkin0(UUID profileID, boolean forceRefresh) {
        if (forceRefresh) {
            skinCache.remove(profileID);
        }

        if (!skinCache.containsKey(profileID)) {
            JSONObject fullProfile = skinLoader.getFullProfile(profileID);
            if (fullProfile == null) {
                skinCache.put(profileID, null);
                return null;
            }

            MinecraftSkin skin = skinLoader.extractSkin(profileID, fullProfile);

            if (skin != null) {
                try {
                    ImageIO.write(skin.texture, "png", new File(cacheDir, skin.uuid.toString() + ".png"));
                    JsonObject skinData = new JsonObject();

                    skinData.addProperty("type", skin.type.name());
                    skinData.addProperty("uuid", skin.uuid.toString());
                    skinData.addProperty("timestamp", skin.timestamp);

                    Files.write(Paths.get(cacheDir.getAbsolutePath(), skin.uuid.toString() + ".json"), skinData.toString().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            skinCache.put(profileID, skin);
            return skin;
        }

        return skinCache.get(profileID);
    }

    private final static class SkinLoader extends CacheLoader<UUID, MinecraftSkin> {
        @Override
        public MinecraftSkin load(UUID key) {
            JSONObject fullProfile = getFullProfile(key);

            if (fullProfile == null) {
                return null;
            }

            return extractSkin(key, fullProfile);
        }

        private final JSONObject getFullProfile(UUID uuid) {
            String fullURL = REQUEST_URL.concat(uuid.toString().replaceAll("-",""));
            try {
                URLConnection connection = new URL(fullURL).openConnection();
                System.out.println(((HttpURLConnection)connection).getResponseMessage());
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                System.out.println(builder.toString());
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(builder.toString());
                return jsonObject;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        private final MinecraftSkin extractSkin(UUID profileUUID, JSONObject fullProfile) {
            checkNotNull(fullProfile);
            if (!fullProfile.containsKey("properties")) {
                throw new IllegalArgumentException("Properties tag missing on full profile");
            }

            String propertiesBase64 = (String) ((JSONObject)((JSONArray) fullProfile.get("properties")).get(0)).get("value");
            String propertiesJSON = new String(Base64.getDecoder().decode(propertiesBase64));

            System.out.println(propertiesJSON);
            try {
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(propertiesJSON);
                JSONObject textures = (JSONObject) jsonObject.get("textures");

                if (!textures.containsKey("SKIN")) {
                    return null;
                }

                JSONObject skin = (JSONObject) textures.get("SKIN");

                MinecraftSkin.Type skinType = MinecraftSkin.Type.STEVE;

                if (skin.containsKey("metadata")) {
                    JSONObject metadata = (JSONObject) skin.get("metadata");
                    if (metadata.get("model").equals("slim")) {
                        skinType = MinecraftSkin.Type.ALEX;
                    } else {
                        skinType = MinecraftSkin.Type.STEVE;
                    }
                }


                String imageURL;


                imageURL = (String) skin.get("url");
                InputStream is = new URL(imageURL).openConnection().getInputStream();
                BufferedImage image = ImageIO.read(is);
                is.close();

                return new MinecraftSkin(skinType, profileUUID, image, new Date().getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static void main(String[] args) {
        SkinResolverService service = new SkinResolverService(new File("skinCache"));
        MinecraftSkin skin = service.getSkin(UUID.fromString("8e5ff5f0-1e6f-4137-9eda-3d0bcf55d470"));
        System.out.println(skin.type);
        System.out.println(skin.uuid.toString());
        System.out.println(skin.texture.toString());
    }
}
