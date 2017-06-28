package me.xorgon.connect4;

import com.supaham.commons.bukkit.area.CuboidRegion;
import com.supaham.commons.bukkit.serializers.MaterialDataSerializer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import pluginbase.config.annotation.NoTypeKey;
import pluginbase.config.annotation.SerializeWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elijah on 14/08/2015.
 */
@NoTypeKey
public final class C4Properties {

    private List<Board> boards = new ArrayList<>();

    public C4Properties() {
    }

    @NoTypeKey
    public final static class Board {
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

        public void setId(String id) {
            this.id = id;
            redBlock = new MaterialData(Material.WOOL, DyeColor.RED.getWoolData());
            blueBlock = new MaterialData(Material.WOOL, DyeColor.BLUE.getWoolData());
        }

        public void setWorld(String world) {
            this.world = world;
        }

        public void setFace(BlockFace face) {
            this.face = face;
        }

        public void setRegion(CuboidRegion region) {
            this.region = region;
        }

        public void setRedBlock(MaterialData redBlock) {
            this.redBlock = redBlock;
        }

        public void setBlueBlock(MaterialData blueBlock) {
            this.blueBlock = blueBlock;
        }

        public boolean isComplete() {
            return id != null
                    && world != null
                    && face != null
                    && region != null
                    && redBlock != null
                    && blueBlock != null;
        }
    }

    public List<Board> getBoards() {
        return boards;
    }

    public void addBoard(Board board) {
        boards.add(board);
    }

    public Board getBoard(String id) {
        for (Board board : boards) {
            if (board.id.equals(id)) {
                return board;
            }
        }
        return null;
    }
}
