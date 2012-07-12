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

public class ParentCommentAdapter extends BaseAdapter {
  Activity context;
	LayoutInflater mInflater;
	ArrayList<HashMap<String, String>> returnList;

	public ParentCommentAdapter(Activity ctxt,
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
			convertView = mInflater.inflate(R.layout.parentcommentlayout, null);
			vh = new ViewHolder();
			vh.txt = (TextView)convertView.findViewById(R.id.parentcomment);

			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		if(returnList.size() > 0)
		vh.txt.setText(returnList.get(position).get("name"));

		return convertView;
	}

	public class ViewHolder {
		TextView txt;

	}

}
