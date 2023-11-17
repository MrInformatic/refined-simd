package com.mrinformatic.refinedsimd.container;

import com.mrinformatic.refinedsimd.network.NetworkNodePatternChest;
import com.mrinformatic.refinedsimd.tile.TilePatternChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerPatternChest extends ContainerBaseBase {
  public ContainerPatternChest(TilePatternChest patternChest, EntityPlayer player) {
    super(patternChest, player);

    NetworkNodePatternChest node = patternChest.getNode();
    IItemHandler patternItems = node.getPatternInventory();

    for (int i = 0; i < NetworkNodePatternChest.SLOTS; ++i) {
      addSlotToContainer(new SlotItemHandler(patternItems, i, 8 + (18 * (i % 9)), 20 + (18 * (i / 9))));
    }

    addPlayerInventory(8, 145);

    transferManager.addBiTransfer(player.inventory, patternItems);
  }
}
