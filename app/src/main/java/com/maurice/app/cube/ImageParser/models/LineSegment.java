package com.maurice.app.cube.ImageParser.models;


import org.opencv.core.Point;

/**
 * Created by maurice on 23/08/15.
 */
public class LineSegment {
    public Point point1,point2;
    double length;
    double angle;

    public LineSegment(Point p1, Point p2){
        this.point1 = p1;
        this.point2 = p2;
        //Make sure point that is smallest is point1
        if((point2.x*point2.x+point2.y*point2.y)<(point1.x*point1.x+point1.y*point1.y)){
            this.point1 = p2;
            this.point2 = p1;
        }

        this.length = Math.sqrt(((point1.x - point2.x) * (point1.x - point2.x)) + ((point1.y - point2.y) * (point1.y - point2.y)));
        this.angle = Math.atan2(point1.y - point2.y, point1.x - point2.x);


    }

    public double getLength(){
//        if(length!=0)
            return length;
//        return Math.sqrt(((point1.x - point2.x) * (point1.x - point2.x)) + ((point1.y - point2.y) * (point1.y - point2.y)));
    }
    public double getAngle(){
        return angle;
    }

}
