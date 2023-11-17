package com.mrinformatic.refinedsimd.gui;

import com.mrinformatic.refinedsimd.container.ContainerRemoteCrafter;
import com.mrinformatic.refinedsimd.tile.TileRemoteCrafter;
import com.raoulvdberge.refinedstorage.util.RenderUtils;

public class GuiRemoteCrafter extends GuiBaseBase {
    public GuiRemoteCrafter(ContainerRemoteCrafter container) {
        super(container, 210, 102);
    }

    @Override
    public void init(int x, int y) {
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("refinedsimd", "gui/remote_crafter.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, RenderUtils.shorten(t(TileRemoteCrafter.NAME.getValue()), 26));
    }
}
