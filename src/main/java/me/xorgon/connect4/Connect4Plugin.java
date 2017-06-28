package me.xorgon.connect4;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import com.supaham.commons.bukkit.SimpleCommonPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Elijah on 14/08/2015.
 */
public class Connect4Plugin extends SimpleCommonPlugin<Connect4Plugin> {

    private static Connect4Plugin instance;
    private C4Manager manager;
    private static final String COMMAND_PREFIX = "c4";
    private CommandsManager<CommandSender> commands;

    public Connect4Plugin() {
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        manager = new C4Manager();
        registerEvents(new C4Listener());
        setupCommands();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        manager.save();
    }

    public static Connect4Plugin getInstance() {
        return instance;
    }

    public C4Manager getManager() {
        return manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    public void setupCommands() {
        this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender commandSender, String s) {
                return commandSender.hasPermission(s);
            }
        };

        CommandsManagerRegistration reg = new CommandsManagerRegistration(this, commands);

        reg.register(C4Command.C4RootCommand.class);

    }

}
