package com.ngxdev.anticheat.api.cmd;

import api.Command;
import com.ngxdev.anticheat.utils.Reflection;
import com.ngxdev.anticheat.utils.DynamicInit;
import com.ngxdev.anticheat.utils.Reflective;
import org.atteo.classindex.IndexSubclasses;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import java.util.*;

@IndexSubclasses
public abstract class FireflyCommand extends org.bukkit.command.Command {

    public static String prefix = "firefly";

    public static void initCommands() {
        SimpleCommandMap commandMap = Reflective.get(Bukkit.getServer(),"commandMap");
        try {
            DynamicInit.getClasses("com.ngxdev", Command.class).forEach((clazz) -> {
                //System.out.println("Initializing command: " + clazz.getSimpleName());
                try {
                    FireflyCommand command = (FireflyCommand) clazz.newInstance();
                    commandMap.register(prefix, command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static List<FireflyCommand> commands = new ArrayList<>();
    private String permission;
    private boolean opOnly;

    protected FireflyCommand() {
        super("-");
        Class<?> caller = getClass();
        if (caller.equals(FireflyCommand.class))
            throw new RuntimeException("Tried to instantiate base command class");
        try {
            Command command = caller.getAnnotation(Command.class);
            override(command.name(), command.desc(), command.usage(), Arrays.asList(command.alias()));
            opOnly = command.opOnly();
            String perm = command.permission();
            if (!perm.equals("")) permission = perm;
            else permission = null;
            commands.add(this);
        } catch (Exception e) {
            throw new RuntimeException(caller.getSimpleName()+" used default constructor yet didn't provide an Command annotation");
        }
    }

    protected FireflyCommand(String name) {
        super(name);
    }

    protected FireflyCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    private void override(String name, String description, String usageMessage, List<String> aliases) {
        Reflection.set(this, "name", name);
        this.setLabel(name);
        this.description = description;
        this.usageMessage = usageMessage;
        this.setAliases(aliases);
    }

    @Override
    public final boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if ((!opOnly && permission==null)
                    ||(opOnly&&commandSender.isOp())
                    ||(permission!=null && commandSender.hasPermission(permission))) {
                playerSender((Player) commandSender,s,strings);
            } else commandSender.sendMessage("Insufficient Permission");
        } else {
            if (commandSender instanceof BlockCommandSender) {
                commandSender.sendMessage("Command blocks not supported");
            } else consoleSender(commandSender,s,strings);
        }
        return true;
    }

    protected abstract void playerSender(Player player, String command, String[] arguments);

    protected void consoleSender(CommandSender sender, String command, String[] arguments) {
        sender.sendMessage("Must be instance of player to execute this command!");
    }

    public static String getArgs(String[] args, int num) {
        String output = "";
        for (int i = num; i < args.length; i++) {
            if (output.equals("")) output = args[i];
            else output = output + " " + args[i];
        }
        return output;
    }
    public static void unregisterAll() {
    	commands.forEach(FireflyCommand::unregister);
    	commands.clear();
    }

	public void unregister() {
		SimpleCommandMap map = Reflection.get(Bukkit.getPluginManager(), "commandMap");
		Iterator<org.bukkit.command.Command> it = new ArrayList<>(map.getCommands()).iterator();
		while (it.hasNext()) {
			org.bukkit.command.Command c = it.next();
			if (c.getName().equalsIgnoreCase(getName())) {
				Map<String, org.bukkit.command.Command> cmds = Reflection.get(map, "knownCommands");
				cmds.remove(c.getName());
				cmds.remove(c.getLabel());
				c.getAliases().forEach(cmds::remove);
				c.unregister(map);
				Reflection.set(map, "knownCommands", cmds);
			}
		}
	}
}

