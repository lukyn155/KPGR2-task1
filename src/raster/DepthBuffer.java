package raster;

public class DepthBuffer implements Raster<Double> {
    private final double[][] buffer;
    private final int width, height;


    public DepthBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new double[width][height];
    }

    @Override
    public void clear() {
        // TODO:
    }

    @Override
    public void setDefaultValue(Double value) {
        // TODO:
    }

    @Override
    public int getWidth() {
        // TODO:
        return 0;
    }

    @Override
    public int getHeight() {
        // TODO:
        return 0;
    }

    @Override
    public Double getValue(int x, int y) {
        // TODO:
        return null;
    }

    @Override
    public void setValue(int x, int y, Double value) {
        // TODO:
    }
}
