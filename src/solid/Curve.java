package solid;

import transforms.Col;
import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

import java.awt.*;

public class Curve extends Solid {
    public Curve(final Mat4 baseMat, final Point3D[] points, Color color) {
        Cubic cubic = new Cubic(baseMat, points);
        int index = 0;
        float n = 0;
        for (int i = 0; i <= 10; i++) {
            n = (float) i / 10;
            vertexBuffer.add(new Vertex(cubic.compute(n), new Col(0,255, 0)));
            if (i != 10) {
                indexBuffer.add(index);
                indexBuffer.add((index + 1));
            }
            index++;
        }
    }
}