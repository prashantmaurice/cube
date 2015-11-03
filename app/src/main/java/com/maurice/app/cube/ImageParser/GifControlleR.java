package com.maurice.app.cube.ImageParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.maurice.app.cube.R;
import com.maurice.app.cube.utils.Logg;

import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by maurice on 02/11/15.
 */
public class GifController {
    static String TAG = "GIFCONTROLLER";
    static GifController instance;

    ArrayList<Integer> resources = new ArrayList<>();
    ArrayList<Mat> gifMats = new ArrayList<>();
    public GifController(Context context){
        long start = System.currentTimeMillis();

        resources.add(R.drawable.monkey1);
//        resources.add(R.drawable.monkey2);
//        resources.add(R.drawable.monkey3);
//        resources.add(R.drawable.monkey4);
//        resources.add(R.drawable.monkey5);
//        resources.add(R.drawable.monkey6);
//        resources.add(R.drawable.monkey7);
//        resources.add(R.drawable.monkey8);
//        resources.add(R.drawable.monkey9);
//        resources.add(R.drawable.monkey10);
//        resources.add(R.drawable.monkey11);
//        resources.add(R.drawable.monkey12);
//        resources.add(R.drawable.monkey13);
//        resources.add(R.drawable.monkey14);
//        resources.add(R.drawable.monkey15);
//        resources.add(R.drawable.monkey16);

        for(Integer resId : resources){
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sample1);
            Mat imageMat = GenUtils.convertBitmapToMat(bitmap);
            gifMats.add(imageMat);
        }

        Logg.d(TAG, "Initialized in : "+(System.currentTimeMillis()-start)+" ms");
    }


    public static GifController getInstance(Context context){
        if(instance==null) instance =  new GifController(context);
        return instance;
    }

    public Mat get(){
        long time = System.currentTimeMillis();
        int frame = (int)(time/100)%16;
        if(frame<0) frame = 0; if(frame>resources.size()-1) frame = resources.size()-1;
        return gifMats.get(frame);
    }
    public Mat get(int frame){
        return gifMats.get(frame);
    }
}
