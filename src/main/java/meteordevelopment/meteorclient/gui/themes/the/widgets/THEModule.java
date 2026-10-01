/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.utils.AlignmentX;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.MathHelper;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

/**
 * A module row: a subtle rounded card at rest, an animated accent gradient pill while enabled.
 */
public class THEModule extends WPressable implements TheWidget {
    private final Module module;
    private final String title;

    private double titleWidth;

    private double hoverProgress;
    private double activeProgress;

    public THEModule(Module module, String title) {
        this.module = module;
        this.title = title;
        this.tooltip = module.description;

        hoverProgress = 0;
        activeProgress = module.isActive() ? 1 : 0;
    }

    @Override
    public double pad() {
        return theme.scale(5);
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        if (titleWidth == 0) titleWidth = theme.textWidth(title);

        width = pad + titleWidth + pad;
        height = pad + theme.textHeight() + pad;
    }

    @Override
    protected void onPressed(int button) {
        if (button == GLFW_MOUSE_BUTTON_LEFT) module.toggle();
        else if (button == GLFW_MOUSE_BUTTON_RIGHT) mc.setScreen(theme.moduleScreen(module));
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        TheGuiTheme theme = theme();
        double pad = pad();

        // Inset card so rows read as separate list entries even when spacing is zero
        double insetX = theme.scale(4);
        double insetY = theme.scale(1);
        double cx = x + insetX;
        double cy = y + insetY;
        double cw = width - insetX * 2;
        double ch = height - insetY * 2;
        double radius = Math.min(theme.cornerRadius(), ch / 2);

        hoverProgress += delta * 9 * ((mouseOver || module.isActive()) ? 1 : -1);
        hoverProgress = MathHelper.clamp(hoverProgress, 0, 1);

        activeProgress += delta * 7 * (module.isActive() ? 1 : -1);
        activeProgress = MathHelper.clamp(activeProgress, 0, 1);

        // Resting card + hover lift
        renderer.roundedQuad(cx, cy, cw, ch, radius, hoverOverlay(0.22 + 0.7 * hoverProgress));

        // Enabled pill
        if (activeProgress > 0) {
            int alpha = (int) (255 * activeProgress);
            Color top = theme.accentA();
            Color bottom = darken(top, 0.55);
            Color edge = withAlpha(theme.accentB(), (int) (170 * activeProgress));
            double border = theme.border();

            renderer.roundedQuad(cx, cy, cw, ch, radius, edge);
            renderer.roundedQuad(
                cx + border, cy + border,
                cw - border * 2, ch - border * 2,
                radius - border,
                withAlpha(top, alpha), withAlpha(bottom, alpha)
            );
        }

        // Title
        double tx = this.x + pad;
        double w = width - pad * 2;

        if (theme.moduleAlignment.get() == AlignmentX.Center) {
            tx += w / 2 - titleWidth / 2;
        } else if (theme.moduleAlignment.get() == AlignmentX.Right) {
            tx += w - titleWidth;
        }

        Color text = activeProgress > 0.5 ? Color.WHITE : theme.textColor.get();
        renderer.text(title, tx, y + pad, text, false);
    }
}
