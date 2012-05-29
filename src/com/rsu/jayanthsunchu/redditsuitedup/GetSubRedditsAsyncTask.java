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
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;

public class GetSubRedditsAsyncTask extends AsyncTask<Context, Integer, String> {
	SubRedditAdapter adapter;
	ListView subList;
	String currentCookie;
	ProgressBar progressBar;
	Activity ctxt;
	ArrayList<HashMap<String, String>> returnArrayList = new ArrayList<HashMap<String, String>>();
	public GetSubRedditsAsyncTask(Activity ctx, ProgressBar pro, ListView list, String cookie){
		this.currentCookie = cookie;
		
		this.subList = list;
		this.progressBar = pro;
		this.ctxt = ctx;
	}
	
	protected void onPreExecute(){
		progressBar.setVisibility(0);
	}
	
	protected void onPostExecute(String result) {
		progressBar.setVisibility(8);
		adapter = new SubRedditAdapter(ctxt, returnArrayList);
		subList.setAdapter(adapter);
	}
	
	@Override
	protected String doInBackground(Context... arg0) {
		// TODO Auto-generated method stub
		loadSubReddits();
		return "Complete";
	}
	
	protected void loadSubReddits(){
		try {
			
				JSONObject jsonReturned = getJSONfromURL(Constants.CONST_SUBREDDITS_URL, currentCookie);
				JSONObject json2 = null;
				JSONArray jArray = null;

				try {
					json2 = jsonReturned.getJSONObject("data");
					jArray = json2.getJSONArray("children");
					for (int i = 0; i < jArray.length(); i++) {

						JSONObject jsonObject1 = new JSONObject();
						jsonObject1 = jArray.getJSONObject(i);
						JSONObject jsonObject = new JSONObject();
						jsonObject = jsonObject1.getJSONObject("data");
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("display_name", jsonObject.getString("display_name"));
						map.put("name", jsonObject.getString("name"));
						map.put("id", jsonObject.getString("id"));
						returnArrayList.add(map);
					}
				}
				catch(Exception ex){
					Log.e("Log_tag", ex.toString() + "2");
				}
				
			
		}
		catch(Exception ex){
			Log.e("log_tag", ex.toString() + "3");
		}
	}
	
	//@SuppressWarnings("null")
	public static JSONObject getJSONfromURL(String url, String cookie) {

		String result = "";
		JSONObject jArray = null;

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		CookieStore ck = new BasicCookieStore();
		Cookie cki = new BasicClientCookie("reddit_session", cookie);
		ck.addCookie(cki);
		client.setCookieStore(ck);
		
		HttpGet httpGet = new HttpGet(url);
		try {
			
			
			Log.i("cookieinfo", cookie + "4");
			HttpResponse response = client.execute(httpGet);
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
