package com.ngxdev.anticheat.storage.mysql;

import com.ngxdev.anticheat.Firefly;
import com.ngxdev.anticheat.storage.sqlite.Query;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;

import static com.ngxdev.anticheat.utils.Log.println;

public class MySQL {
    private static Connection conn;

    public static void init() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver");
                FileConfiguration config = Firefly.config;
                conn = DriverManager.getConnection("jdbc:mysql://" + config.getString("host") + ":3306/?useSSL=false",
                        config.getString("user"),
                        config.getString("password"));
                conn.setAutoCommit(true);
                Query.use(conn);
                Query.prepare("CREATE DATABASE IF NOT EXISTS `" + config.getString("storage.database") + "`").execute();
                Query.prepare("USE `" + config.getString("storage.database") + "`").execute();
                println("Connection to MySQL has been established.");
            }
        } catch (Exception e) {
            println("Failed to load mysql: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void use() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
