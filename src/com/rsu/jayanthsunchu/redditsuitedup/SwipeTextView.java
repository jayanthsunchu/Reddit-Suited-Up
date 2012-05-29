package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

public class SwipeTextView extends TextView implements OnTouchListener {
	GestureDetector gd;
	View.OnTouchListener gl;
	public SwipeTextView(Context context) {
		super(context);
		gd = new GestureDetector(new PostGestureDetectorComment(context, new ArrayList<HashMap<String, String>>(), new
				QuickAction(context)));
		gl = new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return gd.onTouchEvent(event);
			}
		};
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return gd.onTouchEvent(event);
	}

	public class PostGestureDetectorComment extends
			GestureDetector.SimpleOnGestureListener {

		final int swipeMinDistance;
		final int swipeMaxOffPath;
		final int swipeThresholdVelocity;

		Context contextC;
		ArrayList<HashMap<String, String>> returnAList;
		QuickAction thisQuick;

		public PostGestureDetectorComment(Context ctx,
				ArrayList<HashMap<String, String>> list, QuickAction quick) {
			this.contextC = ctx;
			this.returnAList = list;
			DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
			swipeMinDistance = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
			swipeMaxOffPath = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
			swipeThresholdVelocity = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);
			this.thisQuick = quick;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent ev) {

			Toast.makeText(contextC, "single tap up", Toast.LENGTH_LONG).show();
			return false;
		}

		@Override
		public void onLongPress(MotionEvent ev) {

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {

				if (Math.abs(e1.getY() - e2.getY()) > swipeMaxOffPath)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > swipeMinDistance
						&& Math.abs(velocityX) > swipeThresholdVelocity) {

				} else if (e2.getX() - e1.getX() > swipeMinDistance
						&& Math.abs(velocityX) > swipeThresholdVelocity) {

				}
			} catch (Exception ex) {
				// just in case - jc
			}

			return false;
		}
	}

}
