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

import com.rsu.jayanthsunchu.redditsuitedup.SubRedditsActivity.FocusListener;
import com.rsu.jayanthsunchu.redditsuitedup.SubRedditsActivity.LoadSubReddits;
import com.rsu.jayanthsunchu.redditsuitedup.SubRedditsActivity.Validator;

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

public class FavSubRedditsActivity extends Activity {

	ArrayAdapter<String> subRedditList;
	ArrayAdapter<String> autoCompleteList;
	SubRedditAdapter sub;
	GetSubRedditsAsyncTask get;
	ArrayList<HashMap<String, String>> subRedds = new ArrayList<HashMap<String, String>>();
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

	TextView txtFrontpage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		reddDb = new RedditorDB(FavSubRedditsActivity.this);
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
		setContentView(R.layout.favsubreddits_layout);
		setUpViews();

	}

	public void applyTheme() {

		this.setTheme(R.style.DarkTheme);

	}

	public boolean onTouchEvent(MotionEvent e) {

		finish();

		return false;

	}

	public void setUpViews() {
		// txtFrontpage = (TextView) findViewById(R.id.favSubReddits);
		// txtFrontpage.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// SharedPreferences sh = v.getContext().getSharedPreferences(
		// Constants.PREFS_NAME, 0);
		// SharedPreferences.Editor ed = sh.edit();
		// ed.putString("frontpageorwhat", "");
		// ed.commit();
		//
		// ed.putString("sort", "");
		// ed.commit();
		//
		// // Intent backTo = new Intent(v.getContext(),
		// // FrontPageActivity.class);
		//
		// setResult(Constants.CONST_RESULT_CODE, null);
		// finish();
		// }
		//
		// });
		subList = (ListView) findViewById(R.id.subRedditList);
		String[] favList = mySettings.getString("favsubreddits",
				"frontpage, pics").split(",");
		for (int i = 0; i < favList.length; i++) {
			HashMap<String, String> hashFav = new HashMap<String, String>();
			hashFav.put("name", favList[i]);
			subRedds.add(hashFav);
		}
		sub = new SubRedditAdapter(FavSubRedditsActivity.this, subRedds);
		subList.setAdapter(sub);
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

		btnGoAuto = (Button) findViewById(R.id.btnGo);
		btnGoAuto.setTag(autoText);
		btnGoAuto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AutoCompleteTextView autoT = (AutoCompleteTextView) v.getTag();
				if (!autoT.getText().toString().matches("")
						&& autoT.getValidator().isValid(
								autoT.getText().toString())) {
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

}
