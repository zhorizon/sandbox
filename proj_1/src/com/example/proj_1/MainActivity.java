package com.example.proj_1;

import java.lang.ref.WeakReference;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
    public void onStart() {
    	super.onResume();
    	
    	ImageView iv = (ImageView) findViewById(R.id.image_view);
    	
    	loadBitmap(R.drawable.test, iv);
    }
    
    // Copy from Google, Develop > Training > Processing Bitmaps Off the UI Thread
    // Common view components such as ListView and GridView introduce another issue when used in conjunction
    // with the AsyncTask as demonstrated in the previous section. In order to be efficient with memory, these
    // components recycle child views as the user scrolls. If each child view triggers an AsyncTask, there is no
    // guarantee that when it completes, the associated view has not already been recycled for use in another
    // child view. Furthermore, there is no guarantee that the order in which asynchronous tasks are started is
    // the order that they complete.
    
    private void loadBitmap(int resId, ImageView imageView) {
    	Bitmap mPlaceHolderBitmap = null;

    	if (cancelPotentialWork(resId, imageView)) {
    		final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
    		final AsyncDrawable asyncDrawable = new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
    		imageView.setImageDrawable(asyncDrawable);
    		task.execute(resId);
    	}
    }
    
    public static boolean cancelPotentialWork(int data, ImageView imageView) {
    	final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
    	
    	if (bitmapWorkerTask != null) {
    		final int bitmapData = bitmapWorkerTask.data;
    		// If bitmapData is not yet set or it differs from the new data
    		if (bitmapData == 0 || bitmapData != data) {
    			// Cancel previous task
    			bitmapWorkerTask.cancel(true);
    		} else {
    			// The same work is already in progress
    			return false;
    		}
    	}
    	// No task associated with the ImageView, or an existing task was cancelled
    	return true;
    }
    
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
    	if (imageView != null) {
    		final Drawable drawable = imageView.getDrawable();
    		if (drawable instanceof AsyncDrawable) {
    			final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
    			return asyncDrawable.getBitmapWorkerTask();
    		}
    	}
    	return null;
    }
    
    static class AsyncDrawable extends BitmapDrawable {
    	private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
    	
    	public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
    		super(res, bitmap);
    		bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
    	}
    	
    	public BitmapWorkerTask getBitmapWorkerTask() {
    		return bitmapWorkerTaskReference.get();
    	}
    }
    
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    	private final WeakReference<ImageView> imageViewReference;
    	private int data = 0;
    	
    	public BitmapWorkerTask(ImageView imageView) {
    		// Use a WeakReference to ensure the ImageVieew can b garbage collected
    		imageViewReference = new WeakReference<ImageView>(imageView);
    	}

    	// Decode image in background.
		@Override
		protected Bitmap doInBackground(Integer... params) {
			data = params[0];
			return decodeSampleBitmapFromResource(getResources(), data, 100, 100);
		}
		
		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}
			
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
    }
    
    public static Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
    	// First decode with inJustDecodeBounds=true to check dimensions
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeResource(res, resId, options);
    	
    	// Calculate inSampleSize
    	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    	
    	// Decode bitmap with inSampleSize set
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
    		
    		// Calculate the largest inSampleSize value that is a power of 2 and keeps both
    		// height and width larger than the requested height and width.
    		while (halfHeight / inSampleSize > reqHeight || halfWidth / inSampleSize > reqWidth)
    			inSampleSize *= 2;
    	}
    	
    	return inSampleSize;
    }
}
