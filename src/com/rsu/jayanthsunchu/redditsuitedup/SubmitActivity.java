package com.rsu.jayanthsunchu.redditsuitedup;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class SubmitActivity extends TabActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		  setContentView(R.layout.submit_layout);

		    TabHost mTabHost = getTabHost();
		    
		    mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator("SUBMIT - LINK").setContent(R.id.submitLinkLayout));
		    mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator("SUBMIT - TEXT").setContent(R.id.submitTextLayout));
		    mTabHost.setCurrentTab(0);
		    
		    
	}
	
	
}
