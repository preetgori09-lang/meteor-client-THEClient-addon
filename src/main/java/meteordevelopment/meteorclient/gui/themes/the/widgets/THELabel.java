/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class THELabel extends WLabel implements TheWidget {
    public THELabel(String text, boolean title) {
        super(text, title);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (text.isEmpty()) return;

        Color color = this.color != null ? this.color : (title ? theme().titleTextColor.get() : theme().textColor.get());

        if (title && theme().shadows.get()) {
            double offset = theme().scale(1);
            renderer.text(text, x + offset, y + offset, theme().shadowColor.get(), true);
        }

        renderer.text(text, x, y, color, title);
    }
}
