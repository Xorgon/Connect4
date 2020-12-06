package me.xorgon.connect4;

import com.supaham.commons.bukkit.utils.SerializationUtils;
import me.xorgon.connect4.util.PhysicalBoard;
import me.xorgon.connect4.util.Selection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pluginbase.config.datasource.yaml.YamlDataSource;
import pluginbase.messages.messaging.SendablePluginBaseException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Elijah on 14/08/2015.
 */
public class C4Manager {

    private final Connect4Plugin plugin = Connect4Plugin.getInstance();

    private Map<String, PhysicalBoard> boards = new HashMap<>();
    private Map<Player, Selection> selections = new HashMap<>();
    private Map<Integer, C4Game> games = new HashMap<>();

    private C4Properties config;
    private File boardsFile;

    public C4Manager() {
        load();
    }

    public boolean load() {

        YamlDataSource yaml;
        try {
            boardsFile = new File(plugin.getDataFolder(), "boards.yml");
            yaml = SerializationUtils.yaml(boardsFile).build();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        config = SerializationUtils.loadOrCreateProperties(plugin.getLogger(), yaml, new C4Properties());

        for (C4Properties.Board board : config.getBoards()) {
            if (!board.isComplete()) {
                System.out.println(board.getId() + " is not complete and so will not be loaded.");
                continue;
            }
            World world = Bukkit.getWorld(board.getWorld());
            if (world == null) {
                plugin.getLog().severe("'%s' board has null world.", board.getId());
                boards.put(board.getId().toLowerCase(), new PhysicalBoard(null, board));
            } else {
                boards.put(board.getId().toLowerCase(), new PhysicalBoard(world, board));
            }
        }

        return true;
    }

    public boolean save() {
        YamlDataSource yaml;
        try {
            yaml = SerializationUtils.yaml(boardsFile).build();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {
            yaml.save(SerializationUtils.serialize(this.config));
        } catch (SendablePluginBaseException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean loadPhysicalBoard(String id) {
        C4Properties.Board board = config.getBoard(id);
        if (board != null && board.isComplete()) {
            boards.put(id, new PhysicalBoard(Bukkit.getWorld(board.getWorld()), board));
            return true;
        } else {
            return false;
        }
    }

    public PhysicalBoard getBoard(String tag) {
        return boards.get(tag);
    }

    public Map<String, PhysicalBoard> getBoards() {
        return boards;
    }

    public Selection getSelection(Player player) {
        return selections.get(player);
    }

    public void addSelection(Player player, Selection selection) {
        selections.put(player, selection);
    }

    public C4Properties getConfig() {
        return config;
    }
}
