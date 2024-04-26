package control;

import raster.*;
import render.Renderer3D;
import solid.*;
import transforms.*;
import view.Panel;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
    private Solid pyramid2;
    private Solid cube;
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

    ArrayList<Solid> solids = new ArrayList<>();
    int activeSolidIndex = 0;
    Solid activeSolid;

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

        Mat4 matTrans = new Mat4Identity();
        matTrans = matTrans.mul(new Mat4RotZ(45));

        Mat4 matTrans2 = new Mat4Identity();
        matTrans2 = matTrans2.mul(new Mat4Transl(1, 0, 0));

        Mat4 matTrans3 = new Mat4Identity();
        matTrans3 = matTrans3.mul(new Mat4Scale(0.5)).mul(new Mat4Transl(0.5, 0, 0.5));

        Mat4 matTrans4 = new Mat4Identity();
        matTrans4 = matTrans4.mul(new Mat4RotZ(45)).mul(new Mat4Scale(0.5)).mul(new Mat4Transl(-0.3, 0, 0));

        pyramid = new Pyramid();
        pyramid.setModel(matTrans);

        pyramid2 = new Pyramid();
        pyramid2.setModel(matTrans3);

        minecraft = new Minecraft();
        minecraft.setModel(matTrans2);

        cube = new Cube();
        cube.setModel(matTrans4);

        solids.add(pyramid);
        solids.add(pyramid2);
        solids.add(cube);
        activeSolid = solids.get(activeSolidIndex);
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
                } else if (e.getKeyCode() == KeyEvent.VK_N) {
                    if (activeSolidIndex == solids.size() - 1) {
                        activeSolidIndex = 0;
                    } else {
                        activeSolidIndex++;
                    }
                    activeSolid = solids.get(activeSolidIndex);
                } else if (e.getKeyCode() == KeyEvent.VK_Q) {
                    Mat4 activeMat = activeSolid.getModel();
                    Mat4 newMat = activeMat.inverse().get();
                    activeSolid.setModel(activeMat.mul(newMat).mul(new Mat4RotY(-0.5)).mul(activeMat));
                } else if (e.getKeyCode() == KeyEvent.VK_E) {
                    Mat4 activeMat = activeSolid.getModel();
                    Mat4 newMat = activeMat.inverse().get();
                    activeSolid.setModel(activeMat.mul(newMat).mul(new Mat4RotY(0.5)).mul(activeMat));
//                    activeSolid.setModel(activeSolid.getModel().mul(new Mat4RotY(0.5)));
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    activeSolid.setModel(activeSolid.getModel().mul(new Mat4Transl(0, 0, 0.5)));
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    activeSolid.setModel(activeSolid.getModel().mul(new Mat4Transl(0, 0, -0.5)));
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    activeSolid.setModel(activeSolid.getModel().mul(new Mat4Transl(0.5, 0, 0)));
                } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    activeSolid.setModel(activeSolid.getModel().mul(new Mat4Transl(-0.5, 0, 0)));
                } else if (e.getKeyCode() == KeyEvent.VK_B) {
                    Mat4 activeMat = activeSolid.getModel();
                    Mat4 newMat = activeMat.inverse().get();
                    activeSolid.setModel(activeMat.mul(newMat).mul(new Mat4Scale(1.5)).mul(activeMat));
//                    activeSolid.setModel(activeSolid.getModel().mul(new Mat4Scale(1.5)));
                } else if (e.getKeyCode() == KeyEvent.VK_V) {
                    Mat4 activeMat = activeSolid.getModel();
                    Mat4 newMat = activeMat.inverse().get();
                    activeSolid.setModel(activeMat.mul(newMat).mul(new Mat4Scale(0.5)).mul(activeMat));
//                    activeSolid.setModel(activeSolid.getModel().mul(new Mat4Scale(0.5)));
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

        renderer3D.render(solids);

        Point3D[] points = new Point3D[]{
                new Point3D(0,0,0), new Point3D(0.25,0.5,0.1), new Point3D(0.5,0.8,0.2), new Point3D(0.75,0.5,0.1),
                new Point3D(1,0,0), new Point3D(0.25,0.5,0.5), new Point3D(0.5,0.8,0.7), new Point3D(0.75,0.5,0.5),
                new Point3D(0,1,0), new Point3D(0.25,0.5,0.1), new Point3D(0.5,0.8,0.2), new Point3D(0.75,0.5,0.1),
                new Point3D(0,0,0), new Point3D(0.25,0.5,0.5), new Point3D(0.5,0.8,0.7), new Point3D(0.75,0.5,0.5),
        };

        Solid bezierBicubic = new Surface(Cubic.COONS, points);
        renderer3D.render(bezierBicubic);
        renderer3D.render(minecraft);

        panel.repaint();
    }
}
