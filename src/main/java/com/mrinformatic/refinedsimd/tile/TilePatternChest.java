package com.mrinformatic.refinedsimd.tile;

import com.mrinformatic.refinedsimd.network.NetworkNodePatternChest;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TilePatternChest extends TileNode<NetworkNodePatternChest> {
  @Override
  public NetworkNodePatternChest createNode(World world, BlockPos blockPos) {
    return new NetworkNodePatternChest(world, blockPos);
  }

  @Override
  public String getNodeId() {
    return NetworkNodePatternChest.ID;
  }
}
