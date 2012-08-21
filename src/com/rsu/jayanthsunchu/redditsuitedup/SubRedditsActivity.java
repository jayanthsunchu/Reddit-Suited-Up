package com.rsu.jayanthsunchu.redditsuitedup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SubRedditsActivity extends Activity {
	ArrayAdapter<String> subRedditList;
	ArrayAdapter<String> autoCompleteList;
	SubRedditAdapter sub;
	GetSubRedditsAsyncTask get;
	ArrayList<HashMap<String, String>> subRedds = new ArrayList<HashMap<String, String>>();
	HashMap<String, String> defaultHash = new HashMap<String, String>();

	RedditorDB reddDb;
	LoadSubReddits load;
	LinearLayout proBar;
	ListView subList;
	Button btnGoAuto;
	private String currentCookie = null;
	SharedPreferences mySettings;
	SharedPreferences.Editor myEditor;
	TextView txtSubHeadingOne;
	TextView txtSubHeadingTwo;
	String loadMoreSubRedditFlag = "";

	TextView txtFrontpage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		reddDb = new RedditorDB(SubRedditsActivity.this);
		mySettings = getSharedPreferences(Constants.PREFS_NAME, 0);
		myEditor = mySettings.edit();
		if (reddDb.getCount() > 0) {

			String[] current = reddDb.getCurrentUser();

			currentCookie = current[1].trim();

			myEditor.putString("currentusername", current[0].trim());
			myEditor.commit();
			myEditor.putString("redditsession", current[1].trim());
			myEditor.commit();
			myEditor.putString("usermodhash", current[2].trim());
			myEditor.commit();
		}
		// applyTheme();
		setContentView(R.layout.subreddits_layout);
		setUpViews();

	}

	public void applyTheme() {
		mySettings = this.getSharedPreferences(Constants.PREFS_NAME, 0);

		if (mySettings.getString("theme", "white").matches("white")) {
			this.setTheme(R.style.WhiteTheme);
		} else {
			this.setTheme(R.style.DarkTheme);

		}

	}

	public boolean onTouchEvent(MotionEvent e) {

		finish();

		return false;

	}

	public void setUpViews() {
		txtFrontpage = (TextView) findViewById(R.id.favSubReddits);
		txtFrontpage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				SharedPreferences sh = v.getContext().getSharedPreferences(
						Constants.PREFS_NAME, 0);
				SharedPreferences.Editor ed = sh.edit();
				ed.putString("frontpageorwhat", "");
				ed.commit();

				ed.putString("sort", "");
				ed.commit();

				// Intent backTo = new Intent(v.getContext(),
				// FrontPageActivity.class);

				setResult(Constants.CONST_RESULT_CODE, null);
				finish();
			}

		});
		subList = (ListView) findViewById(R.id.subRedditList);
		proBar = (LinearLayout) findViewById(R.id.subRedditProgressBar);
		if (reddDb.getCount() > 0) {

			if (mySettings.getInt("changedornot", 0) == 1) {
				load = new LoadSubReddits();
				load.execute(SubRedditsActivity.this);
			} else if (mySettings.getInt("changedornot", 0) == 0) {
				String[] s = mySettings.getString("subreddits",
						Constants.CONST_SUBREDDITS2.toString()).split(",");
				for (int i = 0; i < s.length; i++) {
					HashMap<String, String> hash = new HashMap<String, String>();
					hash.put("name", s[i].toString());
					subRedds.add(hash);

				}
				sub = new SubRedditAdapter(SubRedditsActivity.this, subRedds);
				subList.setAdapter(sub);
			}
		} else {
			for (int i = 0; i < Constants.CONST_SUBREDDITS.length; i++) {
				HashMap<String, String> hash = new HashMap<String, String>();
				hash.put("name", Constants.CONST_SUBREDDITS[i].toString());
				subRedds.add(hash);

			}
			sub = new SubRedditAdapter(SubRedditsActivity.this, subRedds);
			subList.setAdapter(sub);
		}
		subList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {

				SharedPreferences sh = v.getContext().getSharedPreferences(
						Constants.PREFS_NAME, 0);
				SharedPreferences.Editor ed = sh.edit();

				if (subRedds.get(position).get("name").toString().trim()
						.matches("frontpage")) {
					ed.putString("frontpageorwhat", "");
					ed.commit();
					ed.putString("sort", "");
					ed.commit();
				} else {
					ed.putString("frontpageorwhat",
							"r/"
									+ subRedds.get(position).get("name")
											.toString().trim() + "/");
					ed.commit();
					ed.putString("sort", "");
					ed.commit();
				}
				// Intent backTo = new Intent(v.getContext(),
				// FrontPageActivity.class);

				setResult(Constants.CONST_RESULT_CODE, null);
				finish();
			}

		});
		autoCompleteList = new ArrayAdapter<String>(this,
				R.layout.autocomplete_item, Constants.CONST_SUBREDDITS);

		AutoCompleteTextView autoText = (AutoCompleteTextView) findViewById(R.id.searchSubReddits);
		autoText.setAdapter(autoCompleteList);
		autoText.setValidator(new Validator());
		autoText.setOnFocusChangeListener(new FocusListener());
		TextView txtUser = (TextView) findViewById(R.id.txtUserName);
		if (mySettings.contains("currentusername"))
			txtUser.setText(mySettings.getString("currentusername", ""));
		else
			txtUser.setText("Lurker");
		txtUser.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return true;
			}

		});
		txtSubHeadingOne = (TextView) findViewById(R.id.subHeading1);
		txtSubHeadingTwo = (TextView) findViewById(R.id.subHeading2);

		txtSubHeadingOne.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return true;
			}

		});

		txtSubHeadingTwo.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				return true;
			}

		});

		btnGoAuto = (Button) findViewById(R.id.btnGo);
		btnGoAuto.setTag(autoText);
		btnGoAuto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AutoCompleteTextView autoT = (AutoCompleteTextView) v.getTag();
				if (!autoT.getText().toString().matches("")
						) {
					SharedPreferences sh = v.getContext().getSharedPreferences(
							Constants.PREFS_NAME, 0);
					SharedPreferences.Editor ed = sh.edit();
					ed.putString("frontpageorwhat", "r/"
							+ autoT.getText().toString().trim() + "/");
					ed.commit();

					ed.putString("sort", "");
					ed.commit();

					setResult(Constants.CONST_RESULT_CODE, null);
					finish();

				} else {
					Toast.makeText(v.getContext(),
							"y u no select from the list?", Toast.LENGTH_LONG)
							.show();

				}
			}

		});

	}

	private OnClickListener inboxClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mySettings.getInt("loggedinornot", 0) == 0) {
				Toast.makeText(v.getContext(), "log in for this action.",
						Toast.LENGTH_LONG).show();
			} else {
				myEditor.putString(
						"userinfopref",
						mySettings.getString("userinfopref", "true").replace(
								"true", "false"));// modified in order to lessen
													// the
				myEditor.commit(); // pain of
				// orange inbox, still needs work - jc may
				// 09 2012

				Intent i = new Intent(SubRedditsActivity.this,
						InboxActivity.class);
				finish();
				startActivity(i);
			}
		}

	};

	public void checkMail(Button inbox, SharedPreferences myS) {
		if (myS.contains("userinfopref")) {
			String[] userPrefs = myS.getString("userinfopref",
					"notpresent, notpresent").split(",");

			if (userPrefs[0].matches("true")) {
				inbox.setBackgroundColor(0xffff6600);
			} else {
				inbox.setBackgroundColor(0);
			}

		} else {
			inbox.setBackgroundColor(0);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (reddDb != null)
			reddDb.closeDb();

	}

	class Validator implements AutoCompleteTextView.Validator {

		@Override
		public boolean isValid(CharSequence text) {

			Arrays.sort(Constants.CONST_SUBREDDITS);
			if (Arrays
					.binarySearch(Constants.CONST_SUBREDDITS, text.toString()) > 0) {

				return true;
			}

			return false;
		}

		@Override
		public CharSequence fixText(CharSequence invalidText) {

			return null;
		}
	}

	class FocusListener implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {

			if (v.getId() == R.id.searchSubReddits && !hasFocus) {

				((AutoCompleteTextView) v).performValidation();
			}
		}
	}

	protected class LoadSubReddits extends AsyncTask<Context, Integer, String> {
		// public final ProgressDialog progress = new
		// ProgressDialog(SubRedditsActivity.this);
		protected void onPreExecute() {
			// this.progress.setMessage("Loading Subreddits");
			// this.progress.show();
			proBar.setVisibility(0);
		}

		protected void onPostExecute(String result) {
			proBar.setVisibility(8);
			String subRs = "";
			for (int i = 0; i < subRedds.size(); i++) {
				subRs += subRedds.get(i).get("name").toString() + ",";
			}
			myEditor.putString("subreddits", subRs);
			myEditor.commit();
			sub = new SubRedditAdapter(SubRedditsActivity.this, subRedds);
			subList.setAdapter(sub);
			if (!loadMoreSubRedditFlag.matches("")) {
				Thread loadMoreSubThread = new Thread(null, loadAllSubs);
				loadMoreSubThread.start();
			}
			// Added Support for has_mail - jc - may 08 2012
			// checkMail(btnInbox, mySettings);

		}

		@Override
		protected String doInBackground(Context... arg0) {

			loadSubReddits(mySettings, Constants.CONST_SUBREDDITS_URL);
			loadUserInformation(mySettings, myEditor);
			return "Complete";
		}

	}

	private Runnable loadAllSubs = new Runnable() {

		@Override
		public void run() {
			if (!loadMoreSubRedditFlag.matches("")) {

				loadSubReddits(mySettings, Constants.CONST_SUBREDDITS_URL
						+ "?after=" + loadMoreSubRedditFlag);
				String subRs = "";
				for (int i = 0; i < subRedds.size(); i++) {
					subRs += subRedds.get(i).get("name").toString() + ",";
				}
				myEditor.putString("subreddits", subRs);
				myEditor.commit();
				runOnUiThread(updateSubs);
			}
		}

	};

	private Runnable updateSubs = new Runnable() {

		@Override
		public void run() {
			sub = new SubRedditAdapter(SubRedditsActivity.this, subRedds);
			subList.setAdapter(sub);
			// sub.notifyDataSetChanged();
			// TODO Auto-generated method stub

		}

	};

	public void loadUserInformation(SharedPreferences sh,
			SharedPreferences.Editor edit) {
		try {
			JSONObject jsonRe = getJSONfromURLMe("", "", sh);
			JSONObject data = null;
			if (!jsonRe.has("json")) {
				data = jsonRe.getJSONObject("data");
				edit.putString(
						"userinfopref",
						Boolean.toString(data.getBoolean("has_mail"))
								+ ","
								+ data.getString("name")
								+ ","
								+ Integer.toString(data.getInt("link_karma"))
								+ ","
								+ Integer.toString(data.getInt("comment_karma")));
				edit.commit();

			} else {

			}

		} catch (JSONException ex) {
			ex.printStackTrace();
		}
	}

	public static JSONObject getJSONfromURLMe(String url, String cookie,
			SharedPreferences sh) {

		String result = "";
		JSONObject jArray = null;

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		CookieStore bas = new BasicCookieStore();
		Log.i("status Code", sh.getString("redditsession", "fuck"));
		BasicClientCookie ck = new BasicClientCookie("reddit_session",
				sh.getString("redditsession", ""));
		ck.setDomain(".reddit.com");
		ck.setPath("/");
		ck.setExpiryDate(null);
		bas.addCookie(ck);
		client.setCookieStore(bas);
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				sh.getString("currentusername", "RSU"));

		HttpGet httpGet = new HttpGet(Constants.CONST_USERINFO_URL);
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
				Log.e("faile", "Failed to download file" + "5");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			jArray = new JSONObject(result);
			SharedPreferences.Editor ed = sh.edit();
			ed.putInt("changedornot", 0);
			ed.commit();

		} catch (JSONException e) {

			Log.e("log_tag", "Error parsing data " + e.toString() + "6");
		}

		return jArray;
	}

	protected void loadSubReddits(SharedPreferences shared, String url) {
		try {

			JSONObject jsonReturned = getJSONfromURL(url, currentCookie, shared);
			JSONObject json2 = null;
			JSONArray jArray = null;

			try {
				json2 = jsonReturned.getJSONObject("data");
				jArray = json2.getJSONArray("children");
				HashMap<String, String> defaultHashC = new HashMap<String, String>();
				defaultHashC.put("name", "frontpage");
				subRedds.add(defaultHashC);

				for (int i = 0; i < jArray.length(); i++) {

					JSONObject jsonObject1 = new JSONObject();
					jsonObject1 = jArray.getJSONObject(i);
					JSONObject jsonObject = new JSONObject();
					jsonObject = jsonObject1.getJSONObject("data");
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("name", jsonObject.getString("display_name"));
					map.put("display_name", jsonObject.getString("name"));
					map.put("id", jsonObject.getString("id"));
					subRedds.add(map);
				}
				loadMoreSubRedditFlag = "";
				Log.i("Load More", json2.get("after").toString());
				if (!json2.get("after").equals(null)) {
					loadMoreSubRedditFlag = json2.get("after").toString();
					Log.i("Load More", json2.get("after").toString());
				}
			} catch (Exception ex) {
				Log.e("Log_tag", ex.toString() + "2");
			}

		} catch (Exception ex) {
			Log.e("log_tag", ex.toString() + "3");
		}
	}

	public static JSONObject getJSONfromURL(String url, String cookie,
			SharedPreferences sh) {

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
		HttpGet httpGet = new HttpGet(url);
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
				Log.e("faile", "Failed to download file" + "5");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			jArray = new JSONObject(result);
			SharedPreferences.Editor ed = sh.edit();
			ed.putInt("changedornot", 0);
			ed.commit();

		} catch (JSONException e) {

			Log.e("log_tag", "Error parsing data " + e.toString() + "6");
		}

		return jArray;
	}

	public static JSONObject getJSONfromURLAll() {

		String result = "";
		JSONObject jArray = null;

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();

		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "RSU");
		HttpGet httpGet = new HttpGet(Constants.CONST_REDDITS_URL);
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
			jArray = new JSONObject(result);

		} catch (JSONException e) {

			Log.e("log_tag", "Error parsing data " + e.toString() + "6");
		}

		return jArray;
	}

}
