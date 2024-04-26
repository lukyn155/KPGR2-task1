package raster;

import shader.Shader;
import solid.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.Optional;

public class TriangleRasterizer extends Rasterizer {
    public TriangleRasterizer(ZBuffer zBuffer) {
        super(zBuffer);
    }

    @Override
    public void rasterize(Vertex a, Vertex b, Vertex c, Shader shader) {
        // Dehomogenizace
        Optional<Vertex> dA = a.dehomog();
        Optional<Vertex> dB = b.dehomog();
        Optional<Vertex> dC = c.dehomog();

        // Transformace do okna
        Vec3D vec3D1 = transformToWindow(dA.get().getPosition());
        a = new Vertex(new Point3D(vec3D1), dA.get().getColor(), dA.get().getUv());

        Vec3D vec3D2 = transformToWindow(dB.get().getPosition());
        b = new Vertex(new Point3D(vec3D2), dB.get().getColor(), dB.get().getUv());

        Vec3D vec3D3 = transformToWindow(dC.get().getPosition());
        c = new Vertex(new Point3D(vec3D3), dC.get().getColor(), dC.get().getUv());

        // Seřazení podle Y
        if (a.getPosition().getY() > b.getPosition().getY()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }
        if (b.getPosition().getY() > c.getPosition().getY()) {
            Vertex temp = b;
            b = c;
            c = temp;
        }
        if (a.getPosition().getY() > b.getPosition().getY()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }
        a.setUv(a.getUv().mul(1/a.getPosition().getW()));
        b.setUv(b.getUv().mul(1/b.getPosition().getW()));
        c.setUv(c.getUv().mul(1/c.getPosition().getW()));;

        int aX = (int) Math.round(a.getPosition().getX());
        int aY = (int) Math.round(a.getPosition().getY());

        int bX = (int) Math.round(b.getPosition().getX());
        int bY = (int) Math.round(b.getPosition().getY());

        int cX = (int) Math.round(c.getPosition().getX());
        int cY = (int) Math.round(c.getPosition().getY());

        int yStart = Math.max(0, (int) a.getPosition().getY() + 1);
        double yEnd = Math.min(zBuffer.getWindowHeight() - 1, b.getPosition().getY());
        for (int y = yStart; y <= yEnd; y++) {
            double tAB = (y - aY) / (double) (bY - aY);
            int xAB = (int) Math.round((1 - tAB) * aX + tAB * bX);
            Vertex vAB = a.mul(1 - tAB).add(b.mul(tAB));

            double tAC = (y - aY) / (double) (cY - aY);
            int xAC = (int) Math.round((1 - tAC) * aX + tAC * cX);
            Vertex vAC = a.mul(1 - tAC).add(c.mul(tAC));

            if (xAB > xAC) {
                int tmp = xAB;
                xAB = xAC;
                xAC = tmp;

                Vertex vTmp = vAB;
                vAB = vAC;
                vAC = vTmp;
            }

            int xStart = Math.max(0, (int) vAB.getPosition().getX() + 1);
            double xEnd = Math.min(zBuffer.getWindowWidth() - 1, vAC.getPosition().getX());

            //xAB musí být menší než xAC
            for (int x = xStart; x <= xEnd; x++) {
                double t = (x - xAB) / (double) (xAC - xAB);
                Vertex pixel = vAB.mul(1 - t).add(vAC.mul(t));
//                zBuffer.setPixelWithZTest(x, y, pixel.getPosition().getZ(), pixel.getColor());
                zBuffer.setPixelWithZTest(x, y, pixel.getPosition().getZ(), shader.getColor(pixel));
            }

        }

        yStart = Math.max(0, (int) b.getPosition().getY() + 1);
        yEnd = Math.min(zBuffer.getWindowHeight() - 1, c.getPosition().getY());
        //Cyklus od B do C (druhá část)
        for (int y = yStart; y <= yEnd; y++) {
            // V1
            double tBC = (y - bY) / (double) (cY - bY);
            int xBC = (int) Math.round((1 - tBC) * bX + tBC * cX);
            Vertex vBC = b.mul(1 - tBC).add(c.mul(tBC));

            // V2
            double tAC = (y - aY) / (double) (cY - aY);
            int xAC = (int) Math.round((1 - tAC) * aX + tAC * cX);
            Vertex vAC = a.mul(1 - tAC).add(c.mul(tAC));

            //Kontrola, jestli x1 < x2
            if (xBC > xAC) {
                int tmp = xBC;
                xBC = xAC;
                xAC = tmp;

                Vertex vTmp = vBC;
                vBC = vAC;
                vAC = vTmp;
            }

            int xStart = Math.max(0, (int) vBC.getPosition().getX() + 1);
            double xEnd = Math.min(zBuffer.getWindowWidth() - 1, vAC.getPosition().getX());

            //xAB musí být menší než xAC
            for (int x = xStart; x <= xEnd; x++) {
                double t = (x - xBC) / (double) (xAC - xBC);
                Vertex pixel = vBC.mul(1 - t).add(vAC.mul(t));
//                zBuffer.setPixelWithZTest(x, y, pixel.getPosition().getZ(), pixel.getColor());
                zBuffer.setPixelWithZTest(x, y, pixel.getPosition().getZ(), shader.getColor(pixel));
            }
        }
    }
}
