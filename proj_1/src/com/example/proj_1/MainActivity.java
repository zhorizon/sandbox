package com.example.proj_1;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.widget.ImageView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onResume() {
    	ImageView iv = (ImageView) findViewById(R.id.image_view);
    	
    	iv.setImageBitmap(decodeSampleBitmapFromResource(getResources(), R.drawable.ic_launcher, 10, 10));
    }
    
    public static Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeResource(res, resId, options);
    	
    	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    	
    	options.inJustDecodeBounds = false;
    	
    	return BitmapFactory.decodeResource(res, resId, options);
    }
    
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    	// raw height and width of the image
    	int height = options.outHeight;
    	int width = options.outWidth;
    	int inSampleSize = 1;
    	
    	if (height > reqHeight || width > reqWidth) {
    		final int halfHeight = height / 2;
    		final int halfWidth = width / 2;
    		
    		while (halfHeight / inSampleSize > reqHeight || halfWidth / inSampleSize > reqWidth)
    			inSampleSize *= 2;
    	}
    	
    	return inSampleSize;
    }
}
