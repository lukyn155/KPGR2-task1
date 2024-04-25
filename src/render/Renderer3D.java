package render;

import raster.LineRasterizerTrivial;
import raster.Rasterizer;
import raster.TriangleRasterizer;
import solid.Part;
import solid.Solid;
import solid.Vertex;
import transforms.Mat4;

import java.util.List;

public class Renderer3D {
    private Mat4 model;

    private Mat4 view;
    private Mat4 projection;

    private Mat4 matTransformation;
    private TriangleRasterizer triangleRasterizer;
    private LineRasterizerTrivial lineRasterizer;

    private Rasterizer rasterizer;

    private boolean isWired = false;

    public Renderer3D(TriangleRasterizer triangleRasterizer, LineRasterizerTrivial lineRasterizer) {
        this.rasterizer = triangleRasterizer;
        this.triangleRasterizer = triangleRasterizer;
        this.lineRasterizer = lineRasterizer;
    }
    public void changeRasterizer() {
        if (isWired) {
            this.rasterizer = lineRasterizer;
        } else {
            this.rasterizer = triangleRasterizer;
        }
        isWired = !isWired;
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }

    public void render(Solid solid) {
        // TODO: transformace
        matTransformation = solid.getModel().mul(view).mul(projection);

        for (Part part : solid.getPartBuffer()) {
            switch (part.getType()) {
                case LINES:
                    int startLine = part.getStart();
                    for (int i = 0; i < part.getCount(); i++) {
                        int indexA = startLine;
                        int indexB = startLine + 1;
                        startLine += 2;

                        Vertex a = solid.getVertexBuffer().get(solid.getIndexBuffer().get(indexA));
                        Vertex b = solid.getVertexBuffer().get(solid.getIndexBuffer().get(indexB));
                        clipLine(a, b);
                    }
                    break;
                case TRIANGLES:
                    int start = part.getStart();
                    for (int i = 0; i < part.getCount(); i++) {
                        int indexA = start;
                        int indexB = start + 1;
                        int indexC = start + 2;
                        start += 3;

                        Vertex a = solid.getVertexBuffer().get(solid.getIndexBuffer().get(indexA));
                        Vertex b = solid.getVertexBuffer().get(solid.getIndexBuffer().get(indexB));
                        Vertex c = solid.getVertexBuffer().get(solid.getIndexBuffer().get(indexC));
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

        //fast clip
        if (nA.getPosition().getX() > nA.getPosition().getW() && nB.getPosition().getX() > nB.getPosition().getW() && nC.getPosition().getX() > c.getPosition().getW()) return;
        if (nA.getPosition().getX() < -nA.getPosition().getW() && nB.getPosition().getX() < -nB.getPosition().getW() && nC.getPosition().getX() < -c.getPosition().getW()) return;
        if (nA.getPosition().getY() > nA.getPosition().getW() && nB.getPosition().getY() > nB.getPosition().getW() && nC.getPosition().getY() > c.getPosition().getW()) return;
        if (nA.getPosition().getY() < -nA.getPosition().getW() && nB.getPosition().getY() < -nB.getPosition().getW() && nC.getPosition().getY() < -c.getPosition().getW()) return;

        if (nA.getPosition().getZ() > nA.getPosition().getW() && nB.getPosition().getZ() > nB.getPosition().getW() && nC.getPosition().getZ() > nC.getPosition().getW()) return;
        if (nA.getPosition().getZ() < 0 && nB.getPosition().getZ() < 0 && nC.getPosition().getZ() < 0) return;


        //seřazení vrcholy podle z, aby aZ = max
        if (nA.getPosition().getZ() < nB.getPosition().getZ()) {
            Vertex tmp = nA;
            nA = nB;
            nB = tmp;
        }

        if (nB.getPosition().getZ() < nC.getPosition().getZ()) {
            Vertex tmp = nB;
            nB = nC;
            nC = tmp;
        }

        if (nA.getPosition().getZ() < nB.getPosition().getZ()) {
            Vertex tmp = nA;
            nA = nB;
            nB = tmp;
        }

        double zMin = 0;

        if(nA.getPosition().getZ() < zMin)
            return;

        if(nB.getPosition().getZ() < zMin) {
            double tv1 = (zMin - nB.getPosition().getZ()) / (nB.getPosition().getZ() - nA.getPosition().getZ());
            Vertex v1 = nA.mul(1 - tv1).add(nB.mul(tv1));

            double tv2 = (zMin - nA.getPosition().getZ()) / (nC.getPosition().getZ() - nA.getPosition().getZ());
            Vertex v2 = nA.mul(1 - tv2).add(nC.mul(tv2));

            //triangleRasterizer.rasterize(nA, v1, v2);
            rasterizer.rasterize(nA, v1, v2);
            return;
        }

        if(nC.getPosition().getZ() < zMin) {
            double tv1 = -nA.getPosition().getZ() / (nC.getPosition().getZ() - nA.getPosition().getZ());
            Vertex v1 = nA.mul(1 - tv1).add(nC.mul(tv1));

            double tv2 = -nB.getPosition().getZ() / (nC.getPosition().getZ() - nB.getPosition().getZ());
            Vertex v2 = nB.mul(1 - tv2).add(nC.mul(tv1));

//            triangleRasterizer.rasterize(nA, nB, v2);
//            triangleRasterizer.rasterize(nA, v1, v2);
            rasterizer.rasterize(nA, nB, v2);
            rasterizer.rasterize(nA, v1, v2);
            return;
        }

//        triangleRasterizer.rasterize(nA, nB, nC);
        rasterizer.rasterize(nA, nB, nC);
    }

    private void clipLine(Vertex a, Vertex b) {
        //Trasformace až do projekce
        Vertex nA = new Vertex(a.getPosition().mul(matTransformation), a.getColor(), a.getUv());
        Vertex nB = new Vertex(b.getPosition().mul(matTransformation), b.getColor(), b.getUv());

        //fast clip
        if (nA.getPosition().getX() > nA.getPosition().getW() && nB.getPosition().getX() > nB.getPosition().getW()) return;
        if (nA.getPosition().getX() < -nA.getPosition().getW() && nB.getPosition().getX() < -nB.getPosition().getW()) return;
        if (nA.getPosition().getY() > nA.getPosition().getW() && nB.getPosition().getY() > nB.getPosition().getW()) return;
        if (nA.getPosition().getY() < -nA.getPosition().getW() && nB.getPosition().getY() < -nB.getPosition().getW()) return;

        if (nA.getPosition().getZ() > nA.getPosition().getW() && nB.getPosition().getZ() > nB.getPosition().getW()) return;
        if (nA.getPosition().getZ() < 0 && nB.getPosition().getZ() < 0) return;


        //seřazení vrcholy podle z, aby aZ = max
        if (nA.getPosition().getZ() < nB.getPosition().getZ()) {
            Vertex tmp = nA;
            nA = nB;
            nB = tmp;
        }

        double zMin = 0;

        if(nA.getPosition().getZ() < zMin)
            return;

        if(nB.getPosition().getZ() < zMin) {
            double tv1 = (zMin - nB.getPosition().getZ()) / (nB.getPosition().getZ() - nA.getPosition().getZ());
            Vertex v1 = nA.mul(1 - tv1).add(nB.mul(tv1));

            //triangleRasterizer.rasterize(nA, v1, v2);
            lineRasterizer.rasterize(nA, v1);
            return;
        }
//        triangleRasterizer.rasterize(nA, nB, nC);
        lineRasterizer.rasterize(nA, nB);
    }

    //metoda render pro seznam solidů
    public void render(List<Solid> scene) {
        for (Solid s : scene) {
            render(s);
        }
    }
}
