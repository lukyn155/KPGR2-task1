package raster;

import transforms.Col;

public class ZBuffer {
    private final ImageBuffer imageBuffer;
    private final DepthBuffer depthBuffer;

    public ZBuffer(ImageBuffer imageBuffer) {
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());
    }

    public void setPixelWithZTest(int x, int y, double z, Col color) {
        // Validace Z hodnoty
        if (z >= 0 && z <= 1) {
            // načtu hiodnotu z depth bufferu
            // provonám načtenou hodnotou s hodnotou Z, která vstupuje do metody
            if (depthBuffer.getValue(x, y) != null && depthBuffer.getValue(x, y) > z) {
                depthBuffer.setValue(x, y, z);
                imageBuffer.setValue(x, y, color);
            }
        }
    }

    public void setDefault() {
        depthBuffer.clear();
    }

    public int getWindowWidth() {
        return imageBuffer.getWidth();
    }

    public int getWindowHeight() {
        return imageBuffer.getWidth();
    }
}
