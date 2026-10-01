/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.utils.render.color.Color;

/**
 * A floating glass panel: drop shadow, hairline border, slightly lighter header band
 * and an accent hairline separating the header from the body.
 */
public class THEWindow extends WWindow implements TheWidget {
    public THEWindow(WWidget icon, String title) {
        super(icon, title);
    }

    @Override
    protected WHeader header(WWidget icon) {
        return new THEHeader(icon);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        TheGuiTheme theme = theme();

        double panelHeight = (height - header.height) * animProgress + header.height;
        if (panelHeight <= 1) return;

        double radius = theme.cornerRadius();
        double border = theme.border();

        if (theme.shadows.get()) {
            renderer.shadow(x, y, width, panelHeight, radius, theme.scale(14), theme.shadowColor.get());
        }

        renderer.roundedQuad(x, y, width, panelHeight, radius, theme.outlineColor.get());

        double innerX = x + border;
        double innerY = y + border;
        double innerW = width - border * 2;
        double innerH = panelHeight - border * 2;
        if (innerW <= 1 || innerH <= 1) return;

        double innerRadius = radius - border;
        renderer.roundedQuad(innerX, innerY, innerW, innerH, innerRadius, theme.backgroundColor.get());

        // Header band
        double headerHeight = header.height - border;
        if (headerHeight <= 1) return;

        renderer.roundedQuad(innerX, innerY, innerW, headerHeight, innerRadius, theme.headerColor.get());

        double bottomRound = Math.max(0, innerRadius);
        if (bottomRound > 0) {
            renderer.quad(innerX, innerY + headerHeight - bottomRound, innerW, bottomRound, theme.headerColor.get());
        }

        if (header.mouseOver) {
            Color hover = withAlpha(Color.WHITE, 16);
            renderer.roundedQuad(innerX, innerY, innerW, headerHeight, innerRadius, hover);
            if (bottomRound > 0) renderer.quad(innerX, innerY + headerHeight - bottomRound, innerW, bottomRound, hover);
        }

        // Accent hairline under the header
        double lineY = innerY + headerHeight;
        double lineW = innerW - bottomRound * 2;
        if (lineW > 0) {
            renderer.quad(innerX + bottomRound, lineY, lineW, theme.scale(1), theme.accentA(), theme.accentB());
        }
    }

    private class THEHeader extends WHeader {
        public THEHeader(WWidget icon) {
            super(icon);
        }
    }
}
