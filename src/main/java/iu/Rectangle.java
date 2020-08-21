package iu;


public class Rectangle {

    public int getRect_id() {
        return rect_id;
    }
    public void setRect_id(int rect_id) {
        this.rect_id = rect_id;
    }

    public Rectangle(int rect_id, double d, double e, double f, double g) {
        super();
        this.rect_id = rect_id;
        this.xlow = d;
        this.ylow = e;
        this.xhigh = f;
        this.yhigh = g;
    }
    public Rectangle() {
    }
    public double getYlow() {
        return ylow;
    }
    public void setYlow(double ylow) {
        this.ylow = ylow;
    }
    public double getYhigh() {
        return yhigh;
    }
    public void setYhigh(double yhigh) {
        this.yhigh = yhigh;
    }
    int rect_id;
    double xlow;
    double ylow;
    double xhigh;
    double yhigh;

}
