package com.ngxdev.anticheat.api.ui;

import com.ngxdev.anticheat.api.check.Check;
import api.Command;
import com.ngxdev.anticheat.api.cmd.FireflyCommand;
import com.ngxdev.anticheat.api.ui.book.BookBuilder;
import com.ngxdev.anticheat.api.ui.book.BookHandler;
import com.ngxdev.anticheat.api.ui.book.NMSChatBuilder;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

public class DebugUI {

    @Command(name = "fdebug",alias = "fd",opOnly = true)
    public static class DebugCommand extends FireflyCommand {
        @Override
        protected void playerSender(Player player, String command, String[] arguments) {
            if (arguments.length == 0)
                display(player, Collections.singleton(player));
            else {
                String arg = Utils.concat(arguments);
                PlayerData data = PlayerData.get(player);
                Check found = null;
                for (Check check : data.allChecks) {
                    if (check.getId().equals(arg)) {
                        found = check;
                        break;
                    }
                }
                if (found == null) player.sendMessage("§cInvalid check!");
                else {
                    found.setDebug(!found.isDebug());
                    display(player,data.singleton);
                }
            }
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
        String title = target.getName()+"'s Debugger\n";
        NMSChatBuilder builder = new NMSChatBuilder(title);
        int i = 0;
        for (Check check : data.validChecks) {
            if (check.getId() == null) continue;
            if (i++ > 9) {
                i = 0;
                bookBuilder.addPage(builder);
                builder = new NMSChatBuilder(title);
            }
            String cmd = "/"+DebugCommand.class.getAnnotation(Command.class).name()+" "+check.getId();

            builder.next("\n").next(check.getName()).color(ChatColor.DARK_GRAY)
                    .hovor("id: "+check.getId()+"\nExperimental: "+check.getState().name().toLowerCase()).execute(cmd)
                    .next(": ").color(ChatColor.GRAY)
                    .next(check.isDebug()?"Yes":"No").color(check.isDebug()?ChatColor.DARK_AQUA:ChatColor.DARK_GRAY).bold(true).execute(cmd);
        }
        bookBuilder.addPage(builder);
        return bookBuilder;
    }

}
