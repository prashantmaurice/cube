package com.maurice.app.cube;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.maurice.app.cube.ImageParser.GenUtils;
import com.maurice.app.cube.ImageParser.ImageParser;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

//    static{ System.loadLibrary("opencv_java"); }
    static {
        if (!OpenCVLoader.initDebug()) {
//            // Handle initialization error
        }
    }

    static ImageView imageView,imageView2,imageView3,imageView4,imageView5,imageView6,imageView7,imageView8,imageView9,imageView10,imageView11;
    static TextView t2,t3,t4,t5,t6,t7,t8,t9,t10,t11;
    static ArrayList<ImageView> imageViews = new ArrayList<>();
    static ArrayList<TextView> textViews = new ArrayList<>();
    ImageParser imageparser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(OpenCVLoader.initDebug()){

            setupUi();



            imageparser = ImageParser.getInstance(this);
//            digitRecogniser2 = DigitRecogniser2.getInstance(this);


            //Source Image Bitmap
            Bitmap sourceBitmap = GenUtils.decodeSampledBitmapFromResource(getResources(), R.drawable.sample1, 400, 200);

            //Get rendered bitmap
            try{
                Bitmap renderedBitmap = imageparser.parseBitmap(sourceBitmap);
            }catch (ImageProcessException e){

            }

            //Show rendered bitmap
            imageView.setImageBitmap(sourceBitmap);
//            imageView.setImageBitmap(digitRecogniser2.trainSet.trainDataArr.get(0).bitmap);
//            imageView3.setImageBitmap(GenUtils.convertMatToBitmap(digitRecogniser2.finalMap.get(2)));
//            MainActivity.setDebugImage(digitRecogniser2.finalMap.get(6),2);
//            MainActivity.setDebugImage(digitRecogniser2.finalMap.get(8),3);
//            imageView3.setImageResource(R.drawable.train_3);

            //Solve for sample problem
//            int[][] digits = TrainSet.getInstance(this).sampleProblemArr.get(0).data;
//            GenUtils.printBoard(digits);
//            int[][] solved = SudokuAI.getSolved(digits);
//            GenUtils.printBoard(solved);

        }else{
            Log.e("ERROR", "OpenCVLoader not initialized");
        }

    }

    private void setupUi() {
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);
        imageView6 = (ImageView) findViewById(R.id.imageView6);
        imageView7 = (ImageView) findViewById(R.id.imageView7);
        imageView8 = (ImageView) findViewById(R.id.imageView8);
        imageView9 = (ImageView) findViewById(R.id.imageView9);
        imageView10 = (ImageView) findViewById(R.id.imageView10);
        imageView11 = (ImageView) findViewById(R.id.imageView11);
        imageViews.add(imageView2);imageViews.add(imageView3);
        imageViews.add(imageView4);imageViews.add(imageView5);
        imageViews.add(imageView6);imageViews.add(imageView7);
        imageViews.add(imageView8);imageViews.add(imageView9);
        imageViews.add(imageView10);imageViews.add(imageView11);

        t2 = (TextView) findViewById(R.id.textView2);
        t3 = (TextView) findViewById(R.id.textView3);
        t4 = (TextView) findViewById(R.id.textView4);
        t5 = (TextView) findViewById(R.id.textView5);
        t6 = (TextView) findViewById(R.id.textView6);
        t7 = (TextView) findViewById(R.id.textView7);
        t8 = (TextView) findViewById(R.id.textView8);
        t9 = (TextView) findViewById(R.id.textView9);
        t10 = (TextView) findViewById(R.id.textView10);
        t11 = (TextView) findViewById(R.id.textView11);
        textViews.add(t2);textViews.add(t3);textViews.add(t4);textViews.add(t5);textViews.add(t6);
        textViews.add(t7);textViews.add(t8);textViews.add(t9);textViews.add(t10);textViews.add(t11);

    }

    public static void setDebugImage(Mat mat, int place){
        Bitmap bitmap = GenUtils.convertMatToBitmap(mat);
        if(imageViews.size()>place){
            imageViews.get(place).setImageBitmap(bitmap);
        }
    }
    public static void setMainImage(Mat mat){
        Bitmap bitmap = GenUtils.convertMatToBitmap(mat);
        if(imageView!=null) imageView.setImageBitmap(bitmap);
    }
    public static void setDebugText(String text, int place){
        if(textViews.size()>place){
            textViews.get(place).setText(text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
