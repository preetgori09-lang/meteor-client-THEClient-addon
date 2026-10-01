/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets.input;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class THESlider extends WSlider implements TheWidget {
    public THESlider(double value, double min, double max) {
        super(value, min, max);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        TheGuiTheme theme = theme();

        double handle = handleSize();
        double trackHeight = theme.scale(4);
        double trackX = x + handle / 2;
        double trackWidth = Math.max(0, width - handle);
        double trackY = y + height / 2 - trackHeight / 2;
        double valueWidth = valueWidth();

        // Track
        renderer.roundedQuad(trackX, trackY, trackWidth, trackHeight, trackHeight / 2, theme.sliderRight.get());

        // Filled part
        double filled = Math.min(valueWidth + handle / 2, trackWidth);
        if (filled > trackHeight / 2) {
            renderer.roundedQuad(trackX, trackY, filled, trackHeight, trackHeight / 2, theme.sliderLeft.get(), theme.accentB());
        }

        // Glow behind the handle while interacting
        if (dragging || handleMouseOver) {
            double glow = handle + theme.scale(6);
            renderer.roundedQuad(
                x + valueWidth - theme.scale(3), y - theme.scale(3), glow, glow, glow / 2,
                withAlpha(theme.accentA(), 60)
            );
        }

        // Handle
        Color handleColor = theme.sliderHandle.get(dragging, handleMouseOver);
        renderer.roundedQuad(x + valueWidth, y, handle, handle, handle / 2, handleColor);
    }
}
