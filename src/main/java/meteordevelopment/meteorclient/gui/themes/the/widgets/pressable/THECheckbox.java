/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets.pressable;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.MathHelper;

public class THECheckbox extends WCheckbox implements TheWidget {
    private double animProgress;

    public THECheckbox(boolean checked) {
        super(checked);
        animProgress = checked ? 1 : 0;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        TheGuiTheme theme = theme();
        double border = theme.border();
        double radius = Math.min(theme.cornerRadius(), height / 2);

        animProgress += (checked ? 1 : -1) * delta * 14;
        animProgress = MathHelper.clamp(animProgress, 0, 1);

        Color outline = theme.outlineColor.get(pressed, mouseOver);
        Color fill = theme.backgroundColor.get(false, mouseOver);

        if (checked) outline = withAlpha(theme.checkboxColor.get(), (int) (110 + 145 * animProgress));

        renderBackground(renderer, this, outline, fill);

        if (animProgress > 0) {
            renderer.roundedQuad(
                x + border, y + border,
                width - border * 2, height - border * 2,
                radius - border,
                withAlpha(theme.checkboxColor.get(), (int) (255 * animProgress))
            );
        }

        if (animProgress > 0.45) {
            double p = (animProgress - 0.45) / 0.55;
            Color tick = withAlpha(Color.WHITE, (int) (255 * p));
            double thickness = Math.max(theme.scale(1.5), width * 0.16);

            stroke(
                renderer,
                x + width * 0.26, y + height * 0.52,
                x + width * 0.44, y + height * 0.70,
                thickness, tick
            );
            stroke(
                renderer,
                x + width * 0.44, y + height * 0.70,
                x + width * 0.76, y + height * 0.30,
                thickness, tick
            );
        }
    }
}
