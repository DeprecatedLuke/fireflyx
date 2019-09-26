package com.ngxdev.anticheat.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

public class MojangAPI {
    private static Gson gson = new Gson();

    public static UUID getUUID(String name) {
        try {
            Scanner scanner = new Scanner(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream(), "UTF-8").useDelimiter("\\A");
            String json = scanner.next();
            String uuid = gson.fromJson(json, JsonObject.class).get("id").getAsString();
            return Utils.toUUID(uuid);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getName(UUID uuid) {
        return getName(uuid.toString().replace("-", ""));
    }

    public static String getName(String uuid) {
        try {
            Scanner scanner = new Scanner(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names").openStream(), "UTF-8").useDelimiter("\\A");
            String json = scanner.next();
            JsonArray array = gson.fromJson(json, JsonArray.class);
            return array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
        } catch (Exception e) {
            return null;
        }
    }
}
