package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.HashMap;

import com.rsu.jayanthsunchu.redditsuitedup.R;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProfileAdapter extends BaseAdapter {
	LayoutInflater mInflater;
	Activity context;
	ArrayList<HashMap<String, String>> returnArrayList;

	public ProfileAdapter(Activity ctx, ArrayList<HashMap<String, String>> list) {
		this.context = ctx;
		this.returnArrayList = list;
		this.mInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {

		return returnArrayList.size();
	}

	@Override
	public Object getItem(int position) {

		return returnArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.profilelistlayout, null);
			vh = new ViewHolder();
			vh.txtTitle = (TextView) convertView.findViewById(R.id.txtContent);
			vh.txtVotes = (TextView) convertView.findViewById(R.id.txtVotes);
			vh.txtComments = (TextView) convertView
					.findViewById(R.id.txtComments);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		//improvised - jc - may 08 2012
		if (returnArrayList.get(position).get("kind").matches("t1")) {
			vh.txtTitle.setText(Html.fromHtml(returnArrayList.get(position).get("body")));
			vh.txtVotes.setText(Html.fromHtml("<font color='#686868'><b>" + returnArrayList.get(position).get("author") + "(" + returnArrayList.get(position).get("ups") + " |"
					+ returnArrayList.get(position).get("downs") + ")" + "</b></font>"));
			vh.txtComments.setText("");

		} else {
			vh.txtTitle.setText(returnArrayList.get(position).get("title"));
			vh.txtVotes.setText(returnArrayList.get(position).get("score"));
			vh.txtComments.setText(returnArrayList.get(position).get(
					"num_comments")
					+ " comments"
					+ ", to: "
					+ returnArrayList.get(position).get("subreddit")
					+ ", by: "
					+ returnArrayList.get(position).get("author"));
		}

		return convertView;
	}

	public class ViewHolder {
		TextView txtTitle;
		TextView txtVotes;
		TextView txtComments;
	}

}
