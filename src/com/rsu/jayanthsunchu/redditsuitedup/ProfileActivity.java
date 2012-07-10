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
import org.json.JSONObject;

import com.rsu.jayanthsunchu.redditsuitedup.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends ListActivity {
	TextView txtProfileHeading;
	SharedPreferences redPrefs;
	SharedPreferences.Editor redEditor;
	ArrayList<HashMap<String, String>> returnList = new ArrayList<HashMap<String, String>>();
	ProfileAdapter proAdapter;
	LinearLayout proLayout;
	public static String exceptionDownloading;
	LoadUserLinks loadLinks;
	GetUserInfoAsync getUseInfo;
	String loadingMore = "";
	String userName = "";
	boolean loadingMoreFlag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		applyTheme();
		setContentView(R.layout.profile_layout);
		setUpViews();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		applyTheme();
		setContentView(R.layout.profile_layout);
		setUpViews();
	}

	public void applyTheme() {
		redPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		redEditor = redPrefs.edit();

		if (redPrefs.getString("theme", "white").matches("white")) {
			ProfileActivity.this.setTheme(R.style.WhiteTheme);

		} else {
			ProfileActivity.this.setTheme(R.style.DarkTheme);

		}

	}

	public void setUpViews() {
		Drawable d;
		if (redPrefs.getString("theme", "white").matches("white")) {
			// FrontPageActivity.this.setTheme(R.style.WhiteTheme);
			d = this.getResources().getDrawable(R.drawable.dividerwhite);
			getListView().setDivider(d);

		} else {
			// FrontPageActivity.this.setTheme(R.style.DarkTheme);
			d = this.getResources().getDrawable(R.drawable.dividerblack);
			getListView().setDivider(d);

		}
		this.getListView().setDividerHeight(1);
		txtProfileHeading = (TextView) findViewById(R.id.txtProfile);
		Bundle br = getIntent().getExtras();
		if (br != null) {
			// txtProfileHeading.setText(br.getString("username"));
			// loading user info using getuserinfoasync
			proLayout = (LinearLayout) findViewById(R.id.profileLoadP);
			loadLinks = new LoadUserLinks(br.getString("username"));
			loadLinks.execute(ProfileActivity.this);
			getUseInfo = new GetUserInfoAsync(ProfileActivity.this, redPrefs,
					proLayout, txtProfileHeading, br.getString("username"));
			userName = br.getString("username");
			getUseInfo.execute(ProfileActivity.this);
		} else {
			Toast.makeText(this, "Could not load profile, try again later.",
					Toast.LENGTH_LONG).show();
		}

		getListView().setOnItemClickListener(profileItemClickListener);
	}

	private OnItemClickListener profileItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> av, View v, int position, long id) {
			// Intent comments = new Intent(
			// ProfileActivity.this,
			// CommentsAndLink.class);
			// comments.putExtra("url",
			// returnList.get(position).get("url"));
			// comments.putExtra("id", returnList.get(position)
			// .get("id"));
			// comments.putExtra("author",
			// returnList.get(position).get("author"));
			// comments.putExtra("selftext", returnList.get(position)
			// .get("selftextun"));
			// comments.putExtra("title",
			// returnList.get(position).get("title"));
			// comments.putExtra("score",
			// returnList.get(position).get("score"));
			// comments.putExtra("saved",
			// returnList.get(position).get("saved"));
			// comments.putExtra("vote",
			// returnList.get(position).get("vote"));
			// // finish();
			// startActivity(comments);
		}

	};

	public class LoadUserLinks extends AsyncTask<Context, Integer, String> {
		private String user;

		public LoadUserLinks(String username) {
			this.user = username;
		}

		@Override
		protected String doInBackground(Context... arg0) {
			getDataFromApi("", "", redPrefs, user, "");
			return "COmplete";
		}

		protected void onPreExecute() {
			proLayout.setVisibility(0);
		}

		protected void onPostExecute(String result) {
			proLayout.setVisibility(8);
			if (exceptionDownloading.matches("none")) {
				proAdapter = new ProfileAdapter(ProfileActivity.this,
						returnList);
				if (returnList.size() > 0) {
					View footerView = ((LayoutInflater) ProfileActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
							.inflate(R.layout.neverendingfooter, null, false);
					getListView().addFooterView(footerView);
				} else {
					View footerView = ((LayoutInflater) ProfileActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
							.inflate(R.layout.nothingfooter, null, false);
					footerView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
							startActivity(new Intent(getIntent()));
						}
					});
					getListView().addFooterView(footerView);

				}
				setListAdapter(proAdapter);
				getListView().setOnScrollListener(new OnScrollListener() {

					@Override
					public void onScroll(AbsListView av, int firstVisible,
							int visibleCount, int totalCount) {

						int lastInScreen = firstVisible + visibleCount;
						if ((lastInScreen == totalCount) && !(loadingMoreFlag)) {
							if (returnList.size() > 0) {

								Thread loadMoreThread = new Thread(null,
										loadMorePosts);
								loadMoreThread.start();
								// adapter.notifyDataSetChanged();
							}
						}

					}

					@Override
					public void onScrollStateChanged(AbsListView arg0, int arg1) {
						// TODO Auto-generated method stub

					}

				});
				
			} else {
				Toast.makeText(ProfileActivity.this, exceptionDownloading,
						Toast.LENGTH_LONG).show();

			}
		}
	}

	private Runnable loadMorePosts = new Runnable() {

		@Override
		public void run() {
			loadingMoreFlag = true;
			// TODO Auto-generated method stub
			if (!loadingMore.matches("")) {
				getDataFromApi("", "", redPrefs, userName.trim(), "?after="
						+ loadingMore.trim());
				runOnUiThread(updateProfileUI);
			}

		}

	};

	private Runnable updateProfileUI = new Runnable() {

		@Override
		public void run() {
			loadingMoreFlag = false;
			proAdapter.notifyDataSetChanged();
			loadingMore = "";
			// TODO Auto-generated method stub

		}

	};

	protected void getDataFromApi(String url, String cookie,
			SharedPreferences sh, String user, String queryString) {
		JSONObject json = getJSONfromURLUser(url, cookie, sh, user, queryString);
		JSONObject json2 = null;
		JSONArray jArray = null;

		try {
			json2 = json.getJSONObject("data");
			jArray = json2.getJSONArray("children");
			for (int i = 0; i < jArray.length(); i++) {

				JSONObject jsonObject1 = new JSONObject();
				jsonObject1 = jArray.getJSONObject(i);
				JSONObject jsonObject = new JSONObject();
				jsonObject = jsonObject1.getJSONObject("data");
				if (jsonObject1.getString("kind").matches("t3")) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("title", jsonObject.getString("title"));
					map.put("kind", "t3");
					map.put("subreddit", jsonObject.getString("subreddit"));
					map.put("selftext_html",
							jsonObject.getString("selftext_html"));
					map.put("selftext", jsonObject.getString("selftext"));
					map.put("selftextun", jsonObject.getString("selftext"));
					map.put("likes", jsonObject.getString("likes"));
					map.put("saved", jsonObject.getString("saved"));
					map.put("id", jsonObject.getString("id"));
					map.put("clicked", jsonObject.getString("clicked"));
					map.put("title", jsonObject.getString("title"));

					map.put("score", jsonObject.getString("score"));
					map.put("over_18", jsonObject.getString("over_18"));
					map.put("hidden", jsonObject.getString("hidden"));
					map.put("thumbnail", jsonObject.getString("thumbnail"));
					map.put("subreddit_id",
							jsonObject.getString("subreddit_id"));
					map.put("author_flair_css_class",
							jsonObject.getString("author_flair_css_class"));
					map.put("downs", jsonObject.getString("downs"));
					map.put("is_self", jsonObject.getString("is_self"));
					map.put("permalink", jsonObject.getString("permalink"));
					map.put("name", jsonObject.getString("name"));
					map.put("created", jsonObject.getString("created"));
					map.put("url", jsonObject.getString("url"));
					map.put("author_flair_text",
							jsonObject.getString("author_flair_text"));
					map.put("author", jsonObject.getString("author"));
					map.put("created_utc", jsonObject.getString("created_utc"));
					map.put("num_comments",
							jsonObject.getString("num_comments"));
					map.put("ups", jsonObject.getString("ups"));
					map.put("domain", jsonObject.getString("domain"));
					map.put("vote", "1");
					returnList.add(map);
					// adapter.notifyDataSetChanged();
				} else if (jsonObject1.getString("kind").matches("t1")) {
					HashMap<String, String> maps = new HashMap<String, String>();
					maps.put("body",
							Mdown.getHtml(jsonObject.getString("body")));
					maps.put("author", jsonObject.getString("author"));

					maps.put("id", jsonObject.getString("id"));
					maps.put("ups", jsonObject.getString("ups"));
					maps.put("downs", jsonObject.getString("downs"));
					maps.put("kind", "t1");
					returnList.add(maps);
				}
				loadingMore = "";

				if (!json2.get("after").equals(null))
					loadingMore = json2.get("after").toString();

			}

		} catch (JSONException e) {
			exceptionDownloading = "Error downloading data. Try again later.";
			Log.e("errorcode", "1" + e.toString());
		} catch (Exception ex) {
			exceptionDownloading = "Error downloading data. Try again later.";
			Log.e("errorcode", "2" + ex.toString());
		}
	}

	public static JSONObject getJSONfromURLUser(String url, String cookie,
			SharedPreferences sh, String user, String queryString) {

		String result = "";
		JSONObject jArray = null;

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		CookieStore bas = new BasicCookieStore();
		Log.i("status Code", sh.getString("redditsession", "fuck"));
		BasicClientCookie ck = new BasicClientCookie("reddit_session",
				sh.getString("redditsession", cookie));
		ck.setDomain(".reddit.com");
		ck.setPath("/");
		ck.setExpiryDate(null);
		bas.addCookie(ck);
		client.setCookieStore(bas);
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				sh.getString("currentusername", "RSU"));
		HttpGet httpGet = new HttpGet(Constants.CONST_USERPROFILE_URL
				+ user.trim() + "/.json" + queryString);
		try {

			Log.i("cookieinfo", cookie + "4");
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
				exceptionDownloading = "Error downloading data. Try again later.";
				Log.e("faile", "Failed to download file" + "5");
			}
		} catch (ClientProtocolException e) {
			exceptionDownloading = "Error downloading data. Try again later.";
			e.printStackTrace();
		} catch (IOException e) {
			exceptionDownloading = "Error downloading data. Try again later.";
			e.printStackTrace();
		}
		try {
			jArray = new JSONObject(result);
			if (jArray.length() > 0)
				exceptionDownloading = "none";
			else
				exceptionDownloading = "Error downloading data. Try again later.";
			// SharedPreferences.Editor ed = sh.edit();
			// ed.putInt("changedornot", 0);
			// ed.commit();

		} catch (JSONException e) {
			exceptionDownloading = "Error downloading data. Try again later.";
			Log.e("log_tag", "Error parsing data " + e.toString() + "6");
		}

		return jArray;
	}
}
