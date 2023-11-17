package com.mrinformatic.refinedsimd.gui;

import com.mrinformatic.refinedsimd.RefinedSIMDGui;
import com.mrinformatic.refinedsimd.container.ContainerCombinedExporter;
import com.mrinformatic.refinedsimd.container.ContainerCombinedImporter;
import com.mrinformatic.refinedsimd.container.ContainerPatternChest;
import com.mrinformatic.refinedsimd.container.ContainerRemoteCrafter;
import com.mrinformatic.refinedsimd.tile.TileCombinedExporter;
import com.mrinformatic.refinedsimd.tile.TileCombinedImporter;
import com.mrinformatic.refinedsimd.tile.TilePatternChest;
import com.mrinformatic.refinedsimd.tile.TileRemoteCrafter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    private Container getContainer(int ID, EntityPlayer player, TileEntity tile) {
        switch (ID) {
            case RefinedSIMDGui.REMOTE_CRAFTER:
                return new ContainerRemoteCrafter((TileRemoteCrafter) tile, player);
            case RefinedSIMDGui.PATTERN_CHEST:
                return new ContainerPatternChest((TilePatternChest) tile, player);
            case RefinedSIMDGui.COMBINED_EXPORTER:
                return new ContainerCombinedExporter((TileCombinedExporter) tile, player);
            case RefinedSIMDGui.COMBINED_IMPORTER:
                return new ContainerCombinedImporter((TileCombinedImporter) tile, player);
            default:
                return null;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return getContainer(ID, player, world.getTileEntity(new BlockPos(x, y, z)));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case RefinedSIMDGui.REMOTE_CRAFTER:
                return new GuiRemoteCrafter((ContainerRemoteCrafter) getContainer(ID, player, tile));
            case RefinedSIMDGui.PATTERN_CHEST:
                return new GuiPatternChest((ContainerPatternChest) getContainer(ID, player, tile));
            case RefinedSIMDGui.COMBINED_EXPORTER:
                return new GuiCombinedExporter((ContainerCombinedExporter) getContainer(ID, player, tile));
            case RefinedSIMDGui.COMBINED_IMPORTER:
                return new GuiCombinedImporter((ContainerCombinedImporter) getContainer(ID, player, tile));
            default:
                return null;
        }
    }
}
