/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedButton;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class THEConfirmedButton extends WConfirmedButton implements TheWidget {
    public THEConfirmedButton(String text, String confirmText, GuiTexture texture) {
        super(text, confirmText, texture);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        TheGuiTheme theme = theme();
        double pad = pad();

        Color fg;
        Color bg;
        Color border;

        if (pressedOnce) {
            fg = theme.backgroundColor.get(false, false);
            bg = theme.minusColor.get();
            border = withAlpha(theme.minusColor.get(), 230);
        } else {
            double strength = mouseOver ? 0.34 : 0;
            fg = theme.textColor.get();
            bg = strength > 0
                ? mix(theme.backgroundColor.get(false, false), withAlpha(theme.accentA(), 160), strength)
                : theme.backgroundColor.get(false, false);
            border = strength > 0
                ? withAlpha(theme.accentA(), 170)
                : theme.outlineColor.get(false, mouseOver);
        }

        renderBackground(renderer, this, border, bg);

        String text = getText();

        if (text != null) {
            renderer.text(text, x + width / 2 - textWidth / 2, y + pad, fg, false);
        } else {
            double ts = theme.textHeight();
            renderer.quad(x + width / 2 - ts / 2, y + pad, ts, ts, texture, fg);
        }
    }
}
