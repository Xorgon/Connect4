package me.xorgon.connect4;

import com.supaham.commons.bukkit.utils.SerializationUtils;
import lombok.Getter;
import me.xorgon.connect4.util.PhysicalBoard;
import me.xorgon.connect4.util.Selection;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Elijah on 14/08/2015.
 */
@Getter
public class C4Manager {

    private final Connect4Plugin plugin = Connect4Plugin.getInstance();

    private Map<String, PhysicalBoard> boards = new HashMap<>();
    private Map<Player, Selection> selections = new HashMap<>();
    private Map<Integer, C4Game> games = new HashMap<>();

    private final File file;
    private C4Properties config;

    public C4Manager(File file) {
        this.file = file;
        load();
    }

    public void load(){
        config = SerializationUtils.loadOrCreateProperties(plugin, file, new C4Properties(), "settings");
    }

    public void save(){
    }

    /*public void addBoard(String tag){
        boards.put(tag, new PhysicalBoard());
    }*/

    public PhysicalBoard getBoard(String tag){
        return boards.get(tag);
    }

    public Selection getSelection(Player player){
        return selections.get(player);
    }

    public void addSelection(Player player, Selection selection){
        selections.put(player, selection);
    }

}
