/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.pressable.WTriangle;
import meteordevelopment.meteorclient.utils.render.color.Color;

/**
 * Collapsible section with a rounded hover bar, an accent tick on the left and an accent title.
 */
public class THESection extends WSection implements TheWidget {
    public THESection(String title, boolean expanded, WWidget headerWidget) {
        super(title, expanded, headerWidget);
    }

    @Override
    protected WHeader createHeader() {
        return new THEHeader(title);
    }

    protected class THEHeader extends WHeader {
        private WTriangle triangle;

        public THEHeader(String title) {
            super(title);
        }

        @Override
        public void init() {
            add(theme.label(title).color(theme().accentColor.get())).expandX().centerY();

            if (headerWidget != null) add(headerWidget);

            triangle = new THEHeaderTriangle();
            triangle.theme = theme;
            triangle.action = this::onClick;

            add(triangle).pad(2).right().centerY();
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            TheGuiTheme theme = theme();
            double radius = theme.cornerRadius();

            triangle.rotation = (1 - animProgress) * -90;

            if (mouseOver) {
                renderer.roundedQuad(x, y, width, height, radius, hoverOverlay(1));
            }

            renderer.roundedQuad(x, y, theme.scale(2), height, theme.scale(1), theme.accentA(), theme.accentB());
        }
    }

    protected static class THEHeaderTriangle extends WTriangle implements TheWidget {
        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            Color color = theme().textSecondaryColor.get();
            renderer.rotatedQuad(x, y, width, height, rotation, GuiRenderer.TRIANGLE, color);
        }
    }
}
