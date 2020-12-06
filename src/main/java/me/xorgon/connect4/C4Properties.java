package me.xorgon.connect4;

import com.supaham.commons.bukkit.area.CuboidRegion;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import pluginbase.config.annotation.NoTypeKey;

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
        private String redBlock;
        private String blueBlock;

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

        public String getRedBlock() {
            return redBlock;
        }

        public String getBlueBlock() {
            return blueBlock;
        }

        public void setId(String id) {
            this.id = id;
            redBlock = Material.RED_WOOL.name();
            blueBlock = Material.BLUE_WOOL.name();
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

        public void setRedBlock(String redBlock) {
            this.redBlock = redBlock;
        }

        public void setBlueBlock(String blueBlock) {
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
