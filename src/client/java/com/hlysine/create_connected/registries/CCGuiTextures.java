package com.hlysine.create_connected.registries;

import com.hlysine.create_connected.CreateConnected;
import com.zurrtum.create.catnip.theme.Color;
import com.zurrtum.create.client.catnip.gui.TextureSheetSegment;
import com.zurrtum.create.client.catnip.gui.UIRenderHelper;
import com.zurrtum.create.client.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public enum CCGuiTextures implements ScreenElement, TextureSheetSegment {

    SEQUENCER("sequencer", 256, 205),
    SEQUENCER_INSTRUCTION("sequencer", 0, 16, 237, 22),
    SEQUENCER_DELAY("sequencer", 0, 104, 237, 22),
    SEQUENCER_END("sequencer", 0, 126, 237, 22),
    SEQUENCER_EMPTY("sequencer", 0, 148, 237, 22),
    SEQUENCER_AWAIT("sequencer", 0, 206, 237, 22),
    ;

    public static final int FONT_COLOR = 0xFF575F7A;

    public final Identifier location;
    public final int width;
    public final int height;
    public final int startX;
    public final int startY;

    CCGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    CCGuiTextures(String location, int startX, int startY, int width, int height) {
        this(CreateConnected.MODID, location, startX, startY, width, height);
    }

    CCGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = Identifier.fromNamespaceAndPath(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    @Override
    public Identifier getLocation() {
        return location;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, location, x, y, startX, startY, width, height, 256, 256);
    }

    public void render(GuiGraphicsExtractor graphics, int x, int y, Color c) {
        UIRenderHelper.drawColoredTexture(graphics, bind(), c, x, y, startX, startY, width, height);
    }

    @Override
    public int getStartX() {
        return startX;
    }

    @Override
    public int getStartY() {
        return startY;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
