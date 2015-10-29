package com.maurice.app.cube.ImageParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.maurice.app.cube.ImageParser.models.LineSegment;
import com.maurice.app.cube.ImageParser.models.Rectangle;
import com.maurice.app.cube.MainActivity;
import com.maurice.app.cube.utils.Logg;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by maurice on 16/08/15.
 *
 *
 *
 */




public class ImageParser {
    Context mContext;
    static ImageParser instance;
    static String TAG = "IMAGEPARSER";

    //Image Processing Params
    double lineDetect_threshold1 = 50;
    double lineDetect_threshold2 = 200;
    int digit_learn_size = 40;

    //board Params
    static final int BOARD_SIZE = 9;
    static int IMAGE_WIDTH = 100;
    static int IMAGE_HEIGHT = 100;

    Rectangle[][] rectangles;



    private ImageParser(Context context){
        mContext = context;
    }

    public static ImageParser getInstance(Context context){
        if(instance==null) instance = new ImageParser(context);
        return instance;
    }


    /** MAIN FUNCTION TO GET A CALCULATED BITMAP*/
    public Bitmap parseBitmap(Bitmap bitmap){
        long start = System.currentTimeMillis();

        //Convert image to Mat
        Mat mat = GenUtils.convertBitmapToMat(bitmap);

        //Apply Transformations
        mat = processMat(mat);

        Logg.d("TIME", "Main Processing completed in " + (System.currentTimeMillis() - start) + " ms");

        //Convert back Mat to bitmap
        return GenUtils.convertMatToBitmap(mat);
    }

    public Mat processMat(Mat src){


        Rectangle rect = getrectangle(src);


        //get NumberImages From Boxes
//        Mat[][] numbersCrop = getCroppedMats(src);
//        MainActivity.setDebugImage(numbersCrop[1][7],0);

//        if(true)return getdebug(src);
//        Mat colorPic = new Mat();
//        Imgproc.cvtColor(src3, colorPic, Imgproc.COLOR_GRAY2BGR);

        //Draw Lines
//        for(LineSegment lineSegment : filteredSegments){
//            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(Math.random()*255, Math.random()*255,Math.random()*255), 3);
//            Imgproc.line(colorPic, lineSegment.point1, lineSegment.point2, new Scalar(255, 0,0), (int) (units*4));
//        }



//        DigitRecogniser2 digitRecogniser2 = DigitRecogniser2.getInstance(mContext);
//        MainActivity.setDebugImage(digitRecogniser2.finalMap.get(2),1);

//        if(true) return src;
//        int[][] digits = digitRecogniser2.recogniseDigits(numbersCrop);

        //Get solved Solution
//        int[][] solved = SudokuAI.getSolved(digits);
//        GenUtils.printBoard(solved);
//
//
//        for(int i=0;i<rectangles.length;i++){
//            for(int j=0;j<rectangles[0].length;j++) {
////                if(digits[i][j]==0){
//                    int font = Core.FONT_HERSHEY_SIMPLEX;
//                    Rectangle rect = rectangles[i][j];
//                    Point origin = new Point((rect.lb.x+rect.rb.x)/2-6, (rect.lb.y+rect.lt.y)/2+5);
//                    Imgproc.putText(src, ""+solved[i][j], origin, font, 1, new Scalar(10, 10, 150,100),4);
////                }
//            }
//        }
//
//        //Set solved digits on top of image
//        Mat temp = digitRecogniser2.mapDigitPics.get(2);
//        temp.copyTo(src.rowRange(0, temp.rows()).colRange(0, temp.cols()));


        return src;
//        return extractROI(numbersCrop[0][2]);


//        DigitRecogniser digitRecogniser = new DigitRecogniser(mContext);
//        digitRecogniser.recogniseDigit(boxesCrop[0][2]);
//        return extractROI(boxesCrop[0][2]).mul(digitRecogniser.mapArrayNormal.get(1));

//        return colorPic;
//        return wrapPerspectiveCustom(colorPic, new Rectangle(new Point(0, 0), new Point(300, 0), new Point(0, 600), new Point(300, 600)));
//        Imgproc.line(colorPic, new Point(0,100), new Point(900,100), new Scalar(255, 250,0), 30);
//        return wrapPerspectiveCustom(colorPic, new Rectangle(100));
    }

    private Rectangle getrectangle(Mat src) {
        double units = (float) src.width()/200;
        Logg.d(TAG,"Image size units : "+units);

        //Pre process image
//        src = new Mat(src.size(), CvType.CV_8UC1);

        //Grey image
        Mat srcGry = src.clone();
        Imgproc.cvtColor(src, srcGry, Imgproc.COLOR_RGB2GRAY);
        MainActivity.setDebugImage(srcGry,0);

        //Blur the image
        int blurRadius = (int) (units*1);
        Mat srcBlr = new Mat(srcGry.size(), CvType.CV_8UC1);
        srcGry.copyTo(srcBlr);
        MainActivity.setDebugImage(srcGry, 1);
//        GaussianBlur(srcGry, srcBlr, new Size(blurRadius, blurRadius), 0);


        //Create an adaptive threshold for parsing and inverting image
//        Mat src3 = new Mat(srcGry.size(), CvType.CV_8UC1);
//        Imgproc.adaptiveThreshold(srcGry, src3, 255, Imgproc.T, Imgproc.THRESH_BINARY_INV, 15, 4);
//        MainActivity.setDebugImage(src3, 2);
//        Imgproc.threshold(srcGry, src3, 100,255, Imgproc.THRESH_BINARY_INV);

        /// Canny detector
        Mat srcEdges = new Mat(srcGry.size(), CvType.CV_8UC1);
        int lowThreshold = 5;
        int maxThreshold = 200;
        int kernel_size = 3;
        Imgproc.Canny(srcBlr, srcEdges, lowThreshold, maxThreshold, kernel_size,true);
        MainActivity.setDebugImage(srcEdges, 2);


        //remove small grains
//        Mat src3 = removeSmallBlobs(srcEdges);
//        MainActivity.setDebugImage(src3,3);

        //Find lines
        ArrayList<LineSegment> segments = findLines(srcEdges);
        Mat color = new Mat();
        Imgproc.cvtColor(srcGry, color, Imgproc.COLOR_GRAY2BGR);
        for(LineSegment lineSegment : segments){
            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(Math.random()*255, Math.random()*255,Math.random()*255), 5);
//            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(200, 0,0), (int)units*10);
////            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(200,0,0), 1);
        }

        MainActivity.setDebugImage(color,4);



        return new Rectangle(10);
    }


//    public Mat getdebug(Mat src){
//        double units = (float) src.width()/200;
//        Logg.d(TAG,"Image size units : "+units);
//
//        //Pre process image
////        src = new Mat(src.size(), CvType.CV_8UC1);
//
//        //Grey image
//        Mat srcGry = src.clone();
//        Imgproc.cvtColor(src, srcGry, Imgproc.COLOR_RGB2GRAY);
//
//        //Blur the image
//        int blurRadius = (int) (units*3);
//        Mat srcBlr = new Mat(srcGry.size(), CvType.CV_8UC1);
//        GaussianBlur(srcGry, srcBlr, new Size(blurRadius, blurRadius), 0);
//
//
//        //Create an adaptive threshold for parsing and inverting image
//        Mat src3 = new Mat(srcGry.size(), CvType.CV_8UC1);
//        Imgproc.adaptiveThreshold(srcGry, src3, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 4);
//        //TODO : may be do a floodfill here
//
//        //CHECK : by here image should be square and perspective propererd
//
//
//
//        //find lines in the image
//        ArrayList<LineSegment> segments = findLines(src3);
//
//
//        //Filtered Line segments : filter from around 130 segements to final 20
//        ArrayList<LineSegment> filteredSegments = filterValidLineSegments(segments);
//
//
//
//
//        //remove original lines
//        Mat color = new Mat();
//        Imgproc.cvtColor(src3, color, Imgproc.COLOR_GRAY2BGR);
//        for(LineSegment lineSegment : filteredSegments){
////            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(Math.random()*255, Math.random()*255,Math.random()*255), 3);
////            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(200, 0,0), (int)units*10);
////            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(200,0,0), 1);
//        }
//
//        // CHECK : after this, no lines should be there in color
//
////        if(0==0) return scaleImageToMaxSize(src, 100);
////        if(0==0) return color;
//
//        //get points array
//        Point[][] points = getKeyPoints(filteredSegments);
//
//        //form rectangles array
//        rectangles = getRectanglesFromPoints(points);//stored for future use
//
//        //Draw Lines so that subsequest floodflill will run smoothly
//        for(LineSegment lineSegment : filteredSegments){
//            Imgproc.line(src3, lineSegment.point1, lineSegment.point2, new Scalar(255, 255,1), 2);
//        }
//
//        Imgproc.line(src3, new Point(0,0), new Point(200,200), new Scalar(255, 255,1), 10);
//
//        return src3;
//
//        //Remove original lines through flood fill
////        Size size = new Size(src3.size().width+2,src3.size().height+2);
////        Mat mask = new Mat(size, CvType.CV_8UC1);
////        Rect rect = new Rect(0, 0, src3.width(),src3.height());
////        Scalar lowDiff = new Scalar(0,0,0);
////        Scalar highDiff = new Scalar(120,120,120);
////        Logg.d(TAG, "Started Floodfliiing...");
////        Imgproc.floodFill(src3, mask, points[0][0], new Scalar(0, 200, 0), rect, lowDiff, highDiff, 0);
////        Logg.d(TAG, "Ended Floodfliiing....");
////
////        return src3;
//    }


//    public Mat[][] getCroppedMats(Mat src){
//
//        double units = (float) src.width()/200;
//        Logg.d(TAG,"Image size units : "+units);
//
//        //Pre process image
////        src = new Mat(src.size(), CvType.CV_8UC1);
//
//        //Grey image
//        Mat srcGry = src.clone();
//        Imgproc.cvtColor(src, srcGry, Imgproc.COLOR_RGB2GRAY);
//
//        //Blur the image
//        int blurRadius = (int) (units*1);
//        Mat srcBlr = new Mat(srcGry.size(), CvType.CV_8UC1);
//        srcGry.copyTo(srcBlr);
////        GaussianBlur(srcGry, srcBlr, new Size(blurRadius, blurRadius), 0);
//
//
//        //Create an adaptive threshold for parsing and inverting image
//        Mat src3 = new Mat(srcGry.size(), CvType.CV_8UC1);
//        Imgproc.adaptiveThreshold(srcGry, src3, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 4);
////        Imgproc.threshold(srcGry, src3, 100,255, Imgproc.THRESH_BINARY_INV);
//
//        //remove small grains
//        src3 = removeSmallBlobs(src3);
////        MainActivity.setMainImage(src3);
//
//        //TODO : may be do a floodfill here
//
//        //CHECK : by here image should be square and perspective propererd
//
//
//
//        //find lines in the image
//        ArrayList<LineSegment> segments = findLines(src3);
//
//
//        //Filtered Line segments : filter from around 130 segements to final 20
//        ArrayList<LineSegment> filteredSegments = filterValidLineSegments(segments);
//
//
//
//
//        //remove original lines
//        Mat color = new Mat();
//        Imgproc.cvtColor(src3, color, Imgproc.COLOR_GRAY2BGR);
//        for(LineSegment lineSegment : filteredSegments){
////            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(Math.random()*255, Math.random()*255,Math.random()*255), 3);
//            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(200, 0,0), (int)units*1);
////            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(0,0,0), (int) (units*26));
//        }
//        MainActivity.setMainImage(color);
//
//        // CHECK : after this, no lines should be there in color
//
////        if(0==0) return scaleImageToMaxSize(src, 100);
////        if(0==0) return color;
//
//        //get points array
//        Point[][] points = getKeyPoints(filteredSegments);
//
//        //form rectangles array
//        rectangles = getRectanglesFromPoints(points);
//
//        //Draw Lines so that subsequest floodflill will run smoothly
//        for(LineSegment lineSegment : filteredSegments){
//            Imgproc.line(src3, lineSegment.point1, lineSegment.point2, new Scalar(255, 0,0), 2);
//        }
//        MainActivity.setMainImage(src3);
//
//        //Remove original lines through flood fill
//        Size size = new Size(src3.size().width+2,src3.size().height+2);
//        Mat mask = new Mat(size, CvType.CV_8UC1);
//        Rect rect = new Rect(0, 0, src3.width(),src3.height());
//        Scalar lowDiff = new Scalar(0,0,0);
//        Scalar highDiff = new Scalar(120,120,120);
//        Logg.d(TAG,"Started Floodfliiing...");
//
//        //Startflood filling at few points
//        Imgproc.floodFill(src3, mask, points[1][0], new Scalar(0, 200, 0), rect, lowDiff, highDiff, 0);
//        Imgproc.floodFill(src3, mask, points[0][0], new Scalar(0, 200, 0), rect, lowDiff, highDiff, 0);
//        Imgproc.floodFill(src3, mask, points[5][5], new Scalar(0, 200, 0), rect, lowDiff, highDiff, 0);
//        Imgproc.floodFill(src3, mask, points[0][1], new Scalar(0, 200, 0), rect, lowDiff, highDiff, 0);
//        Logg.d(TAG, "Ended Floodfliiing....");
//        MainActivity.setMainImage(src3);
//
//        //get ImagesArray
//        Mat[][] boxesCrop = getIndividualBoxes(src3, rectangles);
//
//        //get NumberImages From Boxes
//        Mat[][] numbersCrop = getIndividualNumbers(boxesCrop);
//        return numbersCrop;
//    }

    private Mat extractROI(Mat mat){
        RotatedRect rect = null;
        Mat points = Mat.zeros(mat.size(),mat.type());
        Core.findNonZero(mat, points);

        MatOfPoint mpoints = new MatOfPoint(points);
        MatOfPoint2f points2f = new MatOfPoint2f(mpoints.toArray());

        if (points2f.rows() > 0) {
            rect = Imgproc.minAreaRect(points2f);
        }
        if(rect==null) return wrapPerspectiveCustom(mat, new Rectangle(1),digit_learn_size);


        Rect roi = new Rect();
        roi.x = (int) (rect.center.x - (rect.size.width / 2));
        roi.y = (int) (rect.center.y - (rect.size.height / 2));
        roi.width = (int) rect.size.width;
        roi.height = (int) rect.size.height;

        // Crop the original image to the defined ROI
        return wrapPerspectiveCustom(mat, new Rectangle(new Point(roi.x, roi.y), new Point(roi.x+roi.width, roi.y), new Point(roi.x, roi.y+roi.height), new Point(roi.x+roi.width, roi.y+roi.height)),digit_learn_size);
    }


    private Rectangle[][] getRectanglesFromPoints(Point[][] points) {
        int rows = Arrays.asList(points).size();
        int columns = Arrays.asList(points[0]).size();
        Rectangle[][] rectangles = new Rectangle[rows-1][columns-1];

        for(int i=0;i<rows-1;i++){
            for(int j=0;j<columns-1;j++){
                rectangles[i][j] = new Rectangle(points[i][j],points[i][j+1],points[i+1][j],points[i+1][j+1]);
            }
        }
        return rectangles;
    }

    private ArrayList<LineSegment> findLines(Mat src2){
        Mat src = src2.clone();
//        src = dilate(src,1);
//        MainActivity.setDebugImage(src,0);
//        int maxWidth = 100;
//        float ratio = ((float)src.width())/maxWidth;
//        Mat srcScaled = scaleImageToMaxSize(src,maxWidth);
        long startTime = System.currentTimeMillis();
//        Log.d(TAG, "Ratio :  "+ratio);

        //Find lines in the image
        int threshold = 40;//The minimum number of intersections to “detect” a line
        int minLinelength = 20;//The minimum number of points that can form a line. Lines with less than this number of points are disregarded.
        int maxlineGap = 3;//The maximum gap between two points to be considered in the same line.
        Mat lines = new Mat();
        Log.d(TAG, "REACH1 " + (System.currentTimeMillis() - startTime) + " ms");
        Imgproc.HoughLinesP(src, lines, 25, Math.PI / 180, threshold, minLinelength, maxlineGap);
        Log.d(TAG, "REACH2 " + (System.currentTimeMillis() - startTime) + " ms");

        //draw color lines on the image
        ArrayList<LineSegment> segments = new ArrayList<>();
        for (int x = 0; x < lines.rows(); x++)
        {
            double[] vec = lines.get(x,0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            segments.add(new LineSegment(start, end));
        }
        Log.d(TAG, segments.size()+" lines detected in image in "+(System.currentTimeMillis()-startTime)+" ms");
        return segments;
    }

    private ArrayList<LineSegment> filterValidLineSegments(ArrayList<LineSegment> segments){

        ArrayList<LineSegment> filtered = segments;
        filtered = filterSimilarLines(filtered);
        return filtered;
    }

//    private ArrayList<Point> getBoundaryPoints(ArrayList<LineSegment> segments){
//
//    }

    private ArrayList<LineSegment> filterSimilarLines(ArrayList<LineSegment> segments ){
        //params
        double minPointDistance = 60;
        double minangleDistance = 20*(Math.PI/180);

        ArrayList<LineSegment> filtered = new ArrayList<>();
        ArrayList<Integer> rejectedIndices = new ArrayList<>();

        for(int i=0;i<segments.size();i++){
            for(int j=0;j<segments.size();j++){
                if(i==j) continue;
                if(rejectedIndices.contains(i)||rejectedIndices.contains(j)) continue;
                if(Math.abs(segments.get(i).getAngle()-segments.get(j).getAngle())>minangleDistance) continue;//perpendicular lines
                double p1Dist = (new LineSegment(segments.get(i).point1,segments.get(j).point1)).getLength();
                double p2Dist = (new LineSegment(segments.get(i).point2,segments.get(j).point2)).getLength();
                if(p1Dist+p2Dist<minPointDistance) rejectedIndices.add(i);
            }
        }

        //remove segments that are not horizontal or vertical
        for(int i=0;i<segments.size();i++){
            double angle = Math.abs(GenUtils.getAngleFromradians(segments.get(i).getAngle()));
            Logg.d(TAG, "Angle : " + angle);
            if(rejectedIndices.contains(i)) continue;
            if(angle<20) continue;
            if(angle>70&&angle<110) continue;
            if(angle>160&&angle<200) continue;
            rejectedIndices.add(i);
        }

        //Finally add in filtered array
        for(int i=0;i<segments.size();i++){//find highest
            if(!rejectedIndices.contains(i)){
                filtered.add(segments.get(i));
                Logg.d("SELECTED LINE",segments.get(i).point1.toString());
            }


        }

        Log.d(TAG, "Filtered similar lines : "+filtered.size()+" segs from "+segments.size()+" segs");
        return filtered;

    }

    /**
     *  use this to crop an image to required size
     *
     */
    private Mat wrapPerspectiveCustom(Mat src, Rectangle rect){
        return wrapPerspectiveCustom(src,rect,200);
    }
    private Mat wrapPerspectiveCustom(Mat src, Rectangle rect, int size){
        //points are in order  top-left, top-right, bottom-right, bottom-left

        Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
        Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);

        Rectangle dest = new Rectangle(size);

        src_mat.put(0, 0, rect.lt.x, rect.lt.y, rect.rt.x, rect.rt.y, rect.lb.x, rect.lb.y, rect.rb.x, rect.rb.y);
        dst_mat.put(0, 0, dest.lt.x, dest.lt.y, dest.rt.x, dest.rt.y, dest.lb.x, dest.lb.y, dest.rb.x, dest.rb.y);
        Mat perspectiveTransform=Imgproc.getPerspectiveTransform(src_mat, dst_mat);

        Mat dst=src.clone();

        Imgproc.warpPerspective(src, dst, perspectiveTransform, new Size(Math.abs(dest.lt.x - dest.rt.x), Math.abs(dest.lt.y - dest.lb.y)));

        return dst;

    }

    private Point[][] getKeyPoints(ArrayList<LineSegment> filteredSegments){
        //Segregate vertical and horizontal segements
        ArrayList<LineSegment> vertical = new ArrayList<>();
        ArrayList<LineSegment> horizontal = new ArrayList<>();
        for(LineSegment segment : filteredSegments){
            if(Math.abs(Math.abs(segment.getAngle())-Math.PI/2)<Math.PI/4){
                vertical.add(segment);
            }else{
                horizontal.add(segment);
            }
        }

        //sort horizontal
        Collections.sort(horizontal, new Comparator<LineSegment>() {
            @Override
            public int compare(final LineSegment object1, final LineSegment object2) {
                return (int) (object1.point1.y - (object2.point1.y));
            }
        });

        //sort vertical
        Collections.sort(vertical, new Comparator<LineSegment>() {
            @Override
            public int compare(final LineSegment object1, final LineSegment object2) {
                return (int) (object1.point1.x - (object2.point1.x));
            }
        });

        //Find key points as intersecting points
        Point [][] twoDim = new Point [horizontal.size()][vertical.size()];
        for(int i = 0; i < horizontal.size(); i++){
            for(int j = 0; j <vertical.size(); j++) {
                Point intersect = GenUtils.intersectingPoint(horizontal.get(i),vertical.get(j));
                twoDim[i][j] = intersect;
            }
        }
        Log.d(TAG, "2D ARRAY : " + GenUtils.print2dArray(twoDim));
        return twoDim;
    }
//
    private Mat[][] getIndividualBoxes(Mat mat, Rectangle[][] rects){
        Mat clone = mat.clone();
        int rows = Arrays.asList(rects).size();
        int columns = Arrays.asList(rects[0]).size();
        Mat[][] matArr = new Mat[rows][columns];

        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                matArr[i][j] = wrapPerspectiveCustom(clone, rects[i][j],digit_learn_size);
            }
        }
        return matArr;
    }

    /**
     *  Image with border and text inside -> text with 40x40 size
     */
    private Mat[][] getIndividualNumbers(Mat[][] boxes){
        int rows = boxes.length;
        int columns = boxes[0].length;
        Mat[][] matArr = new Mat[rows][columns];

        for(int i=0;i<boxes.length;i++){
            for(int j=0;j<boxes[0].length;j++){
                matArr[i][j] = extractText(boxes[i][j]);
            }
        }
        return matArr;
    }

    private Mat extractText(Mat src) {
//        Size size = new Size(src.size().width+2,src.size().height+2);
//        Mat mask = new Mat(size, CvType.CV_8UC1);
//        Rect rect = new Rect(0, 0, src.width(),src.height());
//        Scalar lowDiff = new Scalar(0,0,0);
//        Scalar highDiff = new Scalar(254,254,254);
//        Logg.d(TAG,"Started Floodfliiing");
//        Imgproc.floodFill(src, mask, new Point(2, 2), new Scalar(0, 200, 0), rect, lowDiff, highDiff, Imgproc.FLOODFILL_FIXED_RANGE);
//        Logg.d(TAG, "Ended Floodfliiing");
        return extractROI(src);
    }


    private Mat scaleImageToMaxSize(Mat src, int size){
        return wrapPerspectiveCustom(src, new Rectangle(new Point(0, 0), new Point(src.height(), 0), new Point(0, src.width()), new Point(src.height(), src.width())), size);
    }


    //Erosion and Dilation
    private Mat erode(Mat src, int erosion_size){
        Mat src_mat=new Mat(src.size(),CvType.CV_8UC1,new Scalar(0,0,0,0));
        Imgproc.erode(src,src_mat,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                new Size( 2*erosion_size + 1, 2*erosion_size+1 ),
                new Point( erosion_size, erosion_size ) ));

        return src_mat;
    }
    private Mat dilate(Mat src, int erosion_size){
        Mat src_mat=new Mat(src.size(),CvType.CV_8UC1,new Scalar(0,0,0,0));
        Imgproc.dilate(src, src_mat, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                new Size(2 * erosion_size + 1, 2 * erosion_size + 1),
                new Point(erosion_size, erosion_size)));

        return src_mat;
    }
    private Mat removeSmallBlobs(Mat src){
        return dilate(erode(src, 1), 1);
    }





}
