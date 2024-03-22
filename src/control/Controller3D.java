package control;

import raster.ImageBuffer;
import raster.Raster;
import raster.TriangleRasterizer;
import raster.ZBuffer;
import solid.Vertex;
import transforms.*;
import view.Panel;

import javax.imageio.ImageIO;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller3D implements Controller {
    private final Panel panel;
    private ZBuffer zBuffer;
    private TriangleRasterizer triangleRasterizer;
    private BufferedImage texture;

    private Mat4 model, projection;
    private Camera camera;

    public Controller3D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners();
        redraw();
    }

    public void initObjects(Raster<Col> raster) {
        raster.setDefaultValue(new Col(0x101010));

        zBuffer = new ZBuffer((ImageBuffer) raster);
        triangleRasterizer = new TriangleRasterizer(zBuffer);

        model = new Mat4Identity();

        Vec3D e = new Vec3D(1, -5, 2);
        camera = new Camera()
                .withPosition(e)
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-15));

        projection = new Mat4PerspRH(
            Math.PI / 3,
            raster.getHeight() / (float) raster.getWidth(),
            0.5,
            50
        );
    }

    @Override
    public void initListeners() {
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
            }
        });
    }

    private void redraw() {
        panel.clear();

        triangleRasterizer.rasterize(
                new Vertex(new Point3D(0, 300, 0.5), new Col(0xff0000), new Vec2D(0.5, 1.)),
                new Vertex(new Point3D(400, 0, 0.5), new Col(0x00ff00), new Vec2D(0, .5)),
                new Vertex(new Point3D(799, 599, 0.5), new Col(0x0000ff), new Vec2D(1., 0.))
        );

        panel.repaint();
    }
}
