package com.mrinformatic.refinedsimd.item;

import com.mrinformatic.refinedsimd.network.NetworkNodeRemoteCrafter;
import com.mrinformatic.refinedsimd.tile.TilePatternChest;
import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.item.itemblock.ItemBlockBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class ItemBlockRemoteCrafter extends ItemBlockBase {
  public ItemBlockRemoteCrafter(BlockBase block, boolean subtypes) {
    super(block, subtypes);
  }

  @Override
  public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
    TileEntity tileEntity = world.getTileEntity(pos);

    if(tileEntity instanceof TilePatternChest) {
      ItemStack heldItem = player.getHeldItem(hand);
      NBTTagCompound heldItemNBT = heldItem.getSubCompound("BlockEntityTag");
      if(heldItemNBT == null) {
        heldItemNBT = new NBTTagCompound();
        heldItem.setTagInfo("BlockEntityTag", heldItemNBT);
      }
      UUID patterChestUUID = ((TilePatternChest) tileEntity).getNode().getUUID();
      heldItemNBT.setUniqueId(NetworkNodeRemoteCrafter.NBT_PATTERN_CHEST_UUID, patterChestUUID);

      return EnumActionResult.SUCCESS;
    }

    return super.onItemUseFirst(player, world, pos, facing, hitX, hitY, hitZ, hand);
  }
}
