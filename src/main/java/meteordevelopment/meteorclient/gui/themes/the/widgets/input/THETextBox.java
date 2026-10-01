/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets.input;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.widgets.input.WMeteorTextBox;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.MathHelper;

public class THETextBox extends WMeteorTextBox implements TheWidget {
    private boolean theCursorVisible;
    private double theCursorTimer;
    private double theAnimProgress;
    private double focusProgress;

    public THETextBox(String text, String placeholder, CharFilter filter, Class<? extends Renderer> renderer) {
        super(text, placeholder, filter, renderer);
    }

    @Override
    protected WContainer createCompletionsRootWidget() {
        return new WVerticalList() {
            @Override
            protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
                TheWidget self = THETextBox.this;
                self.renderElevated(
                    renderer, x, y, width, height,
                    self.theme().tooltipBorder(), self.theme().tooltipBackground()
                );
            }
        };
    }

    @Override
    protected void onCursorChanged() {
        theCursorVisible = true;
        theCursorTimer = 0;
        theAnimProgress = focused ? 1.0 : 0.0;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        TheGuiTheme theme = theme();

        if (theCursorTimer >= 1) {
            theCursorVisible = !theCursorVisible;
            theCursorTimer = 0;
        } else {
            theCursorTimer += delta * 1.75;
        }

        focusProgress += delta * 10 * (focused ? 1 : -1);
        focusProgress = MathHelper.clamp(focusProgress, 0, 1);

        Color border = focusProgress > 0
            ? mix(theme.outlineColor.get(false, mouseOver), theme.accentA(), focusProgress)
            : theme.outlineColor.get(false, mouseOver);

        Color fill = mouseOver
            ? theme.backgroundColor.get(false, true)
            : theme.backgroundColor.get(false, false);

        renderBackground(renderer, this, border, fill);

        double pad = pad();
        double overflowWidth = getOverflowWidthForRender();

        renderer.scissorStart(x + pad, y + pad, width - pad * 2, height - pad * 2);

        if (!text.isEmpty()) {
            this.renderer.render(renderer, x + pad - overflowWidth, y + pad, text, theme.textColor.get());
        } else if (placeholder != null) {
            this.renderer.render(renderer, x + pad - overflowWidth, y + pad, placeholder, theme.placeholderColor.get());
        }

        if (focused && (cursor != selectionStart || cursor != selectionEnd)) {
            double selStart = x + pad + getTextWidth(selectionStart) - overflowWidth;
            double selEnd = x + pad + getTextWidth(selectionEnd) - overflowWidth;

            renderer.roundedQuad(selStart, y + pad, selEnd - selStart, theme.textHeight(), theme.scale(2), theme.textHighlightColor.get());
        }

        theAnimProgress += delta * 10 * (focused && theCursorVisible ? 1 : -1);
        theAnimProgress = MathHelper.clamp(theAnimProgress, 0, 1);

        if ((focused && theCursorVisible) || theAnimProgress > 0) {
            renderer.setAlpha(theAnimProgress);
            renderer.quad(
                x + pad + getTextWidth(cursor) - overflowWidth, y + pad,
                Math.max(1, theme.scale(1.5)), theme.textHeight(),
                theme.accentA()
            );
            renderer.setAlpha(1);
        }

        renderer.scissorEnd();
    }
}
