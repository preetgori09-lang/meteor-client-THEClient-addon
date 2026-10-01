/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.WTooltip;

public class THETooltip extends WTooltip implements TheWidget {
    public THETooltip(String text) {
        super(text);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderElevated(renderer, x, y, width, height, theme().tooltipBorder(), theme().tooltipBackground());
    }
}
