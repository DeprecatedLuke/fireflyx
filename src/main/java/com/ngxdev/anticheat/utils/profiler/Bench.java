package com.ngxdev.anticheat.utils.profiler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Bench {

    public static long run(Runnable r, int count) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++)
            r.run();
        long end = System.currentTimeMillis();
        return end - start;
    }

    public static void console(Runnable r, int count) {
        long time = run(r,count);
        System.out.println("Executed "+count+" times, took "+time+"ms");
    }

    public static void broadcast(Runnable r, int count) {
        long time = run(r,count);
        Bukkit.broadcastMessage("Executed "+count+" times, took "+time+"ms");
    }

    public static void player(Player player, Runnable r, int count) {
        long time = run(r,count);
        player.sendMessage("Executed "+count+" times, took "+time+"ms");
    }

}
