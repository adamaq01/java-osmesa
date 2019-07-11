package fr.adamaq01.osmesa;

import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.SharedLibrary;
import org.lwjgl.system.dyncall.DynCall;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

public class OSMesa {

    protected static final SharedLibrary OSMESA;
    protected static final HashMap<Long, OSMesaContext> CONTEXTS;

    static {
        OSMESA = Library.loadNative("OSMesa");
        CONTEXTS = new HashMap<>();
    }

    public static final int OSMESA_ROW_LENGTH = 0x10;
    public static final int OSMESA_Y_UP = 0x11;
    public static final int OSMESA_WIDTH = 0x20;
    public static final int OSMESA_HEIGHT = 0x21;
    public static final int OSMESA_FORMAT = 0x22;
    public static final int OSMESA_TYPE = 0x23;
    public static final int OSMESA_MAX_WIDTH = 0x24;
    public static final int OSMESA_MAX_HEIGHT = 0x25;

    public static OSMesaContext createContext(OSMesaFormat format) {
        long vm = DynCall.dcNewCallVM(128);
        DynCall.dcArgInt(vm, format.getGlId());
        DynCall.dcArgPointer(vm, MemoryUtil.NULL);
        long pointer = DynCall.dcCallPointer(vm, OSMESA.getFunctionAddress("OSMesaCreateContext"));
        DynCall.dcFree(vm);
        if (pointer == 0)
            throw new RuntimeException("Could not create OSMesaContext !");
        OSMesaContext context = new OSMesaContext(format, pointer);
        CONTEXTS.put(pointer, context);
        return context;
    }

    public static OSMesaContext createContextExt(OSMesaFormat format, int depthBits, int stencilBits, int accumBits) {
        long vm = DynCall.dcNewCallVM(128);
        DynCall.dcArgInt(vm, format.getGlId());
        DynCall.dcArgInt(vm, depthBits);
        DynCall.dcArgInt(vm, stencilBits);
        DynCall.dcArgInt(vm, accumBits);
        DynCall.dcArgPointer(vm, MemoryUtil.NULL);
        long pointer = DynCall.dcCallPointer(vm, OSMESA.getFunctionAddress("OSMesaCreateContextExt"));
        DynCall.dcFree(vm);
        if (pointer == 0)
            throw new RuntimeException("Could not create OSMesaContext !");
        OSMesaContext context = new OSMesaContext(format, pointer);
        CONTEXTS.put(pointer, context);
        return context;
    }

    public static void destroyContext(OSMesaContext context) {
        context.destroy();
    }

    public static ByteBuffer makeContextCurrent(OSMesaContext context, int width, int height) {
        return context.makeCurrent(width, height);
    }

    public static OSMesaContext getCurrentContext() {
        long vm = DynCall.dcNewCallVM(64);
        long pointer = DynCall.dcCallPointer(vm, OSMESA.getFunctionAddress("OSMesaGetCurrentContext"));
        DynCall.dcFree(vm);
        if (pointer == 0)
            throw new RuntimeException("Could not get current OSMesaContext !");
        OSMesaContext context = CONTEXTS.get(pointer);
        if (context == null) {
            context = new OSMesaContext(OSMesaFormat.fromGlId(getIntegerv(OSMESA_FORMAT)), pointer);
            CONTEXTS.put(pointer, context);
        }
        return context;
    }

    public static int getIntegerv(int property) {
        IntBuffer value = MemoryUtil.memAllocInt(1);
        long vm = DynCall.dcNewCallVM(128);
        DynCall.dcArgInt(vm, property);
        DynCall.dcArgPointer(vm, MemoryUtil.memAddress(value));
        DynCall.dcCallVoid(vm, OSMesa.OSMESA.getFunctionAddress("OSMesaGetIntegerv"));
        DynCall.dcFree(vm);
        return value.get(0);
    }
}
