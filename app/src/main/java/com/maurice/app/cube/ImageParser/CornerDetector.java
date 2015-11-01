package com.maurice.app.cube.ImageParser;

import com.maurice.app.cube.utils.Logg;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

/**
 * Created by maurice on 01/11/15.
 */
public class CornerDetector {

    static int CIRCLE_RADIUS = 10;
    static ArrayList<Point> outboundPoints = getOutboundPoints();
    static int POINTS = outboundPoints.size();

    private static ArrayList<Point> getOutboundPoints(){
        // { col, row}
        ArrayList<Point> outboundPoints = new ArrayList<>();
        for(int i=-CIRCLE_RADIUS;i<CIRCLE_RADIUS;i++){
            outboundPoints.add(new Point(i,-CIRCLE_RADIUS));
        }
        for(int i=-CIRCLE_RADIUS;i<CIRCLE_RADIUS;i++){
            outboundPoints.add(new Point(CIRCLE_RADIUS,i));
        }
        for(int i=CIRCLE_RADIUS;i>-CIRCLE_RADIUS;i--){
            outboundPoints.add(new Point(i,CIRCLE_RADIUS));
        }
        for(int i=CIRCLE_RADIUS;i>-CIRCLE_RADIUS;i--){
            outboundPoints.add(new Point(CIRCLE_RADIUS,i));
        }

        for(Point p : outboundPoints){
            Logg.d("IIIII","Point : "+p.toString());
        }
        Logg.d("IIIII","------");

        return outboundPoints;
    }


    /**
     *
     * @param srcGry : this is a  canny detector result
     */
    public static Mat findCorners(Mat srcEdges, Mat srcGry) {
        //        srcEdges = dilate(srcEdges,1);

//        ArrayList<LineSegment> segments = ImageParser.findLines(srcEdges);
//        Mat color2 = new Mat();
//        Imgproc.cvtColor(srcEdges, color2, Imgproc.COLOR_GRAY2BGR);
//        int maxCount = 20;
//        for(LineSegment lineSegment : segments){
//            if(maxCount--<0) break;
//            Imgproc.line(color2, lineSegment.point1, lineSegment.point2, new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255), 1);
//        }
//        if(true) return color2;





        Mat dilatedEdges = ImageParser.dilate(srcEdges,2);
//        Mat eroded = srcEdges.clone();
        long start = System.currentTimeMillis();
        int count = 0;
        Mat color0 = new Mat();
        Imgproc.cvtColor(srcEdges, color0, Imgproc.COLOR_GRAY2BGR);
        for(int i=CIRCLE_RADIUS;i<srcGry.rows()-CIRCLE_RADIUS;i=i+4){
            for(int j=CIRCLE_RADIUS;j<srcGry.cols()-CIRCLE_RADIUS;j=j+4){

//                ArrayList<Integer> values = getOutBoundArr(new Point(i, j), srcGry);

//                Logg.d("DEBUGHH","is : "+srcGry.get(i,j)[0]);
                if(dilatedEdges.get(i,j)[0]>=200){
                    boolean isCorner = isPointCorner(new Point(j, i), srcGry);
//
                    if(isCorner){
                        if(count++>50) break;
//                        Logg.d("DEBUGHH", "match : " + (new Point(i, j)).toString() + " : " + srcGry.get(i, j)[0]);
                        Imgproc.line(color0, new Point(j, i), new Point(j + 1, i), new Scalar(255,100,100), 3);
                    }else{
                        Imgproc.line(color0, new Point(j, i), new Point(j + 1, i), new Scalar(100,100,100), 3);
                    }
                }
            }
        }
        Logg.d("DEBUGTTTT",count+"Corners found in  : "+(System.currentTimeMillis()-start)+" ms");
        return color0;
    }


    private static boolean isPointCorner(Point p, Mat mat){
        ArrayList<Integer> outValues = getOutBoundArr(p, mat);
        ArrayList<Integer> transitions = new ArrayList<>();

        //Find transitions
        for(int i=0;i<outValues.size();i++){
            if(Math.abs(outValues.get(i)-outValues.get((i+1)%POINTS))>150){
                transitions.add(i);
            }
        }

        //Find percent border covered
        int covered = 0;
        for(int i=0;i<outValues.size();i++){
            if(outValues.get(i)>200){
                covered++;
            }
        }
        double percentCovered = (double)covered/outValues.size();
        if(transitions.size()!=2) return false;

        if(true) if(percentCovered<0.5){
            return true;
        }else{
            return false;
        }




//        Logg.d("TRANSITIONS",""+transitions.size());
        if(transitions.size()!=2) return false;
//
//        //Find transitions that are not from straight lines
//        for(Integer t1 : transitions){
//            for(Integer t2 : transitions){
//                if(t1.equals(t2)) continue;
//                int dist =  Math.abs(t1-t2);
//                if(dist>POINTS/2) dist = POINTS-dist;
//                if(dist>POINTS/3){
////                    return false;
//                }
//
//            }
//        }

        return true;


    }

    //returns array of values in outbound circle
    private static ArrayList<Integer> getOutBoundArr(Point p, Mat mat) {
        ArrayList<Integer> values = new ArrayList<>();
        for(Point pTest : outboundPoints){
            values.add((int) mat.get((int)(p.y+pTest.y),(int)(p.x+pTest.x))[0]);;
        }
        return values;
    }


}
