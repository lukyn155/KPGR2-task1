package raster;

import solid.Vertex;
import transforms.Col;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TriangleRasterizer {
    private final ZBuffer zBuffer;

    public TriangleRasterizer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
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

        int aX = (int) Math.round(vertexList.get(0).getPosition().getX());
        int aY = (int) Math.round(vertexList.get(0).getPosition().getY());
        double aZ = a.getPosition().getZ();

        int bX = (int) Math.round(vertexList.get(1).getPosition().getX());
        int bY = (int) Math.round(vertexList.get(1).getPosition().getY());
        double bZ = b.getPosition().getZ();

        int cX = (int) Math.round(vertexList.get(2).getPosition().getX());
        int cY = (int) Math.round(vertexList.get(2).getPosition().getY());
        double cZ = c.getPosition().getZ();

        // TODO. seřadit vrcholy podle y

        for (int y = aY; y <= bY; y++) {
            double tAB = (y - aY) / (double)(bY - aY);
            int xAB = (int) Math.round ((1 - tAB) * aX + tAB * bX);
            // TODO: zAB
            Vertex vAB = a.mul(1 - tAB).add(b.mul(tAB));
            // Vertex vAB = lerp.lerp(a, b, tAB);

            double tAC = (y - aY) / (double)(cY - aY);
            int xAC = (int) Math.round ((1 - tAC) * aX + tAC * cX);
            Vertex vAC = a.mul(1 - tAC).add(c.mul(tAC));
            // TODO: zAC

            // TODO: xAB musí být menší než xAC
            for (int x = xAB; x <= xAC; x++) {
                double t = (x - xAB) / (double) (xAC - xAB);
                Vertex pixel = vAB.mul(1 - t).add(vAC.mul(t));

                // TODO: nový interpolační koef. -> počítám z xAB a xAC
                // TODO: spočítám z
                zBuffer.setPixelWithZTest(x, y, 0.5, pixel.getColor());
            }
        }

        //Cyklus od B do C (druhá část)
        for (int y = bY; y <= cY; y++) {
            // V1
            double tBC = (y - bY) / (double)(cY - bY);
            int xBC = (int) Math.round ((1 - tBC) * bX + tBC * cX);
            Vertex vBC = b.mul(1 - tBC).add(c.mul(tBC));

            // V2
            double tAC = (y - aY) / (double)(cY - aY);
            int xAC = (int) Math.round ((1 - tAC) * aX + tAC * cX);
            Vertex vAC = a.mul(1 - tAC).add(c.mul(tAC));

            //Kontrola, jestli x1 < x2
            for (int x = xBC; x <= xAC; x++) {
                double t = (x - xBC) / (double) (xAC - xBC);
                Vertex pixel = vBC.mul(1 - t).add(vAC.mul(t));

                // TODO: nový interpolační koef. -> počítám z xAB a xAC
                // TODO: spočítám z
                zBuffer.setPixelWithZTest(x, y, 0.5, pixel.getColor());
            }
        }
    }
}
