package me.xorgon.connect4;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.supaham.commons.bukkit.area.CuboidRegion;
import me.xorgon.connect4.util.BlockFaceUtil;
import me.xorgon.connect4.util.PhysicalBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Connect4 commands class.
 */
public class C4Command {

    public static class C4RootCommand {
        @Command(aliases = {"c4", "connect4"}, desc = "The root Connect4 command.")
        @NestedCommand(value = {C4Command.class, C4SetCommand.C4SetRootCommand.class})
        @CommandPermissions("c4.admin")
        public static void connect4(CommandContext args, CommandSender sender) {
        }
    }

    @Command(aliases = {"list"}, desc = "List available boards.")
    public static void listBoards(CommandContext args, CommandSender sender) {
        Connect4Plugin.getInstance().getManager().getBoards().keySet()
                .forEach((s) -> sender.sendMessage(ChatColor.GREEN + s));
    }

    @Command(aliases = {"reload"}, desc = "Reloads Connect4 boards and configuration.")
    public static void reload(CommandContext args, CommandSender sender) {
        C4Manager manager = Connect4Plugin.getInstance().getManager();
        for (PhysicalBoard board : manager.getBoards().values()) {
            board.resetBoard();
            board.resetPlayers();
        }
        manager.save();
        manager.getBoards().clear();
        manager.load();
        sender.sendMessage(ChatColor.GREEN + "Connect4 has been reloaded.");
    }

    @Command(aliases = {"reset"}, desc = "Resets the selected board", usage = "<board name>", min = 1, max = 1)
    public static void reset(CommandContext args, CommandSender sender) {
        C4Manager manager = Connect4Plugin.getInstance().getManager();
        String id = args.getString(0);
        if (manager.getBoards().containsKey(id)) {
            PhysicalBoard board = manager.getBoard(id);
            if (board.getRedPlayer() != null) {
                board.getRedPlayer().sendMessage(ChatColor.RED + "Your board has been reset.");
            }
            if (board.getBluePlayer() != null) {
                board.getBluePlayer().sendMessage(ChatColor.RED + "Your board has been reset.");
            }
            board.resetBoard();
            board.resetPlayers();
            sender.sendMessage(ChatColor.GREEN + id + " has been reset");
        } else {
            sender.sendMessage(ChatColor.RED + "That board does not exist.");
        }
    }

    @Command(aliases = {"add", "addboard"}, desc = "Adds an unconfigured board.", usage = "<board name>", min = 1, max = 1)
    public static void add(CommandContext args, CommandSender sender) {
        C4Manager manager = Connect4Plugin.getInstance().getManager();
        String id = args.getString(0);
        if (manager.getConfig().getBoard(id) != null) {
            sender.sendMessage(ChatColor.RED + "That board already exists.");
        } else {
            C4Properties.Board board = new C4Properties.Board();
            board.setId(id);
            manager.getConfig().addBoard(board);
            sender.sendMessage(ChatColor.YELLOW + "Board created.");
        }
    }

    @Command(aliases = {"remove"}, desc = "Removes the specified board.", usage = "<board name>", min = 1, max = 1)
    public static void remove(CommandContext args, CommandSender sender){
        C4Manager manager = Connect4Plugin.getInstance().getManager();
        String id = args.getString(0);
        C4Properties.Board board = manager.getConfig().getBoard(id);
        if (board != null) {
            manager.getBoards().remove(id);
            for (C4Properties.Board b : manager.getConfig().getBoards()) {
                if (b.getId().equals(id)) {
                    manager.getConfig().getBoards().remove(b);
                    break;
                }
            }
            sender.sendMessage(ChatColor.YELLOW + "Board removed.");
        } else {
            sender.sendMessage(ChatColor.RED + "That board does not exist.");
        }
    }

    @Command(aliases = {"load", "loadboard"}, desc = "Attempts to load specified board.", usage = "<board name>", min = 1, max = 1)
    public static void load(CommandContext args, CommandSender sender) {
        C4Manager manager = Connect4Plugin.getInstance().getManager();
        String id = args.getString(0);
        C4Properties.Board board = manager.getConfig().getBoard(id);
        if (board != null) {
            if (manager.loadPhysicalBoard(id)) {
                sender.sendMessage(ChatColor.YELLOW + "Board loaded.");
            } else {
                sender.sendMessage(ChatColor.RED + "Board failed to load. Missing:");
                if (board.getRegion() == null) {
                    sender.sendMessage(ChatColor.RED + "region - /c4 set region");
                }
                if (board.getFace() == null) {
                    sender.sendMessage(ChatColor.RED + "face - /c4 set face");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "That board does not exist.");
        }
    }

    public static class C4SetCommand {
        public static class C4SetRootCommand {
            @Command(aliases = {"set"}, desc = "Define a setting for a board.")
            @NestedCommand(value = {C4SetCommand.class})
            public static void set(CommandContext args, CommandSender sender) {
            }
        }

        @Command(aliases = {"region", "reg"},
                desc = "Sets the region of the specified board as the current WorldEdit selection.",
                usage = "<board name>",
                min = 1,
                max = 1)
        public static void region(CommandContext args, CommandSender sender) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use that command.");
                return;
            }
            Player player = (Player) sender;
            C4Manager manager = Connect4Plugin.getInstance().getManager();
            String id = args.getString(0);
            C4Properties.Board board = manager.getConfig().getBoard(id);
            if (board != null) {
                WorldEditPlugin worldedit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
                Selection selection = worldedit.getSelection(player);
                Location max = selection.getMaximumPoint();
                Location min = selection.getMinimumPoint();

                // Ensure y difference is 5 and either x or z difference is 6 but not both.
                if (max.getBlockY() - min.getBlockY() + 1 != 5
                        || (max.getBlockX() - min.getBlockX() + 1 == 6) == (max.getBlockZ() - min.getBlockZ() + 1 == 6)) {
                    player.sendMessage(ChatColor.RED + "That selection is not valid. Selection must by 5 high and 6 wide.");
                    return;
                }

                // Ensure the region is perpendicular to the face.
                if (board.getFace() != null) {
                    Vector maxVect = max.toVector();
                    Vector minVect = min.toVector();
                    Vector perp = maxVect.subtract(minVect).crossProduct(new Vector(0, 1, 0));
                    if (perp.normalize().dot(BlockFaceUtil.blockFaceToVector(board.getFace())) == 0) {
                        player.sendMessage(ChatColor.RED + "Region must be perpendicular to the face.");
                        return;
                    }
                }

                CuboidRegion region = new CuboidRegion(max, min);
                board.setRegion(region);
                board.setWorld(selection.getWorld().getName());
                player.sendMessage(ChatColor.YELLOW + "Region set.");

                if (manager.loadPhysicalBoard(id)) {
                    sender.sendMessage(ChatColor.YELLOW + "Board complete and loaded.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "That board does not exist.");
            }
        }

        @Command(aliases = {"face", "facing"},
                desc = "Sets the specified board's direction. Directly face the side of the board with the buttons.",
                usage = "<board name>",
                min = 1,
                max = 1)
        public static void face(CommandContext args, CommandSender sender) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use that command.");
                return;
            }
            Player player = (Player) sender;
            C4Manager manager = Connect4Plugin.getInstance().getManager();
            String id = args.getString(0);
            C4Properties.Board board = manager.getConfig().getBoard(id);
            if (board != null) {
                Vector dir = player.getLocation().getDirection();
                BlockFace face = BlockFaceUtil.blockFaceFromVector(dir);

                // Ensure the face is perpendicular to the region.
                if (board.getRegion() != null) {
                    Vector max = board.getRegion().getMaximumPoint().clone();
                    Vector min = board.getRegion().getMinimumPoint().clone();
                    Vector perp = max.subtract(min).crossProduct(new Vector(0, 1, 0));
                    if (perp.normalize().dot(BlockFaceUtil.blockFaceToVector(face)) == 0) {
                        player.sendMessage(ChatColor.RED + "Face must be perpendicular to the region.");
                        return;
                    }
                }

                board.setFace(face);
                player.sendMessage(ChatColor.YELLOW + "Face set.");

                if (manager.loadPhysicalBoard(id)) {
                    sender.sendMessage(ChatColor.YELLOW + "Board complete and loaded.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "That board does not exist.");
            }
        }
    }

}