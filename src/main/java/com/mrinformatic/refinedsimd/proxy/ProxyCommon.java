package com.mrinformatic.refinedsimd.proxy;

import com.mrinformatic.refinedsimd.RefinedSIMD;
import com.mrinformatic.refinedsimd.RefinedSIMDBlocks;
import com.mrinformatic.refinedsimd.container.ContainerPatternChest;
import com.mrinformatic.refinedsimd.container.ContainerRemoteCrafter;
import com.mrinformatic.refinedsimd.gui.GuiHandler;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.block.info.IBlockInfo;
import com.raoulvdberge.refinedstorage.tile.TileBase;
import com.raoulvdberge.refinedstorage.tile.TileNode;
import com.raoulvdberge.refinedstorage.tile.data.TileDataManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.LinkedList;
import java.util.List;

public class ProxyCommon {
    protected List<Item> itemsToRegister = new LinkedList<>();
    protected List<BlockBase> blocksToRegister = new LinkedList<>();

    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);

        NetworkRegistry.INSTANCE.registerGuiHandler(RefinedSIMD.INSTANCE, new GuiHandler());

        API.instance().addPatternRenderHandler(pattern -> {
            Container container = Minecraft.getMinecraft().player.openContainer;

            if (container instanceof ContainerRemoteCrafter) {
                for (int i = 0; i < 9; ++i) {
                    if (container.getSlot(i).getStack() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });

        API.instance().addPatternRenderHandler(pattern -> {
            Container container = Minecraft.getMinecraft().player.openContainer;

            if (container instanceof ContainerPatternChest) {
                for (int i = 0; i < 9; ++i) {
                    if (container.getSlot(i).getStack() == pattern) {
                        return true;
                    }
                }
            }

            return false;
        });

        registerBlock(RefinedSIMDBlocks.REMOTE_CRAFTER);
        registerBlock(RefinedSIMDBlocks.PATTERN_CHEST);
        registerBlock(RefinedSIMDBlocks.COMBINED_EXPORTER);
        registerBlock(RefinedSIMDBlocks.COMBINED_IMPORTER);
    }

    public void init(FMLInitializationEvent e) {
        // NO OP
    }

    public void postInit(FMLPostInitializationEvent e) {
        // NO OP
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> e) {
        blocksToRegister.forEach(e.getRegistry()::register);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> e) {
        itemsToRegister.forEach(e.getRegistry()::register);
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> e) {

    }

    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck e) {
        if (e.getTargetBlock().getBlock() instanceof BlockBase) {
            e.setCanHarvest(true); // Allow break without tool
        }
    }

    private void registerBlock(BlockBase block) {
        blocksToRegister.add(block);

        registerItem(block.createItem());

        if (block.getInfo().hasTileEntity()) {
            registerTile(block.getInfo());
        }
    }

    private void registerItem(Item item) {
        itemsToRegister.add(item);
    }

    private void registerTile(IBlockInfo info) {
        Class<? extends TileBase> clazz = info.createTileEntity().getClass();

        GameRegistry.registerTileEntity(clazz, info.getId());

        try {
            TileBase tileInstance = clazz.newInstance();

            if (tileInstance instanceof TileNode) {
                API.instance().getNetworkNodeRegistry().add(((TileNode) tileInstance).getNodeId(), (tag, world, pos) -> {
                    NetworkNode node = ((TileNode) tileInstance).createNode(world, pos);

                    node.read(tag);

                    return node;
                });
            }

            tileInstance.getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
