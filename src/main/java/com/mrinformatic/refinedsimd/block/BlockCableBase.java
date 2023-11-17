package com.mrinformatic.refinedsimd.block;

import com.mrinformatic.refinedsimd.RefinedSIMD;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import com.raoulvdberge.refinedstorage.render.model.baked.BakedModelCableCover;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockCableBase extends BlockCable {
  public BlockCableBase(IBlockInfo info) {
    super(info);
  }

  static BlockInfoBuilder createBuilder(String id) {
    return BlockInfoBuilder.forMod(RefinedSIMD.INSTANCE, RefinedSIMD.MODID, id).material(Material.GLASS).soundType(SoundType.GLASS).hardness(0.35F);
  }

  @SideOnly(Side.CLIENT)
  void registerCover(IModelRegistration modelRegistration) {
    modelRegistration.addBakedModelOverride(info.getId(), BakedModelCableCover::new);
  }
}
