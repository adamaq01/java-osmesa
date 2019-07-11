package fr.adamaq01.osmesa;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.dyncall.DynCall;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class OSMesaContext {

    protected OSMesaFormat format;
    protected long pointer;

    protected OSMesaContext(OSMesaFormat format, long pointer) {
        this.format = format;
        this.pointer = pointer;
    }

    public OSMesaFormat getFormat() {
        return format;
    }

    public ByteBuffer makeCurrent(int width, int height) {
        ByteBuffer image = MemoryUtil.memAlloc(width * height * format.getComponents());
        long vm = DynCall.dcNewCallVM(1024);
        DynCall.dcArgPointer(vm, pointer);
        DynCall.dcArgPointer(vm, MemoryUtil.memAddress(image));
        DynCall.dcArgInt(vm, GL33.GL_UNSIGNED_BYTE);
        DynCall.dcArgInt(vm, width);
        DynCall.dcArgInt(vm, height);
        boolean success = DynCall.dcCallBool(vm, OSMesa.OSMESA.getFunctionAddress("OSMesaMakeCurrent"));
        DynCall.dcFree(vm);
        if (!success)
            throw new RuntimeException("Could not make OSMesaContext(" + pointer + ") current !");
        return image;
    }

    public void destroy() {
        long vm = DynCall.dcNewCallVM(128);
        DynCall.dcArgPointer(vm, pointer);
        DynCall.dcCallVoid(vm, OSMesa.OSMESA.getFunctionAddress("OSMesaDestroyContext"));
        DynCall.dcFree(vm);
        OSMesa.CONTEXTS.remove(pointer);
        pointer = MemoryUtil.NULL;
    }

    public ByteBuffer getColorBuffer() {
        IntBuffer width = MemoryUtil.memAllocInt(1);
        IntBuffer height = MemoryUtil.memAllocInt(1);
        IntBuffer format = MemoryUtil.memAllocInt(1);
        PointerBuffer pointerBuffer = MemoryUtil.memAllocPointer(1);
        long vm = DynCall.dcNewCallVM(1024);
        DynCall.dcArgPointer(vm, pointer);
        DynCall.dcArgPointer(vm, MemoryUtil.memAddress(width));
        DynCall.dcArgPointer(vm, MemoryUtil.memAddress(height));
        DynCall.dcArgPointer(vm, MemoryUtil.memAddress(format));
        DynCall.dcArgPointer(vm, MemoryUtil.memAddress(pointerBuffer));
        boolean success = DynCall.dcCallBool(vm, OSMesa.OSMESA.getFunctionAddress("OSMesaGetColorBuffer"));
        DynCall.dcFree(vm);
        if (!success)
            throw new RuntimeException("Could not get Color Buffer from OSMesaContext(" + pointer + ") !");
        return pointerBuffer.getByteBuffer(width.get() * height.get() * OSMesaFormat.fromGlId(format.get()).getComponents());
    }

    public ByteBuffer getDepthBuffer() {
        IntBuffer width = MemoryUtil.memAllocInt(1);
        IntBuffer height = MemoryUtil.memAllocInt(1);
        IntBuffer bytesPerValue = MemoryUtil.memAllocInt(1);
        PointerBuffer pointerBuffer = MemoryUtil.memAllocPointer(1);
        long vm = DynCall.dcNewCallVM(1024);
        DynCall.dcArgPointer(vm, pointer);
        DynCall.dcArgPointer(vm, MemoryUtil.memAddress(width));
        DynCall.dcArgPointer(vm, MemoryUtil.memAddress(height));
        DynCall.dcArgPointer(vm, MemoryUtil.memAddress(bytesPerValue));
        DynCall.dcArgPointer(vm, MemoryUtil.memAddress(pointerBuffer));
        boolean success = DynCall.dcCallBool(vm, OSMesa.OSMESA.getFunctionAddress("OSMesaGetDepthBuffer"));
        DynCall.dcFree(vm);
        if (!success)
            throw new RuntimeException("Could not get Depth Buffer from OSMesaContext(" + pointer + ") !");
        return pointerBuffer.getByteBuffer(width.get() * height.get() * bytesPerValue.get());
    }
}
