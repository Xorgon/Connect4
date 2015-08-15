package me.xorgon.connect4.util;

import com.google.common.base.Preconditions;
import com.supaham.commons.bukkit.players.Players;
import com.supaham.commons.bukkit.utils.BlockFaceUtils;
import com.supaham.commons.utils.WeakSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.xorgon.connect4.C4Properties;
import me.xorgon.connect4.Connect4Plugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Elijah on 14/08/2015.
 */
@Getter
@Setter
public class PhysicalBoard {

    private Map<Integer, Block> slots;
    private List<Block> spaces;
    private Map<Block, Integer> buttons;
    private Block joinButton;
    private Player redPlayer;
    private Player bluePlayer;
    private VirtualBoard board;
    private String tag;
    private BlockFace face;
    private C4Properties.Board config;

    private boolean started = false;
    private boolean redTurn = true;
    private boolean finished = false;

    private WeakSet<FallingBlock> fallingBlocks = new WeakSet<>();

    public PhysicalBoard(@NonNull World world, @NonNull C4Properties.Board serialized) {
        Preconditions.checkArgument(BlockFaceUtils.isHorizontal(serialized.getFace()), "Block face must be horizontal.");

        config = serialized;
        loadBoard(world);

    }

    public void resetBoard() {
        for (Block space : spaces) {
            space.setType(Material.AIR);
        }
        board.initialize();
        finished = false;
    }

    public void resetPlayers() {
        redPlayer = null;
        bluePlayer = null;
        started = false;
        redTurn = true;
    }

    public void placePiece(Player player, int column) {
        if ((player == redPlayer && redTurn) || (player == bluePlayer && !redTurn)) {
            MaterialData material;

            //Virtual Board
            VirtualBoard.SpaceType piece;
            if (player == redPlayer) {
                piece = VirtualBoard.SpaceType.RED;
                material = config.getRedBlock();
            } else {
                piece = VirtualBoard.SpaceType.BLUE;
                material = config.getBlueBlock();
            }
            if (board.placePiece(column, piece)) {
                //Physical Board
                Block slot = slots.get(column);
                FallingBlock block = (FallingBlock) slot.getWorld().spawnFallingBlock(slot.getLocation(), material.getItemType(), material.getData());
                fallingBlocks.add(block);

                if (redTurn) {
                    redTurn = false;
                } else {
                    redTurn = true;
                }
            } else {
                player.sendMessage(ChatColor.RED + "You can't place a piece there.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "It is not your turn.");
        }
    }

    public void handleFallingBlock(FallingBlock block) {
        VirtualBoard.WinStatus winStatus = board.testWin();
        if (winStatus != VirtualBoard.WinStatus.NONE) {
            Collection<? extends Player> players = Players.playersByRadius(Players.worldPlayers(block.getWorld()), 20).get(block.getLocation());
            for (Player player : players) {
                player.sendMessage((winStatus == VirtualBoard.WinStatus.RED) ? (ChatColor.RED + redPlayer.getName()) : (ChatColor.BLUE + bluePlayer.getName())
                        + ChatColor.GREEN + " wins!");
            }

            finished = true;

            Bukkit.getScheduler().runTaskLater(Connect4Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    resetBoard();
                    resetPlayers();
                }
            }, 15*20);
        }
        fallingBlocks.remove(block);
    }

    public void loadBoard(World world){
        Location min = config.getRegion().getMinimumPoint().toLocation(world);
        Location max = config.getRegion().getMaximumPoint().toLocation(world);

        int width = 0;
        int offX = 0;
        int offZ = 0;

        if (min.getBlockX() == max.getBlockX()) {
            width = max.getBlockZ() - min.getBlockZ();
            offZ = 1;
        } else {
            width = max.getBlockX() - min.getBlockX();
            offX = 1;
        }

        for (int i = 0; i < width; i++) {
            Block button = min.clone().add(offX * i, -1, offZ * i).getBlock().getRelative(config.getFace().getOppositeFace());
            buttons.put(button, i);

            int height = max.getBlockY() - min.getBlockY() - 1;
            Block slot = min.clone().add(offX * i, height, offZ * i).getBlock();
            slots.put(i, slot);
        }
    }
}
