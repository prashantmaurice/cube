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
            //Sort them
            double minPoint = 10000;
            int startIndex = 0;
            for(int i=0;i<4;i++){
                if(matOfPoint.get(i,0)[0]+matOfPoint.get(i,0)[1]<minPoint){
                    startIndex = i;
                    minPoint = matOfPoint.get(i,0)[0]+matOfPoint.get(i,0)[1];
                }
            }


            this.lt = new Point(matOfPoint.get((startIndex+0)%4,0)[0],matOfPoint.get((startIndex+0)%4,0)[1]);
            this.lb = new Point(matOfPoint.get((startIndex+1)%4,0)[0],matOfPoint.get((startIndex+1)%4,0)[1]);
            this.rb = new Point(matOfPoint.get((startIndex+2)%4,0)[0],matOfPoint.get((startIndex+2)%4,0)[1]);
            this.rt = new Point(matOfPoint.get((startIndex+3)%4,0)[0],matOfPoint.get((startIndex+3)%4,0)[1]);
        }




        //sort
//        sortRectangle();
    }

    public Rectangle mul(double value){
        lt = new Point(value * lt.x,value * lt.y);
        lb = new Point(value * lb.x,value * lb.y);
        rb = new Point(value * rb.x,value * rb.y);
        rt = new Point(value * rt.x,value * rt.y);
        return this;
    }
    public Rectangle add(Rectangle rect){
        lt = new Point(rect.lt.x + lt.x,rect.lt.y + lt.y);
        lb = new Point(rect.lb.x + lb.x,rect.lb.y + lb.y);
        rb = new Point(rect.rb.x + rb.x,rect.rb.y + rb.y);
        rt = new Point(rect.rt.x + rt.x,rect.rt.y + rt.y);
        return this;
    }

    public void sortRectangle(){
        if(lt.y<rt.y){
            Point temp = lt;
            lt = rt;
            rt = temp;
        }
        if(lb.y<rb.y){
            Point temp = lb;
            lb = rb;
            rb = temp;
        }
        if(lt.x>lb.x){
            Point temp = lt;
            lt = lb;
            lb = temp;
        }
        if(rt.x>rb.x){
            Point temp = rt;
            rt = rb;
            rb = temp;
        }
    }

    public void print() {
        Logg.d("RECT","Points = lt:"+lt.toString()+" rt:"+rt.toString()+" lb:"+lb.toString()+" rb:"+rb.toString()+"");
    }

    public double getArea(){
        return Math.abs(((lt.x*rt.y - rt.x*lt.y) + (rt.x*rb.y - rb.x*rt.y) + (rb.x*lb.y - lb.x*rb.y) + (lb.x*lt.y - lt.x*lb.y)))/2;
    }

}
