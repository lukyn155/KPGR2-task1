package solid;

import transforms.Col;
import transforms.Point3D;

public class Cube extends Solid {

    public Cube() {
        // vb
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 0), new Col(255,0,0))); // v0
        vertexBuffer.add(new Vertex(new Point3D(1, 0, 0), new Col(0,255,0))); // v1
        vertexBuffer.add(new Vertex(new Point3D(1, 1, 0), new Col(0,0,255))); // v2
        vertexBuffer.add(new Vertex(new Point3D(0, 1, 0), new Col(255,0,0))); // v3
        vertexBuffer.add(new Vertex(new Point3D(0, 0, 1), new Col(0,255,0))); // v4
        vertexBuffer.add(new Vertex(new Point3D(1, 0, 1), new Col(0,0,255))); // v5
        vertexBuffer.add(new Vertex(new Point3D(1, 1, 1), new Col(255,0,0))); // v6
        vertexBuffer.add(new Vertex(new Point3D(0, 1, 1), new Col(0,255,0))); // v7

        // ib
        addIndices(
                0, 1, 2,
                0, 2, 3,
                0, 1, 5,
                0, 5, 4,
                0, 3, 7,
                0, 4, 7,
                2, 3, 7,
                2, 7, 6,
                1, 2, 5,
                2, 5, 6,
                4, 5, 6,
                4, 6, 7
        );

        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 12));
    }
}