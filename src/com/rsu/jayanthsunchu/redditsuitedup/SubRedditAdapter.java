package com.rsu.jayanthsunchu.redditsuitedup;


import java.util.ArrayList;
import java.util.HashMap;

import com.rsu.jayanthsunchu.redditsuitedup.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SubRedditAdapter extends BaseAdapter {
	Activity context;
	LayoutInflater mInflater;
	ArrayList<HashMap<String, String>> returnList;

	public SubRedditAdapter(Activity ctxt,
			ArrayList<HashMap<String, String>> arrayList) {
		this.context = ctxt;
		this.returnList = arrayList;
		this.mInflater = (LayoutInflater) ctxt
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return returnList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return returnList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.favsubredditlayout, null);
			vh = new ViewHolder();
			vh.txt = (TextView)convertView.findViewById(R.id.txtItem);
			
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		
		vh.txt.setText(returnList.get(position).get("name"));
		
		return convertView;
	}
	
	public class ViewHolder {
		TextView txt;
		
	}

}
