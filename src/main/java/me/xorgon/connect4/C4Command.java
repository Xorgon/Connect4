package me.xorgon.connect4;

import me.xorgon.connect4.util.PhysicalBoard;
import me.xorgon.connect4.util.Selection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Connect4 commands class.
 */
public class C4Command implements CommandExecutor {
    Connect4Plugin plugin = Connect4Plugin.getInstance();
    C4Manager manager = plugin.getManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Connect4(player, args);
        } else {
            C4ConsoleVersion game = new C4ConsoleVersion();
        }
        return true;
    }

    private boolean Connect4(Player player, String[] args) {
        if (args.length > 0) {
            /*if (args[0].equalsIgnoreCase("point1")) {
                Selection s;
                if (manager.getSelections().containsKey(player)) {
                    s = manager.getSelection(player);
                } else {
                    s = new Selection(player);
                    manager.addSelection(player, s);
                }
                Block targetBlock = player.getTargetBlock((Set<Material>) null, 16);
                s.setPoint1(targetBlock.getLocation().toVector());
                player.sendMessage("Point 1 set.");
            }
            if (args[0].equalsIgnoreCase("point2")) {
                Selection s;
                if (manager.getSelections().containsKey(player)) {
                    s = manager.getSelection(player);
                } else {
                    s = new Selection(player);
                    manager.addSelection(player, s);
                }
                Block targetBlock = player.getTargetBlock((Set<Material>) null, 16);
                s.setPoint2(targetBlock.getLocation().toVector());
                player.sendMessage("Point 2 set.");
            }*/
            if (args[0].equalsIgnoreCase("list")){
                manager.getBoards().keySet().forEach((s) -> player.sendMessage(ChatColor.GREEN + s));
            }
            if (args[0].equalsIgnoreCase("reload")){
                for (PhysicalBoard board : manager.getBoards().values()) {
                    board.resetBoard();
                    board.resetPlayers();
                }
                manager.getBoards().clear();
                manager.load();
                player.sendMessage(ChatColor.GREEN + "Connect4 has been reloaded.");
            }
            if (args.length > 1) {
                /*if (args[0].equalsIgnoreCase("setboard")) {
                    Selection sel = manager.getSelection(player);
                    PhysicalBoard board = manager.getBoard(args[1]);
                    board.loadBoard(player.getWorld());
                }
                if (args[0].equalsIgnoreCase("setjoinbutton")) {
                    Block block = player.getTargetBlock((Set<Material>) null, 16);
                    Material t = block.getType();
                    if (t == Material.STONE_BUTTON || t == Material.WOOD_BUTTON) {
                        PhysicalBoard board = manager.getBoard(args[1]);
                        if (board == null) {
                            player.sendMessage("That board does not exist.");
                        } else {
                            board.setJoinButton(block);
                            player.sendMessage("You have set the join button for " + args[1]);
                        }
                    }

                }
                if (args[0].equalsIgnoreCase("addboard")) {
                    //manager.addBoard(args[1]);
                    player.sendMessage("Board " + args[1] + " has been created.");
                }*/
                if (args[0].equalsIgnoreCase("reset")){
                    if (manager.getBoards().containsKey(args[1].toLowerCase())) {
                        PhysicalBoard board = manager.getBoard(args[1]);
                        board.resetBoard();
                        board.resetPlayers();
                        player.sendMessage(ChatColor.GREEN + args[1] + " has been reset");
                    } else {
                        player.sendMessage(ChatColor.RED + "That board does not exist.");
                    }
                }
            }
            return true;
        } else

        {
            player.sendMessage(ChatColor.RED + "Correct usage: /connect4 <command> <board tag>");
            player.sendMessage(ChatColor.RED + "list, reset, reload");
            return true;
        }
    }
}