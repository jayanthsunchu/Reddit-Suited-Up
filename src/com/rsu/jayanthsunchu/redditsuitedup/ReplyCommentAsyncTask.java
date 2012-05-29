package com.rsu.jayanthsunchu.redditsuitedup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

public class ReplyCommentAsyncTask extends AsyncTask<Context, Integer, String> {
	SharedPreferences sharedPrefs;
	Context context;
	Activity act;
	String comment;
	String idForOpertation;
	boolean Flag;
	ProgressDialog prg;
	String result;
	Bundle fromTheir;

	public ReplyCommentAsyncTask(Context ctx, String com, String id,
			boolean flag, Activity ac, Bundle brr) {
		this.context = ctx;
		this.comment = com;
		this.idForOpertation = id;
		this.Flag = flag;
		this.sharedPrefs = ctx.getSharedPreferences(Constants.PREFS_NAME, 0);
		this.prg = new ProgressDialog(ctx);
		this.act = ac;
		this.fromTheir = brr;
	}

	@Override
	protected String doInBackground(Context... arg0) {

		return postReply(comment, idForOpertation, Flag, sharedPrefs);
	}

	protected void onPostExecute(String result) {
		if (prg.isShowing())
			prg.dismiss();
		Intent data = new Intent(context, CommentsAndLink.class);
		fromTheir.putString("newtext", comment);
		data.putExtras(fromTheir);
		act.setResult(Constants.CONST_RESULT_COMMENT_CODE, data);
		act.finish();
	}

	protected void onPreExecute() {
		prg.setMessage("posting reply");
		prg.show();
	}

	@SuppressWarnings("unused")
	public static String postReply(String comment, String id, boolean flag,
			SharedPreferences sh) {
		JSONObject jArray = null;
		String noexception = "false";
		// Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.CONST_REPLY_URL);
		String result = "";
		CookieStore bas = new BasicCookieStore();
		// Log.i("status Code", sh.getString("redditsession", "fuck"));
		BasicClientCookie ck = new BasicClientCookie("reddit_session",
				sh.getString("redditsession", ""));
		ck.setDomain(".reddit.com");
		ck.setPath("/");
		ck.setExpiryDate(null);
		bas.addCookie(ck);
		httpclient.setCookieStore(bas);
		httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				sh.getString("currentusername", "RSU"));

		StringBuilder builder = new StringBuilder();

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("thing_id", id));

			nameValuePairs.add(new BasicNameValuePair("text", comment));
			nameValuePairs.add(new BasicNameValuePair("uh", sh.getString(
					"usermodhash", "")));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

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

			} else {

			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();

		}

		try {

			if (result.matches("\\{\\}"))
				// success
				noexception = "true";
			else
				jArray = new JSONObject(result);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		return noexception;

	}

}
