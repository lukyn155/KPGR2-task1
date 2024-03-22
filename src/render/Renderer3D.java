package render;

import raster.TriangleRasterizer;
import solid.Part;
import solid.Solid;
import solid.Vertex;
import transforms.Mat4;

public class Renderer3D {
    private Mat4 model;

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }

    private Mat4 view;
    private Mat4 projection;

    private Mat4 matTransformation;

    private TriangleRasterizer triangleRasterizer;
    // TODO: proj, view


    public Renderer3D(TriangleRasterizer triangleRasterizer) {
        this.triangleRasterizer = triangleRasterizer;
    }

    public void render(Solid solid) {
        // TODO: transformace
        matTransformation = solid.getModel().mul(view).mul(projection);

        for (Part part : solid.getPartBuffer()) {
            switch (part.getType()) {
                case LINES:
                    // TODO: implementovat
                    break;
                case TRIANGLES:
                    int start = part.getStart();
                    for (int i = 0; i < part.getCount(); i++) {
                        int indexA = start;
                        int indexB = start + 1;
                        int indexC = start + 2;
                        start += 3;

                        Vertex a = solid.getVertexBuffer().get(indexA);
                        Vertex b = solid.getVertexBuffer().get(indexB);
                        Vertex c = solid.getVertexBuffer().get(indexC);
                        clipTriangle(a, b, c);
                    }
                    break;
            }
        }
    }

    private void clipTriangle(Vertex a, Vertex b, Vertex c) {
        //Trasformace až do projekce
        Vertex nA = new Vertex(a.getPosition().mul(matTransformation), a.getColor(), a.getUv());
        Vertex nB = new Vertex(b.getPosition().mul(matTransformation), b.getColor(), b.getUv());
        Vertex nC = new Vertex(c.getPosition().mul(matTransformation), c.getColor(), c.getUv());

        // TODO: fast clip
        if (nA.getPosition().getX() > nA.getPosition().getW() && nB.getPosition().getX() > nB.getPosition().getW() && nC.getPosition().getX() > c.getPosition().getW()) return;
        if (nA.getPosition().getX() < -nA.getPosition().getW() && nB.getPosition().getX() < -nB.getPosition().getW() && nC.getPosition().getX() < -c.getPosition().getW()) return;
        if (nA.getPosition().getY() > nA.getPosition().getW() && nB.getPosition().getY() > nB.getPosition().getW() && nC.getPosition().getY() > c.getPosition().getW()) return;
        if (nA.getPosition().getY() < -nA.getPosition().getW() && nB.getPosition().getY() < -nB.getPosition().getW() && nC.getPosition().getY() < -c.getPosition().getW()) return;

        if (nA.getPosition().getZ() > nA.getPosition().getW() && nB.getPosition().getZ() > nB.getPosition().getW() && nC.getPosition().getZ() > nC.getPosition().getW()) return;
        if (nA.getPosition().getZ() < 0 && nB.getPosition().getZ() < 0 && nC.getPosition().getZ() < 0) return;


        // TODO: sežadit vrcholy podle z, aby aZ = max

        double zMin = 0;

        if(a.getPosition().getZ() < zMin)
            return;

        if(b.getPosition().getZ() < zMin) {
            double tv1 = (zMin - a.getPosition().getZ()) / (b.getPosition().getZ() - a.getPosition().getZ());
            Vertex v1 = a.mul(1 - tv1).add(b.mul(tv1));

            double tv2 = (zMin - a.getPosition().getZ()) / (c.getPosition().getZ() - a.getPosition().getZ());
            Vertex v2 = a.mul(1 - tv2).add(c.mul(tv2));

            triangleRasterizer.rasterize(a, v1, v2);
            return;
        }

        if(c.getPosition().getZ() < zMin) {
            // TODO: implementovat
        }

        triangleRasterizer.rasterize(a, b, c);
    }

    // TODO: metoda render pro seznam solidů
}
