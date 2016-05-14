package me.xorgon.connect4;

import com.supaham.commons.bukkit.area.CuboidRegion;
import com.supaham.commons.bukkit.serializers.MaterialDataSerializer;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import pluginbase.config.annotation.SerializeWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elijah on 14/08/2015.
 */
public class C4Properties {

    private List<Board> boards = new ArrayList<>();

    public static class Board {
        private String id;
        private String world;
        private BlockFace face;
        private CuboidRegion region;
        @SerializeWith(MaterialDataSerializer.class)
        private MaterialData redBlock;
        @SerializeWith(MaterialDataSerializer.class)
        private MaterialData blueBlock;

        public String getId() {
            return id;
        }

        public String getWorld() {
            return world;
        }

        public BlockFace getFace() {
            return face;
        }

        public CuboidRegion getRegion() {
            return region;
        }

        public MaterialData getRedBlock() {
            return redBlock;
        }

        public MaterialData getBlueBlock() {
            return blueBlock;
        }
    }

    public List<Board> getBoards() {
        return boards;
    }
}
