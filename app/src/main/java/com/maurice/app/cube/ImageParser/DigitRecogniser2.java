package com.maurice.app.cube.ImageParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.maurice.app.cube.MainActivity;
import com.maurice.app.cube.utils.Logg;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by maurice on 23/08/15.
 */
public class DigitRecogniser2 {
    String TAG = "DIGITRECOGNISER2" ;
    static DigitRecogniser2 instance;

    Context mContext;
    ArrayList<Bitmap> bitmapArray = new ArrayList<>();
    ArrayList<Mat> mapArrayNormal = new ArrayList<>();
    ArrayList<Mat> mapArrayInvert = new ArrayList<>();

    public HashMap<Integer, Mat> mapDigitPics = new HashMap<>();
    public HashMap<Integer,Mat> finalMap = new HashMap<>();
    public TrainSet trainSet;

    private DigitRecogniser2(Context context){
        mContext = context;
        trainSet = TrainSet.getInstance(mContext);

        //setupTrainData
        setupTrainData();



//        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.train_1));
//        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.one));
//        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.two));
//        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.three));
//        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.four));
//        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.five));
//        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.six));
//        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.seven));
//        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.eight));
//        bitmapArray.add(BitmapFactory.decodeResource(context.getResources(), R.drawable.nine));

//        for(Bitmap bitmap : bitmapArray){
//
//            Mat src = GenUtils.convertBitmapToMat(bitmap);
//            Mat grey = src.clone();
//            Imgproc.cvtColor(src, grey, Imgproc.COLOR_RGB2GRAY);
//
//            Mat inverted = wrapPerspectiveCustom(grey,new Rectangle(grey.width(),grey.height()));
//            Mat normal= new Mat(inverted.rows(),inverted.cols(), inverted.type(), new Scalar(1,1,1));
//            Core.subtract(normal, inverted, normal);
//            mapArrayNormal.add(normal);
//            mapArrayInvert.add(inverted);
//        }
    }

    public static DigitRecogniser2 getInstance(Context context){
        if(instance==null) instance = new DigitRecogniser2(context);
        return instance;
    }

    private void setupTrainData() {
//        for(int i=1;i<10;i++){
//            finalMap.put(i,new Mat(new Size(50,50),CvType.CV_8UC1));
//        }


        Logg.d(TAG, "Setting Up training data");
        ImageParser imageParser = ImageParser.getInstance(mContext);
        HashMap<Integer, Integer> countMap = new HashMap<>();
        for(TrainSet.TrainDataAnswer trainer : trainSet.trainDataArr){
            Mat mat = GenUtils.convertBitmapToMat(trainer.bitmap);
            Mat[][] croppedMats = imageParser.getCroppedMats(mat);
//            if(croppedMats.length!=9||croppedMats[0].length!=9) continue;
            for(int i=0;i<croppedMats.length;i++){
                for(int j=0;j<croppedMats[0].length;j++){
                    if(i>8||j>8) continue;
                    int number = trainer.data[i][j];
                    if(!mapDigitPics.containsKey(number)) mapDigitPics.put(number,croppedMats[i][j].clone());
                    if(finalMap.containsKey(number)){
                        float newPercent =  (float)1/(countMap.get(number)+1);
                        Mat before = finalMap.get(number);
                        Core.addWeighted(before, 1-newPercent, croppedMats[i][j],newPercent,0,before);
                        countMap.put(number, countMap.get(number) + 1);
                        Logg.d("DEBUGpp","i="+i+" j="+j+" num="+number);
                    }else{
                        countMap.put(number,1);
                        finalMap.put(number,croppedMats[i][j].clone());
                        Logg.d("DEBUGpp", "i="+i + " j=" + j + " num=" + number);
                    }
                }
            }
        }

    }


    public int recogniseDigit(Mat mat){
        if(mat==null) return 0;
        int probableDigit = 0;
        int highestMatch = 0;
        int SCALE_SIZE = 10;//px to which image is scaled to find average brightness

        long startTime;
        for(int i=1;i<10;i++) {
//            Log.d(TAG, "SIZE " + mat.width());
//            Log.d(TAG, "SIZE " + mat.width());
            startTime = System.currentTimeMillis();
            Mat positive = new Mat(mat.size(), CvType.CV_8UC1);
            Mat positiveSmall = new Mat(new Size(SCALE_SIZE,SCALE_SIZE), CvType.CV_8UC1);
            Mat negative = positive.clone();
            Mat negative2 = positive.clone();
            Mat negativeSmall = new Mat(new Size(SCALE_SIZE,SCALE_SIZE), CvType.CV_8UC1);
            Mat matLearned = finalMap.get(i);
//            Log.d(TAG, "REACH0 " + (System.currentTimeMillis() - startTime) + " ms");
            Core.multiply(matLearned, mat, positive);
            Core.subtract(matLearned, mat, negative);
            Core.subtract(mat, matLearned, negative2);
            Core.add(negative, negative2, negative);
//            Log.d(TAG, "REACH1 " + (System.currentTimeMillis() - startTime) + " ms ");
            Imgproc.resize(positive,positiveSmall,new Size(SCALE_SIZE,SCALE_SIZE));
            int sumPos = GenUtils.brightness(positiveSmall);
//            Log.d(TAG, "REACH2 " + (System.currentTimeMillis() - startTime) + " ms ");
            Imgproc.resize(negative, negativeSmall, new Size(SCALE_SIZE, SCALE_SIZE));
            int sumNeg = GenUtils.brightness(negativeSmall);

//            Log.d(TAG, "REACH3 " + (System.currentTimeMillis() - startTime) + " ms");
            Logg.d("MATCH POS", "" + i + " : " + sumPos + " = " + sumNeg);
            Logg.d("MATCH NEG", "" + i + " : " + (sumPos - sumNeg));
//            sumPos = 10000;
            if (highestMatch < (sumPos - sumNeg) && sumPos>200) {
                highestMatch = (sumPos - sumNeg);
                probableDigit = i;
            }
//            Log.d(TAG, "REACH4 " + (System.currentTimeMillis() - startTime) + " ms");
        }
        Log.d(TAG, "RECOGNISED " +probableDigit+",");
        return probableDigit;

    }


    public int[][] recogniseDigits(Mat[][] numbersCrop) {
        int[][] digits = new int[9][9];
        for(int i=0;i<numbersCrop.length;i++){
            for(int j=0;j<numbersCrop[0].length;j++){
                digits[i][j] = recogniseDigit(numbersCrop[i][j]);
                Log.d(TAG, "RECOGNISED " + digits[i][j] + " : " + i + "," + j);
            }
        }

        //DEBUG
        int SCALE_SIZE = 10;
        if(true){
            int i = 0, j=2;
            Mat matLearned = finalMap.get(8);
            Mat number = numbersCrop[i][j];

            Mat positive = new Mat(number.size(), CvType.CV_8UC1,new Scalar(0));
            Mat positiveSmall = new Mat(new Size(SCALE_SIZE,SCALE_SIZE), CvType.CV_8UC1, new Scalar(0));
            Mat negative = new Mat(number.size(), CvType.CV_8UC1,new Scalar(0));
            Mat negativeSmall = new Mat(new Size(SCALE_SIZE,SCALE_SIZE), CvType.CV_8UC1, new Scalar(0));
            Mat negative2 = new Mat(number.size(), CvType.CV_8UC1,new Scalar(0));
            Mat negativeSmall2 = new Mat(new Size(SCALE_SIZE,SCALE_SIZE), CvType.CV_8UC1, new Scalar(0));

            Core.multiply(matLearned, number, positive);
            Core.subtract(matLearned, number, negative);
            Core.subtract(number, matLearned, negative2);
            Core.add(negative, negative2, negative);
            Imgproc.resize(positive, positiveSmall, new Size(SCALE_SIZE, SCALE_SIZE));
            Imgproc.resize(negative, negativeSmall, new Size(SCALE_SIZE, SCALE_SIZE));
            Imgproc.resize(negative2, negativeSmall2, new Size(SCALE_SIZE, SCALE_SIZE));

            MainActivity.setDebugImage(positive, 4);
            MainActivity.setDebugImage(negative, 5);
            MainActivity.setDebugImage(positiveSmall, 6);
            MainActivity.setDebugImage(negativeSmall, 7);
            MainActivity.setDebugText("" + GenUtils.brightness(positiveSmall) + "-" + GenUtils.brightness(negativeSmall), 6);
            MainActivity.setDebugText("" + (GenUtils.brightness(positiveSmall) -GenUtils.brightness(negativeSmall)), 7);
        }
        if(true){
            int i = 0, j=2;
            Mat matLearned = finalMap.get(6);
            Mat number = numbersCrop[i][j];

            Mat positive = new Mat(number.size(), CvType.CV_8UC1,new Scalar(0));
            Mat positiveSmall = new Mat(new Size(SCALE_SIZE,SCALE_SIZE), CvType.CV_8UC1, new Scalar(0));
            Mat negative = new Mat(number.size(), CvType.CV_8UC1,new Scalar(0));
            Mat negativeSmall = new Mat(new Size(SCALE_SIZE,SCALE_SIZE), CvType.CV_8UC1, new Scalar(0));
            Mat negative2 = new Mat(number.size(), CvType.CV_8UC1,new Scalar(0));
            Mat negativeSmall2 = new Mat(new Size(SCALE_SIZE,SCALE_SIZE), CvType.CV_8UC1, new Scalar(0));

            Core.multiply(matLearned, number, positive);
            Core.subtract(matLearned, number, negative);
            Core.subtract(number, matLearned, negative2);
            Core.add(negative, negative2, negative);
            Imgproc.resize(positive, positiveSmall, new Size(SCALE_SIZE, SCALE_SIZE));
            Imgproc.resize(negative, negativeSmall, new Size(SCALE_SIZE, SCALE_SIZE));
            Imgproc.resize(negative2, negativeSmall2, new Size(SCALE_SIZE, SCALE_SIZE));

            MainActivity.setDebugImage(positiveSmall, 8);
            MainActivity.setDebugImage(negativeSmall, 9);
            MainActivity.setDebugText("" + GenUtils.brightness(positiveSmall) + "-" + GenUtils.brightness(negativeSmall), 8);
            MainActivity.setDebugText("" + (GenUtils.brightness(positiveSmall) - GenUtils.brightness(negativeSmall)), 9);
//            MainActivity.setDebugImage(negativeSmall2, 11);
        }



        //Print results
        GenUtils.printBoard(digits);
        int errorCount = 0;
        int[][] solution = TrainSet.getInstance(mContext).sampleProblemArr.get(0).data;
        for(int i=0;i<digits.length;i++){
            for(int j=0;j<digits[0].length;j++){
                if(digits[i][j]!=solution[i][j])errorCount++;
            }
        }
        Logg.d(TAG,"Error Count in detecting correct numbers : "+errorCount);






        return digits;
    }



}
