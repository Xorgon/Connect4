package me.xorgon.connect4.util;

import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * Created by Elijah on 28/06/2017.
 */
public class BlockFaceUtil {
    public static BlockFace blockFaceFromVector(Vector vector) {
        vector.normalize();
        int x = (int) Math.round(vector.getX());
        int y = (int) Math.round(vector.getY());
        int z = (int) Math.round(vector.getZ());
        if (z == -1) return BlockFace.NORTH;
        if (x == 1) return BlockFace.EAST;
        if (z == 1) return BlockFace.SOUTH;
        if (x == -1) return BlockFace.WEST;
        if (y == 1) return BlockFace.UP;
        if (y == -1) return BlockFace.DOWN;
        return null;
    }

    public static Vector blockFaceToVector(BlockFace face) {
        return new Vector(face.getModX(), face.getModY(), face.getModZ());
    }
}
