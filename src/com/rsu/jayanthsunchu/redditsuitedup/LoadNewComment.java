package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class LoadNewComment extends AsyncTask<Context, Integer, String> {
	String idFor;
	Node<HashMap<String, String>> newNode;
	Node<HashMap<String, String>> wholeList;
	ArrayList<HashMap<String, String>> returnArray;
	
	ProgressDialog dil;
	Context context;
	CommentsAdapter adapter;
	ListView listView;
	Activity act;
	String postAuthor;
	public LoadNewComment(Context ctx, Node<HashMap<String, String>> list,
			Node<HashMap<String, String>> newn,
			ArrayList<HashMap<String, String>> returna, String id,
			CommentsAdapter ca, ListView li, Activity ac, String a) {
		this.postAuthor = a;
		this.context = ctx;
		this.wholeList = list;
		this.newNode = newn;
		this.returnArray = returna;
		this.idFor = id;
		this.dil = new ProgressDialog(context);
		this.adapter = ca;
		this.listView = li;
		this.act = ac;
	}

	@Override
	protected String doInBackground(Context... arg0) {
		Node.recursiveFun(wholeList.getChildren(), idFor, newNode, context);
		// returnArrayList.add(maps);

		return "complete";
	}

	protected void onPreExecute() {

		dil.setMessage("loading");
		dil.show();
	}

	protected void onPostExecute(String result) {
		if (dil.isShowing())
			dil.dismiss();
		adapter = new CommentsAdapter(act, returnArray, wholeList, postAuthor);
		adapter.notifyDataSetChanged();
		if (listView != null) {

			listView.setAdapter(adapter);
			listView.invalidateViews();
			listView.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					if (firstVisibleItem >= visibleItemCount) {
						adapter.checking = 0;
					}
				}

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {

				}

			});
		}
	}

}
