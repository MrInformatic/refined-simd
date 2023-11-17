package com.mrinformatic.refinedsimd.gui;

import com.mrinformatic.refinedsimd.container.ContainerPatternChest;
import com.mrinformatic.refinedsimd.tile.TileRemoteCrafter;
import com.raoulvdberge.refinedstorage.util.RenderUtils;

public class GuiPatternChest extends GuiBaseBase {
  public GuiPatternChest(ContainerPatternChest container) {
    super(container, 176, 227);
  }

  @Override
  public void init(int x, int y) {
  }

  @Override
  public void update(int x, int y) {
  }

  @Override
  public void drawBackground(int x, int y, int mouseX, int mouseY) {
    bindTexture("refinedsimd", "gui/pattern_chest.png");

    drawTexture(x, y, 0, 0, screenWidth, screenHeight);
  }

  @Override
  public void drawForeground(int mouseX, int mouseY) {
    drawString(7, 7, RenderUtils.shorten(t(TileRemoteCrafter.NAME.getValue()), 26));
    drawString(7, 133, t("container.inventory"));
  }
}
