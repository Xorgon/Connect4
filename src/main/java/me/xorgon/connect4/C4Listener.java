package me.xorgon.connect4;

import me.xorgon.connect4.util.PhysicalBoard;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
                        if (clickedBlock.equals(block)) {
                            if (board.getRedPlayer() == null) {
                                board.setRedPlayer(event.getPlayer());
                            } else if (board.getBluePlayer() == null) {
                                board.setBluePlayer(event.getPlayer());
                                board.setStarted(true);
                            } else if (!board.isFinished() && board.isStarted()) {
                                board.placePiece(event.getPlayer(), board.getButtons().get(block));
                            }
                        }
                    }
                }
            }
        }
    }
}
