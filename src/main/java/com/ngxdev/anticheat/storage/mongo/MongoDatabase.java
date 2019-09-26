package com.ngxdev.anticheat.storage.mongo;

import com.ngxdev.anticheat.Firefly;
import com.ngxdev.anticheat.utils.Utils;

import java.io.File;

import static com.ngxdev.anticheat.utils.Log.println;

public class MongoDatabase {
    public static void init() {
        try {
            File mongo_lib = new File(Firefly.getInstance().getDataFolder(), "mongo.jar");
            if (!mongo_lib.exists()) {
                println("Downloading mongo...");
                Utils.download(mongo_lib, "http://central.maven.org/maven2/org/mongodb/mongo-java-driver/3.5.0/mongo-java-driver-3.5.0.jar");
            }
            Utils.injectURL(mongo_lib.toURI().toURL());
        } catch (Exception e) {
            println("Failed to load mongo: " + e.getMessage());
        }
    }
}
