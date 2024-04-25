package control;

import raster.*;
import render.Renderer3D;
import solid.Minecraft;
import solid.Pyramid;
import solid.Solid;
import transforms.*;
import view.Panel;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Controller3D implements Controller {
    private final Panel panel;

    private Renderer3D renderer3D;
    private ZBuffer zBuffer;
    private TriangleRasterizer triangleRasterizer;
    private LineRasterizerTrivial lineRasterizerTrivial;
    private BufferedImage texture;

    private Mat4 model, projection;
    private Camera camera;

    private Solid pyramid;
    private Solid minecraft;

    private int firstX;
    private int firstY;

    private int secondX;
    private int secondY;
    double azimut = 90;
    double zenit = 0;

    int pX = 0;
    int pY = -2;
    double pZ = 0.3;
    final double step = 0.25;

    private String projType = "persp";

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
        lineRasterizerTrivial = new LineRasterizerTrivial(zBuffer);
        renderer3D = new Renderer3D(triangleRasterizer, lineRasterizerTrivial);

//        model = new Mat4Identity();
//
//        Vec3D e = new Vec3D(1, -5, -1);
//        camera = new Camera()
//                .withPosition(e)
//                .withAzimuth(Math.toRadians(90))
//                .withZenith(Math.toRadians(0));
//
//        projection = new Mat4PerspRH(
//            Math.PI / 3,
//            raster.getHeight() / (float) raster.getWidth(),
//            0.5,
//            50
//        );

        camera = new Camera(
                new Vec3D(pX, pY, pZ),
                Math.toRadians(azimut),
                Math.toRadians(zenit),
                1,
                true
        );

        projection = new Mat4PerspRH(
                Math.PI / 4,
                raster.getHeight() / (double) raster.getWidth(),
                0.1,
                20
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

        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                    //TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    firstX = e.getX();
                    firstY = e.getY();
                    // rasterizer.rasterize(x, y, e.getX(),e.getY(), Color.RED);
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    //TODO
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    //TODO
                }

                redraw();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isControlDown()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        //TODO
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        //TODO
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                    //TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    secondX = e.getX();
                    secondY = e.getY();

                    int dx = secondX - firstX;
                    int dy = secondY - firstY;

                    zenit -= (double) 180 * dy / panel.getHeight();
                    if (zenit > 90) zenit = 90;
                    if (zenit < -90) zenit = -90;


                    azimut -= (double) 180 * dx / panel.getWidth();
                    azimut = azimut % 360;

                    firstX = secondX;
                    firstY = secondY;
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    //TODO
                } else if (SwingUtilities.isMiddleMouseButton(e)) {
                    //TODO
                }
                redraw();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // na klávesu C vymazat plátno
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    //TODO
                } else if (e.getKeyCode() == KeyEvent.VK_O) {
                    projection = new Mat4OrthoRH(
                            3,
                            3,
                            0.1,
                            20
                    );
                    projType = "ort";
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    projection = new Mat4PerspRH(
                            Math.PI / 4,
                            panel.getRaster().getHeight() / (double) panel.getRaster().getWidth(),
                            0.1,
                            20
                    );
                    projType = "persp";
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    camera = camera.forward(step);
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    camera = camera.left(step);
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    camera = camera.right(step);
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    camera = camera.backward(step);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    camera = camera.up(step);
                } else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    camera = camera.down(step);
                } else if (e.getKeyCode() == KeyEvent.VK_M) {
                    renderer3D.changeRasterizer();
                }
                redraw();
            }
        });
    }

    private void redraw() {
        panel.clear();
        zBuffer.setDefault();

        renderer3D.setProjection(projection);

        camera = camera.withAzimuth(Math.toRadians(azimut)).withZenith(Math.toRadians(zenit));
        renderer3D.setView(camera.getViewMatrix());

        Mat4 matTrans = new Mat4Identity();
        matTrans = matTrans.mul(new Mat4RotZ(45));

        Mat4 matTrans2 = new Mat4Identity();
        matTrans2 = matTrans.mul(new Mat4Transl(1, 0, 0));

        pyramid = new Pyramid();
        pyramid.setModel(matTrans);

        minecraft = new Minecraft();
        minecraft.setModel(matTrans2);

        renderer3D.render(pyramid);
        renderer3D.render(minecraft);

        panel.repaint();
    }
}
