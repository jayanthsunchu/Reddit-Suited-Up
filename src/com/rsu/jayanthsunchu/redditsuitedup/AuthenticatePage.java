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
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AuthenticatePage extends AsyncTask<Context, Integer, String> {
	private String userName;
	private String passWord;
	private RedditorDB reddDb;
	Activity context;
	public static String sessionCookie;
	public static String ExceptionRaised;
	private final ProgressDialog anDialog;
	SharedPreferences settings;
	SharedPreferences.Editor editor;

	protected AuthenticatePage(String username, String password, RedditorDB db,
			Activity ctxt, ProgressDialog progress) {
		this.userName = username;
		this.passWord = password;
		reddDb = new RedditorDB(ctxt);
		this.context = ctxt;
		this.anDialog = progress;
		settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
		editor = settings.edit();
	}

	protected void onPreExecute() {
		
		anDialog.setMessage("logging in");
		anDialog.show();
	}

	@Override
	protected String doInBackground(Context... params) {
		// TODO Auto-generated method stub
		loginProcedure(userName, passWord, editor, settings);
		return "Complete";
	}

	protected void onPostExecute(String result) {
		if (this.anDialog.isShowing()) {
			this.anDialog.dismiss();

			if (ExceptionRaised.equals("none")) {
				editor.putInt("loggedinornot", 1);
				editor.commit();
				context.setResult(Constants.CONST_RESULT_CODE2);
				context.finish();

			} else {
				Toast.makeText(
						context,
						"There was an error. Please try again later."
								+ ExceptionRaised, Toast.LENGTH_LONG).show();
			}

		}
	}

	public void loginProcedure(String username, String password,
			SharedPreferences.Editor sh, SharedPreferences sp) {

		JSONObject jsonReturn = loginAuthentication(username, password, sh);
		if (ExceptionRaised.equals("none")) {

			JSONObject jsonReturnObject = null;
			JSONArray jsonArray = null;
			JSONObject anotherJson = null;
			try {

				jsonReturnObject = jsonReturn.getJSONObject("json");
				jsonArray = jsonReturnObject.getJSONArray("errors");

				anotherJson = jsonReturnObject.getJSONObject("data");
				if (jsonArray.length() == 0
						&& anotherJson.getString("cookie") != null) {

					sessionCookie = anotherJson.getString("cookie");
					if (reddDb.getCount() > 0)
						reddDb.removeDefaults();
					reddDb.newUser(
							username,
							password,
							sp.getString("redditsession",
									anotherJson.getString("cookie")),
							anotherJson.getString("modhash"), "forever", "true");

					sh.putString("usermodhash",
							anotherJson.getString("modhash"));
					sh.commit();

					Log.i("cookieinfo", anotherJson.getString("cookie"));

				} else {

				}
			} catch (JSONException ex) {
				Log.e("log_tag", ex.toString());
			}
		}

	}

	public static JSONObject loginAuthentication(String username,
			String password, SharedPreferences.Editor ed) {
		JSONObject jArray = null;

		// Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		HttpPost httppost = new HttpPost(Constants.CONST_LOGIN_URL + username);
		String result = "";

		if (!username.matches(""))
			httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
					username);
		else
			httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
					"Reddit Suited Up");

		StringBuilder builder = new StringBuilder();

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("user", username));
			nameValuePairs.add(new BasicNameValuePair("passwd", password));
			nameValuePairs.add(new BasicNameValuePair("api_type", "json"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (!httpclient.getCookieStore().getCookies().isEmpty()) {
				List<Cookie> cookies = httpclient.getCookieStore().getCookies();
				for (Cookie c : cookies) {
					if (c.getName().equals("reddit_session")) {
						ed.putString("redditsession", c.getValue().toString());
						ed.commit();

						ed.putString("currentusername", username);
						ed.commit();
						ed.putInt("changedornot", 1);
						ed.commit();
					}
				}

			}
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
				ExceptionRaised = "Failed to download file";

			}

		} catch (ClientProtocolException e) {
			ExceptionRaised = "client protocol exception";

			e.printStackTrace();
		} catch (IOException e) {

			ExceptionRaised = "io exception";
			e.printStackTrace();

		}

		try {
			jArray = new JSONObject(result);
			JSONObject js = jArray.getJSONObject("json");
			JSONArray ja = js.getJSONArray("errors");
			if (ja.length() > 0)
				ExceptionRaised = "Wrong Password.";
			else
				ExceptionRaised = "none";
		} catch (JSONException e) {
			ExceptionRaised = "json exception" + result;
			e.printStackTrace();
		}

		return jArray;

	}

}
