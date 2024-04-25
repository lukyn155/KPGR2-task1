package raster;

import solid.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.Optional;

public class LineRasterizerTrivial extends Rasterizer {
    public LineRasterizerTrivial(ZBuffer zBuffer) {
        super(zBuffer);
    }

    public void rasterize(Vertex a, Vertex b) {
        // Dehomogenizace
        Optional<Vertex> dA = a.dehomog();
        Optional<Vertex> dB = b.dehomog();

        // Transformace do okna
        Vec3D vec3D1 = transformToWindow(dA.get().getPosition());
        a = new Vertex(new Point3D(vec3D1), dA.get().getColor());

        Vec3D vec3D2 = transformToWindow(dB.get().getPosition());
        b = new Vertex(new Point3D(vec3D2), dB.get().getColor());

        // Seřazení podle Y
        if (a.getPosition().getY() > b.getPosition().getY()) {
            Vertex temp = a;
            a = b;
            b = temp;
        }

        double dxAB = a.getPosition().getX() - b.getPosition().getX();
        double dyAB = a.getPosition().getY() - b.getPosition().getY();
        double dzAB = a.getPosition().getZ() - b.getPosition().getZ();

        // Určíme, která osa je hlavní (ta s největším rozdílem)
        double steps = Math.max(Math.abs(dxAB), Math.abs(dyAB));
        steps = Math.max(steps, Math.abs(dzAB));

        // Vypočítáme přírůstky pro každou osu
        double xIncrement = dxAB / steps;
        double yIncrement = dyAB / steps;
        double zIncrement = dzAB / steps;

        // Inicializujeme proměnné pro průchod přímkou
        double x = a.getPosition().getX(), y = a.getPosition().getY(), z = a.getPosition().getZ();

        // Projdeme přímkou a vykreslíme pixely, pokud jsou blíže než stávající pixel v z-bufferu
        for (int i = 0; i <= steps; i++) {
            int xi = (int) Math.round(x);
            int yi = (int) Math.round(y);

            // Zkontrolujeme z-buffer
            zBuffer.setPixelWithZTest(xi, yi, z, a.getColor());

            // Posuneme se na další bod na přímce
            x -= xIncrement;
            y -= yIncrement;
            z += zIncrement;
        }
    }

    @Override
    public void rasterize(Vertex a, Vertex b, Vertex c) {
        // Dehomogenizace
        Optional<Vertex> dA = a.dehomog();
        Optional<Vertex> dB = b.dehomog();
        Optional<Vertex> dC = c.dehomog();

        // Transformace do okna
        Vec3D vec3D1 = transformToWindow(dA.get().getPosition());
        a = new Vertex(new Point3D(vec3D1), dA.get().getColor());

        Vec3D vec3D2 = transformToWindow(dB.get().getPosition());
        b = new Vertex(new Point3D(vec3D2), dB.get().getColor());

        Vec3D vec3D3 = transformToWindow(dC.get().getPosition());
        c = new Vertex(new Point3D(vec3D3), dC.get().getColor());

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
        double dxAB = a.getPosition().getX() - b.getPosition().getX();
        double dyAB = a.getPosition().getY() - b.getPosition().getY();
        double dzAB = a.getPosition().getZ() - b.getPosition().getZ();

        double dxAC = a.getPosition().getX() - c.getPosition().getX();
        double dyAC = a.getPosition().getY() - c.getPosition().getY();
        double dzAC = a.getPosition().getZ() - c.getPosition().getZ();

        double dxBC = b.getPosition().getX() - c.getPosition().getX();
        double dyBC = b.getPosition().getY() - c.getPosition().getY();
        double dzBC = b.getPosition().getZ() - c.getPosition().getZ();

        // Určíme, která osa je hlavní (ta s největším rozdílem)
        double steps = Math.max(Math.abs(dxAB), Math.abs(dyAB));
        steps = Math.max(steps, Math.abs(dzAB));

        // Vypočítáme přírůstky pro každou osu
        double xIncrement = dxAB / steps;
        double yIncrement = dyAB / steps;
        double zIncrement = dzAB / steps;

        // Inicializujeme proměnné pro průchod přímkou
        double x = a.getPosition().getX(), y = a.getPosition().getY(), z = a.getPosition().getZ();

        // Projdeme přímkou a vykreslíme pixely, pokud jsou blíže než stávající pixel v z-bufferu
        for (int i = 0; i <= steps; i++) {
            int xi = (int) Math.round(x);
            int yi = (int) Math.round(y);

            // Zkontrolujeme z-buffer
            zBuffer.setPixelWithZTest(xi, yi, z, new Col(255,0,0));

            // Posuneme se na další bod na přímce
            x -= xIncrement;
            y -= yIncrement;
            z += zIncrement;
        }

        // Určíme, která osa je hlavní (ta s největším rozdílem)
        steps = Math.max(Math.abs(dxAC), Math.abs(dyAC));
        steps = Math.max(steps, Math.abs(dzAC));

        // Vypočítáme přírůstky pro každou osu
        xIncrement = dxAC / steps;
        yIncrement = dyAC / steps;
        zIncrement = dzAC / steps;

        x = a.getPosition().getX();
        y = a.getPosition().getY();
        z = a.getPosition().getZ();
        for (int i = 0; i <= steps; i++) {
//            System.out.println("X: " + x + " Y: " + y + " Z: " + z);
            int xi = (int) Math.round(x);
            int yi = (int) Math.round(y);

            // Zkontrolujeme z-buffer
            zBuffer.setPixelWithZTest(xi, yi, z, new Col(255,0,0));

            // Posuneme se na další bod na přímce
            x -= xIncrement;
            y -= yIncrement;
            z += zIncrement;
        }

        // Určíme, která osa je hlavní (ta s největším rozdílem)
        steps = Math.max(Math.abs(dxBC), Math.abs(dyBC));
        steps = Math.max(steps, Math.abs(dzBC));

        // Vypočítáme přírůstky pro každou osu
        xIncrement = dxBC / steps;
        yIncrement = dyBC / steps;
        zIncrement = dzBC / steps;

        x = b.getPosition().getX();
        y = b.getPosition().getY();
        z = b.getPosition().getZ();
        for (int i = 0; i <= steps; i++) {
//            System.out.println("X: " + x + " Y: " + y + " Z: " + z);
            int xi = (int) Math.round(x);
            int yi = (int) Math.round(y);

            // Zkontrolujeme z-buffer
            zBuffer.setPixelWithZTest(xi, yi, z, new Col(255,0,0));

            // Posuneme se na další bod na přímce
            x -= xIncrement;
            y -= yIncrement;
            z += zIncrement;
        }
    }
}