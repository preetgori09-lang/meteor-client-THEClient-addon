/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorWidget;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.utils.render.color.Color;

/**
 * Shared rendering helpers for every widget of the {@link TheGuiTheme}.
 * <p>
 * Everything is built out of rounded rectangles, hairline borders and soft drop shadows so the
 * whole GUI reads as one consistent, glassy surface instead of a pile of flat squares.
 */
public interface TheWidget extends MeteorWidget {
    @Override
    default TheGuiTheme theme() {
        return (TheGuiTheme) getTheme();
    }

    // Backgrounds

    /** Rounded, hairline-bordered panel. */
    default void renderBackground(GuiRenderer renderer, WWidget widget, Color borderColor, Color fillColor) {
        TheGuiTheme theme = theme();
        double radius = theme.cornerRadius();
        double border = theme.border();

        renderer.roundedQuad(widget.x, widget.y, widget.width, widget.height, radius, borderColor);
        renderer.roundedQuad(
            widget.x + border, widget.y + border,
            widget.width - border * 2, widget.height - border * 2,
            radius - border, fillColor
        );
    }

    /** Rounded panel driven by the theme's three state colors (normal / hovered / pressed). */
    default void renderBackground(GuiRenderer renderer, WWidget widget, boolean pressed, boolean mouseOver) {
        TheGuiTheme theme = theme();
        renderBackground(renderer, widget, theme.outlineColor.get(pressed, mouseOver), theme.backgroundColor.get(pressed, mouseOver));
    }

    /** Rounded panel with a soft drop shadow, used for floating surfaces (windows, popups, tooltips). */
    default void renderElevated(GuiRenderer renderer, double x, double y, double width, double height, Color borderColor, Color fillColor) {
        TheGuiTheme theme = theme();
        double radius = theme.cornerRadius();
        double border = theme.border();

        if (theme.shadows.get()) {
            renderer.shadow(x, y, width, height, radius, theme.scale(14), theme.shadowColor.get());
        }

        renderer.roundedQuad(x, y, width, height, radius, borderColor);
        renderer.roundedQuad(x + border, y + border, width - border * 2, height - border * 2, radius - border, fillColor);
    }

    // Color helpers

    default Color withAlpha(Color color, int alpha) {
        return new Color(color.r, color.g, color.b, Math.max(0, Math.min(255, alpha)));
    }

    default Color darken(Color color, double factor) {
        return new Color((int) (color.r * factor), (int) (color.g * factor), (int) (color.b * factor), color.a);
    }

    /** Overlay used to brighten a surface on hover. */
    default Color hoverOverlay(double progress) {
        return withAlpha(Color.WHITE, (int) (26 * Math.max(0, Math.min(1, progress))));
    }

    /** Linear blend between two colors. */
    default Color mix(Color from, Color to, double t) {
        double p = Math.max(0, Math.min(1, t));

        return new Color(
            (int) (from.r + (to.r - from.r) * p),
            (int) (from.g + (to.g - from.g) * p),
            (int) (from.b + (to.b - from.b) * p),
            (int) (from.a + (to.a - from.a) * p)
        );
    }

    // Shapes

    /**
     * Draws a rounded stroke between two points. Used for the checkbox tick.
     */
    default void stroke(GuiRenderer renderer, double x1, double y1, double x2, double y2, double thickness, Color color) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.hypot(dx, dy);

        if (length < 0.0001) return;

        double nx = -dy / length * thickness / 2;
        double ny = dx / length * thickness / 2;

        renderer.triangle(x1 - nx, y1 - ny, x1 + nx, y1 + ny, x2 + nx, y2 + ny, color);
        renderer.triangle(x1 - nx, y1 - ny, x2 + nx, y2 + ny, x2 - nx, y2 - ny, color);
    }
}
