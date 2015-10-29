package com.maurice.app.cube.ImageParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.maurice.app.cube.ImageParser.models.Rectangle;
import com.maurice.app.cube.R;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

/**
 * Created by maurice on 23/08/15.
 */
public class DigitRecogniser {
    String TAG = "RECOGNISER" ;

    ArrayList<Bitmap> bitmapArray = new ArrayList<>();
    ArrayList<Mat> mapArrayNormal = new ArrayList<>();
    ArrayList<Mat> mapArrayInvert = new ArrayList<>();

    public DigitRecogniser(Context context){
        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.zero));
        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.one));
        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.two));
        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.three));
        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.four));
        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.five));
        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.six));
        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.seven));
        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.eight));
        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.nine));

        for(Bitmap bitmap : bitmapArray){

            Mat src = GenUtils.convertBitmapToMat(bitmap);
            Mat grey = src.clone();
            Imgproc.cvtColor(src, grey, Imgproc.COLOR_RGB2GRAY);

            Mat inverted = wrapPerspectiveCustom(grey,new Rectangle(grey.width(),grey.height()));
            Mat normal= new Mat(inverted.rows(),inverted.cols(), inverted.type(), new Scalar(1,1,1));
            Core.subtract(normal, inverted, normal);
            mapArrayNormal.add(normal);
            mapArrayInvert.add(inverted);
        }
    }
    private Mat wrapPerspectiveCustom(Mat src, Rectangle rect){
        //points are in order  top-left, top-right, bottom-right, bottom-left

        Mat src_mat=new Mat(4,1, CvType.CV_32FC2);
        Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);

        Rectangle dest = new Rectangle(200);

        src_mat.put(0, 0, rect.lt.x, rect.lt.y, rect.rt.x, rect.rt.y, rect.lb.x, rect.lb.y, rect.rb.x, rect.rb.y);
        dst_mat.put(0, 0, dest.lt.x, dest.lt.y, dest.rt.x, dest.rt.y, dest.lb.x, dest.lb.y, dest.rb.x, dest.rb.y);
        Mat perspectiveTransform=Imgproc.getPerspectiveTransform(src_mat, dst_mat);

        Mat dst=src.clone();

        Imgproc.warpPerspective(src, dst, perspectiveTransform, new Size(Math.abs(dest.lt.x - dest.rt.x), Math.abs(dest.lt.y - dest.lb.y)));

        return dst;

    }
    public void recogniseDigit(Mat mat){
        for(int i=0;i< mapArrayNormal.size();i++){
            int sum = 0;
            int posN = 0;
            int negN = 0;
            Mat positive = mapArrayNormal.get(i).mul(mat);
            Mat negative = mapArrayInvert.get(i).mul(GenUtils.invertMat(mat));
            for(int x=0;x<=positive.rows();x++){
                for(int y=0;y<=positive.cols();y++){
                    double pos[] = positive.get(y,x);
                    double neg[] = negative.get(y,x);
                    if(pos!=null){
                        sum +=pos[0]-0.5*neg[0];
                        posN += pos[0];
                        negN += neg[0];
                    }

                }
            }
            Log.d(TAG,"Mask  "+i+" = " + posN+" - "+negN+" = "+sum);
        }
    }




}
