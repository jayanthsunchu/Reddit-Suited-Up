package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;


public class PostGestureDetector extends
		GestureDetector.SimpleOnGestureListener {

	Context context;
	ArrayList<HashMap<String, String>> returnList;

	public PostGestureDetector(Context ctx,
			ArrayList<HashMap<String, String>> list) {
		this.context = ctx;
		this.returnList = list;
	}

	private static final int SWIPE_MIN_DISTANCE = 50;
	
	private static final int SWIPE_THRESHOLD_VELOCITY = 150;

	@Override
	public boolean onSingleTapConfirmed(MotionEvent ev) {

		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent ev) {

		return true;
	}

	@Override
	public void onShowPress(MotionEvent ev) {

	}

	@Override
	public void onLongPress(MotionEvent ev) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// right to left swipe
//					if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
//						Toast.makeText(context, "left", Toast.LENGTH_LONG).show();
//						return true;
//					}
//					else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
//						Toast.makeText(context, "right", Toast.LENGTH_LONG).show();
//						return true;
//					}
		return false;
	}

	@Override
	public boolean onDown(MotionEvent ev) {

		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		// right to left swipe
		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			Toast.makeText(context, "left", Toast.LENGTH_LONG).show();
			return true;
		} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
				&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
			Toast.makeText(context, "right", Toast.LENGTH_LONG).show();
			return true;
		}

		return true;
	}
}
