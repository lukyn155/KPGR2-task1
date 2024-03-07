package raster;

import solid.Part;
import solid.Vertex;
import transforms.Col;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TriangleRasterizer {
    private final ZBuffer zBuffer;

    public TriangleRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    private Vertex min(Vertex a, Vertex b) {
        if (a.getPosition().getY() < a.getPosition().getY()) {
            return a;
        } else {
            return b;
        }
    }

    public void rasterize(Vertex a, Vertex b, Vertex c) {
        ArrayList<Vertex> vertexList = new ArrayList<>();
        vertexList.add(a);
        vertexList.add(b);
        vertexList.add(c);
        vertexList.sort(Comparator.comparingDouble(c2 -> c2.getPosition().getY()));

        for (int i = 0; i < vertexList.size(); i++) {
            System.out.println(vertexList.get(i).getPosition().getY());
        }


        // TODO: odebrat, jen pro debug
        ((ImageBuffer) zBuffer.getImageBuffer()).getImg().getGraphics().drawLine(
                (int) vertexList.get(0).getPosition().getX(), (int) vertexList.get(0).getPosition().getY(),
                (int) vertexList.get(1).getPosition().getX(), (int) vertexList.get(1).getPosition().getY()
        );
        ((ImageBuffer) zBuffer.getImageBuffer()).getImg().getGraphics().drawLine(
                (int) vertexList.get(0).getPosition().getX(), (int) vertexList.get(0).getPosition().getY(),
                (int) vertexList.get(2).getPosition().getX(), (int) vertexList.get(2).getPosition().getY()
        );
        ((ImageBuffer) zBuffer.getImageBuffer()).getImg().getGraphics().drawLine(
                (int) vertexList.get(1).getPosition().getX(), (int) vertexList.get(1).getPosition().getY(),
                (int) vertexList.get(2).getPosition().getX(), (int) vertexList.get(2).getPosition().getY()
        );

        int yA = (int) vertexList.get(0).getPosition().getY();
        int xA = (int) vertexList.get(0).getPosition().getX();
        int yB = (int) vertexList.get(1).getPosition().getY();
        int xB = (int) vertexList.get(1).getPosition().getX();
        int yC = (int) vertexList.get(2).getPosition().getY();
        int xC = (int) vertexList.get(2).getPosition().getX();

        // Cyklus od A do B (první část)
        for (int y = yA; y <= yB; y++) {
            // V1
            double t1 = (y - yA) / (double) (yB - yA);
            int x1 = (int) Math.round((1 - t1) * xA + t1 * xB);
            //double z1 = (1 - t1) * zA + t1 * zB;
            Col col1 = vertexList.get(0).getColor().mul(1 - t1).add(vertexList.get(1).getColor().mul(t1));

            // V2
            double t2 = (y - yA) / (double) (yC - yA);
            int x2 = (int) Math.round((1 - t2) * xA + t2 * xC);
            //double z2 = (1 - t2) * zA + t2 * zC;
            Col col2 = vertexList.get(0).getColor().mul(1 - t2).add(vertexList.get(2).getColor().mul(t2));

            // Kontrola, jestli x1 < x2
            for (int x = x1; x <= x2; x++) {
                double t3 = (x - x1) / (double) (x2 - x1);
                //double z = (1 - t3) * z1 + t3 * z2;
                Col col = col1.mul(1 - t3).add(col2.mul(t3));

                zBuffer.setPixelWithZTest(x, y, 0.5, col);
            }
        }

        //Cyklus od B do C (druhá část)
        for (int y = yB; y <= yC; y++) {
            // V1
            double t1 = (y - yB) / (double) (yC - yB);
            int x1 = (int) Math.round((1 - t1) * xB + t1 * xC);
            //double z1 = (1 - t1) * zA + t1 * zB;
            Col col1 = vertexList.get(1).getColor().mul(1 - t1).add(vertexList.get(2).getColor().mul(t1));

            // V2
            double t2 = (y - yA) / (double) (yC - yA);
            int x2 = (int) Math.round((1 - t2) * xA + t2 * xC);
            //double z2 = (1 - t2) * zA + t2 * zC;
            Col col2 = vertexList.get(0).getColor().mul(1 - t2).add(vertexList.get(2).getColor().mul(t2));

            //Kontrola, jestli x1 < x2
            for (int x = x1; x <= x2; x++) {
                double t3 = (x - x1) / (double) (x2 - x1);
                //double z = (1 - t3) * z1 + t3 * z2;
                Col col = col1.mul(1 - t3).add(col2.mul(t3));

                zBuffer.setPixelWithZTest(x, y, 0.5, col);
            }
        }

    }
}
