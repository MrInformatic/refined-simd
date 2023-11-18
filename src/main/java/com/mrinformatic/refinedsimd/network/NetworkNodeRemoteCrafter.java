package com.mrinformatic.refinedsimd.network;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerUpgrade;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NetworkNodeRemoteCrafter extends NetworkNode implements ICraftingPatternContainer {
  public enum CrafterMode {
    IGNORE,
    SIGNAL_UNLOCKS_AUTOCRAFTING,
    SIGNAL_LOCKS_AUTOCRAFTING,
    PULSE_INSERTS_NEXT_SET;

    public static CrafterMode getById(int id) {
      if (id >= 0 && id < values().length) {
        return values()[id];
      }

      return IGNORE;
    }
  }

  public static final String ID = "remote-crafter";

  public static final String DEFAULT_NAME = "gui.refinedstorage:remote-crafter";

  private static final String NBT_DISPLAY_NAME = "DisplayName";
  private static final String NBT_UUID = "RemoteCrafterUuid";
  public static final String NBT_PATTERN_CHEST_UUID = "PatternChestUuid";
  private static final String NBT_MODE = "Mode";
  private static final String NBT_LOCKED = "Locked";
  private static final String NBT_WAS_POWERED = "WasPowered";

  private NetworkNodePatternChest patternChest;
  private UUID patterChestUUID;
  private List<ICraftingPattern> patterns = new ArrayList();

  private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, new ListenerNetworkNode(this), ItemUpgrade.TYPE_SPEED);

  // Used to prevent infinite recursion on getRootContainer() when there's e.g. two crafters facing each other.
  private boolean visited = false;

  private CrafterMode mode = CrafterMode.IGNORE;
  private boolean locked = false;
  private boolean wasPowered;

  @Nullable
  private String displayName;

  @Nullable
  private UUID uuid = null;

  public NetworkNodeRemoteCrafter(World world, BlockPos pos) {
    super(world, pos);
  }

  public void invalidate() {
    patterns.clear();

    if(patternChest == null) {
      return;
    }

    for (int i = 0; i < patternChest.getPatternInventory().getSlots(); ++i) {
      ItemStack patternStack = patternChest.getPatternInventory().getStackInSlot(i);

      if (!patternStack.isEmpty()) {
        ICraftingPattern pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(world, patternStack, this);

        if (pattern.isValid()) {
          patterns.add(pattern);
        }
      }
    }
  }

  @Override
  public int getEnergyUsage() {
    return RS.INSTANCE.config.crafterUsage + upgrades.getEnergyUsage() + (RS.INSTANCE.config.crafterPerPatternUsage * patterns.size());
  }

  @Override
  public void update() {
    super.update();

    if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET) {
      if (world.isBlockPowered(pos)) {
        this.wasPowered = true;

        markDirty();
      } else if (wasPowered) {
        this.wasPowered = false;
        this.locked = false;

        markDirty();
      }
    }
  }

  @Override
  protected void onConnectedStateChange(INetwork network, boolean state) {
    super.onConnectedStateChange(network, state);

    network.getCraftingManager().rebuild();
  }

  @Override
  public void onConnected(INetwork network) {
    super.onConnected(network);

    NetworkNodePatternChest newPatternChest = network.getNodeGraph().all().stream()
        .filter(node -> node instanceof NetworkNodePatternChest)
        .map(node -> ((NetworkNodePatternChest) node))
        .filter(node -> node.getUUID().equals(patterChestUUID))
        .findAny()
        .orElse(null);

    this.register(newPatternChest);
  }

  @Override
  public void onDisconnected(INetwork network) {
    super.onDisconnected(network);

    network.getCraftingManager().getTasks().stream()
        .filter(task -> task.getPattern().getContainer().getPosition().equals(pos))
        .forEach(task -> network.getCraftingManager().cancel(task.getId()));

    this.unregister();
  }

  public void register(NetworkNodePatternChest patternChest) {
    if(this.patternChest == patternChest || patternChest == null) {
      return;
    }

    this.patternChest = patternChest;
    this.patternChest.register(this);
    this.invalidate();
  }

  public void unregister() {
    if(this.patternChest == null) {
      return;
    }

    NetworkNodePatternChest oldPatternChest = patternChest;
    this.patternChest = null;
    oldPatternChest.unregister(this);

    this.invalidate();
  }

  @Override
  protected void onDirectionChanged() {
    if (network != null) {
      network.getCraftingManager().rebuild();
    }
  }

  @Override
  public void read(NBTTagCompound tag) {
    super.read(tag);

    StackUtils.readItems(upgrades, 0, tag);

    if (tag.hasKey(NBT_DISPLAY_NAME)) {
      displayName = tag.getString(NBT_DISPLAY_NAME);
    }

    if (tag.hasUniqueId(NBT_UUID)) {
      uuid = tag.getUniqueId(NBT_UUID);
    }

    if (tag.hasUniqueId(NBT_PATTERN_CHEST_UUID)) {
      patterChestUUID = tag.getUniqueId(NBT_PATTERN_CHEST_UUID);
    }

    if (tag.hasKey(NBT_MODE)) {
      mode = CrafterMode.getById(tag.getInteger(NBT_MODE));
    }

    if (tag.hasKey(NBT_LOCKED)) {
      locked = tag.getBoolean(NBT_LOCKED);
    }

    if (tag.hasKey(NBT_WAS_POWERED)) {
      wasPowered = tag.getBoolean(NBT_WAS_POWERED);
    }
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public NBTTagCompound write(NBTTagCompound tag) {
    super.write(tag);

    StackUtils.writeItems(upgrades, 0, tag);

    if (displayName != null) {
      tag.setString(NBT_DISPLAY_NAME, displayName);
    }

    if (uuid != null) {
      tag.setUniqueId(NBT_UUID, uuid);
    }

    if (patterChestUUID != null) {
      tag.setUniqueId(NBT_PATTERN_CHEST_UUID, patterChestUUID);
    }

    tag.setInteger(NBT_MODE, mode.ordinal());
    tag.setBoolean(NBT_LOCKED, locked);
    tag.setBoolean(NBT_WAS_POWERED, wasPowered);

    return tag;
  }

  @Override
  public int getUpdateInterval() {
    switch (upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED)) {
      case 0:
        return 10;
      case 1:
        return 8;
      case 2:
        return 6;
      case 3:
        return 4;
      case 4:
        return 2;
      default:
        return 0;
    }
  }

  @Override
  public int getMaximumSuccessfulCraftingUpdates() {
    switch (upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED)) {
      case 0:
        return 1;
      case 1:
        return 2;
      case 2:
        return 3;
      case 3:
        return 4;
      case 4:
        return 5;
      default:
        return 1;
    }
  }

  @Override
  @Nullable
  public IItemHandler getConnectedInventory() {
    ICraftingPatternContainer proxy = getRootContainer();
    if (proxy == null) {
      return null;
    }

    return WorldUtils.getItemHandler(proxy.getFacingTile(), proxy.getDirection().getOpposite());
  }

  @Nullable
  @Override
  public IFluidHandler getConnectedFluidInventory() {
    ICraftingPatternContainer proxy = getRootContainer();
    if (proxy == null) {
      return null;
    }

    return WorldUtils.getFluidHandler(proxy.getFacingTile(), proxy.getDirection().getOpposite());
  }

  @Override
  @Nullable
  public TileEntity getConnectedTile() {
    ICraftingPatternContainer proxy = getRootContainer();
    if (proxy == null) {
      return null;
    }

    return proxy.getFacingTile();
  }

  @Override
  public List<ICraftingPattern> getPatterns() {
    return patterns;
  }

  @Override
  @Nullable
  public IItemHandlerModifiable getPatternInventory() {
    return null;
  }

  @Override
  public String getName() {
    if (displayName != null) {
      return displayName;
    }

    TileEntity facing = getConnectedTile();

    if (facing instanceof IWorldNameable && ((IWorldNameable) facing).getName() != null) {
      return ((IWorldNameable) facing).getName();
    }

    if (facing != null) {
      return world.getBlockState(facing.getPos()).getBlock().getUnlocalizedName() + ".name";
    }

    return DEFAULT_NAME;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @Nullable
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public BlockPos getPosition() {
    return pos;
  }

  public CrafterMode getMode() {
    return mode;
  }

  public void setMode(CrafterMode mode) {
    this.mode = mode;
    this.wasPowered = false;
    this.locked = false;

    this.markDirty();
  }

  @Nullable
  public IItemHandler getPatternItems() {
    if(this.patternChest == null) {
      return null;
    }

    return this.patternChest.getPatternInventory();
  }

  public IItemHandler getUpgrades() {
    return upgrades;
  }

  @Override
  public IItemHandler getDrops() {
    return upgrades;
  }

  @Override
  public boolean hasConnectivityState() {
    return true;
  }

  @Override
  @Nullable
  public ICraftingPatternContainer getRootContainer() {
    if (visited) {
      return null;
    }

    INetworkNode facing = API.instance().getNetworkNodeManager(world).getNode(pos.offset(getDirection()));
    if (!(facing instanceof ICraftingPatternContainer) || facing.getNetwork() != network) {
      return this;
    }

    visited = true;
    ICraftingPatternContainer facingContainer = ((ICraftingPatternContainer) facing).getRootContainer();
    visited = false;

    return facingContainer;
  }

  public Optional<ICraftingPatternContainer> getRootContainerNotSelf() {
    ICraftingPatternContainer root = getRootContainer();

    if (root != null && root != this) {
      return Optional.of(root);
    }

    return Optional.empty();
  }

  @Override
  public UUID getUuid() {
    if (this.uuid == null) {
      this.uuid = UUID.randomUUID();

      markDirty();
    }

    return uuid;
  }

  public UUID getPatterChestUUID() {
    return patterChestUUID;
  }

  @Override
  public boolean isLocked() {
    Optional<ICraftingPatternContainer> root = getRootContainerNotSelf();
    if (root.isPresent()) {
      return root.get().isLocked();
    }

    switch (mode) {
      case IGNORE:
        return false;
      case SIGNAL_LOCKS_AUTOCRAFTING:
        return world.isBlockPowered(pos);
      case SIGNAL_UNLOCKS_AUTOCRAFTING:
        return !world.isBlockPowered(pos);
      case PULSE_INSERTS_NEXT_SET:
        return locked;
      default:
        return false;
    }
  }

  @Override
  public void onUsedForProcessing() {
    Optional<ICraftingPatternContainer> root = getRootContainerNotSelf();
    if (root.isPresent()) {
      root.get().onUsedForProcessing();

      return;
    }

    if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET) {
      this.locked = true;

      markDirty();
    }
  }
}
