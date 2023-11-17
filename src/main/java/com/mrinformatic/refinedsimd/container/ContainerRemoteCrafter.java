package com.mrinformatic.refinedsimd.container;

import com.mrinformatic.refinedsimd.network.NetworkNodeRemoteCrafter;
import com.mrinformatic.refinedsimd.tile.TileRemoteCrafter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerRemoteCrafter extends ContainerBaseBase {
    public ContainerRemoteCrafter(TileRemoteCrafter crafter, EntityPlayer player) {
        super(crafter, player);

        NetworkNodeRemoteCrafter node = crafter.getNode();
        IItemHandler upgrades = node.getUpgrades();

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(upgrades, i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 20);

        transferManager.addBiTransfer(player.inventory, upgrades);
    }
}
