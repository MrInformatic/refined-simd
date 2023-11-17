package com.mrinformatic.refinedsimd.container;

import com.raoulvdberge.refinedstorage.container.ContainerBase;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

public abstract class ContainerBaseBase extends ContainerBase {
  public ContainerBaseBase(@Nullable TileBase tile, EntityPlayer player) {
    super(tile, player);
  }

  @Override
  public boolean canInteractWith(EntityPlayer playerIn) {
    return true;
  }
}
