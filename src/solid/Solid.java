package solid;

import shader.Shader;
import shader.ShaderInterpolated;
import transforms.Mat4;
import transforms.Mat4Identity;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Solid {
    protected  final ArrayList<Part> partBuffer = new ArrayList<>();
    protected  final ArrayList<Vertex> vertexBuffer = new ArrayList<>();
    protected  final ArrayList<Integer> indexBuffer = new ArrayList<>();

    protected Shader shader = new ShaderInterpolated();
    private Mat4 model = new Mat4Identity();

    public void setModel(Mat4 model) {
        this.model = model;
    }

    protected void addIndices(Integer... indices) {
        indexBuffer.addAll(Arrays.asList(indices));
    }
    public Mat4 getModel() {
        return model;
    }

    public ArrayList<Part> getPartBuffer() {
        return partBuffer;
    }

    public ArrayList<Vertex> getVertexBuffer() {
        return vertexBuffer;
    }

    public ArrayList<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public Shader getShader() {
        return shader;
    }
    public void setShader(Shader shader) {
        this.shader = shader;
    }
}
