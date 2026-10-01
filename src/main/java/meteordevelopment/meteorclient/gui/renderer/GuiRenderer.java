/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.renderer;

import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.operations.TextOperation;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.renderer.packer.TexturePacker;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.Utils.getWindowHeight;
import static meteordevelopment.meteorclient.utils.Utils.getWindowWidth;

public class GuiRenderer {
    private static final Color WHITE = new Color(255, 255, 255);

    private static final int ROUNDED_CORNER_SEGMENTS = 6;
    private static final int SHADOW_LAYERS = 6;
    private static final int[] ROUNDED_POINTS = new int[(ROUNDED_CORNER_SEGMENTS + 1) * 4];

    private static final TexturePacker TEXTURE_PACKER = new TexturePacker();
    private static Texture TEXTURE;

    public static GuiTexture CIRCLE;
    public static GuiTexture TRIANGLE;
    public static GuiTexture EDIT;
    public static GuiTexture RESET;
    public static GuiTexture FAVORITE_NO, FAVORITE_YES;
    public static GuiTexture COPY, PASTE;

    public GuiTheme theme;

    private final Renderer2D r = new Renderer2D(false);
    private final Renderer2D rTex = new Renderer2D(true);

    private final Pool<Scissor> scissorPool = new Pool<>(Scissor::new);
    private final Stack<Scissor> scissorStack = new ObjectArrayList<>();

    private final Pool<TextOperation> textPool = new Pool<>(TextOperation::new);
    private final List<TextOperation> texts = new ObjectArrayList<>();

    private final List<Runnable> postTasks = new ObjectArrayList<>();

    public String tooltip, lastTooltip;
    public WWidget tooltipWidget;
    private double tooltipAnimProgress;

    private DrawContext drawContext;

    public static GuiTexture addTexture(Identifier id) {
        return TEXTURE_PACKER.add(id);
    }

    @PostInit
    public static void init() {
        CIRCLE = addTexture(MeteorClient.identifier("textures/icons/gui/circle.png"));
        TRIANGLE = addTexture(MeteorClient.identifier("textures/icons/gui/triangle.png"));
        EDIT = addTexture(MeteorClient.identifier("textures/icons/gui/edit.png"));
        RESET = addTexture(MeteorClient.identifier("textures/icons/gui/reset.png"));
        FAVORITE_NO = addTexture(MeteorClient.identifier("textures/icons/gui/favorite_no.png"));
        FAVORITE_YES = addTexture(MeteorClient.identifier("textures/icons/gui/favorite_yes.png"));

        COPY = addTexture(MeteorClient.identifier("textures/icons/gui/copy.png"));
        PASTE = addTexture(MeteorClient.identifier("textures/icons/gui/paste.png"));

        TEXTURE = TEXTURE_PACKER.pack();
    }

    public void begin(DrawContext drawContext) {
        this.drawContext = drawContext;
        this.drawContext.createNewRootLayer();

        var matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        matrices.scale(1.0f / mc.getWindow().getScaleFactor());

        scissorStart(0, 0, getWindowWidth(), getWindowHeight());
    }

    public void end() {
        scissorEnd();

        for (Runnable task : postTasks) task.run();
        postTasks.clear();

        drawContext.getMatrices().popMatrix();
        drawContext.createNewRootLayer();
    }

    public void beginRender() {
        r.begin();
        rTex.begin();
    }

    public void endRender() {
        endRender(null);
    }

    public void endRender(Scissor scissor) {
        if (scissor != null) scissor.push();

        r.end();
        rTex.end();

        r.render();
        rTex.render("u_Texture", TEXTURE.getGlTextureView(), TEXTURE.getSampler());

        // Normal text
        theme.textRenderer().begin(theme.scale(1));
        for (TextOperation text : texts) {
            if (!text.title) text.run(textPool);
        }
        theme.textRenderer().end();

        // Title text
        theme.textRenderer().begin(theme.scale(1.25));
        for (TextOperation text : texts) {
            if (text.title) text.run(textPool);
        }
        theme.textRenderer().end();

        texts.clear();

        if (scissor != null) scissor.pop();
    }

    public void scissorStart(double x, double y, double width, double height) {
        if (!scissorStack.isEmpty()) {
            Scissor parent = scissorStack.top();

            if (x < parent.x) x = parent.x;
            else if (x + width > parent.x + parent.width) width -= (x + width) - (parent.x + parent.width);

            if (y < parent.y) y = parent.y;
            else if (y + height > parent.y + parent.height) height -= (y + height) - (parent.y + parent.height);

            endRender(parent);
        }

        scissorStack.push(scissorPool.get().set(x, y, width, height));
        drawContext.enableScissor((int) x, (int) y, (int) (x + width), (int) (y + height));

        beginRender();
    }

    public void scissorEnd() {
        Scissor scissor = scissorStack.pop();

        endRender(scissor);

        scissor.push();
        for (Runnable task : scissor.postTasks) task.run();
        scissor.pop();

        drawContext.disableScissor();
        if (!scissorStack.isEmpty()) beginRender();

        scissorPool.free(scissor);
    }

    public boolean renderTooltip(DrawContext drawContext, double mouseX, double mouseY, double delta) {
        tooltipAnimProgress += (tooltip != null ? 1 : -1) * delta * 14;
        tooltipAnimProgress = MathHelper.clamp(tooltipAnimProgress, 0, 1);

        boolean toReturn = false;

        if (tooltipAnimProgress > 0) {
            if (tooltip != null && !tooltip.equals(lastTooltip)) {
                tooltipWidget = theme.tooltip(tooltip);
                tooltipWidget.init();
            }

            double deltaX = -tooltipWidget.x + mouseX + 12;
            double deltaY = -tooltipWidget.y + mouseY + 12;

            if (mouseX + 12 + tooltipWidget.width > getWindowWidth()) deltaX = -tooltipWidget.x + getWindowWidth() - tooltipWidget.width;
            if (mouseY + 12 + tooltipWidget.height > getWindowHeight()) deltaY = -tooltipWidget.y + getWindowHeight() - tooltipWidget.height;

            tooltipWidget.move(deltaX, deltaY);

            setAlpha(tooltipAnimProgress);

            begin(drawContext);
            tooltipWidget.render(this, mouseX, mouseY, delta);
            end();

            setAlpha(1);

            lastTooltip = tooltip;
            toReturn = true;
        }

        tooltip = null;
        return toReturn;
    }

    public void setAlpha(double a) {
        r.setAlpha(a);
        rTex.setAlpha(a);

        theme.textRenderer().setAlpha(a);
    }

    public void tooltip(String text) {
        tooltip = text;
    }

    public void quad(double x, double y, double width, double height, Color cTopLeft, Color cTopRight, Color cBottomRight, Color cBottomLeft) {
        r.quad(x, y, width, height, cTopLeft, cTopRight, cBottomRight, cBottomLeft);
    }
    public void quad(double x, double y, double width, double height, Color colorLeft, Color colorRight) {
        quad(x, y, width, height, colorLeft, colorRight, colorRight, colorLeft);
    }
    public void quad(double x, double y, double width, double height, Color color) {
        quad(x, y, width, height, color, color);
    }
    public void quad(WWidget widget, Color color) {
        quad(widget.x, widget.y, widget.width, widget.height, color);
    }
    public void quad(double x, double y, double width, double height, GuiTexture texture, Color color) {
        rTex.texQuad(x, y, width, height, texture.get(width, height), color);
    }

    // Rounded quads

    /**
     * Fills a rounded rectangle with a single color. Falls back to a plain quad when the radius is negligible.
     */
    public void roundedQuad(double x, double y, double width, double height, double radius, Color color) {
        roundedQuad(x, y, width, height, radius, color, color, color, color);
    }

    /**
     * Fills a rounded rectangle with a horizontal gradient.
     */
    public void roundedQuad(double x, double y, double width, double height, double radius, Color colorLeft, Color colorRight) {
        roundedQuad(x, y, width, height, radius, colorLeft, colorRight, colorRight, colorLeft);
    }

    /**
     * Fills a rounded rectangle with a bilinear gradient defined by the four corner colors.
     */
    public void roundedQuad(double x, double y, double width, double height, double radius, Color cTopLeft, Color cTopRight, Color cBottomRight, Color cBottomLeft) {
        if (width <= 0 || height <= 0) return;

        double rad = Math.min(radius, Math.min(width, height) / 2);
        if (rad <= 0.5) {
            quad(x, y, width, height, cTopLeft, cTopRight, cBottomRight, cBottomLeft);
            return;
        }

        int segmentsPerCorner = ROUNDED_CORNER_SEGMENTS;
        int pointCount = (segmentsPerCorner + 1) * 4;
        int vertexCount = pointCount + 1; // + center
        int indexCount = pointCount * 3;

        r.triangles.ensureCapacity(vertexCount, indexCount);

        double centerX = x + width / 2;
        double centerY = y + height / 2;

        int center = r.triangles
            .vec2(centerX, centerY)
            .colorPacked(gradient(cTopLeft, cTopRight, cBottomRight, cBottomLeft, 0.5, 0.5))
            .next();

        int[] points = ROUNDED_POINTS;
        int i = 0;

        for (int corner = 0; corner < 4; corner++) {
            double cornerX, cornerY, startAngle;

            switch (corner) {
                case 0 -> { cornerX = x + rad; cornerY = y + rad; startAngle = 270; }            // top left
                case 1 -> { cornerX = x + rad; cornerY = y + height - rad; startAngle = 180; }    // bottom left
                case 2 -> { cornerX = x + width - rad; cornerY = y + height - rad; startAngle = 90; } // bottom right
                default -> { cornerX = x + width - rad; cornerY = y + rad; startAngle = 0; }      // top right
            }

            for (int j = 0; j <= segmentsPerCorner; j++) {
                double angle = Math.toRadians(startAngle - 90.0 * j / segmentsPerCorner);
                double px = cornerX + Math.cos(angle) * rad;
                double py = cornerY + Math.sin(angle) * rad;

                points[i++] = r.triangles
                    .vec2(px, py)
                    .colorPacked(gradient(cTopLeft, cTopRight, cBottomRight, cBottomLeft, (px - x) / width, (py - y) / height))
                    .next();
            }
        }

        for (int j = 0; j < pointCount; j++) {
            r.triangles.triangle(center, points[j], points[(j + 1) % pointCount]);
        }
    }

    /**
     * Draws a soft drop shadow around a rounded rectangle by stacking faint layers from the outside in.
     */
    public void shadow(double x, double y, double width, double height, double radius, double spread, Color color) {
        if (spread <= 0 || width <= 0 || height <= 0 || color.a <= 0) return;

        double targetAlpha = Math.min(color.a / 255.0, 0.99);
        int layerAlpha = (int) (255 * (1 - Math.pow(1 - targetAlpha, 1.0 / SHADOW_LAYERS)));
        if (layerAlpha <= 0) return;

        Color layer = new Color(color.r, color.g, color.b, layerAlpha);

        for (int i = 0; i < SHADOW_LAYERS; i++) {
            double t = i / (double) (SHADOW_LAYERS - 1); // 0 = outermost, 1 = flush with the shape
            double grow = spread * (1 - t);

            roundedQuad(x - grow, y - grow, width + grow * 2, height + grow * 2, radius + grow, layer);
        }
    }

    private static int gradient(Color cTL, Color cTR, Color cBR, Color cBL, double u, double v) {
        if (u < 0) u = 0;
        else if (u > 1) u = 1;
        if (v < 0) v = 0;
        else if (v > 1) v = 1;

        int r = lerp(lerp(cTL.r, cTR.r, u), lerp(cBL.r, cBR.r, u), v);
        int g = lerp(lerp(cTL.g, cTR.g, u), lerp(cBL.g, cBR.g, u), v);
        int b = lerp(lerp(cTL.b, cBR.b, u), lerp(cBL.b, cBR.b, u), v);
        int a = lerp(lerp(cTL.a, cTR.a, u), lerp(cBL.a, cBR.a, u), v);

        return (r << 24) | (g << 16) | (b << 8) | a;
    }

    private static int lerp(int a, int b, double t) {
        return (int) Math.round(a + (b - a) * t);
    }

    public void rotatedQuad(double x, double y, double width, double height, double rotation, GuiTexture texture, Color color) {
        rTex.texQuad(x, y, width, height, rotation, texture.get(width, height), color);
    }

    public void triangle(double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
        r.triangle(x1, y1, x2, y2, x3, y3 ,color);
    }

    public void text(String text, double x, double y, Color color, boolean title) {
        texts.add(getOp(textPool, x, y, color).set(text, theme.textRenderer(), title));
    }

    public void texture(double x, double y, double width, double height, double rotation, Texture texture) {
        post(() -> {
            rTex.begin();
            rTex.texQuad(x, y, width, height, rotation, 0, 0, 1, 1, WHITE);
            rTex.end();

            rTex.render(texture.getGlTextureView(), texture.getSampler());
        });
    }

    public void post(Runnable task) {
        scissorStack.top().postTasks.add(task);
    }

    public void item(ItemStack itemStack, int x, int y, float scale, boolean overlay) {
        RenderUtils.drawItem(drawContext, itemStack, x, y, scale, overlay, null, false);
    }

    public void absolutePost(Runnable task) {
        postTasks.add(task);
    }

    private <T extends GuiRenderOperation<T>> T getOp(Pool<T> pool, double x, double y, Color color) {
        T op = pool.get();
        op.set(x, y, color);
        return op;
    }
}
