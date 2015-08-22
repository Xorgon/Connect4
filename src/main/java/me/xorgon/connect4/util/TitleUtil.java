package me.xorgon.connect4.util;

import com.supaham.commons.bukkit.text.FancyMessage;
import com.supaham.commons.bukkit.title.Title;
import org.bukkit.entity.Player;

/**
 * Created by Elijah on 18/08/2015.
 */
public class TitleUtil {

    public static void sendTitle(Player player, String title, String subtitle){
        Title.sendTimes(player, 0, 70, 10);
        Title.sendSubtitle(player, new FancyMessage(title), new FancyMessage(subtitle));
    }

    public static void sendTitle(Player player, String title, String subtitle, boolean overrideTimes){
        if (!overrideTimes) {
            Title.sendTimes(player, 0, 70, 10);
        }
        Title.sendSubtitle(player, new FancyMessage(title), new FancyMessage(subtitle));
    }

    public static void sendTitle(Player player, String title){
        Title.sendTimes(player, 0, 70, 10);
        Title.sendTitle(player, new FancyMessage(title));
    }

    public static void sendTitle(Player player, String title, boolean overrideTimes){
        if (!overrideTimes) {
            Title.sendTimes(player, 0, 70, 10);
        }
        Title.sendTitle(player, new FancyMessage(title));
    }
}
