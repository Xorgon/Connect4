package me.xorgon.connect4.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Elijah on 14/08/2015.
 */
public class Serializers {

    public static Object serializeLocation(Location location, boolean storeWorld) {
        return location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch() +
                (storeWorld? location.getWorld().getName() : "");
    }

    public static Location deserializeLocation(Object serialized, boolean storeWorld) {
        String[] split = ((String) serialized).split(",");
        double x = Double.valueOf(split[0]);
        double y = Double.valueOf(split[1]);
        double z = Double.valueOf(split[2]);
        float yaw = Float.parseFloat(split[3]);
        float pitch = Float.parseFloat(split[4]);
        if (storeWorld) {
            World world = Bukkit.getWorld(split[5]);
            Validate.notNull(world, "'" + split[5] + "' is not a valid world.");
            return new Location(world, x, y, z, yaw, pitch);
        } else {
            return new Location(null, x, y, z, yaw, pitch);
        }
    }

}
