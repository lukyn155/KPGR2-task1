package raster;

import shader.Shader;
import solid.Vertex;
import transforms.Point3D;
import transforms.Vec3D;

public abstract class Rasterizer {
    protected final ZBuffer zBuffer;
    protected Rasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    public abstract void rasterize(Vertex a, Vertex b, Vertex c, Shader shader);

    protected Vec3D transformToWindow(Point3D vec) {
        return new Vec3D(vec)
                .mul(new Vec3D(1,-1,1))
                .add(new Vec3D(1,1,0))
                .mul(new Vec3D((zBuffer.getWindowWidth() - 1)  / 2., (zBuffer.getWindowHeight() - 1) / 2., 1));
    }
}
