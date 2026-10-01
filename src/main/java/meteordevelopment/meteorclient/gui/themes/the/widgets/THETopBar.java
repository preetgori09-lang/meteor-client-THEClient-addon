/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the.widgets;

import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.gui.themes.the.TheGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.TheWidget;
import meteordevelopment.meteorclient.gui.widgets.WTopBar;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPressable;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.screen.Screen;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static org.lwjgl.glfw.GLFW.*;

public class THETopBar extends WTopBar implements TheWidget {
    @Override
    public void init() {
        clear();
        for (Tab tab : Tabs.get()) add(new THEButton(tab));
    }

    @Override
    protected Color getButtonColor(boolean pressed, boolean hovered) {
        return theme().backgroundColor.get(pressed, hovered);
    }

    @Override
    protected Color getNameColor() {
        return theme().textSecondaryColor.get();
    }

    protected class THEButton extends WPressable {
        private final Tab tab;

        public THEButton(Tab tab) {
            this.tab = tab;
        }

        @Override
        protected void onCalculateSize() {
            double pad = pad();

            width = pad + theme.textWidth(tab.name) + pad;
            height = pad + theme.textHeight() + pad;
        }

        @Override
        protected void onPressed(int button) {
            Screen screen = mc.currentScreen;

            if (!(screen instanceof TabScreen tabScreen) || tabScreen.tab != tab) {
                double mouseX = mc.mouse.getX();
                double mouseY = mc.mouse.getY();

                tab.openScreen(theme);
                glfwSetCursorPos(mc.getWindow().getHandle(), mouseX, mouseY);
            }
        }

        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            TheGuiTheme theme = theme();
            double pad = pad();
            double radius = height / 2;

            boolean active = mc.currentScreen instanceof TabScreen tabScreen && tabScreen.tab == tab;
            double strength = pressed ? 0.6 : (mouseOver ? 0.32 : 0);

            if (active) {
                renderer.roundedQuad(x, y, width, height, radius, darken(theme.accentA(), 0.65), theme.accentA());
            } else if (strength > 0) {
                renderer.roundedQuad(
                    x, y, width, height, radius,
                    mix(getButtonColor(false, false), withAlpha(theme.accentA(), 150), strength)
                );
            } else {
                renderer.roundedQuad(x, y, width, height, radius, getButtonColor(false, false));
            }

            renderer.text(
                tab.name, x + pad, y + pad,
                active ? Color.WHITE : getNameColor(), false
            );
        }
    }
}
