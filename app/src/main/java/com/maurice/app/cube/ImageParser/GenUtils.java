package com.maurice.app.cube.ImageParser;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.maurice.app.cube.ImageParser.models.LineSegment;
import com.maurice.app.cube.utils.Logg;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

/**
 * Created by maurice on 16/08/15.
 */
public class GenUtils {
    public static Mat convertBitmapToMat(Bitmap image){
        Mat mat = new Mat ( image.getHeight(), image.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap myBitmap32 = image.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(myBitmap32, mat);
        return mat;
    }
    public static Bitmap convertMatToBitmap(Mat mat){
        Bitmap resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);;
        Utils.matToBitmap(mat, resultBitmap);
        return resultBitmap;
    }

    public static Point intersectingPoint(LineSegment l1, LineSegment l2){
        float x12 = (float) (l1.point1.x - l1.point2.x);
        float x34 = (float) (l2.point1.x - l2.point2.x);
        float y12 = (float) (l1.point1.y - l1.point2.y);
        float y34 = (float) (l2.point1.y - l2.point2.y);

        float c = x12 * y34 - y12 * x34;

        if (Math.abs(c) < 0.01)
        {
            // No intersection
            return new Point(0,0);
        }
        else
        {
            // Intersection
            float a = (float) (l1.point1.x  * l1.point2.y - l1.point1.y * l1.point2.x);
            float b = (float) (l2.point1.x * l2.point2.y - l2.point1.y  * l2.point2.x);

            float x = (a * x34 - b * x12) / c;
            float y = (a * y34 - b * y12) / c;

            return new Point(x,y);
        }
    }
    public static <T> String print2dArray(T[][] arr){
        String printStr = "\n";
        for(T[] row : arr){
            for(T elem : row){
                printStr += ""+elem.toString();
            }
            printStr+="\n";
        }
        return printStr;
//        Log.d("Array",printStr);
    }
    public static Mat invertMat(Mat src){
        Mat dst= new Mat(src.rows(),src.cols(), src.type(), new Scalar(1,1,1));
        Core.subtract(dst, src, dst);
        return dst;
    }
    public static double getAngleFromradians(double radians){
        return (radians/Math.PI)*180;
    }
    public static void printBoard(int[][] digits) {
        String text = "";
        for(int i=0;i<digits.length;i++){
            text += Arrays.toString(digits[i])+"\n";
        }
        Logg.d("BOARD", "=\n" + text);
//        TrainSet.getInstance()
    }

    public static int brightness(Mat src){
        int sumPos = 0;
        for (int x = 0; x <= src.rows(); x++) {
            for (int y = 0; y <= src.cols(); y++) {
                double pos[] = src.get(y, x);
                if (pos != null) {
                    sumPos += pos[0];
                }

            }
        }
        return sumPos;
    }

    /**
     * Get bitmap efficiently
     *
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    public static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Point findIntersection(LineSegment l1, LineSegment l2){
        Logg.d("INTERSECT1",l1.point1.toString()+":"+l1.point2.toString());
        Logg.d("INTERSECT2",l2.point1.toString()+":"+l2.point2.toString());
        double x1 = l1.point1.x;
        double x2 = l1.point2.x;
        double x3 = l2.point1.x;
        double x4 = l2.point2.x;
        double y1 = l1.point1.y;
        double y2 = l1.point2.y;
        double y3 = l2.point1.y;
        double y4 = l2.point2.y;
        double x =  (((x1*y2-x2*y1)*(x3-x4))-((x3*y4-x4*y3)*(x1-x2)))/
                (((x1-x2)*(y3-y4))-((y1-y2)*(x3-x4)));
        double y =  (((x1*y2-x2*y1)*(y3-y4))-((x3*y4-x4*y3)*(y1-y2)))/
                (((x1-x2)*(y3-y4)-(y1-y2)*(x3-x4)));
        Logg.d("INTERSECTION",(new Point(x,y)).toString());
        return new Point(x,y);
    }
    public static void drawPoint(Mat mat, Point point, Scalar color){
        Imgproc.line(mat, point, new Point(point.x + 1, point.y), color, 3);
    }
}
