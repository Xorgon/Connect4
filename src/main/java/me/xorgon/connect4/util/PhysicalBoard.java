package me.xorgon.connect4.util;

import com.google.common.base.Preconditions;
import com.supaham.commons.bukkit.players.Players;
import com.supaham.commons.bukkit.utils.BlockFaceUtils;
import com.supaham.commons.utils.WeakSet;
import me.xorgon.connect4.C4Properties;
import me.xorgon.connect4.Connect4Plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Elijah on 14/08/2015.
 */
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
    private World world;


    private boolean canInteract = true;
    private boolean started = false;
    private boolean redTurn = true;
    private boolean finished = false;
    private Vector center;

    private BukkitTask initTimer;
    private BukkitTask finTimer;

    private WeakSet<FallingBlock> fallingBlocks = new WeakSet<>();

    public PhysicalBoard(World world, C4Properties.Board serialized) {
        Preconditions.checkArgument(BlockFaceUtils.isHorizontal(serialized.getFace()), "Block face must be horizontal.");

        this.world = world;
        config = serialized;
        board = new VirtualBoard();
        loadBoard(world);
        center = config.getRegion().getMaximumPoint().clone().midpoint(config.getRegion().getMinimumPoint());
    }

    public void resetBoard() {
        if (world == null) {
            world = Bukkit.getWorld(config.getWorld());
        } else {
            System.out.println("Could not find world " + config.getWorld());
            return;
        }
        for (Vector space : config.getRegion()) {
            space.toLocation(world).getBlock().setType(Material.AIR);
        }
        board.initialize();
        finished = false;
        canInteract = true;
        if (initTimer != null){
            initTimer.cancel();
        }
        if (finTimer != null){
            finTimer.cancel();
        }
    }

    public void resetPlayers() {
        redPlayer = null;
        bluePlayer = null;
        started = false;
        redTurn = true;
    }

    public void placePiece(Player player, int column) {
        if ((player == redPlayer && redTurn) || (player == bluePlayer && !redTurn)) {
            Material material;

            //Virtual Board
            VirtualBoard.SpaceType piece;
            if (player == redPlayer) {
                piece = VirtualBoard.SpaceType.RED;
                material = Material.getMaterial(config.getRedBlock());
            } else {
                piece = VirtualBoard.SpaceType.BLUE;
                material = Material.getMaterial(config.getBlueBlock());
            }
            if (board.placePiece(column, piece)) {
                //Physical Board
                Block slot = slots.get(column);
                FallingBlock block = slot.getWorld().spawnFallingBlock(slot.getLocation().add(0.5, 1, 0.5), material.createBlockData());
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

            TitleUtil.sendTitle(redPlayer, winString, redWin ? ChatColor.YELLOW + "Congratulations!" : ChatColor.YELLOW + "Better luck next time.");
            TitleUtil.sendTitle(bluePlayer, winString, !redWin ? ChatColor.YELLOW + "Congratulations!" : ChatColor.YELLOW + "Better luck next time.");

            finished = true;

            Bukkit.getScheduler().runTaskLater(Connect4Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    resetBoard();
                    resetPlayers();
                }
            }, 10 * 20);
        } else if (winStatus == VirtualBoard.WinStatus.DRAW){
            TitleUtil.sendTitle(redPlayer, ChatColor.YELLOW + "It's a draw!","");
            TitleUtil.sendTitle(bluePlayer, ChatColor.YELLOW + "It's a draw!","");

            finished = true;

            Bukkit.getScheduler().runTaskLater(Connect4Plugin.getInstance(), () -> {
                resetBoard();
                resetPlayers();
            }, 10 * 20);

        } else {
            canInteract = true;
            if (redTurn) {
                redTurn = false;
                TitleUtil.sendTitle(bluePlayer, ChatColor.YELLOW + "Your turn!");
            } else {
                redTurn = true;
                TitleUtil.sendTitle(redPlayer, ChatColor.YELLOW + "Your turn!");
            }
            resetTimers();
        }
        fallingBlocks.remove(block);
    }

    public void loadBoard(World world) {
        if (world == null) {
            return;
        }
        this.world = world;
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
                    TitleUtil.sendTitle(redPlayer, ChatColor.RED + "10 Seconds");
                } else {
                    TitleUtil.sendTitle(bluePlayer, ChatColor.RED + "10 Seconds");
                }
            }
        }, 50 * 20);
        finTimer = Bukkit.getScheduler().runTaskLater(Connect4Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (redTurn) {
                    TitleUtil.sendTitle(redPlayer, ChatColor.RED + "You forfeit the game.", ChatColor.YELLOW + "You ran out of time.");
                    TitleUtil.sendTitle(bluePlayer, ChatColor.RED + redPlayer.getName() + ChatColor.YELLOW + " forfeit.", ChatColor.YELLOW + "They ran out of time.");
                } else {
                    TitleUtil.sendTitle(bluePlayer, ChatColor.RED + "You forfeit the game.", ChatColor.YELLOW + "You ran out of time.");
                    TitleUtil.sendTitle(redPlayer, ChatColor.BLUE + bluePlayer.getName() + ChatColor.YELLOW + " forfeit.", ChatColor.YELLOW + "They ran out of time.");
                }
                resetBoard();
                resetPlayers();
            }
        }, 60 * 20);
    }

    public WeakSet<FallingBlock> getFallingBlocks() {
        return fallingBlocks;
    }

    public Player getRedPlayer() {
        return redPlayer;
    }

    public Player getBluePlayer() {
        return bluePlayer;
    }

    public Vector getCenter() {
        return center;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setRedPlayer(Player redPlayer) {
        this.redPlayer = redPlayer;
    }

    public void setBluePlayer(Player bluePlayer) {
        this.bluePlayer = bluePlayer;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isCanInteract() {
        return canInteract;
    }

    public Map<Block, Integer> getButtons() {
        return buttons;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public VirtualBoard getBoard() {
        return board;
    }

    public World getWorld() {
        return world;
    }

    public C4Properties.Board getConfig() {
        return config;
    }
}
