package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

public class LoadComments extends AsyncTask<Context, Integer, String> {
	Context context;
	String idForOperation;
	Activity act;
	SharedPreferences sh;
	ArrayList<HashMap<String, String>> returnArrayList = new ArrayList<HashMap<String, String>>();

	CommentsAdapter adapter;
	Tree<HashMap<String, String>> treeComments = new Tree<HashMap<String, String>>();
	Node<HashMap<String, String>> commentNodes = new Node<HashMap<String, String>>();
	LinearLayout progressLayout;
	ListView lstView;
	String postAuthor;
	public LoadComments(Context ctx, Activity ac, String id,
			SharedPreferences prefs, ArrayList<HashMap<String, String>> arl,
			CommentsAdapter ca, Tree<HashMap<String, String>> tree,
			Node<HashMap<String, String>> node, LinearLayout pro, ListView lst, String author) {
		this.context = ctx;
		this.act = ac;
		this.postAuthor = author;
		this.idForOperation = id;
		this.sh = prefs;
		this.returnArrayList = arl;
		this.adapter = ca;
		this.treeComments = tree;
		this.commentNodes = node;
		this.progressLayout = pro;
		this.lstView = lst;
	}

	@Override
	protected String doInBackground(Context... arg0) {
		loadComments();
		return "complete";
	}

	protected void onPreExecute() {
		progressLayout.setVisibility(0);
	}

	protected void onPostExecute(String result) {
		progressLayout.setVisibility(8);
		adapter = new CommentsAdapter(act, returnArrayList, commentNodes, postAuthor);
		lstView.setAdapter(adapter);

		lstView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem >= visibleItemCount){
					adapter.checking= 0;
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
				
			}

		});

	}

	public void loadComments() {

		JSONArray commentData = CommentsAndLink.getComments(idForOperation
				+ "/.json", sh);
		// Log.e("commentlog", Integer.toString(commentData.length()));

		JSONObject json2 = null;
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<HashMap<String, String>> alists = new ArrayList<HashMap<String, String>>();
		map.put("body", "comments");
		ArrayList<HashMap<String, String>> alist = new ArrayList<HashMap<String, String>>();
		alist.add(map);
		commentNodes.setData(map);
		treeComments.setRootElement(commentNodes);

		try {
			int level = 0;
			int index = 0;
			json2 = commentData.getJSONObject(1);
			JSONObject s = json2.getJSONObject("data");
			JSONArray arr = s.getJSONArray("children");
			ArrayList arrr;

			for (int i = 0; i < arr.length(); i++) {
				JSONObject json = arr.getJSONObject(i);
				String st = json.getString("kind");

				if (st.matches("t1")) {
					Node<HashMap<String, String>> comNode = new Node<HashMap<String, String>>();
					JSONObject jsonComment = json.getJSONObject("data");
					// String sComment = jsonComment.getString("body");
					HashMap<String, String> maps = new HashMap<String, String>();
					maps.put("links", jsonComment.getString("body"));
					maps.put("body", Mdown.getHtml(jsonComment.getString("body")));
					maps.put("author", jsonComment.getString("author"));
					maps.put("name", jsonComment.getString("name"));
					maps.put("id", jsonComment.getString("id"));
					maps.put("ups", jsonComment.getString("ups"));
					maps.put("downs", jsonComment.getString("downs"));
					maps.put("level", Integer.toString(level));
					maps.put("index", Integer.toString(index));
					maps.put("vote", "0");
					ArrayList<HashMap<String, String>> thisList = new ArrayList<HashMap<String, String>>();
					thisList.add(maps);
					comNode.setData(maps);
					commentNodes.insertChildAt(index, comNode);

					index++;
					JSONObject replies = new JSONObject();
					replies = jsonComment.optJSONObject("replies");
					if (replies != null) {
						recursiveCheckFunction(replies, comNode, level + 1);
					}

				}
			}
			List<Node<HashMap<String, String>>> childAr = commentNodes
					.getChildren();
			for (int i = 0; i < childAr.size(); i++) {
				Node<HashMap<String, String>> chi = childAr.get(i);
				HashMap<String, String> nodeData = chi.getData();
				HashMap<String, String> addMap = new HashMap<String, String>();
				addMap.put("links", nodeData.get("links"));
				addMap.put("body", Mdown.getHtml(nodeData.get("body")));
				addMap.put("name", nodeData.get("name"));
				addMap.put("level", nodeData.get("level"));
				addMap.put("author", nodeData.get("author"));
				addMap.put("vote", "0");
				addMap.put("id", nodeData.get("id"));
				addMap.put("ups", nodeData.get("ups"));
				addMap.put("downs", nodeData.get("downs"));
				addMap.put("index", nodeData.get("index"));
				returnArrayList.add(addMap);
				// adapter.notifyDataSetChanged();
			}

		}

		catch (Exception ex) {
			// Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
			ex.printStackTrace();
		}
	}

	public void recursiveCheckFunction(JSONObject replies,
			Node<HashMap<String, String>> parentNode, int level) {

		try {
			JSONObject repOne = replies.getJSONObject("data");
			JSONArray repliesArray = new JSONArray();
			repliesArray = repOne.getJSONArray("children");
			for (int j = 0; j < repliesArray.length(); j++) {
				JSONObject jsonReply = repliesArray.getJSONObject(j);
				JSONObject jsonReplyObject = jsonReply.getJSONObject("data");

				Node<HashMap<String, String>> comPuNode = new Node<HashMap<String, String>>();
				HashMap<String, String> mapsReply = new HashMap<String, String>();
				String body = "nothing";
				String author = "";
				String id = "";
				String ups = "";
				String downs = "";
				if (jsonReplyObject.has("body")) {
					body = Mdown.getHtml(jsonReplyObject.getString("body"));
					author = jsonReplyObject.getString("author");
					id = jsonReplyObject.getString("id");
					ups = jsonReplyObject.getString("ups");
					downs = jsonReplyObject.getString("downs");
					mapsReply.put("links", jsonReplyObject.getString("body"));
					mapsReply.put("body", body);
					mapsReply.put("author", author);
					mapsReply.put("name", jsonReplyObject.getString("name"));
					mapsReply.put("id", id);
					mapsReply.put("ups", ups);
					mapsReply.put("downs", downs);
					mapsReply.put("vote", "0");
					mapsReply.put("level", Integer.toString(level));
					mapsReply.put("index", Integer.toString(j));
					comPuNode.setData(mapsReply);
					parentNode.insertChildAt(j, comPuNode);

					JSONObject childReplies = jsonReplyObject
							.optJSONObject("replies");
					if (childReplies != null) {
						recursiveCheckFunction(childReplies, comPuNode, level + 1);
					}
				}
				

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			// Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
		}
	}

}
