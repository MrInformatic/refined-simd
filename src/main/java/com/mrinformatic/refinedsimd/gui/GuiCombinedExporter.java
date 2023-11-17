package com.mrinformatic.refinedsimd.gui;

import com.mrinformatic.refinedsimd.container.ContainerCombinedExporter;
import com.mrinformatic.refinedsimd.tile.TileCombinedExporter;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonCompare;
import com.raoulvdberge.refinedstorage.gui.control.SideButtonRedstoneMode;

public class GuiCombinedExporter extends GuiBaseBase {
    public GuiCombinedExporter(ContainerCombinedExporter container) {
        super(container, 211, 173);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(this, TileCombinedExporter.REDSTONE_MODE));

        addSideButton(new SideButtonCompare(this, TileCombinedExporter.COMPARE, IComparer.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(this, TileCombinedExporter.COMPARE, IComparer.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("refinedsimd", "gui/combined_exporter.png");

        drawTexture(x, y, 0, 0, screenWidth, screenHeight);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.refinedsimd:exporter.items"));
        drawString(7, 42, t("gui.refinedsimd:exporter.fluids"));
        drawString(7, 78, t("container.inventory"));
    }
}
