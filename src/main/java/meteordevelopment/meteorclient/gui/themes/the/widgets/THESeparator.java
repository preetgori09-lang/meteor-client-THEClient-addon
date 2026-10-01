/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.WHorizontalSeparator;

public class THESeparator extends WHorizontalSeparator implements TheWidget {
    public THESeparator(String text) {
        super(text);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (text == null) renderWithoutText(renderer);
        else renderWithText(renderer);
    }

    private void renderWithoutText(GuiRenderer renderer) {
        TheGuiTheme theme = theme();
        double h = theme.scale(1);
        double y = this.y + height / 2 - h / 2;

        renderer.quad(x, y, width, h, theme.accentA(), theme.accentB());
    }

    private void renderWithText(GuiRenderer renderer) {
        TheGuiTheme theme = theme();
        double gap = theme.scale(2);
        double h = theme.scale(1);

        double textStart = Math.round(width / 2.0 - textWidth / 2.0 - gap);
        double textEnd = gap + textStart + textWidth + gap;
        double offsetY = Math.round(height / 2.0);

        renderer.quad(x, y + offsetY, textStart, h, theme.separatorEdges.get(), theme.separatorCenter.get());
        renderer.text(text, x + textStart + gap, y, theme.separatorText.get(), false);
        renderer.quad(x + textEnd, y + offsetY, width - textEnd, h, theme.separatorCenter.get(), theme.separatorEdges.get());
    }
}
