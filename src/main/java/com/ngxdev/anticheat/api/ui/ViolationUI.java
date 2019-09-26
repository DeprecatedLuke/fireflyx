package com.ngxdev.anticheat.api.ui;

import com.ngxdev.anticheat.api.check.Check;
import api.Command;
import com.ngxdev.anticheat.api.cmd.FireflyCommand;
import com.ngxdev.anticheat.api.ui.book.BookBuilder;
import com.ngxdev.anticheat.api.ui.book.BookHandler;
import com.ngxdev.anticheat.api.ui.book.NMSChatBuilder;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ViolationUI {

    @Command(name = "fviolations",alias = "fv",opOnly = true)
    public static class DebugCommand2 extends FireflyCommand {
        @Override
        protected void playerSender(Player player, String command, String[] arguments) {
            ViolationUI.display(player, Collections.singleton(player));
        }
    }

    public static void display(Player target, Collection<Player> display) {
        BookBuilder builder = get(target);
        ItemStack stack = builder.get();
        for (Player p : display)
            BookHandler.sendBook(p,stack);
    }

    private static BookBuilder get(Player target) {
        PlayerData data = PlayerData.get(target);
        BookBuilder bookBuilder = new BookBuilder();
        String title = target.getName()+"'s Violations\n";
        NMSChatBuilder builder = new NMSChatBuilder(title);
        int i = 0;
        List<ViolationEntry> l = new ArrayList<>();
        data.validChecks.forEach(e->{
            ViolationEntry v = new ViolationEntry(e,e.getViolationData().getTotalViolationCount());
            l.add(v);
        });
        l.sort(new ViolationComparator()); // Comparator.comparingInt(o -> -o.violations));
        for (ViolationEntry e : l) {
            if (i++ > 9) {
                i = 0;
                bookBuilder.addPage(builder);
                builder = new NMSChatBuilder(title);
            }
            builder.next("\n").next(e.check.getName()).color(ChatColor.DARK_GRAY)
                    .hovor("id: "+e.check.getId()+"\nState: "+e.check.getState().name().toLowerCase())
                   /* .underline(e.check.isExperimental())*/.next(": ")
                    .next(Integer.toString(e.violations)).color(e.violations!=0?ChatColor.RED:ChatColor.DARK_AQUA).bold(true);
        }
        bookBuilder.addPage(builder);
        return bookBuilder;
    }

    @AllArgsConstructor
    private static class ViolationEntry {
        private Check check;
        private int violations;
    }

    private static class ViolationComparator implements Comparator<ViolationEntry> {
        @Override
        public int compare(ViolationEntry o1, ViolationEntry o2) {
            int result = o2.violations - o1.violations;
            if (result == 0)
                result = o1.check.getName().toLowerCase().compareTo(o2.check.getName().toLowerCase());
            return result;
        }
    }

}
