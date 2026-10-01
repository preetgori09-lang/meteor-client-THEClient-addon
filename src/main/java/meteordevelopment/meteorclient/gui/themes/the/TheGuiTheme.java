/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.themes.the;

import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.gui.themes.the.widgets.*;
import meteordevelopment.meteorclient.gui.themes.the.widgets.input.THEDropdown;
import meteordevelopment.meteorclient.gui.themes.the.widgets.input.THESlider;
import meteordevelopment.meteorclient.gui.themes.the.widgets.input.THETextBox;
import meteordevelopment.meteorclient.gui.themes.the.widgets.pressable.*;
import meteordevelopment.meteorclient.gui.utils.CharFilter;
import meteordevelopment.meteorclient.gui.widgets.*;
import meteordevelopment.meteorclient.gui.widgets.containers.WSection;
import meteordevelopment.meteorclient.gui.widgets.containers.WView;
import meteordevelopment.meteorclient.gui.widgets.containers.WWindow;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.input.WSlider;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.*;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

/**
 * "THE" - the default GUI theme of THE Client.
 * <p>
 * It keeps every technical part of the Meteor theme (settings, screens, widget tree) but replaces the
 * flat square look with rounded glassy panels, hairline borders, soft drop shadows and an accent gradient.
 */
public class TheGuiTheme extends MeteorGuiTheme {
    private final SettingGroup sgThe = settings.createGroup("THE");

    // Shape

    public final Setting<Double> cornerRadius = sgThe.add(new DoubleSetting.Builder()
        .name("corner-radius")
        .description("Radius of the rounded corners, in pixels.")
        .defaultValue(7.0)
        .range(0, 20)
        .sliderRange(0, 20)
        .decimalPlaces(1)
        .build()
    );

    public final Setting<Boolean> shadows = sgThe.add(new BoolSetting.Builder()
        .name("shadows")
        .description("Draws soft drop shadows behind windows and popups.")
        .defaultValue(true)
        .build()
    );

    // Accent

    public final Setting<SettingColor> accentColor2 = sgThe.add(new ColorSetting.Builder()
        .name("accent-2")
        .description("End color of the accent gradient.")
        .defaultValue(new SettingColor(34, 211, 238))
        .build()
    );

    public final Setting<SettingColor> headerColor = sgThe.add(new ColorSetting.Builder()
        .name("header")
        .description("Background color of window headers.")
        .defaultValue(new SettingColor(26, 26, 38, 255))
        .build()
    );

    public final Setting<SettingColor> shadowColor = sgThe.add(new ColorSetting.Builder()
        .name("shadow")
        .description("Color of drop shadows.")
        .defaultValue(new SettingColor(0, 0, 0, 110))
        .build()
    );

    public TheGuiTheme() {
        super("THE");

        applyPalette();
    }

    /** The default look of THE Client: deep blue-black glass, violet -> cyan accent, soft borders. */
    private void applyPalette() {
        // Accent
        accentColor.setDefaultValue(new SettingColor(124, 92, 255));
        checkboxColor.setDefaultValue(new SettingColor(124, 92, 255));
        plusColor.setDefaultValue(new SettingColor(72, 224, 160));
        minusColor.setDefaultValue(new SettingColor(255, 100, 118));
        favoriteColor.setDefaultValue(new SettingColor(255, 198, 66));

        // Text
        textColor.setDefaultValue(new SettingColor(236, 237, 245));
        textSecondaryColor.setDefaultValue(new SettingColor(141, 144, 164));
        textHighlightColor.setDefaultValue(new SettingColor(124, 92, 255, 110));
        titleTextColor.setDefaultValue(new SettingColor(255, 255, 255));
        loggedInColor.setDefaultValue(new SettingColor(72, 224, 160));
        placeholderColor.setDefaultValue(new SettingColor(255, 255, 255, 48));

        // Background
        backgroundColor.setDefaults(
            new SettingColor(16, 16, 24, 236),
            new SettingColor(26, 26, 38, 236),
            new SettingColor(38, 38, 54, 236)
        );
        moduleBackground.setDefaultValue(new SettingColor(40, 40, 58, 255));

        // Outline
        outlineColor.setDefaults(
            new SettingColor(255, 255, 255, 22),
            new SettingColor(255, 255, 255, 46),
            new SettingColor(124, 92, 255, 140)
        );

        // Separator
        separatorText.setDefaultValue(new SettingColor(186, 176, 255));
        separatorCenter.setDefaultValue(new SettingColor(34, 211, 238, 190));
        separatorEdges.setDefaultValue(new SettingColor(124, 92, 255, 0));

        // Scrollbar
        scrollbarColor.setDefaults(
            new SettingColor(255, 255, 255, 34),
            new SettingColor(255, 255, 255, 66),
            new SettingColor(124, 92, 255, 180)
        );

        // Slider
        sliderHandle.setDefaults(
            new SettingColor(255, 255, 255, 235),
            new SettingColor(255, 255, 255, 255),
            new SettingColor(255, 255, 255, 255)
        );
        sliderLeft.setDefaultValue(new SettingColor(124, 92, 255));
        sliderRight.setDefaultValue(new SettingColor(255, 255, 255, 26));
    }

    // Shape helpers

    /** Global corner radius in GUI units. */
    public double cornerRadius() {
        return scale(cornerRadius.get());
    }

    /** Thickness of the hairline border around a panel. */
    public double border() {
        return scale(1);
    }

    /** Accent gradient start. */
    public Color accentA() {
        return accentColor.get();
    }

    /** Accent gradient end. */
    public Color accentB() {
        return accentColor2.get();
    }

    /** Slightly more opaque panel color, used for floating overlays like tooltips. */
    public Color tooltipBackground() {
        Color c = backgroundColor.get();
        return new Color(c.r, c.g, c.b, Math.min(255, c.a + 24));
    }

    /** Slightly brighter border, used for floating overlays like tooltips. */
    public Color tooltipBorder() {
        Color c = outlineColor.get();
        return new Color(c.r, c.g, c.b, Math.min(255, c.a + 46));
    }

    // Widgets

    @Override
    public WWindow window(WWidget icon, String title) {
        return w(new THEWindow(icon, title));
    }

    @Override
    public WLabel label(String text, boolean title, double maxWidth) {
        if (maxWidth == 0 && !text.contains("\n")) return w(new THELabel(text, title));
        return w(new THEMultiLabel(text, title, maxWidth));
    }

    @Override
    public WHorizontalSeparator horizontalSeparator(String text) {
        return w(new THESeparator(text));
    }

    @Override
    protected WButton button(String text, GuiTexture texture) {
        return w(new THEButton(text, texture));
    }

    @Override
    protected WConfirmedButton confirmedButton(String text, String confirmText, GuiTexture texture) {
        return w(new THEConfirmedButton(text, confirmText, texture));
    }

    @Override
    public WMinus minus() {
        return w(new THEMinus());
    }

    @Override
    public WConfirmedMinus confirmedMinus() {
        return w(new THEConfirmedMinus());
    }

    @Override
    public WPlus plus() {
        return w(new THEPlus());
    }

    @Override
    public WCheckbox checkbox(boolean checked) {
        return w(new THECheckbox(checked));
    }

    @Override
    public WSlider slider(double value, double min, double max) {
        return w(new THESlider(value, min, max));
    }

    @Override
    public WTextBox textBox(String text, String placeholder, CharFilter filter, Class<? extends WTextBox.Renderer> renderer) {
        return w(new THETextBox(text, placeholder, filter, renderer));
    }

    @Override
    public <T> WDropdown<T> dropdown(T[] values, T value) {
        return w(new THEDropdown<>(values, value));
    }

    @Override
    public WTriangle triangle() {
        return w(new THETriangle());
    }

    @Override
    public WTooltip tooltip(String text) {
        return w(new THETooltip(text));
    }

    @Override
    public WView view() {
        return w(new THEView());
    }

    @Override
    public WSection section(String title, boolean expanded, WWidget headerWidget) {
        return w(new THESection(title, expanded, headerWidget));
    }

    @Override
    public WAccount account(WidgetScreen screen, Account<?> account) {
        return w(new THEAccount(screen, account));
    }

    @Override
    public WWidget module(Module module, String title) {
        return w(new THEModule(module, title));
    }

    @Override
    public WQuad quad(Color color) {
        return w(new THEQuad(color));
    }

    @Override
    public WTopBar topBar() {
        return w(new THETopBar());
    }

    @Override
    public WVerticalSeparator verticalSeparator() {
        return w(new THEVerticalSeparator());
    }

    // Other

    @Override
    public TextRenderer textRenderer() {
        return TextRenderer.get();
    }
}
