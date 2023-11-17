package com.mrinformatic.refinedsimd.tile;

import com.mrinformatic.refinedsimd.network.NetworkNodeCombinedExporter;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileCombinedExporter extends TileNode<NetworkNodeCombinedExporter> {
    public static final TileDataParameter<Integer, TileCombinedExporter> COMPARE = IComparable.createParameter();

    public TileCombinedExporter() {
        dataManager.addWatchedParameter(COMPARE);
    }

    @Override
    @Nonnull
    public NetworkNodeCombinedExporter createNode(World world, BlockPos pos) {
        return new NetworkNodeCombinedExporter(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeCombinedExporter.ID;
    }
}
