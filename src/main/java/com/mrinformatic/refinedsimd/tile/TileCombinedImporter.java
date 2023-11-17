package com.mrinformatic.refinedsimd.tile;

import com.mrinformatic.refinedsimd.network.NetworkNodeCombinedImporter;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileCombinedImporter extends TileNode<NetworkNodeCombinedImporter> {
    public static final TileDataParameter<Integer, TileCombinedImporter> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileCombinedImporter> MODE = IFilterable.createParameter();

    public TileCombinedImporter() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
    }

    @Override
    @Nonnull
    public NetworkNodeCombinedImporter createNode(World world, BlockPos pos) {
        return new NetworkNodeCombinedImporter(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeCombinedImporter.ID;
    }
}
