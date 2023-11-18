package com.mrinformatic.refinedsimd.network;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.HashSet;
import java.util.UUID;

public class NetworkNodePatternChest extends NetworkNode {
  public static final String ID = "pattern-chest";
  private static final String NBT_UUID = "PatternChestUuid";

  public static final int SLOTS = 54;
  private UUID uuid;
  private final HashSet<NetworkNodeRemoteCrafter> registeredCrafters = new HashSet<NetworkNodeRemoteCrafter>();
  private boolean reading;
  private boolean disconnecting;
  private final ItemHandlerBase patternsInventory = new ItemHandlerBase(SLOTS, new ListenerNetworkNode(this), s -> isValidPatternInSlot(world, s)) {
    @Override
    protected void onContentsChanged(int slot) {
      super.onContentsChanged(slot);

      if (!reading) {
        if (!world.isRemote) {
          invalidate();
        }

        if (network != null) {
          network.getCraftingManager().rebuild();
        }
      }
    }

    @Override
    public int getSlotLimit(int slot) {
      return 1;
    }
  };

  public static boolean isValidPatternInSlot(World world, ItemStack stack) {
    return stack.getItem() instanceof ICraftingPatternProvider && ((ICraftingPatternProvider) stack.getItem()).create(world, stack, null).isValid();
  }

  public NetworkNodePatternChest(World world, BlockPos pos) {
    super(world, pos);
    this.uuid = UUID.randomUUID();
    this.markDirty();
  }

  @Override
  public void onConnected(INetwork network) {
    super.onConnected(network);
    network.getNodeGraph().all().stream()
        .filter(node -> node instanceof NetworkNodeRemoteCrafter)
        .map(node -> (NetworkNodeRemoteCrafter)node)
        .filter(node -> this.uuid.equals(node.getPatterChestUUID()))
        .forEach(this::register);
  }

  @Override
  public void onDisconnected(INetwork network) {
    super.onDisconnected(network);
    disconnecting = true;
    registeredCrafters.forEach(this::unregister);
    registeredCrafters.clear();
    disconnecting = false;
  }

  @Override
  public NBTTagCompound write(NBTTagCompound tag) {
    super.write(tag);

    tag.setUniqueId(NBT_UUID, uuid);

    StackUtils.writeItems(patternsInventory, 0, tag);

    return tag;
  }

  @Override
  public void read(NBTTagCompound tag) {
    super.read(tag);

    this.uuid = tag.getUniqueId(NBT_UUID);

    reading = true;
    StackUtils.readItems(patternsInventory, 0, tag);
    reading = false;

    this.invalidate();
  }

  @Override
  public int getEnergyUsage() {
    return 0;
  }

  @Override
  public String getId() {
    return ID;
  }

  public UUID getUUID() {
    return this.uuid;
  }
  public IItemHandlerModifiable getPatternInventory() {
    return patternsInventory;
  }

  @Override
  public IItemHandler getDrops() {
    return patternsInventory;
  }

  public void register(NetworkNodeRemoteCrafter remoteCrafter) {
    if(registeredCrafters.add(remoteCrafter)) {
      remoteCrafter.register(this);
    }
  }

  public void unregister(NetworkNodeRemoteCrafter remoteCrafter) {
    if(disconnecting) {
      remoteCrafter.unregister();
      return;
    }

    if(registeredCrafters.remove(remoteCrafter)) {
      remoteCrafter.unregister();
    }
  }

  private void invalidate() {
    this.registeredCrafters.forEach(NetworkNodeRemoteCrafter::invalidate);
  }
}
