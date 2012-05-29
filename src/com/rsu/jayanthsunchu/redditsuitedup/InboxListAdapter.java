package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InboxListAdapter extends BaseAdapter {
	ArrayList<HashMap<String, String>> returnArrayList;
	Activity context;
	LayoutInflater ourInflater;

	public InboxListAdapter(Activity ctx,
			ArrayList<HashMap<String, String>> list) {
		this.context = ctx;

		this.returnArrayList = list;
		ourInflater = (LayoutInflater) ctx
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
			convertView = ourInflater.inflate(R.layout.inboxlistlayout, null);
			vh = new ViewHolder();
			vh.txtBody = (TextView) convertView
					.findViewById(R.id.txtMessageBody);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		if(returnArrayList.get(position).get("new").matches("false")){
			
			vh.txtBody.setText(Html.fromHtml("<font size='13sp'><b>"
					+ returnArrayList.get(position).get("subject")
					+ "</b></font><br />"
					+ "<font size='11sp' color='#646464'><i>"
					+ returnArrayList.get(position).get("author")
					+ " <br />"
					+ DateUtils.getRelativeTimeSpanString((long) Float
							.parseFloat(returnArrayList.get(position).get(
									"created_utc")) * 1000) + " via "
					+ returnArrayList.get(position).get("subreddit")
					+ "</i></font><br />"
					+ returnArrayList.get(position).get("body")));
		}
		else {
			vh.txtBody.setText(Html.fromHtml("<font size='13sp'><b>"
					+ returnArrayList.get(position).get("subject") + "(new message)"
					+ "</b></font><br />"
					+ "<font size='11sp' color='#646464'><i>"
					+ returnArrayList.get(position).get("author")
					+ " <br />"
					+ DateUtils.getRelativeTimeSpanString((long) Float
							.parseFloat(returnArrayList.get(position).get(
									"created_utc")) * 1000) + " via "
					+ returnArrayList.get(position).get("subreddit")
					+ "</i></font><br />"
					+ returnArrayList.get(position).get("body")));
			
		}
		
		
		
		return convertView;
	}

	public class ViewHolder {
		TextView txtBody;
	}
}
