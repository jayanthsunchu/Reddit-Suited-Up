package com.rsu.jayanthsunchu.redditsuitedup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentsAndLink extends Activity {
	ArrayList<HashMap<String, String>> returnArrayList = new ArrayList<HashMap<String, String>>();
	LoadComments loadComments;
	CommentsAdapter adapter;
	Tree<HashMap<String, String>> treeComments = new Tree<HashMap<String, String>>();
	Node<HashMap<String, String>> commentNodes = new Node<HashMap<String, String>>();

	LoadNewComment loadnew;

	private ViewPager awesomePager;
	ProgressDialog dil;
	private static int NUM_AWESOME_VIEWS = 2;
	private Context cxt;
	private AwesomePagerAdapter awesomeAdapter;
	SharedPreferences sh;
	ListView listView;
	String url = "";
	private Activity ac;
	AsynchronousOptions asyncOperations;
	String id = "";
	String selfText = "";
	String author = "";
	String title = "";
	String score = "";
	String vote = "";
	String saved = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentsandlink);
		cxt = this;
		ac = CommentsAndLink.this;
		Bundle ext = getIntent().getExtras();

		if (ext != null) {
			url = ext.getString("url");
			id = ext.getString("id");
			selfText = ext.getString("selftext");
			author = ext.getString("author");
			title = ext.getString("title");
			score = ext.getString("score");
			saved = ext.getString("saved");
			vote = ext.getString("vote");
		}

		sh = this.getSharedPreferences(Constants.PREFS_NAME, 0);
		awesomeAdapter = new AwesomePagerAdapter(url, id, selfText, author,
				title, score, vote, saved, sh);
		setUpViews();
		awesomePager = (ViewPager) findViewById(R.id.commentandlinkspager);
		awesomePager.setAdapter(awesomeAdapter);
	}

	public void setUpViews() {
		ImageView imgUpvote = (ImageView) findViewById(R.id.iv2upvote);
		ImageView imgDownvote = (ImageView) findViewById(R.id.iv2downvote);
		Button btnSave = (Button) findViewById(R.id.btn2save);

		Button btnShare = (Button) findViewById(R.id.btn2share);
		Button btnUser = (Button) findViewById(R.id.btn2user);
		btnShare.setOnClickListener(shareClick);
		btnUser.setOnClickListener(userClick);

		if (saved.matches("true")) {
			btnSave.setText("UNSAVE");
			btnSave.setOnClickListener(unsaveClick);
		} else {
			btnSave.setText("SAVE");
			btnSave.setOnClickListener(saveClick);

		}
		if (vote.matches("1")) {
			imgUpvote.setImageResource(R.drawable.upvoteselected);
		} else if (vote.matches("-1")) {
			imgDownvote.setImageResource(R.drawable.downvoteselected);
		}
		imgUpvote.setTag(imgDownvote);
		imgDownvote.setTag(imgUpvote);
		imgUpvote.setOnClickListener(upvoteClick);

		imgDownvote.setOnClickListener(downvoteClick);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Constants.CONST_RESULT_COMMENT_CODE) {
			Bundle br = intent.getExtras();
			if (br != null) {
				HashMap<String, String> maps = new HashMap<String, String>();
				maps.put("links", br.getString("newtext"));
				maps.put("body", Mdown.getHtml(br.getString("newtext")));
				maps.put("author", sh.getString("currentusername", "RSU"));
				maps.put("name", "t1_something");
				maps.put("id", "33");
				maps.put("ups", "1");
				maps.put("downs", "0");
				maps.put("level", "1");
				maps.put("index", "1");
				maps.put("vote", "1");
				Node<HashMap<String, String>> newNode = new Node<HashMap<String, String>>();
				newNode.setData(maps);
				if (!br.getBoolean("flag")) {

					loadnew = new LoadNewComment(CommentsAndLink.this,
							commentNodes, newNode, returnArrayList,
							br.getString("id"), adapter, listView,
							CommentsAndLink.this, author);
					loadnew.execute(CommentsAndLink.this);
				} else {
					returnArrayList.add(maps);
					commentNodes.addChild(newNode);
					adapter = new CommentsAdapter(CommentsAndLink.this,
							returnArrayList, commentNodes, author);
					adapter.notifyDataSetChanged();
					listView.setAdapter(adapter);
					listView.invalidateViews();
					listView.setOnScrollListener(new OnScrollListener() {

						@Override
						public void onScroll(AbsListView view,
								int firstVisibleItem, int visibleItemCount,
								int totalItemCount) {
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.commentoptions, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.openLinkInBrowser:
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i);
			break;
		}
		return true;

	}

	private OnClickListener upvoteClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (sh.getInt("loggedinornot", 0) == 0) {
				Toast.makeText(v.getContext(), "log in for this action",
						Toast.LENGTH_LONG).show();
			} else {
				if (vote == "0") {
					ImageView im = (ImageView) v;
					im.setImageResource(R.drawable.upvoteselected);
					asyncOperations = new AsynchronousOptions(v.getContext(),
							"t3_" + id, sh, "upvote");
					asyncOperations.execute(v.getContext());
					vote = "1";
					ImageView an = (ImageView) v.getTag();
					an.setImageResource(R.drawable.downvotewhite);
				} else {
					ImageView im = (ImageView) v;
					im.setImageResource(R.drawable.upvotewhite);
					asyncOperations = new AsynchronousOptions(v.getContext(),
							"t3_" + id, sh, "rescind");
					asyncOperations.execute(v.getContext());
					vote = "0";
					ImageView an = (ImageView) v.getTag();
					an.setImageResource(R.drawable.downvotewhite);
				}
			}
		}

	};

	private OnClickListener downvoteClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (sh.getInt("loggedinornot", 0) == 0) {
				Toast.makeText(v.getContext(), "log in for this action",
						Toast.LENGTH_LONG).show();
			} else {
				if (vote == "0") {
					ImageView im = (ImageView) v;
					im.setImageResource(R.drawable.downvoteselected);
					asyncOperations = new AsynchronousOptions(v.getContext(),
							"t3_" + id, sh, "downvote");
					asyncOperations.execute(v.getContext());
					vote = "-1";
					ImageView an = (ImageView) v.getTag();
					an.setImageResource(R.drawable.upvotewhite);
				} else {
					ImageView im = (ImageView) v;
					im.setImageResource(R.drawable.downvotewhite);
					asyncOperations = new AsynchronousOptions(v.getContext(),
							"t3_" + id, sh, "rescind");
					asyncOperations.execute(v.getContext());
					vote = "0";
					ImageView an = (ImageView) v.getTag();
					an.setImageResource(R.drawable.upvotewhite);
				}
			}
		}
	};

	private OnClickListener saveClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (sh.getInt("loggedinornot", 0) == 0) {
				Toast.makeText(v.getContext(), "log in for this action",
						Toast.LENGTH_LONG).show();
			} else {
				Button btn = (Button) v;
				btn.setText("UNSAVE");
				asyncOperations = new AsynchronousOptions(v.getContext(), "t3_"
						+ id, sh, "save");
				asyncOperations.execute(v.getContext());
				saved = "true";
			}
		}

	};

	private OnClickListener unsaveClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (sh.getInt("loggedinornot", 0) == 0) {
				Toast.makeText(v.getContext(), "log in for this action",
						Toast.LENGTH_LONG).show();
			} else {
				Button btn = (Button) v;
				btn.setText("SAVE");
				asyncOperations = new AsynchronousOptions(v.getContext(), "t3_"
						+ id, sh, "unsave");
				asyncOperations.execute(v.getContext());
				saved = "false";
			}
		}

	};

	private OnClickListener shareClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Toast.makeText(v.getContext(),
					"Feature yet to be added. Keep checking for updates.",
					Toast.LENGTH_LONG).show();
		}
	};

	private OnClickListener userClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent profile = new Intent(v.getContext(), ProfileActivity.class);
			profile.putExtra("username", author.trim());

			v.getContext().startActivity(profile);
		}
	};

	public class AwesomePagerAdapter extends PagerAdapter {
		QuickAction quickAction;

		String curl = "";
		String idForOperation = "";
		String postTitle = "";
		String postText = "";
		String postAuthor = "";
		String postScore = "";
		String saved = "";
		String vote = "";
		SharedPreferences sharedPrefs;

		@Override
		public int getCount() {
			return NUM_AWESOME_VIEWS;
		}

		public AwesomePagerAdapter(String c, String id, String selftext,
				String author, String title, String score, String vote,
				String saved, SharedPreferences sh) {
			this.curl = c;
			this.idForOperation = id;
			this.postTitle = title;
			this.postAuthor = author;
			this.postScore = score;
			this.postText = selftext;
			this.sharedPrefs = sh;
			this.vote = vote;
			this.saved = saved;
		}

		@Override
		public Object instantiateItem(View collection, int position) {

			View linLayout = new View(cxt);

			LayoutInflater ms = (LayoutInflater) cxt
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			LayoutInflater ms2 = (LayoutInflater) cxt
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			if (position == 1) {
				linLayout = ms.inflate(R.layout.browserlayout, null);
				if (!curl.matches("")) {
					WebView wv = (WebView) linLayout
							.findViewById(R.id.linkWebView);
					LinearLayout lin = (LinearLayout) linLayout
							.findViewById(R.id.browserLayoutProgress);

					//

					wv.getSettings().setLoadWithOverviewMode(true);
					wv.getSettings().setUseWideViewPort(true);
					wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
					wv.setScrollbarFadingEnabled(false);
					lin.setVisibility(0);
					wv.getSettings().setJavaScriptEnabled(true);
					wv.setTag(lin);

					wv.loadUrl(curl);
					wv.setWebViewClient(new WebViewClient() {
						@Override
						public void onPageFinished(WebView view, String url) {
							super.onPageFinished(view, url);
							LinearLayout linLayout = (LinearLayout) view
									.getTag();
							linLayout.setVisibility(8);
						}
					});

				}
			} else if (position == 0) {
				linLayout = ms.inflate(R.layout.commentslayout, null);
				LinearLayout proLayout = (LinearLayout) linLayout
						.findViewById(R.id.commentsLayoutProgress);
				listView = (ListView) linLayout
						.findViewById(R.id.commentsListView);

				ViewGroup header = (ViewGroup) ms2.inflate(
						R.layout.commentsheader, null);

				TextView txtscore = (TextView) header.findViewById(R.id.score);
				TextView txtpost = (TextView) header
						.findViewById(R.id.posttitle);
				TextView txtselftext = (TextView) header
						.findViewById(R.id.selftext);
				Button btnPost = (Button) header.findViewById(R.id.postComment);
				btnPost.setTag(new String[] { postTitle, idForOperation });
				btnPost.setOnClickListener(postListener);
				txtscore.setText(postScore + " " + postAuthor);
				txtpost.setText(postTitle);
				if (!postText.trim().matches("")) {
					txtselftext.setTag(postText);
					txtselftext.setText(Html.fromHtml(Mdown.getHtml(postText)));
					txtselftext.setOnClickListener(selfTextClickListener);
				}

				listView.addHeaderView(header);
				loadComments = new LoadComments(cxt, ac, idForOperation, sh,
						returnArrayList, adapter, treeComments, commentNodes,
						proLayout, listView, postAuthor);
				loadComments.execute(cxt);
			}

			((ViewPager) collection).addView(linLayout, 0);

			return linLayout;
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		@Override
		public void finishUpdate(View v) {
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

		private OnClickListener postListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (sharedPrefs.getInt("loggedinornot", 0) == 0) {
					Toast.makeText(v.getContext(), "log in for this action",
							Toast.LENGTH_LONG).show();
				} else {
					String[] req = (String[]) v.getTag();
					Intent in = new Intent(v.getContext(), ReplyActivity.class);
					in.putExtra("id", "t3_" + req[1]);
					in.putExtra("text", req[0]);
					in.putExtra("flag", true);
					startActivityForResult(in,
							Constants.CONST_REFRESH_COMMENT_CODE);
					overridePendingTransition(R.anim.slide_top_to_bottom,
							R.anim.shrink_from_top);
				}
			}

		};

		private OnClickListener selfTextClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {

				String postText = (String) v.getTag();

				quickAction = new QuickAction(v.getContext(),
						QuickAction.HORIZONTAL, true);

				quickAction.setLinkList(postText, CommentsAndLink.this);

				quickAction.show(v);
				quickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
			}
		};

	}

	public static JSONArray getComments(String url, SharedPreferences sh) {

		String result = "";
		JSONArray jArray = null;

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		CookieStore bas = new BasicCookieStore();
		// Log.i("status Code", sh.getString("redditsession", "fuck"));
		BasicClientCookie ck = new BasicClientCookie("reddit_session",
				sh.getString("redditsession", ""));
		ck.setDomain(".reddit.com");
		ck.setPath("/");
		ck.setExpiryDate(null);
		bas.addCookie(ck);
		client.setCookieStore(bas);
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				sh.getString("currentusername", "RSU"));

		HttpGet httpGet = new HttpGet(Constants.CONST_COMMENTS_URL + url);
		try {

			// Log.i("cookieinfo", cookie + "4");
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			// Log.i("status Code", Integer.toString(statusCode));
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				result = builder.toString();
				Log.i("status Code", result);
			} else {
				Log.e("faile", "Failed to download file" + "5");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			jArray = new JSONArray(result);

		} catch (JSONException e) {

			Log.e("log_tag", "Error parsing data " + e.toString() + "6"
					+ result);
		}

		return jArray;
	}

}
