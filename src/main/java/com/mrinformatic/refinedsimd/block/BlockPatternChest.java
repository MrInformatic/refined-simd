package com.mrinformatic.refinedsimd.block;

import com.mrinformatic.refinedsimd.RefinedSIMD;
import com.mrinformatic.refinedsimd.RefinedSIMDGui;
import com.mrinformatic.refinedsimd.tile.TilePatternChest;
import com.mrinformatic.refinedsimd.tile.TileRemoteCrafter;
import com.raoulvdberge.refinedstorage.block.BlockNode;
import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import com.raoulvdberge.refinedstorage.block.info.BlockInfoBuilder;
import com.raoulvdberge.refinedstorage.render.IModelRegistration;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockPatternChest extends BlockNode {
  public BlockPatternChest() {
    super(BlockInfoBuilder.forMod(RefinedSIMD.INSTANCE, RefinedSIMD.MODID, "pattern_chest").tileEntity(TilePatternChest::new).create());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerModels(IModelRegistration modelRegistration) {
    modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "inventory"));
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, placer, stack);

    if (!world.isRemote) {
      TileEntity tile = world.getTileEntity(pos);

      if (tile instanceof TileRemoteCrafter && stack.hasDisplayName()) {
        ((TileRemoteCrafter) tile).getNode().setDisplayName(stack.getDisplayName());
        ((TileRemoteCrafter) tile).getNode().markDirty();
      }
    }
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    if (!canAccessGui(state, world, pos, hitX, hitY, hitZ)) {
      return false;
    }

    return openNetworkGui(RefinedSIMDGui.PATTERN_CHEST, player, world, pos, side);
  }

  @Override
  public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    super.getDrops(drops, world, pos, state, fortune);

    String displayName = ((TileRemoteCrafter) world.getTileEntity(pos)).getNode().getDisplayName();

    if (displayName != null) {
      for (ItemStack drop : drops) {
        if (drop.getItem() == Item.getItemFromBlock(this)) {
          drop.setStackDisplayName(displayName);
        }
      }
    }
  }
}
