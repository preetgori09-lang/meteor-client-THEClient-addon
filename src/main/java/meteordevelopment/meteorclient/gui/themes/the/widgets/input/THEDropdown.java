/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets.input;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.input.WMeteorDropdown;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

public class THEDropdown<T> extends WMeteorDropdown<T> implements TheWidget {
    public THEDropdown(T[] values, T value) {
        super(values, value);
    }

    @Override
    protected WDropdownRoot createRootWidget() {
        return new THERoot();
    }

    @Override
    protected WDropdownValue createValueWidget() {
        return new THEValue();
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        TheGuiTheme theme = theme();
        double pad = pad();
        double s = theme.textHeight();

        renderBackground(renderer, this, pressed, mouseOver);

        String text = get().toString();
        double w = theme.textWidth(text);
        renderer.text(text, x + pad + maxValueWidth / 2 - w / 2, y + pad, theme.textColor.get(), false);

        renderer.rotatedQuad(
            x + pad + maxValueWidth + pad, y + pad, s, s, 0,
            GuiRenderer.TRIANGLE, mouseOver || pressed ? theme.accentA() : theme.textSecondaryColor.get()
        );
    }

    private class THERoot extends WDropdownRoot {
        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            TheWidget self = THEDropdown.this;
            self.renderElevated(renderer, x, y, width, height, self.theme().tooltipBorder(), self.theme().tooltipBackground());
        }
    }

    private class THEValue extends WDropdownValue {
        @Override
        protected void onCalculateSize() {
            double pad = pad();

            width = pad + theme.textWidth(value.toString()) + pad;
            height = pad + theme.textHeight() + pad;
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            TheGuiTheme theme = theme();

            boolean selected = get() == value;
            double radius = theme.cornerRadius();

            if (selected || mouseOver) {
                renderer.roundedQuad(x, y, width, height, radius, hoverOverlay(selected ? 1 : 0.6));
            }

            if (selected) {
                renderer.roundedQuad(x, y, theme.scale(2), height, theme.scale(1), theme.accentA(), theme.accentB());
            }

            String text = value.toString();
            renderer.text(
                text, x + width / 2 - theme.textWidth(text) / 2, y + pad(),
                selected ? theme.textColor.get() : theme.textSecondaryColor.get(), false
            );
        }
    }
}
