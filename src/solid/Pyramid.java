package solid;

import transforms.Col;
import transforms.Point3D;

import java.awt.*;

public class Pyramid extends Solid{
    public Pyramid() {
        // vb
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0), new Col(255, 0,0)));
        vertexBuffer.add(new Vertex(new Point3D(1, 0, 0), new Col(0, 255,0)));
        vertexBuffer.add(new Vertex(new Point3D(0.5, 1, 0), new Col(0, 0,255)));
        vertexBuffer.add(new Vertex(new Point3D(0.5, 0.5, 1), new Col(255,0,0)));

        // ib
        addIndices(
                0, 1, 2,
                0, 1, 3,
                0, 2, 3,
                1, 2, 3
        );

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 4));
    }
}
