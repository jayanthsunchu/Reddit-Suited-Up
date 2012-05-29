package com.rsu.jayanthsunchu.redditsuitedup;

import android.view.GestureDetector;
import android.view.View;

public class ImpGestureDetector extends GestureDetector {
	View cur;
	public ImpGestureDetector(OnGestureListener listener) {
		super(listener);
		// TODO Auto-generated constructor stub
	}
	
	public void setCurv(View v){
		cur = v;
	}
	
	public View getCurv(){
		return cur;
	}
	
	

}
