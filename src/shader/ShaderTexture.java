package shader;

import solid.Vertex;
import transforms.Col;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ShaderTexture implements Shader {
    private BufferedImage texture;

    public ShaderTexture() {
        try {
            texture = ImageIO.read(new File("./res/bricks.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public Col getColor(Vertex v) {
        int x = (int) (v.getUv().getX() * (texture.getWidth() - 1));
        int y = (int) (v.getUv().getY() * (texture.getHeight() - 1));
        return new Col(texture.getRGB(x, y));
    }
}
