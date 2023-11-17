package com.mrinformatic.refinedsimd.container;

import com.mrinformatic.refinedsimd.tile.TileCombinedImporter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCombinedImporter extends ContainerBaseBase {
    public ContainerCombinedImporter(TileCombinedImporter importer, EntityPlayer player) {
        super(importer, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(importer.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(importer.getNode().getItemFilters(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilterFluid(importer.getNode().getFluidFilters(), i, 8 + (18 * i), 55));
        }

        addPlayerInventory(8, 90);

        transferManager.addBiTransfer(player.inventory, importer.getNode().getUpgrades());
        transferManager.addItemFilterTransfer(player.inventory, importer.getNode().getItemFilters());
        transferManager.addFluidFilterTransfer(player.inventory, importer.getNode().getFluidFilters());
    }
}
