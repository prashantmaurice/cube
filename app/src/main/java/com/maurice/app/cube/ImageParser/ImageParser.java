package com.maurice.app.cube.ImageParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.maurice.app.cube.CameraActivity;
import com.maurice.app.cube.ImageParser.models.LineSegment;
import com.maurice.app.cube.ImageParser.models.Rectangle;
import com.maurice.app.cube.ImageProcessException;
import com.maurice.app.cube.MainActivity;
import com.maurice.app.cube.R;
import com.maurice.app.cube.utils.Logg;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    static GifController gifController;
    Mat image;//Image to be printed on screen


    private ImageParser(Context context){
        mContext = context;
    }

    public static ImageParser getInstance(Context context){
        if(instance==null) instance = new ImageParser(context);
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);
//        Mat imageMat = GenUtils.convertBitmapToMat(bitmap);
//        gifController = GifController.getInstance(context);
        return instance;
    }


    /** MAIN FUNCTION TO GET A CALCULATED BITMAP*/
    public Bitmap parseBitmap(Bitmap bitmap) throws ImageProcessException{
        long start = System.currentTimeMillis();

        //Convert image to Mat
        Mat mat = GenUtils.convertBitmapToMat(bitmap);

        Logg.d("TIME", "B1 : " + (System.currentTimeMillis() - start) + " ms");

        //Apply Transformations
        mat = processMat(mat);

        Logg.d("TIME", "Main Processing completed in " + (System.currentTimeMillis() - start) + " ms");

        //Convert back Mat to bitmap
        return GenUtils.convertMatToBitmap(mat);
    }

    public Mat processMat(Mat src) throws ImageProcessException{
        int width = 864;
        int height = 480;

        int scale = 2;
        Size size = new Size(width/scale,height/scale);//the dst image size,e.g.100x100

        Mat dst = new Mat(size,src.type());//dst image
        Imgproc.resize(src, dst, size);

        //Process the Image
        Mat pros =  getProcessedMat(dst);

        Mat dst2 = new Mat(src.size(),src.type());
        Imgproc.resize(pros, dst2,src.size());

        return dst2;

        //reduce Image size
//        return  scaleImageToMaxSize(src,200);

//        return getProcessedMat(src);


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



//        DigitRecogniser2 digitRecogniser2 = DigitRecogniser2.getInstance(mContext);=
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


//        return src;
//        return extractROI(numbersCrop[0][2]);


//        DigitRecogniser digitRecogniser = new DigitRecogniser(mContext);
//        digitRecogniser.recogniseDigit(boxesCrop[0][2]);
//        return extractROI(boxesCrop[0][2]).mul(digitRecogniser.mapArrayNormal.get(1));

//        return colorPic;
//        return wrapPerspectiveCustom(colorPic, new Rectangle(new Point(0, 0), new Point(300, 0), new Point(0, 600), new Point(300, 600)));
//        Imgproc.line(colorPic, new Point(0,100), new Point(900,100), new Scalar(255, 250,0), 30);
//        return wrapPerspectiveCustom(colorPic, new Rectangle(100));
    }

    private Mat getProcessedMat(Mat src) throws ImageProcessException{
        double units = (float) src.width()/200;
        Logg.d(TAG, "Image size units : " + units);

//        Mat hsv = new Mat();
//        Imgproc.cvtColor(src,hsv, Imgproc.COLOR_BGR2HSV);
//
//        //Pre process image
////        src = new Mat(src.size(), CvType.CV_8UC1);
//
//        Scalar lower_blue = new Scalar(110,50,50,0);
//        Scalar upper_blue = new Scalar(110,255,255,255);
//
//        // Threshold the HSV image to get only blue colors
//        Mat mask = new Mat();
//        Core.inRange(hsv, lower_blue, upper_blue, mask);
//
//        //Bitwise-AND mask and original image
//        Core.bitwise_and(src,mask, src);
//        if(true)return mask;


        if(true) return findMarkers(src);

//        if(true) return findFromWhiteMarkers(src);


        //Color detector
        Mat srcDetect = src.clone();
        double[] center = src.get(src.rows() / 2, src.cols() / 2);
        Imgproc.line(srcDetect, new Point(src.cols() / 2, src.rows() / 2), new Point(src.cols() / 2, (src.rows() / 2) + 1), new Scalar(255, 255, 255), 4);
        Imgproc.putText(srcDetect, "" + Arrays.toString(center), new Point(0,100), Core.FONT_HERSHEY_SIMPLEX, 0.3, new Scalar(10, 10, 150, 100),1);
        if(true) return srcDetect;


        //Grey image
        Mat srcGry = src.clone();
        Mat srcHsv = src.clone();
        Imgproc.cvtColor(src, srcHsv, Imgproc.COLOR_BGR2HSV_FULL);
//        List<Mat> channels = new ArrayList<>();
        List<Mat> hsvchannels = new ArrayList<>();
//        Core.split(src, channels);//RGB fashion
        Core.split(srcHsv, hsvchannels);
//        Mat greenchannel = channels.get();
//        Imgproc.threshold(channels.get(0), srcGry, 200, 255, Imgproc.THRESH_BINARY);
//        Imgproc.cvtColor(src, srcGry, Imgproc.COLOR_RGB2GRAY);
//        if(true) return srcHsv;

//        if(true) return srcGry;

        //Find whites
//        Mat whites = src.clone();
//        Imgproc.threshold(srcGry, whites, 255 * 0.62, 255, Imgproc.THRESH_BINARY);


        //Find Blacks
//        Mat blacks = src.clone();
//        Imgproc.threshold(srcGry, blacks, 255 * CameraActivity.seek, 255, Imgproc.THRESH_BINARY_INV);//0.67

        //remove large blacks
//        Mat blacks2 = erode(blacks, (int) (50 * 0.12));
//        blacks2 = dilate(blacks2, (int) (50 * 0.12));
//        Core.bitwise_xor(blacks, blacks2, blacks);

        //remove smaller ones
//        blacks = erode(blacks, (int) (50 * 0.03));
//        blacks = dilate(blacks, (int) (50 * 0.03));

//        if(true) return blacks;

        //remove blacks not near white
//        Mat whites2 = dilate(whites, (int) (30 * 0.15));
//        Core.bitwise_and(blacks, whites2, blacks);


        //find colors
//        Mat reds = srcGry.clone();
//        Imgproc.threshold(hsvchannels.get(1), reds, 255 * 0.80, 255, Imgproc.THRESH_BINARY);//0.67
//        Mat greens = srcGry.clone();
//        Imgproc.threshold(hsvchannels.get(0), greens, 255 * CameraActivity.seek, 255, Imgproc.THRESH_BINARY);//0.48
//        Mat blues = srcGry.clone();
//        Imgproc.threshold(channels.get(2), blues, 255 * CameraActivity.seek, 255, Imgproc.THRESH_BINARY);
//
//        //find pure reds
//        Mat purered = reds.clone();
//        Mat greenAndBlue = reds.clone();
//        Core.bitwise_or(greens, blues, greenAndBlue);
//        Core.bitwise_xor(reds, greenAndBlue, purered);
//        Core.bitwise_and(purered, reds, purered);


//        int range = 40;
        Mat purereds = src.clone();
        Mat purereds2 = src.clone();
        Imgproc.threshold(hsvchannels.get(0), purereds, 255 * 0.62, 255, Imgproc.THRESH_BINARY);
        Imgproc.threshold(hsvchannels.get(0), purereds2, 255 * 0.69, 255, Imgproc.THRESH_BINARY_INV);
        Core.bitwise_and(purereds, purereds2, purereds);
        Mat brightness = src.clone();
        Imgproc.threshold(hsvchannels.get(2), brightness, 255 * 0.31, 255, Imgproc.THRESH_BINARY);


        purereds = erode(purereds, 4);
        purereds = dilate(purereds, 4);
        Core.bitwise_and(purereds, brightness, purereds);
        if(true) return purereds;
//        purered = erode(purered, 1);
//        purered = dilate(purered, 3);
//        //remove large red dots
//        Mat purered2 = erode(purered, (int) (50 * 0.12));
//        purered2 = dilate(purered2, (int) (50 * 0.12));
//        Core.bitwise_xor(purered, purered2, purered);
////        if(true) return purered;
//
//        //Find reds near whites and blacks
//        Mat overlap1 = reds.clone();
//        Core.bitwise_and(dilate(blacks, (int) (30 * 0.08)), dilate(purered, (int) (30 * 0.08)), overlap1);
//        Core.bitwise_and(overlap1, dilate(purered, (int) (30 * 0.08)), overlap1);




//        Imgproc.threshold(srcGry, srcGry, 100, 255, Imgproc.THRESH_BINARY_INV);
//        Imgproc.adaptiveThreshold(srcGry, whites, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 45, 44);
//        if(true) return overlap1;


        //remove grains
        Mat temp;
//        temp = erode(srcGry,3);
//        temp = dilate(temp, 6);
//        Core.bitwise_and(srcGry, temp,srcGry);



//        if(true)return srcGry;


//        Imgproc.cornerHarris(srcGry, srcGry, 20, 3, 44);
//        if(true)return srcGry;

//        MainActivity.setDebugImage(srcGry,0);

        //Blur the image
//        int blurRadius = (int) (units*1);
//        Mat srcBlr = new Mat(srcGry.size(), CvType.CV_8UC1);
//        srcGry.copyTo(srcBlr);
//        MainActivity.setDebugImage(srcGry, 1);
//        if(true) return srcBlr;

//        GaussianBlur(srcGry, srcBlr, new Size(blurRadius, blurRadius), 0);


        //Create an adaptive threshold for parsing and inverting image
//        Mat src3 = new Mat(srcGry.size(), CvType.CV_8UC1);
//        Imgproc.adaptiveThreshold(srcGry, src3, 255, Imgproc.T, Imgproc.THRESH_BINARY_INV, 15, 4);
//        MainActivity.setDebugImage(src3, 2);
//        Imgproc.threshold(srcGry, src3, 100,255, Imgproc.THRESH_BINARY_INV);

        /// Canny detector
        Mat srcEdges = new Mat(srcGry.size(), CvType.CV_8UC1);
//        int lowThreshold = 5;
//        int maxThreshold = 300;
//        int kernel_size = 3;
//        Imgproc.Canny(srcGry, srcEdges, lowThreshold, maxThreshold, kernel_size,true);
////        srcEdges = dilate(srcEdges,1);
////        MainActivity.setDebugImage(srcEdges, 2);



//        //Corners Detect
        Rectangle corners = CornerDetector.findCorners(srcEdges,srcGry);
        Mat color1 = src.clone();
////        Imgproc.cvtColor(srcGry, color1, Imgproc.COLOR_GRAY2BGR);
//        GenUtils.drawPoint(color1, corners.lb, new Scalar(255, 0, 0));
//        GenUtils.drawPoint(color1, corners.lt, new Scalar(0, 255, 0));
//        GenUtils.drawPoint(color1, corners.rb, new Scalar(255, 255, 0));
//        GenUtils.drawPoint(color1, corners.rt, new Scalar(0, 255, 255));


        //Draw image on top
//        gifController.get(0);
        if(image==null){
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image);
            image = GenUtils.convertBitmapToMat(bitmap);
        }
        Mat color2 = addImageOnExisting(color1, corners, image);



//        Mat ROI = srcEdges.submat(0, 100, 0, 100);
        if(true) return color2;

//        if(true)throw new ImageProcessException("0");



        //remove small grains
//        Mat src3 = removeSmallBlobs(srcEdges);
//        MainActivity.setDebugImage(src3,3);

        //Find lines
        ArrayList<LineSegment> segments = findLines(srcEdges);
        Mat color0 = new Mat();
        Imgproc.cvtColor(srcGry, color0, Imgproc.COLOR_GRAY2BGR);
        int maxCount = 20;
        for(LineSegment lineSegment : segments){
            if(maxCount--<0) break;
            Imgproc.line(color0, lineSegment.point1, lineSegment.point2, new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255), 1);
//            Logg.d("LINE",lineSegment.point1.toString()+" : "+lineSegment.point2.toString());
//            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(200, 0,0), (int)units*10);
////            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(200,0,0), 1);
        }
        MainActivity.setDebugImage(color0,3);
//        if(true) return color0;
        if(segments.size()<5) throw new ImageProcessException("I2");
//        if(true)throw new ImageProcessException("I");

        //Filtered Line segments : filter from around 40 segements to final 4
        ArrayList<LineSegment> filteredSegments = filterValidLineSegments(segments);

        Mat color = new Mat();
        Imgproc.cvtColor(srcGry, color, Imgproc.COLOR_GRAY2BGR);
        for(LineSegment lineSegment : filteredSegments){
            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(Math.random()*255, Math.random()*255,Math.random()*255), 3);
            Logg.d("LINE",lineSegment.point1.toString()+" : "+lineSegment.point2.toString());
//            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(200, 0,0), (int)units*10);
////            Imgproc.line(color, lineSegment.point1, lineSegment.point2, new Scalar(200,0,0), 1);
        }
        MainActivity.setDebugImage(color, 4);
        if(filteredSegments.size()<5) throw new ImageProcessException("I2");
        if(true) return color;


        //Find Bounding rectangle lines
        Rectangle rect = findRectangleFromFourLines(filteredSegments);
        rect.print();

        //Draw Bounding Rectangle
        color = new Mat();
        Imgproc.cvtColor(srcGry, color, Imgproc.COLOR_GRAY2BGR);
        Imgproc.line(color, new Point(100,100), new Point(100,200), new Scalar(Math.random() * 255, 0,0), 5);
        for(int i=0;i<4;i++){
            Imgproc.line(color, rect.lb, rect.lt, new Scalar(Math.random()*255, Math.random()*255,Math.random()*255), 5);
            Imgproc.line(color, rect.lb, rect.rb, new Scalar(Math.random()*255, Math.random()*255,Math.random()*255), 5);
            Imgproc.line(color, rect.rb, rect.rt, new Scalar(Math.random()*255, Math.random()*255,Math.random()*255), 5);
            Imgproc.line(color, rect.rt, rect.lt, new Scalar(Math.random()*255, Math.random()*255,Math.random()*255), 5);
        }
        MainActivity.setDebugImage(color, 5);
        if(true) return color;


        //Get image that needs to be displayed inside box
//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image);
//        Mat imageMat = GenUtils.convertBitmapToMat(bitmap);

//        MatOfPoint2f obj = new MatOfPoint2f();
//        obj.fromList(objList);
//
//        MatOfPoint2f scene = new MatOfPoint2f();
//        scene.fromList(sceneList);
//
//        Mat H = Calib3d.findHomography(obj, scene);
//        Mat logoWarped;
//        // Warp the logo image to change its perspective
//        warpPerspective(imageLogo,logoWarped,H,imageMain.size() );
//        showFinal(imageMain,logoWarped);


        //add image on top of original image
//        Mat imgTrain = imageMat;
//        Mat imgMask = new Mat(imgTrain.size(), CvType.CV_8UC1, new Scalar(255));
//        Mat imgMaskWarped = new Mat();
//
//        Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
//        Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);
//        Rectangle srcR = new Rectangle(128);
//        src_mat.put(0, 0, srcR.lt.x, srcR.lt.y, srcR.rt.x, srcR.rt.y, srcR.lb.x, srcR.lb.y, srcR.rb.x, srcR.rb.y);
//        dst_mat.put(0, 0, srcR.lt.x, srcR.lt.y, srcR.rt.x, srcR.rt.y, srcR.lb.x, srcR.lb.y, rect.rb.x, srcR.rb.y);
//        Mat TRANSFORMATION_MATRIX=Imgproc.getPerspectiveTransform(src_mat, dst_mat);
//
//        Imgproc.warpPerspective(imgMask, imgMaskWarped, TRANSFORMATION_MATRIX, color.size());
//
//        Mat imgTrainWarped = new Mat();
//        Imgproc.warpPerspective(imgTrain, imgTrainWarped, TRANSFORMATION_MATRIX, color.size());
//
//// now copy only masked pixel:
//        imageMat.copyTo(color);



//        color = addImageOnExisting(src, rect, imageMat);
//        MainActivity.setDebugImage(color, 0);





        return color;
    }

    private Mat findFromWhiteMarkers(Mat src) {
        //Find gray image
        Mat srcGry = src.clone();
        Mat srcGryColor = src.clone();//used for overlaying stuff
        Imgproc.cvtColor(src, srcGry, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(srcGry, srcGryColor, Imgproc.COLOR_GRAY2BGR);

        //Find binary image
        Mat srcBin = srcGry.clone();
        Imgproc.threshold(srcGry, srcBin, 255 * CameraActivity.seek, 255, Imgproc.THRESH_BINARY);
        return srcBin;
    }

    private Mat findMarkers(Mat src) {

        //Find gray image
        Mat srcGry = src.clone();
        Mat srcGryColor = src.clone();//used for overlaying stuff
        Imgproc.cvtColor(src, srcGry, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(srcGry, srcGryColor, Imgproc.COLOR_GRAY2BGR);

        //Find binary image
        Mat srcBin = srcGry.clone();
        Imgproc.threshold(srcGry, srcBin, 255 * 0.5, 255, Imgproc.THRESH_BINARY);

        //Find contours
        Mat hierarchy = srcGry.clone();
        List<MatOfPoint> contoursList = new ArrayList<>() ;
//        Imgproc.CV_RETR_TREE ==3
        //Imgproc.CV_CHAIN_APPROX_SIMPLE ==2
        Imgproc.findContours(srcBin, contoursList, hierarchy, 3, 2, new Point(0, 0));
        if(contoursList.size()==0) return srcGry;


        //Find areas of contours
        final Map<MatOfPoint, Double> areas = new HashMap<>();
        for(MatOfPoint matOfPoint : contoursList){
            areas.put(matOfPoint, Imgproc.contourArea(matOfPoint));
        }

        //Find biggest contour
        Collections.sort(contoursList, new Comparator<MatOfPoint>() {
            @Override
            public int compare(MatOfPoint lhs, MatOfPoint rhs) {
                if(areas.get(lhs).equals(areas.get(rhs))) return 0;
                return (areas.get(lhs)<areas.get(rhs))?1:-1;
            }
        });
        MatOfPoint screenContour = contoursList.get(0);
        Logg.d("CONTOUR","screen size : "+screenContour.rows());


        //Approx to a rectangle
        MatOfPoint screenContourRect = new MatOfPoint();
        MatOfPoint2f screenContourRect2F = new MatOfPoint2f();
        MatOfPoint2f screenContour2F = new MatOfPoint2f();
        screenContour.convertTo(screenContour2F, CvType.CV_32FC2);
        Imgproc.approxPolyDP(screenContour2F, screenContourRect2F, 10, true);
        screenContourRect2F.convertTo(screenContourRect, CvType.CV_32S);
        if(screenContourRect.rows()!=4) return srcGry;

        //Find rectangle and draw
        Rectangle rect = new Rectangle(screenContourRect);
        Scalar color = new Scalar(255, 0,0);
        for(int i=0;i<4;i++){
            Imgproc.line(srcGryColor, rect.lb, rect.lt, color, 2);
            Imgproc.line(srcGryColor, rect.lb, rect.rb, color, 2);
            Imgproc.line(srcGryColor, rect.rb, rect.rt, color, 2);
            Imgproc.line(srcGryColor, rect.rt, rect.lt, color, 2);
        }


        Logg.d("CONTOUR", "screen size2 : " + screenContourRect.rows());

        //Find Camera Extrinsic and Intrinsic variables
        if(image==null){
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.image);
            image = GenUtils.convertBitmapToMat(bitmap);
        }
        MatOfDouble mDistortionCoefficients = new MatOfDouble();
        ArrayList<Mat> rvecs = new ArrayList<>();
        ArrayList<Mat> tvecs = new ArrayList<>();

        Mat intrinsic = new Mat(3, 3, CvType.CV_32FC1);
        double f = 100*CameraActivity.seek;//focal length of camera
        double k = 50;
        double a = k*f; //scale factor
        double s = 0;//s is the skew, only non-zero if u and v are non-perpendicular.
        double u = src.width()/2;
        double v = src.height()/2;
        intrinsic.put(0,0,a);
        intrinsic.put(1,0,s);
        intrinsic.put(2,0,u);
        intrinsic.put(0,1,0);
        intrinsic.put(1,1,a);
        intrinsic.put(2,1,v);
        intrinsic.put(0,2,0);
        intrinsic.put(1,2,0);
        intrinsic.put(2,2,1);
        int w = src.width();
        int h = src.height();
        int mFlags2 = Calib3d.CALIB_FIX_PRINCIPAL_POINT +
                Calib3d.CALIB_ZERO_TANGENT_DIST +
                Calib3d.CALIB_FIX_ASPECT_RATIO +
                Calib3d.CALIB_FIX_K4 +
                Calib3d.CALIB_FIX_K5;

        ArrayList<Mat> points3D = new ArrayList<>();
        Mat a1 = new MatOfPoint3f();
        a1.push_back(new MatOfPoint3f(new Point3(w, 0, 0)));
        a1.push_back(new MatOfPoint3f(new Point3(0, 0, 0)));
        a1.push_back(new MatOfPoint3f(new Point3(0, h, 0)));
        a1.push_back(new MatOfPoint3f(new Point3(w, h, 0)));
        points3D.add(a1);

        ArrayList<Mat> points2D = new ArrayList<>();
        Mat a2 = new MatOfPoint2f();
        a2.push_back(new MatOfPoint2f(new Point(rect.lt.x, rect.lt.y)));
        a2.push_back(new MatOfPoint2f(new Point(rect.rt.x, rect.rt.y)));
        a2.push_back(new MatOfPoint2f(new Point(rect.rb.x, rect.rb.y)));
        a2.push_back(new MatOfPoint2f(new Point(rect.lb.x, rect.lb.y)));
        points2D.add(a2);

        //calibrateCamera(objectPoints, imagePoints, imageSize, cameraMatrix, distCoeffs, rvecs, tvecs, flags)
        Calib3d.calibrateCamera(points3D, points2D, new Size(w,h), intrinsic, mDistortionCoefficients, rvecs, tvecs);
//        Logg.d("OUTPUT4", "" + Arrays.toString(tvecs.get(0).get(0, 0)));
        rect.print();


        //Check if those values are correct
//        MatOfPoint3f a3 = new MatOfPoint3f();
//        a3.push_back(new MatOfPoint3f(new Point3(w, 0, 0)));
//        a3.push_back(new MatOfPoint3f(new Point3(0, 0, 0)));
//        a3.push_back(new MatOfPoint3f(new Point3(0, h, 0)));
//        a3.push_back(new MatOfPoint3f(new Point3(w, h, 0)));
//        MatOfPoint2f a4 = new MatOfPoint2f();
//
//
//        Mat jacobian = new Mat();//optional output
//        double aspectRatio = 0;//optional output
//        Calib3d.projectPoints(a3, rvecs.get(0), tvecs.get(0), intrinsic, mDistortionCoefficients, a4, jacobian, aspectRatio);
//
//        Logg.d("OUTPUT0", "" + rect.lt.toString());
//        Logg.d("OUTPUT1", "" + Arrays.toString(a2.get(0,0)));
//        Logg.d("OUTPUT2", ""+Arrays.toString(a4.toArray()));
//        Rectangle outputRect = new Rectangle(new MatOfPoint(a4.toArray()));



        //Remove white color from screen
        List<MatOfPoint> list = new ArrayList<>();
        list.add(screenContour);
        Imgproc.fillPoly(srcGryColor, list, new Scalar(0,0,0));

        //Create mapper that will be displayed inside screen
        Mat onlyGrid = new Mat(srcGryColor.size(),srcGryColor.type());
        Mat srcOverlayWhite = new Mat(srcGryColor.size(),srcGryColor.type());
        Mat srcOverlayActual = new Mat(srcGryColor.size(),srcGryColor.type());
        List<MatOfPoint> listTemp2 = new ArrayList<>();
        listTemp2.add(screenContour);
        Imgproc.fillPoly(srcOverlayWhite, listTemp2, new Scalar(255, 255, 255));
        int gridLength = 30;
        int gridNum = 10;
        for(int p=0;p<gridNum;p++){

            List<Point3> points = new ArrayList<>();
            points.add(new Point3(w, 0, -p*gridLength));
            points.add(new Point3(0, 0, -p*gridLength));
            points.add(new Point3(0, h, -p*gridLength));
            points.add(new Point3(w, h, -p*gridLength));
            Rectangle outputRect = findCameraPerspectiveOf(points,rvecs.get(0), tvecs.get(0), intrinsic, mDistortionCoefficients);

            //Draw using resulting
            int colorShade = 255-(p*255/gridNum);
            Scalar colorScalar = new Scalar(colorShade,colorShade,colorShade);
            for(int i=0;i<4;i++){
                Imgproc.line(onlyGrid, outputRect.lb, outputRect.lt, colorScalar, 1);
                Imgproc.line(onlyGrid, outputRect.lb, outputRect.rb, colorScalar, 1);
                Imgproc.line(onlyGrid, outputRect.rb, outputRect.rt, colorScalar, 1);
                Imgproc.line(onlyGrid, outputRect.rt, outputRect.lt, colorScalar, 1);
            }

            //Add in mapper
            Mat temp2 = new Mat(srcGryColor.size(),srcGryColor.type());
            Core.bitwise_and(srcOverlayWhite, onlyGrid, temp2);
            Core.add(srcOverlayActual, temp2, srcOverlayActual);
        }




//        srcOverlayActual
//        Core.bitwise_and(roi, roi,srcGry,src4);




        //overlay mapper on top of original image
        Core.add(srcGryColor, srcOverlayActual, srcGryColor);





        return srcGryColor;
    }

    private Rectangle findCameraPerspectiveOf(List<Point3> points, Mat rvec, Mat tvec, Mat intrinsic, MatOfDouble mDistortionCoefficients) {
        //Check if those values are correct
        MatOfPoint3f a3 = new MatOfPoint3f();
        a3.push_back(new MatOfPoint3f(points.get(0)));
        a3.push_back(new MatOfPoint3f(points.get(1)));
        a3.push_back(new MatOfPoint3f(points.get(2)));
        a3.push_back(new MatOfPoint3f(points.get(3)));
        MatOfPoint2f a4 = new MatOfPoint2f();


        Mat jacobian = new Mat();//optional output
        double aspectRatio = 0;//optional output
        Calib3d.projectPoints(a3, rvec, tvec, intrinsic, mDistortionCoefficients, a4, jacobian, aspectRatio);

        return new Rectangle(new MatOfPoint(a4.toArray()));
    }


    private Mat cameraPoseFromHomography(Mat h) {
        Log.d("DEBUG", "cameraPoseFromHomography: homography : " + h.toString());

        Mat pose = Mat.eye(3, 4, CvType.CV_32FC1);  // 3x4 matrix, the camera pose
        float norm1 = (float) Core.norm(h.col(0));
        float norm2 = (float) Core.norm(h.col(1));
        float tnorm = (norm1 + norm2) / 2.0f;       // Normalization value

        Mat normalizedTemp = new Mat();
        Core.normalize(h.col(0), normalizedTemp);
        normalizedTemp.convertTo(normalizedTemp, CvType.CV_32FC1);
        normalizedTemp.copyTo(pose.col(0));

        Core.normalize(h.col(1), normalizedTemp);
        normalizedTemp.convertTo(normalizedTemp, CvType.CV_32FC1);
        normalizedTemp.copyTo(pose.col(1));

        Mat p3 = pose.col(0).cross(pose.col(1));
        p3.copyTo(pose.col(2));

        Mat temp = h.col(2);
//        double[] buffer = new double[3];
//        h.col(2).get(0, 0, buffer);
        pose.put(0, 3, mutiplyArr(h.get(0,2),1 / tnorm));
        pose.put(1, 3, mutiplyArr(h.get(1,2),1 / tnorm));
        pose.put(2, 3, mutiplyArr(h.get(2,2),1 / tnorm));

        return pose;
    }
    double[] mutiplyArr(double[] arr, double scalar){
        double[] arr2 = new double[arr.length];
        for(int i=0;i<arr.length;i++) arr2[i] = arr[i]*scalar;
        return arr2;
    }


    public static Mat addImageOnExisting(Mat src, Rectangle rect, Mat image){

        //points are in order  top-left, top-right, bottom-right, bottom-left

//        Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
//        Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);

//        Rectangle srcR = new Rectangle(128);

//        src_mat.put(0, 0, srcR.lt.x, srcR.lt.y, srcR.rt.x, srcR.rt.y, srcR.lb.x, srcR.lb.y, srcR.rb.x, srcR.rb.y);
//        dst_mat.put(0, 0, srcR.lt.x, srcR.lt.y, srcR.rt.x, srcR.rt.y, srcR.lb.x, srcR.lb.y, rect.rb.x, srcR.rb.y);

//        Point[] dstPoints = new Point[4];
//        int w1 = 200;
//        int h1 = 200;
//        dstPoints[0] = new Point(10, 0);
//        dstPoints[1] = new Point(w1, 0);
//        dstPoints[2] = new Point(w1, h1);
//        dstPoints[3] = new Point(0, h1);
//
//        MatOfPoint2f canonicalMarker = new MatOfPoint2f();
//        canonicalMarker.fromArray(dstPoints);

        ArrayList<Point> points2 = new ArrayList<>();
        points2.add(new Point(rect.rt.x,rect.rt.y));//top right
        points2.add(new Point(rect.lt.x,rect.lt.y));//top left
        points2.add(new Point(rect.lb.x,rect.lb.y));//bottom left
        points2.add(new Point(rect.rb.x, rect.rb.y));//bottom right

//        Logg.d("GGGGGG", "Reach1 : " + (System.currentTimeMillis() - start));
        Mat dst;
        dst = warpPerspective2(points2,image,src.width(),src.height(),image.width(),image.height());
//        Logg.d("GGGGGG", "Reach9 : " + (System.currentTimeMillis() - start));
//        Mat dst2 = src.clone();
//        Core.add(src, dst, dst2);

        //Add both
        long start = System.currentTimeMillis();
        src = addImages(src,dst);


        Logg.d("GGGGGG", "Reach4 : " + (System.currentTimeMillis() - start));
        if(true)return src;


//        Calib3d.findHomography(p, h, p2h);
//        cvWarpPerspective(src, src, p2h);


        //Add Image
        //Now create a mask of logo and create its inverse mask also
//        Mat imageGry = image.clone();
//        Imgproc.cvtColor(image, imageGry, Imgproc.COLOR_RGB2GRAY);
//        Mat src3 = new Mat(imageGry.size(), CvType.CV_8UC1);
//        Imgproc.threshold(imageGry, src3, 1, 255, Imgproc.THRESH_BINARY_INV);
//        Mat src4 = new Mat(src3.size(), CvType.CV_8UC1);
//        Core.bitwise_not(src3, src4);

        // Now black-out the area of logo in ROI
//        Mat srcGry = src.clone();
//        Mat roi = new Mat(src.size(),src.type());
//        Core.bitwise_and(roi, roi,srcGry,src4);
//        Core.add(srcGry, dst, dst2);
//        MainActivity.setDebugImage(srcGry, 6);
//        MainActivity.setDebugImage(srcGry, 6);


//        img1_bg = cv2.bitwise_and(roi,roi,mask = mask_inv)


        return src;

    }


    static Mat addImages(Mat src, Mat dst){
        long start = System.currentTimeMillis();
//        for(int i=0;i<src.rows();i++){
//            for(int j=0;j<src.cols();j++){
//                if(dst.get(i,j)[0]!=0){
//                    src.put(i,j,dst.get(i,j));
//                }
//            }
//        }
//        Mat img2gray = new Mat(dst.size(),CvType.CV_8UC1);
//        Mat mask = img2gray.clone();
//        Imgproc.cvtColor(dst,img2gray,Imgproc.COLOR_BGR2GRAY);
//        Imgproc.threshold(img2gray, mask, 10, 255, Imgproc.THRESH_BINARY_INV);
//
//        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2BGR);
//        Core.bitwise_and(img2gray, mask, src);
        Core.addWeighted(src,0.3,dst,0.3,1,src);
//        Core.add(src, dst, src);

        return src;
    }

//    Mat doOpposite(Rectangle rect, Mat mat){
//
//    }

    static Mat getPerspectiveTransformation2(ArrayList<Point> inputPoints, int w, int h) {
        Point[] canonicalPoints = new Point[4];
        canonicalPoints[0] = new Point(w, 0);
        canonicalPoints[1] = new Point(0, 0);
        canonicalPoints[2] = new Point(0, h);
        canonicalPoints[3] = new Point(w, h);

        MatOfPoint2f canonicalMarker = new MatOfPoint2f();
        canonicalMarker.fromArray(canonicalPoints);

        Point[] points = new Point[4];
        for (int i = 0; i < 4; i++) {
            points[i] = new Point(inputPoints.get(i).x, inputPoints.get(i).y);
        }
        MatOfPoint2f marker = new MatOfPoint2f(points);
        return Imgproc.getPerspectiveTransform(canonicalMarker,marker);
    }

    static Mat warpPerspective2(ArrayList<Point> inputPoints, Mat mat, int w, int h, int width, int height) {
        long start = System.currentTimeMillis();
        Mat transform = getPerspectiveTransformation2(inputPoints, width, height);
        Mat unWarpedMarker = new Mat(w, h, CvType.CV_8UC1);
        Imgproc.warpPerspective(mat, unWarpedMarker, transform, new Size(w, h));
        return unWarpedMarker;
    }

    static Mat getPerspectiveTransformation(ArrayList<Point> inputPoints, int w, int h) {
        Point[] canonicalPoints = new Point[4];
        canonicalPoints[0] = new Point(w, 0);
        canonicalPoints[1] = new Point(0, 0);
        canonicalPoints[2] = new Point(0, h);
        canonicalPoints[3] = new Point(w, h);

        MatOfPoint2f canonicalMarker = new MatOfPoint2f();
        canonicalMarker.fromArray(canonicalPoints);

        Point[] points = new Point[4];
        for (int i = 0; i < 4; i++) {
            points[i] = new Point(inputPoints.get(i).x, inputPoints.get(i).y);
        }
        MatOfPoint2f marker = new MatOfPoint2f(points);
        return Imgproc.getPerspectiveTransform(marker, canonicalMarker);
    }

    static Mat warpPerspective(ArrayList<Point> inputPoints, Mat mat, int w, int h) {
        Mat transform = getPerspectiveTransformation(inputPoints, w, h);
        Mat unWarpedMarker = new Mat(w, h, CvType.CV_8UC1);
        Imgproc.warpPerspective(mat, unWarpedMarker, transform, new Size(w, h));
        return unWarpedMarker;
    }


    private Rectangle findRectangleFromFourLines(ArrayList<LineSegment> filteredSegments) {

//        Collections.sort(filteredSegments, new Comparator<LineSegment>() {
//            public int compare(LineSegment o1, LineSegment o2) {
//                return (int) (o2.getAngle() - o1.getAngle());
//            }
//        });


        Point p1 = GenUtils.findIntersection(filteredSegments.get(0), filteredSegments.get(1));
        Point p2 = GenUtils.findIntersection(filteredSegments.get(0), filteredSegments.get(2));
        Point p3 = GenUtils.findIntersection(filteredSegments.get(0), filteredSegments.get(3));
        Point p4 = GenUtils.findIntersection(filteredSegments.get(1), filteredSegments.get(2));
        Point p5 = GenUtils.findIntersection(filteredSegments.get(1), filteredSegments.get(3));
        Point p6 = GenUtils.findIntersection(filteredSegments.get(2), filteredSegments.get(3));

        Rect r = new Rect(new Point(0,0),new Point(1000,1000));
        ArrayList<Point> points = new ArrayList<>();
        if(p1.inside(r))  points.add(p1);
        if(p2.inside(r))  points.add(p2);
        if(p3.inside(r))  points.add(p3);
        if(p4.inside(r))  points.add(p4);
        if(p5.inside(r))  points.add(p5);
        if(p6.inside(r))  points.add(p6);

        //Find the order
        if(points.size()==4){
            Collections.sort(points, new Comparator<Point>() {
                public int compare(Point o1, Point o2) {
                    return (int) (o2.x - o1.x);
                }
            });
        }


        if(points.size()==4) return new Rectangle(points.get(0),points.get(1),points.get(2),points.get(3));
        return new Rectangle(p1,p2,p4,p3);
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

    public static ArrayList<LineSegment> findLines(Mat src2){
        Mat src = src2.clone();
//        src = dilate(src,1);
//        MainActivity.setDebugImage(src,0);
//        int maxWidth = 100;
//        float ratio = ((float)src.width())/maxWidth;
//        Mat srcScaled = scaleImageToMaxSize(src,maxWidth);
        long startTime = System.currentTimeMillis();
//        Log.d(TAG, "Ratio :  "+ratio);

        //Find lines in the image (20,20,3)
        int threshold = 20;//The minimum number of intersections to “detect” a line
        int minLinelength = 30;//The minimum number of points that can form a line. Lines with less than this number of points are disregarded.
        int maxlineGap = 20;//The maximum gap between two points to be considered in the same line.
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

//        //remove segments that are not horizontal or vertical
//        for(int i=0;i<segments.size();i++){
//            double angle = Math.abs(GenUtils.getAngleFromradians(segments.get(i).getAngle()));
//            Logg.d(TAG, "Angle : " + angle);
//            if(rejectedIndices.contains(i)) continue;
//            if(angle<20) continue;
//            if(angle>70&&angle<110) continue;
//            if(angle>160&&angle<200) continue;
//            rejectedIndices.add(i);
//        }

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
    public static Mat erode(Mat src, int erosion_size){
        Mat src_mat=new Mat(src.size(),CvType.CV_8UC1,new Scalar(0,0,0,0));
        Imgproc.erode(src,src_mat,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,
                new Size( 2*erosion_size + 1, 2*erosion_size+1 ),
                new Point( erosion_size, erosion_size ) ));

        return src_mat;
    }
    public static Mat dilate(Mat src, int erosion_size){
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
