package me.xorgon.connect4;

import com.supaham.commons.bukkit.SimpleCommonPlugin;
import org.bukkit.Bukkit;

import java.io.File;

/**
 * Created by Elijah on 14/08/2015.
 */
public class Connect4Plugin extends SimpleCommonPlugin<Connect4Plugin> {

    private static Connect4Plugin instance;
    private C4Manager manager;
    private static final String COMMAND_PREFIX = "c4";

    public Connect4Plugin() {
        super(Connect4Plugin.class, COMMAND_PREFIX);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;
        manager = new C4Manager(new File(getDataFolder(), "boards.yml"));
        registerEvents(new C4Listener());
        getCommand("c4").setExecutor(new C4Command());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static Connect4Plugin getInstance() {
        return instance;
    }

    public C4Manager getManager() {
        return manager;
    }
}
