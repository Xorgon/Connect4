package me.xorgon.connect4;

import com.supaham.commons.bukkit.area.CuboidRegion;
import com.supaham.commons.bukkit.serializers.MaterialDataSerializer;
import lombok.Getter;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import pluginbase.config.SerializationRegistrar;
import pluginbase.config.annotation.SerializeWith;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elijah on 14/08/2015.
 */
@Getter
public class C4Properties {

    private List<Board> boards = new ArrayList<>();

    static {
        SerializationRegistrar.registerClass(Board.class);
    }

    @Getter
    public static class Board {
        private BlockFace face;
        private CuboidRegion region;
        @SerializeWith(MaterialDataSerializer.class)
        private MaterialData redBlock;
        @SerializeWith(MaterialDataSerializer.class)
        private MaterialData blueBlock;
    }
}
