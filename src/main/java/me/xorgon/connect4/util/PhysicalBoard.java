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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by Elijah on 14/08/2015.
 */
@Getter
@Setter
public class PhysicalBoard {

    private Map<Integer, Block> slots = new HashMap<>();
    private List<Block> spaces = new ArrayList<>();
    private Map<Block, Integer> buttons = new HashMap<>();
    private Block joinButton;
    private Player redPlayer;
    private Player bluePlayer;
    private VirtualBoard board;
    private String tag;
    private BlockFace face;
    private final C4Properties.Board config;
    private final World world;


    private boolean canInteract = true;
    private boolean started = false;
    private boolean redTurn = true;
    private boolean finished = false;
    private Vector center;

    private BukkitTask initTimer;
    private BukkitTask finTimer;

    private WeakSet<FallingBlock> fallingBlocks = new WeakSet<>();

    public PhysicalBoard(@NonNull World world, @NonNull C4Properties.Board serialized) {
        Preconditions.checkArgument(BlockFaceUtils.isHorizontal(serialized.getFace()), "Block face must be horizontal.");

        this.world = world;
        config = serialized;
        board = new VirtualBoard();
        loadBoard(world);
        center = config.getRegion().getMaximumPoint().clone().midpoint(config.getRegion().getMinimumPoint());
    }

    public void resetBoard() {
        for (Vector space : config.getRegion()) {
            space.toLocation(world).getBlock().setType(Material.AIR);
        }
        board.initialize();
        finished = false;
        canInteract = true;
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
                FallingBlock block = (FallingBlock) slot.getWorld().spawnFallingBlock(slot.getLocation().add(0, 1, 0), material.getItemType(), material.getData());
                fallingBlocks.add(block);

                canInteract = false;

                initTimer.cancel();
                finTimer.cancel();
            } else {
                player.sendMessage(ChatColor.RED + "You can't place a piece there.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "It is not your turn.");
        }
    }

    public void handleFallingBlock(FallingBlock block) {
        VirtualBoard.WinStatus winStatus = board.testWin();
        if (winStatus != VirtualBoard.WinStatus.NONE && winStatus != VirtualBoard.WinStatus.DRAW) {
            boolean redWin = winStatus == VirtualBoard.WinStatus.RED;
            String winString = (redWin ? ChatColor.RED + redPlayer.getName() : ChatColor.BLUE + bluePlayer.getName())
                    + ChatColor.YELLOW + " wins!";
            String announce = (redWin ? ChatColor.RED + redPlayer.getName() : ChatColor.BLUE + bluePlayer.getName())
            + ChatColor.YELLOW + " beat " + (!redWin ? ChatColor.RED + redPlayer.getName() : ChatColor.BLUE + bluePlayer.getName()) + ChatColor.YELLOW + " at Connect 4!";
            Collection<? extends Player> players = Players.playersByRadius(Players.worldPlayers(block.getWorld()), 20).get(block.getLocation());
            for (Player player : players) {
                player.sendMessage(announce);
            }

            redPlayer.sendTitle(winString, redWin ? ChatColor.YELLOW + "Congratulations!" : ChatColor.YELLOW + "Better luck next time.");
            bluePlayer.sendTitle(winString, !redWin ? ChatColor.YELLOW + "Congratulations!" : ChatColor.YELLOW + "Better luck next time.");

            finished = true;

            Bukkit.getScheduler().runTaskLater(Connect4Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    resetBoard();
                    resetPlayers();
                }
            }, 10 * 20);
        } else if (winStatus == VirtualBoard.WinStatus.DRAW){
            redPlayer.sendTitle(ChatColor.YELLOW + "It's a draw!","");
            bluePlayer.sendTitle(ChatColor.YELLOW + "It's a draw!","");

            finished = true;

            Bukkit.getScheduler().runTaskLater(Connect4Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    resetBoard();
                    resetPlayers();
                }
            }, 10 * 20);

        } else {
            canInteract = true;
            if (redTurn) {
                redTurn = false;
                bluePlayer.sendTitle(ChatColor.YELLOW + "Your turn!", "");
            } else {
                redTurn = true;
                redPlayer.sendTitle(ChatColor.YELLOW + "Your turn!", "");
            }
            resetTimers();
        }
        fallingBlocks.remove(block);
    }

    public void loadBoard(World world) {
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

        for (int i = 0; i < width + 1; i++) {
            Block button = min.clone().add(offX * i, -1, offZ * i).getBlock().getRelative(config.getFace().getOppositeFace());
            buttons.put(button, i);

            int height = max.getBlockY() - min.getBlockY() - 1;
            Block slot = min.clone().add(offX * i, height, offZ * i).getBlock();
            slots.put(i, slot);
        }

        resetBoard();
    }

    public void resetTimers(){
        initTimer = Bukkit.getScheduler().runTaskLater(Connect4Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (redTurn) {
                    redPlayer.sendTitle(ChatColor.RED + "10 Seconds", "");
                } else {
                    bluePlayer.sendTitle(ChatColor.RED + "10 Seconds", "");
                }
            }
        }, 50 * 20);
        finTimer = Bukkit.getScheduler().runTaskLater(Connect4Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (redTurn) {
                    redPlayer.sendTitle(ChatColor.RED + "You forfeit the game.", ChatColor.YELLOW + "You ran out of time.");
                    bluePlayer.sendTitle(ChatColor.RED + redPlayer.getName() + ChatColor.YELLOW + " forfeit.", ChatColor.YELLOW + "They ran out of time.");
                } else {
                    bluePlayer.sendTitle(ChatColor.RED + "You forfeit the game.", ChatColor.YELLOW + "You ran out of time.");
                    redPlayer.sendTitle(ChatColor.BLUE + bluePlayer.getName() + ChatColor.YELLOW + " forfeit.", ChatColor.YELLOW + "They ran out of time.");
                }
                resetBoard();
                resetPlayers();
            }
        }, 60 * 20);
    }
}
