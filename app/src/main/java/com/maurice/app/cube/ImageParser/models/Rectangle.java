package com.maurice.app.cube.ImageParser.models;


import com.maurice.app.cube.utils.Logg;

import org.opencv.core.MatOfPoint;
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
    public Rectangle(MatOfPoint matOfPoint){
        if(matOfPoint.rows()==4){
            this.lt = new Point(matOfPoint.get(0,0)[0],matOfPoint.get(0,0)[1]);
            this.rt = new Point(matOfPoint.get(1,0)[0],matOfPoint.get(1,0)[1]);
            this.rb = new Point(matOfPoint.get(2,0)[0],matOfPoint.get(2,0)[1]);
            this.lb = new Point(matOfPoint.get(3,0)[0],matOfPoint.get(3,0)[1]);
        }
    }

    public void print() {
        Logg.d("RECT","Points = lt:"+lt.toString()+" rt:"+rt.toString()+" lb:"+lb.toString()+" rb:"+rb.toString()+"");
    }

    public double getArea(){
        return Math.abs(((lt.x*rt.y - rt.x*lt.y) + (rt.x*rb.y - rb.x*rt.y) + (rb.x*lb.y - lb.x*rb.y) + (lb.x*lt.y - lt.x*lb.y)))/2;
    }
}
