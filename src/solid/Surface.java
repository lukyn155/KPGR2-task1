package solid;

import transforms.*;

import java.awt.*;

public class Surface extends Solid {
    public Surface(final Mat4 baseMat, final Point3D[] points) {
        Bicubic bicubic = new Bicubic(baseMat, points);
        int index = 0;
        float u = 0;
        float v = 0;
        for (int i = 0; i <= 10; i++) {
            u = (float) i / 10;
            for (int j = 0; j <= 10; j++) {
                v = (float) j / 10;
                vertexBuffer.add(new Vertex(bicubic.compute(u, v), new Col(0, 255, 0)));
                if (i != 10) {
                    indexBuffer.add(index);
                    indexBuffer.add((index + 1));
                }
                if (i != 10) {
                    indexBuffer.add(index);
                    indexBuffer.add((index + 11));
                }
                if (i != 0) {
                    indexBuffer.add(index);
                    indexBuffer.add((index - 9));
                }
                index++;
            }
        }
        int linesCount = getIndexBuffer().size() / 2;
        partBuffer.add(new Part(TopologyType.LINES, 0, linesCount));
    }
}
