/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WConfirmedMinus;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class THEConfirmedMinus extends WConfirmedMinus implements TheWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        TheGuiTheme theme = theme();
        double pad = pad();
        double s = theme.scale(3);

        Color accent = theme.minusColor.get();
        Color fg = pressedOnce ? theme.backgroundColor.get(false, false) : accent;
        Color bg = pressedOnce
            ? accent
            : mix(theme.backgroundColor.get(false, false), withAlpha(accent, 150), mouseOver ? 0.28 : 0);

        renderBackground(renderer, this, withAlpha(accent, pressedOnce ? 230 : (mouseOver ? 190 : 100)), bg);

        renderer.roundedQuad(x + pad, y + height / 2 - s / 2, width - pad * 2, s, s / 2, fg);
    }
}
