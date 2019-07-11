package fr.adamaq01.osmesa;

import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public enum OSMesaFormat {

    COLOR_INDEX(GL11.GL_COLOR_INDEX, 1),
    RGBA(GL11.GL_RGBA, 4),
    BGRA(0x1, 4),
    ARGB(0x2, 4),
    RGB(GL11.GL_RGB, 3),
    BGR(0x4, 3),
    RGB_565(0x5, 3);

    private int glId;
    private int components;

    OSMesaFormat(int glId, int components) {
        this.glId = glId;
        this.components = components;
    }

    public int getGlId() {
        return glId;
    }

    public int getComponents() {
        return components;
    }

    public static OSMesaFormat fromGlId(int glId) {
        return Arrays.stream(values()).filter(format -> format.glId == glId).findFirst().get();
    }
}
