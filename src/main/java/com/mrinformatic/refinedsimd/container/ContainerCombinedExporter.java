package com.mrinformatic.refinedsimd.container;

import com.mrinformatic.refinedsimd.tile.TileCombinedExporter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCombinedExporter extends ContainerBaseBase {
    public ContainerCombinedExporter(TileCombinedExporter exporter, EntityPlayer player) {
        super(exporter, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(exporter.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(exporter.getNode().getItemFilters(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilterFluid(exporter.getNode().getFluidFilters(), i, 8 + (18 * i), 55));
        }

        addPlayerInventory(8, 90);

        transferManager.addBiTransfer(player.inventory, exporter.getNode().getUpgrades());
        transferManager.addItemFilterTransfer(player.inventory, exporter.getNode().getItemFilters());
        transferManager.addFluidFilterTransfer(player.inventory, exporter.getNode().getFluidFilters());
    }
}
