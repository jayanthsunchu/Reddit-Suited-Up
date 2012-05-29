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
import android.widget.LinearLayout;
import android.widget.TextView;

public class AccountListAdapter extends BaseAdapter {
	LayoutInflater lInflater;
	ArrayList<HashMap<String, String>> arrayList;
	public Activity context;

	public AccountListAdapter(Activity ctx,
			ArrayList<HashMap<String, String>> returnList) {
		this.context = ctx;
		this.arrayList = returnList;
		this.lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {

		return arrayList.size();
	}

	@Override
	public Object getItem(int v) {

		return arrayList.get(v);
	}

	@Override
	public long getItemId(int v) {

		return v;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = lInflater.inflate(R.layout.accountlistlayout, null);
			vh = new ViewHolder();
			vh.txt = (TextView) convertView.findViewById(R.id.txtAccountName);
			vh.addLayout = (LinearLayout) convertView
					.findViewById(R.id.addDefaultLayout);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		vh.txt.setText(arrayList.get(position).get("name"));
		if (arrayList.get(position).get("current").trim().matches("true")) {

			vh.addLayout.setVisibility(0);
		} else {
			vh.addLayout.setVisibility(8);

		}
		return convertView;
	}

	public class ViewHolder {
		TextView txt;
		LinearLayout addLayout;
	}

}
