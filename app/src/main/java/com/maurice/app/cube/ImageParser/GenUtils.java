package com.maurice.app.cube.ImageParser;

import android.graphics.Bitmap;

import com.maurice.app.cube.ImageParser.models.LineSegment;
import com.maurice.app.cube.utils.Logg;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

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
}
