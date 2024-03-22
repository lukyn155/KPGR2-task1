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
        // TODO: implementovat
        // načtu hiodnotu z depth bufferu
        // provonám načtenou hodnotou s hodnotou Z, která vstupuje do metody
        // vyhodnotím podmínku
        // podle podmínky, buď:
        // a) skončit - nic se nestane
        // b) obarvím, upravím hodnotu v depth bufferu
        imageBuffer.setValue(x, y, color);
    }
}
