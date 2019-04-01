package sample;

public class Box {
    public int startFrame;
    public int endFrame;
    public int linkFrame;
    public int boxX;
    public int boxY;
    public int width;
    public int height;
    public String target;
    public Box(int startFrame, int endFrame, int linkFrame, int boxX, int boxY, int width, int height, String target) {
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.linkFrame = linkFrame;
        this.boxX = boxX;
        this.boxY = boxY;
        this.width = width;
        this.height = height;
        this.target = target;
    }
}
