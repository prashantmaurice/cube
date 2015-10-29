package com.maurice.app.cube.ImageParser.models;


import org.opencv.core.Point;

/**
 * Created by maurice on 23/08/15.
 */
public class Rectangle {
    public Point lt,rt,rb,lb;
    double length;
    double angle;

    public Rectangle(Point lt, Point rt, Point lb,Point rb){
        this.lt = lt;
        this.rt = rt;
        this.lb = lb;
        this.rb = rb;
    }
    public Rectangle(double size){
        this.lt = new Point(0,0);
        this.rt = new Point(size,0);
        this.lb = new Point(0,size);
        this.rb = new Point(size,size);
    }
    public Rectangle(Rectangle rectangle) {
        this.lt = rectangle.lt;
        this.rt = rectangle.rt;
        this.lb = rectangle.lb;
        this.rb = rectangle.rb;
    }
        public Rectangle(double width, double height){
        this.lt = new Point(0,0);
        this.rt = new Point(width,0);
        this.lb = new Point(0,height);
        this.rb = new Point(width,height);
    }
}
