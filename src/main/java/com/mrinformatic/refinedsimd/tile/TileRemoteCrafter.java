package com.mrinformatic.refinedsimd.tile;

import com.mrinformatic.refinedsimd.network.NetworkNodeRemoteCrafter;
import com.raoulvdberge.refinedstorage.gui.TileDataParameterClientListenerCrafter;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileRemoteCrafter extends TileNode<NetworkNodeRemoteCrafter> {
    public static final TileDataParameter<String, TileRemoteCrafter> NAME = new TileDataParameter<>(DataSerializers.STRING, NetworkNodeRemoteCrafter.DEFAULT_NAME, t -> t.getNode().getName());
    public static final TileDataParameter<Integer, TileRemoteCrafter> MODE = new TileDataParameter<>(DataSerializers.VARINT, NetworkNodeRemoteCrafter.CrafterMode.IGNORE.ordinal(), t -> t.getNode().getMode().ordinal(), (t, v) -> t.getNode().setMode(NetworkNodeRemoteCrafter.CrafterMode.getById(v)));
    private static final TileDataParameter<Boolean, TileRemoteCrafter> HAS_ROOT = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().getRootContainerNotSelf().isPresent(), null, (t, v) -> new TileDataParameterClientListenerCrafter().onChanged(t, v));

    public TileRemoteCrafter() {
        dataManager.addWatchedParameter(NAME);
        dataManager.addWatchedParameter(MODE);
        dataManager.addParameter(HAS_ROOT);
    }

    @Override
    @Nonnull
    public NetworkNodeRemoteCrafter createNode(World world, BlockPos pos) {
        return new NetworkNodeRemoteCrafter(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeRemoteCrafter.ID;
    }
}
