/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;

public class THETriangle extends WTriangle implements TheWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.rotatedQuad(
            x, y, width, height, rotation, GuiRenderer.TRIANGLE,
            mouseOver || pressed ? theme().textColor.get() : theme().textSecondaryColor.get()
        );
    }
}
