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

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.rsu.jayanthsunchu.redditsuitedup.FrontPageActivity.FrontPageTask;
import com.rsu.jayanthsunchu.redditsuitedup.QuickAction.OnActionItemClickListener;

public class InboxActivity extends ListActivity {
	private static final int ID_ALL = 10;
	private static final int ID_UNREAD = 6;
	private static final int ID_MESSAGES = 7;
	private static final int ID_CREPLIES = 8;
	private static final int ID_PREPLIES = 9;
	private static final int ID_SENT = 11;
	private static final int ID_MODERATOR = 12;
	private static final int ID_REPLYTO = 14;
	private static final int ID_PARENT = 15;
	QuickAction sortBy;
	public static String exceptionDownloading = "none";
	InboxListAdapter adapter;
	LinearLayout proBarLayout;
	TextView txtOptions;
	LoadMessages loadMessage;
	ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String, String>>();
	QuickAction listActions;
	SharedPreferences redPrefs;
	TextView txtCompose;
	SharedPreferences.Editor redEditor;
	ViewContextAsyncTask vcTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		applyTheme();
		setContentView(R.layout.inbox_layout);
		setUpViews();
	}

	public void applyTheme() {
		redPrefs = getSharedPreferences(Constants.PREFS_NAME, 0);
		redEditor = redPrefs.edit();

		if (redPrefs.getString("theme", "white").matches("white")) {
			InboxActivity.this.setTheme(R.style.WhiteTheme);

		} else {
			InboxActivity.this.setTheme(R.style.DarkTheme);

		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		applyTheme();
		setContentView(R.layout.inbox_layout);
		setUpViews();
	}

	public void setUpViews() {

		Drawable d;
		if (redPrefs.getString("theme", "white").matches("white")) {
			// FrontPageActivity.this.setTheme(R.style.WhiteTheme);
			d = this.getResources().getDrawable(R.drawable.dividerwhite);
			this.getListView().setDivider(d);

		} else {
			// FrontPageActivity.this.setTheme(R.style.DarkTheme);
			d = this.getResources().getDrawable(R.drawable.dividerblack);
			this.getListView().setDivider(d);

		}

		this.getListView().setDividerHeight(1);
		proBarLayout = (LinearLayout) findViewById(R.id.inboxLoadProgressBar);
		txtOptions = (TextView) findViewById(R.id.txtOption);
		txtCompose = (TextView)findViewById(R.id.compose);
		
		txtCompose.setOnClickListener(composeClickListener);
		ActionItem newItem = new ActionItem(ID_ALL, "all", null);
		ActionItem controItem = new ActionItem(ID_UNREAD, "unread", null);
		ActionItem hotItem = new ActionItem(ID_MESSAGES, "messages", null);
		ActionItem topItem = new ActionItem(ID_CREPLIES, "comment replies",
				null);
		ActionItem savedItem = new ActionItem(ID_PREPLIES, "post replies", null);
		ActionItem submittedItem = new ActionItem(ID_SENT, "sent messages",
				null);
		ActionItem likedItem = new ActionItem(ID_MODERATOR,
				"moderator messages", null);

		sortBy = new QuickAction(this, QuickAction.VERTICAL, false);
		sortBy.addActionItem(hotItem);
		sortBy.addActionItem(newItem);
		sortBy.addActionItem(controItem);
		sortBy.addActionItem(topItem);
		sortBy.addActionItem(savedItem);
		sortBy.addActionItem(submittedItem);
		sortBy.addActionItem(likedItem);
		sortBy.setOnActionItemClickListener(sortClick);

		// list items actions menu - jc - may 09 2012
		ActionItem replyToItem = new ActionItem(ID_REPLYTO, "reply",
				getResources().getDrawable(R.drawable.reply));
		ActionItem viewParent = new ActionItem(ID_PARENT, "view parent",
				getResources().getDrawable(R.drawable.reply));
		viewParent.setSticky(true);
		listActions = new QuickAction(this, QuickAction.HORIZONTAL, true);
		listActions.addActionItem(replyToItem);
		listActions.addActionItem(viewParent);
		listActions.setOnDismissListener(inboxActionsDismiss);
		listActions.setOnActionItemClickListener(listActionClick);

		loadMessage = new LoadMessages(InboxActivity.this,
				Constants.CONST_ALL_URL, proBarLayout);
		loadMessage.execute(InboxActivity.this);
		txtOptions.setText("Options" + "(all)");

		txtOptions.setOnClickListener(showOptions);

		getListView().setOnItemClickListener(inboxItemClick);

	}
	
	private OnClickListener composeClickListener = new OnClickListener() {
		@Override
		public void onClick(View v){
			Intent composeIntent = new Intent(InboxActivity.this, ComposeActivity.class);
			startActivity(composeIntent);
		}
		
	};

	private OnDismissListener inboxActionsDismiss = new OnDismissListener() {
		@Override
		public void onDismiss() {

		}

	};

	// list actions click listener and list item click listener - jc - may 09
	// 2012
	private OnActionItemClickListener listActionClick = new OnActionItemClickListener() {

		@Override
		public void onItemClick(QuickAction source, int pos, int actionId) {
			int position = source.getListViewPosition();
			if (actionId == ID_REPLYTO) {
				Intent in = new Intent(InboxActivity.this, ReplyActivity.class);
				in.putExtra("id", arrayList.get(position).get("name"));
				in.putExtra("text", arrayList.get(position).get("body"));
				in.putExtra("flag", false);

				startActivity(in);
				overridePendingTransition(R.anim.slide_top_to_bottom,
						R.anim.shrink_from_top);
			} else if (actionId == ID_PARENT) {

				vcTask = new ViewContextAsyncTask(InboxActivity.this, "parent",
						source, InboxActivity.this.getSharedPreferences(
								Constants.PREFS_NAME, 0), arrayList);
				vcTask.execute(InboxActivity.this);
			}
		}

	};

	// AsyncTask for showing parent comment or context in profile or inbox views
	public class ViewContextAsyncTask extends
			AsyncTask<Context, Integer, String> {
		Activity context;
		String flag;
		QuickAction currentSource;
		SharedPreferences thisPrefs;
		int currentposition;
		ArrayList<HashMap<String, String>> currentList = new ArrayList<HashMap<String, String>>();
		ArrayList<HashMap<String, String>> lstToPass = new ArrayList<HashMap<String, String>>();

		public ViewContextAsyncTask(Activity ctx, String whichFlag,
				QuickAction source, SharedPreferences sh,
				ArrayList<HashMap<String, String>> thisList) {
			this.context = ctx;
			this.flag = whichFlag;
			this.currentSource = source;
			this.thisPrefs = sh;
			this.currentList = thisList;
			this.currentposition = source.getListViewPosition();
			HashMap<String, String> defaultMap = new HashMap<String, String>();
			defaultMap.put("name", "loading parent comment");
			defaultMap.put("author", "");
			lstToPass.add(defaultMap);

		}

		@Override
		protected String doInBackground(Context... arg0) {
			if (flag.matches("parent")) {
				HashMap<String, String> something = new HashMap<String, String>();

				String[] splitContext = currentList.get(currentposition)
						.get("context").split("/");
				String context = "";
				for (int i = 0; i < splitContext.length - 1; i++) {
					context = context + splitContext[i] + "/";
				}
				String[] splitId = arrayList.get(currentposition)
						.get("parent_id").split("_");

				SharedPreferences sh = InboxActivity.this.getSharedPreferences(
						Constants.PREFS_NAME, 0);
				try {
					JSONArray jjOb = getParentJson(Constants.CONST_REDDIT_URL2
							+ context + splitId[1].trim() + "/.json", sh);
					something = parseJSONParentComment(jjOb);
				} catch (Exception ex) {
					something.put("name", "no parent comment");
					something.put("author", "");
					
					ex.printStackTrace();
				}
				if (!lstToPass.isEmpty())
					lstToPass.clear();

				lstToPass.add(something);
				// Log.i("log_tag", context);
				// Toast.makeText(source.getRespectiveView().getContext(),
				// something, Toast.LENGTH_LONG).show();
				// source.setParentComment(something, InboxActivity.this);

			}

			return "Complete";

		}

		protected void onPostExecute(String result) {
			currentSource.setParentComment(lstToPass, context);

		}

		protected void onPreExecute() {
			currentSource.setParentComment(lstToPass, context);
		}
	}

	private HashMap<String, String> parseJSONParentComment(JSONArray jsonResult) {
		HashMap<String, String> toReturn = new HashMap<String, String>();
		try {
			JSONObject json2 = jsonResult.getJSONObject(1);

			JSONObject s = json2.getJSONObject("data");
			JSONArray arr = s.getJSONArray("children");
			
			for (int i = 0; i < arr.length(); i++) {
				JSONObject json = arr.getJSONObject(i);
				String st = json.getString("kind");
				

				if (st.matches("t1")) {

					JSONObject jsonComment = json.getJSONObject("data");
					toReturn.put("name",
							Mdown.getHtml(jsonComment.getString("body")));
					toReturn.put("author", jsonComment.getString("author"));
					return toReturn;

				}

			}
		} catch (JSONException ex) {
			toReturn.put("name", "no parent");
			toReturn.put("author", "");
			
			ex.printStackTrace();

		}
		return toReturn;
	}

	private OnItemClickListener inboxItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> av, View v, int position, long id) {
			listActions.clearCurrentList(InboxActivity.this);
			listActions.setListViewPosition(position);

			listActions.show(v);

			listActions.setAnimStyle(QuickAction.ANIM_REFLECT);

		}

	};

	// show options to sort messages click listener - jc - may 08 2012
	private OnClickListener showOptions = new OnClickListener() {

		@Override
		public void onClick(View v) {
			sortBy.show(v);
			sortBy.setAnimStyle(QuickAction.ANIM_REFLECT);
		}

	};

	// on action item click listener - jc - may 08 2012
	private OnActionItemClickListener sortClick = new OnActionItemClickListener() {

		@Override
		public void onItemClick(QuickAction source, int pos, int actionId) {
			if (actionId == ID_ALL) {
				loadMessage = new LoadMessages(InboxActivity.this,
						Constants.CONST_ALL_URL, proBarLayout);
				loadMessage.execute(InboxActivity.this);
				txtOptions.setText("Options" + "(all)");
			} else if (actionId == ID_UNREAD) {
				loadMessage = new LoadMessages(InboxActivity.this,
						Constants.CONST_UNREAD_URL, proBarLayout);
				loadMessage.execute(InboxActivity.this);
				txtOptions.setText("Options" + "(unread)");

			} else if (actionId == ID_MESSAGES) {
				loadMessage = new LoadMessages(InboxActivity.this,
						Constants.CONST_MESSAGES_URL, proBarLayout);
				loadMessage.execute(InboxActivity.this);
				txtOptions.setText("Options" + "(messages)");

			} else if (actionId == ID_CREPLIES) {
				loadMessage = new LoadMessages(InboxActivity.this,
						Constants.CONST_COMMENTREPLY_URL, proBarLayout);
				loadMessage.execute(InboxActivity.this);
				txtOptions.setText("Options" + "(comments)");

			} else if (actionId == ID_PREPLIES) {
				loadMessage = new LoadMessages(InboxActivity.this,
						Constants.CONST_SELFREPLY_URL, proBarLayout);
				loadMessage.execute(InboxActivity.this);
				txtOptions.setText("Options" + "(post replies)");
			} else if (actionId == ID_SENT) {
				loadMessage = new LoadMessages(InboxActivity.this,
						Constants.CONST_SENT_URL, proBarLayout);
				loadMessage.execute(InboxActivity.this);
				txtOptions.setText("Options" + "(sent)");
			} else if (actionId == ID_MODERATOR) {
				loadMessage = new LoadMessages(InboxActivity.this,
						Constants.CONST_MODERATOR_URL, proBarLayout);
				loadMessage.execute(InboxActivity.this);
				txtOptions.setText("Options" + "(moderator)");
			}

		}

	};

	// load messages - asynchronous task - jc - may 08 2012

	public class LoadMessages extends AsyncTask<Context, Integer, String> {
		String url;
		SharedPreferences prefs;
		LinearLayout proBar;
		Context context;

		public LoadMessages(Context ctx, String ur, LinearLayout lin) {
			this.url = ur;
			this.prefs = ctx.getSharedPreferences(Constants.PREFS_NAME, 0);
			this.proBar = lin;
		}

		@Override
		protected String doInBackground(Context... arg0) {

			return getDataFromApi(url, prefs);
		}

		protected void onPreExecute() {
			arrayList.clear();
			proBar.setVisibility(0);
		}

		protected void onPostExecute(String result) {
			if (result.matches("none")) {
				proBar.setVisibility(8);

				adapter = new InboxListAdapter(InboxActivity.this, arrayList);
				setListAdapter(adapter);

			} else {

			}
		}

	}

	public static JSONObject getInboxJson(String url, SharedPreferences sh) {

		String result = "";
		JSONObject jArray = null;

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
		HttpGet httpGet = new HttpGet(url);
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
				exceptionDownloading = "Error downloading data.";
				Log.e("faile", "Failed to download file" + "5");
			}
		} catch (ClientProtocolException e) {
			exceptionDownloading = "Error downloading data.";
			e.printStackTrace();
		} catch (IOException e) {
			exceptionDownloading = "Error downloading data.";
			e.printStackTrace();
		}
		try {
			if (result.matches("\\{\\}")) {
				// handle exception
				exceptionDownloading = "Error downloading data.";
			} else
				jArray = new JSONObject(result);
			// SharedPreferences.Editor ed = sh.edit();
			// ed.putInt("changedornot", 0);
			// ed.commit();

		} catch (JSONException e) {
			exceptionDownloading = "Error downloading data.";
			Log.e("log_tag", "Error parsing data " + e.toString() + "6");
		}

		return jArray;
	}

	public static JSONArray getParentJson(String url, SharedPreferences sh) {

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
		HttpGet httpGet = new HttpGet(url);
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
				exceptionDownloading = "Error downloading data.";
				Log.e("faile", "Failed to download file" + "5");
			}
		} catch (ClientProtocolException e) {
			exceptionDownloading = "Error downloading data.";
			e.printStackTrace();
		} catch (IOException e) {
			exceptionDownloading = "Error downloading data.";
			e.printStackTrace();
		}
		try {
			if (result.matches("\\{\\}")) {
				// handle exception
				exceptionDownloading = "Error downloading data.";
			} else
				jArray = new JSONArray(result);
			// SharedPreferences.Editor ed = sh.edit();
			// ed.putInt("changedornot", 0);
			// ed.commit();

		} catch (JSONException e) {
			exceptionDownloading = "Error downloading data.";
			Log.e("log_tag", "Error parsing data " + e.toString() + "6");
		}

		return jArray;
	}

	// get data from api - jc - may 08 2012
	protected String getDataFromApi(String url, SharedPreferences sh) {
		JSONObject json = getInboxJson(url, sh);
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
				HashMap<String, String> maps = new HashMap<String, String>();
				maps.put("body", Mdown.getHtml(jsonObject.getString("body")));
				maps.put("was_comment", jsonObject.getString("was_comment"));
				maps.put("first_message", jsonObject.getString("first_message"));
				maps.put("name", jsonObject.getString("name"));
				maps.put("author", jsonObject.getString("author"));
				maps.put("created_utc", jsonObject.getString("created_utc"));
				maps.put("new", jsonObject.getString("new"));
				maps.put("subject", jsonObject.getString("subject"));
				maps.put("context", jsonObject.getString("context"));
				maps.put("subreddit", jsonObject.getString("subreddit"));
				maps.put("parent_id", jsonObject.getString("parent_id"));
				maps.put("context", jsonObject.getString("context"));
				arrayList.add(maps);

			}

		} catch (JSONException e) {
			exceptionDownloading = "Error downloading data. Try again later.";
			Log.e("errorcode", "1" + e.toString());
		} catch (Exception ex) {
			exceptionDownloading = "Error downloading data. Try again later.";
			Log.e("errorcode", "2" + ex.toString());
		}
		return exceptionDownloading;
	}

}
