package com.mrinformatic.refinedsimd.gui;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import net.minecraft.inventory.Container;

public abstract class GuiBaseBase extends GuiBase {
  public GuiBaseBase(Container container, int screenWidth, int screenHeight) {
    super(container, screenWidth, screenHeight);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    super.func_146976_a(partialTicks, mouseX, mouseY);
  }
}
