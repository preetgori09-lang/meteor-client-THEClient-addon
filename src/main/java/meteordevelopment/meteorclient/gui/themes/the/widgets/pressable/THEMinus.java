/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class THEMinus extends WMinus implements TheWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        TheGuiTheme theme = theme();
        double pad = pad();
        double s = theme.scale(3);

        Color color = theme.minusColor.get();

        renderBackground(
            renderer, this,
            withAlpha(color, mouseOver || pressed ? 220 : 110),
            mix(theme.backgroundColor.get(false, false), withAlpha(color, 150), pressed ? 0.5 : (mouseOver ? 0.28 : 0))
        );

        renderer.roundedQuad(x + pad, y + height / 2 - s / 2, width - pad * 2, s, s / 2, color);
    }
}
