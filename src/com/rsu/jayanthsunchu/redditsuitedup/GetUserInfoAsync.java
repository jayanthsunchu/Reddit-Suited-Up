package com.rsu.jayanthsunchu.redditsuitedup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GetUserInfoAsync extends AsyncTask<Context, Integer, String> {

	Context context;
	SharedPreferences shPrefs;
	String currentuser;
	LinearLayout linLayout;
	TextView toSet;
	String toSetText = "";

	public GetUserInfoAsync(Context ctx, SharedPreferences sh,
			LinearLayout lin, TextView txt, String user) {
		this.context = ctx;
		this.shPrefs = sh;
		this.linLayout = lin;
		this.currentuser = user;
		this.toSet = txt;
	}

	@Override
	protected String doInBackground(Context... params) {
		return loadUserInfo();
	}

	protected void onPreExecute() {
		// linLayout.setVisibility(0);
	}

	protected void onPostExecute(String result) {
		// linLayout.setVisibility(8);
		toSet.setText(Html.fromHtml(toSetText));
		if (result.matches("success")) {
		} else {
			Toast.makeText(context,
					"Failed to load user info. Try again later.",
					Toast.LENGTH_LONG).show();
		}
	}

	public String loadUserInfo() {
		try {
			JSONObject jsonRe = getUserJson(currentuser, shPrefs);
			JSONObject data = null;
			if (!jsonRe.has("json")) {
				data = jsonRe.getJSONObject("data");
				Date time = new Date();
				toSetText = "<b>" + data.getString("name") + "</b>" + "<br />"
						+ "<font color='#C0C0C0'><i>" + Integer.toString(data.getInt("link_karma")) + " link karma | "
						
						+ Integer.toString(data.getInt("comment_karma")) + " comment karma" + "</font></i>"
						+ "<br />redditor since: " + DateUtils.getRelativeTimeSpanString((long) Float.parseFloat(data
						.getString("created_utc")) * 1000);

				// edit.putString(
				// "userinfopref",
				// Boolean.toString(data.getBoolean("has_mail"))
				// + ","
				// + data.getString("name")
				// + ","
				// + Integer.toString(data.getInt("link_karma"))
				// + ","
				// + Integer.toString(data.getInt("comment_karma")));
				// edit.commit();
				return "success";
			} else {
				return "failure";
			}

		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return "failure";
	}

	public static JSONObject getUserJson(String username, SharedPreferences sh) {

		String result = "";
		JSONObject jArray = null;

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();

		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				sh.getString("currentusername", "RSU"));

		HttpGet httpGet = new HttpGet(Constants.CONST_ALLUSER_INFO + username
				+ "/about/.json");
		try {

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
