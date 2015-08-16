package me.xorgon.connect4;

import me.xorgon.connect4.util.PhysicalBoard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Elijah on 14/08/2015.
 */
public class C4Listener implements Listener {

    Connect4Plugin plugin = Connect4Plugin.getInstance();
    C4Manager manager = plugin.getManager();

    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        for (PhysicalBoard board : manager.getBoards().values()) {
            for (FallingBlock fallingBlock : board.getFallingBlocks()) {
                if (event.getEntity() == fallingBlock) {
                    board.handleFallingBlock(fallingBlock);
                    return;
                }
            }
        }
    }

    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player p = event.getPlayer();
        for (PhysicalBoard board : manager.getBoards().values()) {
            Player redP = board.getRedPlayer();
            Player blueP = board.getBluePlayer();
            if ((redP != null && blueP != null) && (redP.equals(p) || blueP.equals(p))){
                double dSquared = p.getLocation().toVector().distanceSquared(board.getCenter());
                if (dSquared > 24 * 24){
                    if (p.equals(redP)){
                        redP.sendTitle(ChatColor.RED + "You forfeit the game.", "");
                        blueP.sendTitle(ChatColor.RED + p.getName() + ChatColor.YELLOW + "forfeit.", "");
                    } else {
                        blueP.sendTitle(ChatColor.RED + "You forfeit the game.", "");
                        redP.sendTitle(ChatColor.BLUE + p.getName() + ChatColor.YELLOW + "forfeit.", "");
                    }
                    board.resetPlayers();
                    board.resetBoard();
                } else if (dSquared > 16 * 16){
                    p.sendTitle("", ChatColor.RED + "If you go further away you will forfeit.");
                }
            }
        }
    }
    
    @EventHandler
    public void onButtonPress(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            Material type = clickedBlock.getType();
            if (type == Material.STONE_BUTTON || type == Material.WOOD_BUTTON) {
                for (PhysicalBoard board : manager.getBoards().values()) {
                    for (Block block : board.getButtons().keySet()) {
                        if (clickedBlock.equals(block) && board.isCanInteract()) {
                            Player player = event.getPlayer();
                            if (board.getRedPlayer() == null) {
                                for (PhysicalBoard pBoard : manager.getBoards().values()) {
                                    if (player.equals(pBoard.getRedPlayer()) || player.equals(pBoard.getBluePlayer())){
                                        if (pBoard.isStarted()) {
                                            player.sendMessage(ChatColor.RED + "You are already in a game.");
                                            return;
                                        }
                                    }
                                }
                                board.setRedPlayer(player);
                                player.sendTitle(ChatColor.YELLOW + "You have joined as " + ChatColor.RED + "Red", "");
                            } else if (board.getBluePlayer() == null && player != board.getRedPlayer()) {
                                for (PhysicalBoard pBoard : manager.getBoards().values()) {
                                    if (player.equals(pBoard.getRedPlayer()) || player.equals(pBoard.getBluePlayer())){
                                        if (pBoard.isStarted()) {
                                            player.sendMessage(ChatColor.RED + "You are already in a game.");
                                            return;
                                        }
                                    }
                                }
                                board.setBluePlayer(player);
                                player.sendTitle(ChatColor.YELLOW + "Starting game", ChatColor.RED + "Red" + ChatColor.YELLOW + " turn, you are " + ChatColor.BLUE + "Blue.");
                                board.getRedPlayer().sendTitle(ChatColor.YELLOW + "Starting game", ChatColor.RED + "Red" + ChatColor.YELLOW + " turn.");
                                board.setStarted(true);
                                board.getBoard().initialize();
                            } else if (!board.isFinished() && board.isStarted()) {
                                if (player.equals(board.getBluePlayer()) || player.equals(board.getRedPlayer())) {
                                    board.placePiece(player, board.getButtons().get(block));
                                } else {
                                    player.sendMessage(ChatColor.RED + "This game is already in progress.");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        for (PhysicalBoard board : manager.getBoards().values()) {
            if (board.getRedPlayer() != null && board.getRedPlayer().equals(player)) {
                board.getBluePlayer().sendTitle(ChatColor.RED + "Your opponent quit.", "");
                board.resetBoard();
                board.resetPlayers();
            } else if (board.getBluePlayer() != null && board.getBluePlayer().equals(player)){
                board.getRedPlayer().sendTitle(ChatColor.RED + "Your opponent quit.", "");
                board.resetBoard();
                board.resetPlayers();
            }
        }
    }
}
