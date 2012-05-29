package com.rsu.jayanthsunchu.redditsuitedup;

import java.io.File;
import java.io.FileOutputStream;


import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import com.rsu.jayanthsunchu.redditsuitedup.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

public class ImageManager {

	// private HashMap<String, SoftReference<Bitmap>> imageMap2 = new
	// HashMap<String, SoftReference<Bitmap>>();
	private HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
	private File cacheDir;
	private ImageQueue imageQueue = new ImageQueue();
	private Thread imageLoaderThread = new Thread(new ImageQueueManager());
	private int displayWidth;
	private int MAX_IMAGE_SIZE = 1600;

	public ImageManager(Context context) {
		
		imageLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);

		
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdDir, "data/codehenge");
		} else
			cacheDir = context.getCacheDir();

		if (!cacheDir.exists())
			cacheDir.mkdirs();

		try {
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			Display disply = wm.getDefaultDisplay();
			displayWidth = disply.getWidth();

		} catch (Exception ex) {
			Log.e("log_tag", ex.toString());

		}

	}

	public void displayImage(String url, Activity activity, ImageView imageView) {
		if (imageMap.containsKey(imageView.getTag().toString()))
			imageView.setImageBitmap(imageMap
					.get(imageView.getTag().toString()));
		else {
			queueImage(url, activity, imageView);
			imageView.setImageResource(R.drawable.loadingalien);
		}
	}

	private void queueImage(String url, Activity activity, ImageView imageView) {
		
		imageQueue.Clean(imageView);
		ImageRef p = new ImageRef(imageView.getTag().toString(), imageView);

		synchronized (imageQueue.imageRefs) {
			imageQueue.imageRefs.push(p);
			imageQueue.imageRefs.notifyAll();
		}

		
		if (imageLoaderThread.getState() == Thread.State.NEW)
			imageLoaderThread.start();
	}

	private Bitmap getBitmap(String url) {
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);

		
		Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
		if (bitmap != null)
			return bitmap;

		
		try {

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new URL(url).openConnection()
					.getInputStream(), null, opts);
			int scale = 1;
			if (opts.outHeight > MAX_IMAGE_SIZE
					|| opts.outHeight > MAX_IMAGE_SIZE) {
				scale = (int) Math.pow(2, (int) Math.round(Math
						.log(MAX_IMAGE_SIZE
								/ (double) Math.max(opts.outHeight,
										opts.outWidth))
						/ Math.log(0.5)));
			}
			BitmapFactory.Options opts2 = new BitmapFactory.Options();
			opts2.inSampleSize = scale;
			opts2.inPurgeable = true;
			bitmap = BitmapFactory.decodeStream(new URL(url).openConnection()
					.getInputStream(), null, opts2);
			
			float scalingFactor = (float) displayWidth
					/ (float) bitmap.getWidth();
			int scaleWidth = (int) (bitmap.getWidth() * scalingFactor);
			int scaleHeight = (int) (bitmap.getHeight() * scalingFactor);

			Bitmap bmd = Bitmap.createScaledBitmap(bitmap, scaleWidth,
					scaleHeight, true);
			bitmap.recycle();
			bitmap = null;
			writeFile(bmd, f);

			return bmd;
		} catch (Exception ex) {
			Log.e("log_tag", ex.toString());
			return null;
		}
	}

	private void writeFile(Bitmap bmp, File f) {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.JPEG, 40, out);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
			}
		}
	}

	

	private class ImageRef {
		public String url;
		public ImageView imageView;

		public ImageRef(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	
	private class ImageQueue {
		private Stack<ImageRef> imageRefs = new Stack<ImageRef>();

		
		public void Clean(ImageView view) {

			for (int i = 0; i < imageRefs.size();) {
				if (imageRefs.get(i).imageView == view)
					imageRefs.remove(i);
				else
					++i;
			}
		}
	}

	private class ImageQueueManager implements Runnable {
		@Override
		public void run() {
			try {
				while (true) {
					
					if (imageQueue.imageRefs.size() == 0) {
						synchronized (imageQueue.imageRefs) {
							imageQueue.imageRefs.wait();
						}
					}

					
					if (imageQueue.imageRefs.size() != 0) {
						ImageRef imageToLoad;

						synchronized (imageQueue.imageRefs) {
							imageToLoad = imageQueue.imageRefs.pop();
						}

						Bitmap bmp = getBitmap(imageToLoad.url);
						// imageMap.put(imageToLoad.url, bmp);
						Object tag = imageToLoad.imageView.getTag();

						
						if (tag != null
								&& ((String) tag).equals(imageToLoad.url)) {
							BitmapDisplayer bmpDisplayer = new BitmapDisplayer(
									bmp, imageToLoad.imageView);

							Activity a = (Activity) imageToLoad.imageView
									.getContext();

							a.runOnUiThread(bmpDisplayer);
						}
					}

					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
			}
		}
	}

	
	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageView imageView;

		public BitmapDisplayer(Bitmap b, ImageView i) {
			bitmap = b;
			imageView = i;
		}

		public void run() {
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);

			}

			else
				imageView.setImageResource(R.drawable.loadingalien);
		}
	}
}