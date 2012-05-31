package com.rsu.jayanthsunchu.redditsuitedup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rsu.jayanthsunchu.redditsuitedup.QuickAction.OnActionItemClickListener;
import com.rsu.jayanthsunchu.redditsuitedup.RedditorDB.AuthenticationObject;

public class FrontPageActivity extends ListActivity {
	/** Called when the activity is first created. */
	RedditorDB reddDb;

	AuthenticationObject authObj;
	GestureDetector mGestureDetector;
	View.OnTouchListener mGestureListener;
	String queryString;
	boolean loadingMore;
	AuthenticatePage authPage;
	// linear layouts to maintain visibility of action bar - jc - may 30 2012
	LinearLayout actionBar;
	LinearLayout actionBarOpener;
	LinearLayout mainBar;
	public ProgressDialog progressDialog;
	SharedPreferences redditSUPreferences;
	SharedPreferences.Editor redditPrefEditor;
	ArrayList<HashMap<String, String>> returnList = new ArrayList<HashMap<String, String>>();
	TextView txtTitle;
	LinearLayout progBar;
	FrontPageTask fpTask;
	ImageView imgRefresh;
	ImageView imgSettings;
	ImageView imgLoginAs;
	ImageView imgClose;
	ImageView imgOpen;
	AsynchronousOptions asyncOperations;
	Button btnSortBy;
	FrontPageListAdapter adapter;
	private static final int ID_UP = 1;
	private static final int ID_DOWN = 2;
	private static final int ID_LINK = 3;
	private static final int ID_SAVE = 4;
	private static final int ID_USER = 5;
	private static final int ID_HOT = 10;
	private static final int ID_NEW = 6;
	private static final int ID_CONTRO = 7;
	private static final int ID_TOP = 8;
	private static final int ID_SAVED = 9;
	private static final int ID_SUBMITTED = 11;
	private static final int ID_LIKED = 12;
	private static final int ID_DISLIKED = 13;
	private static final int ID_HIDDEN = 14;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		applyTheme();
		setContentView(R.layout.main);
		reddDb = new RedditorDB(this);
		setUpViews();
		fpTask = new FrontPageTask(redditSUPreferences, progBar);
		fpTask.execute(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		applyTheme();
		setContentView(R.layout.main);
		setUpViews();
	}

	public void applyTheme() {
		redditSUPreferences = this
				.getSharedPreferences(Constants.PREFS_NAME, 0);

		if (redditSUPreferences.getString("theme", "white").matches("white")) {
			FrontPageActivity.this.setTheme(R.style.WhiteTheme);

		} else {
			FrontPageActivity.this.setTheme(R.style.DarkTheme);

		}

	}

	private OnClickListener refreshClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getIntent());
			finish();
			startActivity(intent);
		}
	};

	private OnClickListener settingsClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			startActivityForResult(new Intent(v.getContext(),
					SettingsActivity.class), Constants.CONST_THEME_REQUEST);
			overridePendingTransition(R.anim.rail, R.anim.rail);

		}
	};

	private OnClickListener loginClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent loginIntent2 = new Intent(v.getContext(),
					LoginActivity.class);
			// finish();
			startActivityForResult(loginIntent2,
					Constants.CONST_REFRESH_ACTIVITY_CODE);
			overridePendingTransition(R.anim.rail, R.anim.rail);

		}
	};

	private OnClickListener closeClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			actionBar.setVisibility(8);
			mainBar.setVisibility(8);
			actionBarOpener.setVisibility(0);

		}
	};

	private OnClickListener openClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			actionBar.setVisibility(0);
			mainBar.setVisibility(0);
			actionBarOpener.setVisibility(8);

		}
	};

	public void setUpViews() {
		// action bar items and their onclick listeners and linear layouts to
		// maintain visibility of action bar - jc - may 30 2012
		actionBar = (LinearLayout) findViewById(R.id.actionBar);
		actionBarOpener = (LinearLayout) findViewById(R.id.actionBarOpener);
		mainBar = (LinearLayout)findViewById(R.id.subRedditMenu);
		imgRefresh = (ImageView) findViewById(R.id.imgRefresh);
		imgSettings = (ImageView) findViewById(R.id.imgSettings);
		imgLoginAs = (ImageView) findViewById(R.id.imgLoginAs);
		imgClose = (ImageView) findViewById(R.id.imgClose);
		imgOpen = (ImageView) findViewById(R.id.imgOpen);

		imgRefresh.setOnClickListener(refreshClickListener);
		imgSettings.setOnClickListener(settingsClickListener);
		imgLoginAs.setOnClickListener(loginClickListener);
		imgClose.setOnClickListener(closeClickListener);
		imgOpen.setOnClickListener(openClickListener);

		progressDialog = new ProgressDialog(FrontPageActivity.this);

		redditPrefEditor = redditSUPreferences.edit();
		redditPrefEditor.putInt("loggedinornot", reddDb.getCount());
		redditPrefEditor.commit();
		redditPrefEditor.putBoolean("showdialog", false);
		redditPrefEditor.commit();
		ActionItem nextItem = null;
		ActionItem prevItem = null;
		ActionItem searchItem = null;
		ActionItem infoItem = null;
		ActionItem eraseItem = null;
		ActionItem browseItem = null;
		Drawable d;
		if (redditSUPreferences.getString("theme", "white").matches("white")) {
			// FrontPageActivity.this.setTheme(R.style.WhiteTheme);
			d = this.getResources().getDrawable(R.drawable.dividerwhite);
			this.getListView().setDivider(d);

		} else {
			// FrontPageActivity.this.setTheme(R.style.DarkTheme);
			d = this.getResources().getDrawable(R.drawable.dividerblack);
			this.getListView().setDivider(d);

		}

		nextItem = new ActionItem(ID_UP, "", getResources().getDrawable(
				R.drawable.upvotewhite));
		prevItem = new ActionItem(ID_USER, "user", getResources().getDrawable(
				R.drawable.userwhite));
		searchItem = new ActionItem(ID_SAVE, "save", getResources()
				.getDrawable(R.drawable.savewhite));
		infoItem = new ActionItem(ID_LINK, "li+co", getResources().getDrawable(
				R.drawable.openwhite));
		eraseItem = new ActionItem(ID_DOWN, "", getResources().getDrawable(
				R.drawable.downvotewhite));
		browseItem = new ActionItem(54, "link", getResources().getDrawable(
				R.drawable.browse));

		this.getListView().setDividerHeight(1);

		Button btnSubReddits = new Button(FrontPageActivity.this);
		btnSubReddits = (Button) findViewById(R.id.btnSubReddits);

		txtTitle = (TextView) findViewById(R.id.txtTitle);

		progBar = (LinearLayout) findViewById(R.id.subRedditP);

		btnSubReddits.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent subIntent = new Intent(v.getContext(),
						SubRedditsActivity.class);

				startActivityForResult(subIntent,
						Constants.CONST_REFRESH_ACTIVITY_CODE);
				overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
			}

		});
		if (!redditSUPreferences.getString("frontpageorwhat", "").matches(""))
			txtTitle.setText(redditSUPreferences.getString("frontpageorwhat",
					"frontpage"));
		else
			txtTitle.setText("frontpage");

		// quickaction

		ActionItem newItem = new ActionItem(ID_NEW, "new", null);
		ActionItem controItem = new ActionItem(ID_CONTRO, "controversial", null);
		ActionItem hotItem = new ActionItem(ID_HOT, "hot", null);
		ActionItem topItem = new ActionItem(ID_TOP, "top", null);
		ActionItem savedItem = new ActionItem(ID_SAVED, "saved", null);
		ActionItem submittedItem = new ActionItem(ID_SUBMITTED, "submitted",
				null);
		ActionItem likedItem = new ActionItem(ID_LIKED, "liked", null);
		ActionItem dislikedItem = new ActionItem(ID_DISLIKED, "disliked", null);
		ActionItem hiddenItem = new ActionItem(ID_HIDDEN, "hidden", null);

		final QuickAction quickAction = new QuickAction(this,
				QuickAction.HORIZONTAL, false);

		final QuickAction sortBy = new QuickAction(this, QuickAction.VERTICAL,
				false);
		sortBy.addActionItem(hotItem);
		sortBy.addActionItem(newItem);
		sortBy.addActionItem(controItem);
		sortBy.addActionItem(topItem);
		sortBy.addActionItem(savedItem);
		sortBy.addActionItem(submittedItem);
		sortBy.addActionItem(likedItem);
		sortBy.addActionItem(dislikedItem);
		sortBy.addActionItem(hiddenItem);

		sortBy.setOnActionItemClickListener(new OnActionItemClickListener() {

			@Override
			public void onItemClick(QuickAction source, int pos, int actionId) {
				if (actionId == ID_HOT) {
					redditPrefEditor.putString("sort", "");
					redditPrefEditor.commit();
					finish();
					startActivity(new Intent(getIntent()));

				} else if (actionId == ID_NEW) {
					redditPrefEditor.putString("sort", "/new");
					redditPrefEditor.commit();
					finish();
					startActivity(new Intent(getIntent()));
				} else if (actionId == ID_CONTRO) {
					redditPrefEditor.putString("sort", "/controversial");
					redditPrefEditor.commit();
					finish();
					startActivity(new Intent(getIntent()));
				} else if (actionId == ID_TOP) {
					redditPrefEditor.putString("sort", "/top");
					redditPrefEditor.commit();
					finish();
					startActivity(new Intent(getIntent()));
				} else if (actionId == ID_SAVED) {
					if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
						Toast.makeText(FrontPageActivity.this,
								"log in for saved links", Toast.LENGTH_LONG)
								.show();
					} else {
						redditPrefEditor.putString(
								"sort",
								"/user/"
										+ redditSUPreferences.getString(
												"currentusername", "")
										+ "/saved");
						redditPrefEditor.commit();
						finish();
						startActivity(new Intent(getIntent()));
					}

				} else if (actionId == ID_SUBMITTED) {
					if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
						Toast.makeText(FrontPageActivity.this,
								"log in for submitted links", Toast.LENGTH_LONG)
								.show();
					} else {
						redditPrefEditor.putString(
								"sort",
								"/user/"
										+ redditSUPreferences.getString(
												"currentusername", "")
										+ "/submitted");
						redditPrefEditor.commit();
						finish();
						startActivity(new Intent(getIntent()));
					}
				}

				else if (actionId == ID_LIKED) {
					if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
						Toast.makeText(FrontPageActivity.this,
								"log in for liked links", Toast.LENGTH_LONG)
								.show();
					} else {
						redditPrefEditor.putString(
								"sort",
								"/user/"
										+ redditSUPreferences.getString(
												"currentusername", "")
										+ "/liked");
						redditPrefEditor.commit();
						finish();
						startActivity(new Intent(getIntent()));
					}
				}

				else if (actionId == ID_DISLIKED) {
					if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
						Toast.makeText(FrontPageActivity.this,
								"log in for disliked links", Toast.LENGTH_LONG)
								.show();
					} else {
						redditPrefEditor.putString(
								"sort",
								"/user/"
										+ redditSUPreferences.getString(
												"currentusername", "")
										+ "/disliked");
						redditPrefEditor.commit();
						finish();
						startActivity(new Intent(getIntent()));
					}
				}

				else if (actionId == ID_HIDDEN) {
					if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
						Toast.makeText(FrontPageActivity.this,
								"log in for hidden links", Toast.LENGTH_LONG)
								.show();
					} else {
						redditPrefEditor.putString(
								"sort",
								"/user/"
										+ redditSUPreferences.getString(
												"currentusername", "")
										+ "/hidden");
						redditPrefEditor.commit();
						finish();
						startActivity(new Intent(getIntent()));
					}
				}

			}

		});

		// add action items into QuickAction
		quickAction.addActionItem(nextItem);
		quickAction.addActionItem(prevItem);
		quickAction.addActionItem(searchItem);
		quickAction.addActionItem(infoItem);
		quickAction.addActionItem(browseItem);
		quickAction.addActionItem(eraseItem);

		btnSortBy = (Button) findViewById(R.id.btnSortBy);
		btnSortBy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				sortBy.show(v);
				sortBy.setAnimStyle(QuickAction.ANIM_REFLECT);
			}

		});

		quickAction
				.setOnActionItemClickListener(new OnActionItemClickListener() {

					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						// ActionItem actionItem =
						// quickAction.getActionItem(pos);

						// here we can filter which action item was clicked with
						// pos or actionId parameter
						int po = quickAction.getListViewPosition();
						if (actionId == ID_UP) {
							if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
								Toast.makeText(FrontPageActivity.this,
										"log in for this action",
										Toast.LENGTH_LONG).show();
							} else {
								if (returnList.get(po).get("vote").matches("0")) {
									returnList.get(po).put("vote", "1");
									adapter.notifyDataSetChanged();

									source.dismiss();
									asyncOperations = new AsynchronousOptions(
											FrontPageActivity.this, "t3_"
													+ returnList.get(po).get(
															"id"),
											redditSUPreferences, "upvote");
									asyncOperations
											.execute(FrontPageActivity.this);
								}

								else {
									returnList.get(po).put("vote", "0");
									adapter.notifyDataSetChanged();

									source.dismiss();
									asyncOperations = new AsynchronousOptions(
											FrontPageActivity.this, "t3_"
													+ returnList.get(po).get(
															"id"),
											redditSUPreferences, "rescind");
									asyncOperations
											.execute(FrontPageActivity.this);
								}
							}

						} else if (actionId == ID_DOWN) {

							if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
								Toast.makeText(FrontPageActivity.this,
										"log in for this action",
										Toast.LENGTH_LONG).show();
							} else {
								if (returnList.get(po).get("vote").matches("0")) {
									returnList.get(po).put("vote", "-1");
									adapter.notifyDataSetChanged();

									source.dismiss();
									asyncOperations = new AsynchronousOptions(
											FrontPageActivity.this, "t3_"
													+ returnList.get(po).get(
															"id"),
											redditSUPreferences, "downvote");
									asyncOperations
											.execute(FrontPageActivity.this);
								} else {
									returnList.get(po).put("vote", "0");
									adapter.notifyDataSetChanged();

									source.dismiss();
									asyncOperations = new AsynchronousOptions(
											FrontPageActivity.this, "t3_"
													+ returnList.get(po).get(
															"id"),
											redditSUPreferences, "rescind");
									asyncOperations
											.execute(FrontPageActivity.this);
								}
							}

						} else if (actionId == ID_LINK) {
							returnList.get(po).put("clicked", "true");
							adapter.notifyDataSetChanged();
							Intent comments = new Intent(
									FrontPageActivity.this,
									CommentsAndLink.class);
							comments.putExtra("url",
									returnList.get(po).get("url"));
							comments.putExtra("id", returnList.get(po)
									.get("id"));
							comments.putExtra("author",
									returnList.get(po).get("author"));
							comments.putExtra("selftext", returnList.get(po)
									.get("selftextun"));
							comments.putExtra("title",
									returnList.get(po).get("title"));
							comments.putExtra("score",
									returnList.get(po).get("score"));
							comments.putExtra("saved",
									returnList.get(po).get("saved"));
							comments.putExtra("vote",
									returnList.get(po).get("vote"));
							// finish();
							startActivity(comments);
						} else if (actionId == ID_SAVE) {
							if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
								Toast.makeText(FrontPageActivity.this,
										"log in for this action",
										Toast.LENGTH_LONG).show();
							} else {

								if (returnList.get(po).get("saved")
										.matches("true")) {
									returnList.get(po).put("saved", "false");
									adapter.notifyDataSetChanged();

									source.dismiss();

									asyncOperations = new AsynchronousOptions(
											FrontPageActivity.this, "t3_"
													+ returnList.get(po).get(
															"id"),
											redditSUPreferences, "unsave");
									asyncOperations
											.execute(FrontPageActivity.this);

								} else if (returnList.get(po).get("saved")
										.matches("false")) {
									returnList.get(po).put("saved", "true");
									adapter.notifyDataSetChanged();

									source.dismiss();
									asyncOperations = new AsynchronousOptions(
											FrontPageActivity.this, "t3_"
													+ returnList.get(po).get(
															"id"),
											redditSUPreferences, "save");
									asyncOperations
											.execute(FrontPageActivity.this);
								}
							}

						} else if (actionId == ID_USER) {
							Intent profile = new Intent(FrontPageActivity.this,
									ProfileActivity.class);
							profile.putExtra("username", returnList.get(po)
									.get("author"));

							startActivity(profile);
							// Toast.makeText(
							// FrontPageActivity.this,
							// "Feature yet to be added. Please check for the updates.",
							// Toast.LENGTH_LONG).show();
						} else if (actionId == 54) {
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(returnList.get(po).get("url")));
							startActivity(i);
						}
					}

				});

		// getListView().setOnItemClickListener(new OnItemClickListener() {
		//
		// // @Override
		// public void onItemClick(AdapterView<?> av, View v, int position,
		// long id) {
		// returnList.get(position).put("clicked", "true");
		// adapter.notifyDataSetChanged();
		// Intent comments = new Intent(FrontPageActivity.this,
		// CommentsAndLink.class);
		// comments.putExtra("url",
		// returnList.get(position).get("url"));
		// comments.putExtra("id", returnList.get(position).get("id"));
		// comments.putExtra("author",
		// returnList.get(position).get("author"));
		// comments.putExtra("selftext",
		// returnList.get(position).get("selftextun"));
		// comments.putExtra("title", returnList.get(position)
		// .get("title"));
		// comments.putExtra("score", returnList.get(position)
		// .get("score"));
		// comments.putExtra("saved", returnList.get(position)
		// .get("saved"));
		// comments.putExtra("vote",
		// returnList.get(position).get("vote"));
		// // finish();
		// startActivity(comments);
		//
		// quickAction.show(v);
		// quickAction.setListViewPosition(position);
		// quickAction.setAnimStyle(QuickAction.ANIM_REFLECT);
		//
		// }
		//
		// });

		// getListView().setOnItemLongClickListener(new
		// OnItemLongClickListener() {
		// @Override
		// public boolean onItemLongClick(AdapterView<?> av, View v,
		// int position, long id) {
		// // quickAction.show(v);
		// // quickAction.setListViewPosition(position);
		// // quickAction.setAnimStyle(QuickAction.ANIM_REFLECT);
		// // return true;
		// return false;
		// }
		//
		// });

		mGestureDetector = new GestureDetector(
				new PostGestureDetectorFrontPage(FrontPageActivity.this,
						returnList, quickAction));
		mGestureListener = new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				return mGestureDetector.onTouchEvent(event);
			}
		};

		getListView().setOnTouchListener(mGestureListener);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Constants.CONST_RESULT_CODE
				|| resultCode == Constants.CONST_THEME_RESULT) {
			finish();
			startActivity(new Intent(getIntent()));
		}
		if (resultCode == Constants.CONST_RESULT_CODE2) {
			redditPrefEditor.putInt("changedornot", 1);
			redditPrefEditor.commit();
			finish();
			startActivity(new Intent(getIntent()));
		}

	}

	private Runnable loadMorePosts = new Runnable() {

		@Override
		public void run() {
			loadingMore = true;

			getDataFromApi(redditSUPreferences.getString("frontpageorwhat", "")
					+ redditSUPreferences.getString("sort", ""),
					redditSUPreferences.getString("redditsession", ""),
					redditSUPreferences, queryString);

			runOnUiThread(updateFrontPage);
		}

	};

	private Runnable updateFrontPage = new Runnable() {

		@Override
		public void run() {
			adapter.notifyDataSetChanged();
			loadingMore = false;
		}

	};

	public class FrontPageTask extends AsyncTask<Context, Integer, String> {
		SharedPreferences reddPrefs;
		LinearLayout pBar;

		public FrontPageTask(SharedPreferences sh, LinearLayout pb) {
			this.reddPrefs = sh;
			this.pBar = pb;
		}

		@Override
		protected String doInBackground(Context... arg0) {

			try {
				Log.i("status Code",
						"loading"
								+ reddPrefs
										.getString("frontpageorwhat", "fuck")
								+ reddPrefs.getString("redditsession", "fuck2"));
				getDataFromApi(reddPrefs.getString("frontpageorwhat", "")
						+ reddPrefs.getString("sort", ""),
						reddPrefs.getString("redditsession", ""), reddPrefs, "");
			} catch (Exception ex) {
				Log.e("errorcode", ex.toString());
			}
			return "Complete";
		}

		protected void onPreExecute() {
			pBar.setVisibility(0);

		}

		protected void onPostExecute(String result) {
			pBar.setVisibility(8);
			adapter = new FrontPageListAdapter(FrontPageActivity.this,
					returnList);

			if (returnList.size() > 0) {
				View footerView = ((LayoutInflater) FrontPageActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.neverendingfooter, null, false);
				getListView().addFooterView(footerView);
			} else {
				View footerView = ((LayoutInflater) FrontPageActivity.this
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

			setListAdapter(adapter);

			getListView().setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScroll(AbsListView view, int firstVisible,
						int visibleCount, int totalCount) {

					if (firstVisible >= visibleCount) {
						adapter.count = 0;
					}
					int lastInScreen = firstVisible + visibleCount;
					if ((lastInScreen == totalCount) && !(loadingMore)) {
						if (returnList.size() > 0) {

							queryString = "?count="
									+ returnList.size()
									+ "&after=t3_"
									+ returnList.get(returnList.size() - 1)
											.get("id");
							Thread loadMoreThread = new Thread(null,
									loadMorePosts);
							loadMoreThread.start();
							// adapter.notifyDataSetChanged();
						}
					}

				}

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					// TODO Auto-generated method stub

				}

			});

		}

	}

	protected void getDataFromApi(String url, String cookie,
			SharedPreferences sh, String queryString) {
		JSONObject json = getJSONfromURL(url, cookie, sh, queryString);

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
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("title", jsonObject.getString("title"));

				map.put("subreddit", jsonObject.getString("subreddit"));
				map.put("selftext_html", jsonObject.getString("selftext_html"));
				map.put("selftext",
						Mdown.getHtml(jsonObject.getString("selftext")));
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
				map.put("subreddit_id", jsonObject.getString("subreddit_id"));
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
				map.put("num_comments", jsonObject.getString("num_comments"));
				map.put("ups", jsonObject.getString("ups"));
				map.put("domain", jsonObject.getString("domain"));
				map.put("vote", "0");

				returnList.add(map);
				// adapter.notifyDataSetChanged();

			}

		} catch (JSONException e) {

			Log.e("errorcode", "1" + e.toString());
		} catch (Exception ex) {
			Log.e("errorcode", "2" + ex.toString());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onRestart() {
		super.onRestart();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (reddDb != null)
			reddDb.closeDb();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
			inflater.inflate(R.menu.loggedout_menu, menu);
		} else {
			inflater.inflate(R.menu.loggedin_menu, menu);
		}

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (redditSUPreferences.getInt("loggedinornot", 0) != 0) {
			menu.clear();
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.loggedin_menu, menu);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.login:
			// showDialog(Constants.CONST_LOGIN_DIALOG_ID);

			Intent loginIntent = new Intent(FrontPageActivity.this,
					LoginActivity.class);
			// finish();
			startActivityForResult(loginIntent,
					Constants.CONST_REFRESH_ACTIVITY_CODE);
			overridePendingTransition(R.anim.rail, R.anim.rail);
			break;

		case R.id.refresh:
			Intent intent = new Intent(getIntent());
			finish();
			startActivity(intent);
			break;

		case R.id.ass:
			Intent loginIntent2 = new Intent(FrontPageActivity.this,
					LoginActivity.class);
			// finish();
			startActivityForResult(loginIntent2,
					Constants.CONST_REFRESH_ACTIVITY_CODE);
			overridePendingTransition(R.anim.rail, R.anim.rail);
			break;

		case R.id.settings:
			startActivityForResult(new Intent(this, SettingsActivity.class),
					Constants.CONST_THEME_REQUEST);
			overridePendingTransition(R.anim.rail, R.anim.rail);
			break;

		}
		return true;
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = new Dialog(FrontPageActivity.this,
				R.drawable.dialogstyle);
		switch (id) {
		case Constants.CONST_LOGIN_DIALOG_ID:
			dialog.setContentView(R.layout.login_dialog);
			dialog.setTitle("Login");

			Button btnLogin = (Button) dialog.findViewById(R.id.loginButton);
			btnLogin.setTag(dialog);
			btnLogin.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Dialog dialogCheck = (Dialog) v.getTag();
					EditText txtUserName = (EditText) dialogCheck
							.findViewById(R.id.userNameField);
					EditText txtPassWord = (EditText) dialogCheck
							.findViewById(R.id.passWordField);

					if (!txtUserName.getText().toString().trim().matches("")
							|| !txtPassWord.getText().toString().trim()
									.matches("")) {

						// authPage = new
						// AuthenticatePage(txtUserName.getText().toString().trim(),
						// txtPassWord.getText().toString().trim(), reddDb,
						// FrontPageActivity.this, progressDialog);
						// authPage.execute(FrontPageActivity.this);

					} else {
						Toast.makeText(v.getContext(), "Enter first Bosdeeke",
								Toast.LENGTH_LONG).show();
					}

				}

			});
			break;

		case Constants.CONST_SUBREDDITS_DIALOG_ID:
			dialog.setContentView(R.layout.subreddits_layout);
			break;
		}
		return dialog;
	}

	public static JSONObject getJSONfromURL(String url, String cookie,
			SharedPreferences sh, String queryString) {

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

		HttpGet httpGet = new HttpGet(Constants.CONST_REDDIT_URL + url
				+ ".json" + queryString);
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

		} catch (JSONException e) {

			Log.e("log_tag", "Error parsing data " + e.toString() + "6");
		}

		return jArray;
	}

	public static String postVote(String id, String dir, SharedPreferences sh) {
		JSONObject jArray = null;
		String noexception = "false";
		// Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.CONST_VOTE_URL);
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
			nameValuePairs.add(new BasicNameValuePair("id", id));
			nameValuePairs.add(new BasicNameValuePair("dir", dir));
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

	public static String saveOrUnsave(String id, String url,
			SharedPreferences sh) {
		JSONObject jArray = null;
		String noexception = "false";
		// Create a new HttpClient and Post Header
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
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
			nameValuePairs.add(new BasicNameValuePair("id", id));

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

	public class PostGestureDetectorFrontPage extends
			GestureDetector.SimpleOnGestureListener {
		final ViewConfiguration vc = ViewConfiguration
				.get(FrontPageActivity.this);

		final int swipeMinDistance;
		final int swipeMaxOffPath;
		final int swipeThresholdVelocity;

		Context context;
		ArrayList<HashMap<String, String>> returnAList;
		QuickAction thisQuick;
		View curv;

		public void setCurv(View cur) {
			this.curv = cur;
		}

		public PostGestureDetectorFrontPage(Context ctx,
				ArrayList<HashMap<String, String>> list, QuickAction quick) {
			this.context = ctx;
			this.returnAList = list;
			DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
			swipeMinDistance = (int) (120.0f * dm.densityDpi / 160.0f + 0.5);
			swipeMaxOffPath = (int) (250.0f * dm.densityDpi / 160.0f + 0.5);
			swipeThresholdVelocity = (int) (200.0f * dm.densityDpi / 160.0f + 0.5);
			this.thisQuick = quick;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent ev) {
			ListView li = getListView();
			int position = li.pointToPosition((int) ev.getX(), (int) ev.getY());
			// handled force close while loading - jc - may 21 2012
			if (returnAList.size() <= 0)
				return false;
			if (redditSUPreferences.getBoolean("ctg", false)) {
				returnAList.get(position).put("clicked", "true");
				adapter.notifyDataSetChanged();
				Intent comments = new Intent(context, CommentsAndLink.class);
				comments.putExtra("url", returnAList.get(position).get("url"));
				comments.putExtra("id", returnAList.get(position).get("id"));
				comments.putExtra("author",
						returnAList.get(position).get("author"));
				comments.putExtra("selftext",
						returnAList.get(position).get("selftextun"));
				comments.putExtra("title",
						returnAList.get(position).get("title"));
				comments.putExtra("score",
						returnAList.get(position).get("score"));
				comments.putExtra("saved",
						returnAList.get(position).get("saved"));
				comments.putExtra("vote", returnAList.get(position).get("vote"));
				// finish();
				startActivity(comments);

			} else {
				if (li.getChildAt(position - li.getFirstVisiblePosition()) != null) {
					thisQuick.show(li.getChildAt(position
							- li.getFirstVisiblePosition()));
					thisQuick.setListViewPosition(position);
					thisQuick.setAnimStyle(QuickAction.ANIM_REFLECT);
				}
			}

			return false;
		}

		@Override
		public void onLongPress(MotionEvent ev) {
			ListView li = getListView();
			int pos = li.pointToPosition((int) ev.getX(), (int) ev.getY());
			if (li.getChildAt(pos - li.getFirstVisiblePosition()) != null) {
				thisQuick
						.show(li.getChildAt(pos - li.getFirstVisiblePosition()));
				thisQuick.setListViewPosition(pos);
				thisQuick.setAnimStyle(QuickAction.ANIM_REFLECT);
			}

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				ListView li = getListView();
				int po = li.pointToPosition((int) e1.getX(), (int) e1.getY());
				if (Math.abs(e1.getY() - e2.getY()) > swipeMaxOffPath)
					return false;
				// handled force close while loading - jc - may 21 2012
				if (returnAList.size() <= 0)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > swipeMinDistance
						&& Math.abs(velocityX) > swipeThresholdVelocity) {

					// adding swipe to vote as a preference - jc - may 10 2012
					if (redditSUPreferences.getBoolean("stv", false)) {
						if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
							Toast.makeText(FrontPageActivity.this,
									"log in for this action", Toast.LENGTH_LONG)
									.show();
						} else {
							if (returnList.get(po).get("vote").matches("0")) {
								returnList.get(po).put("vote", "-1");
								adapter.notifyDataSetChanged();

								asyncOperations = new AsynchronousOptions(
										FrontPageActivity.this, "t3_"
												+ returnList.get(po).get("id"),
										redditSUPreferences, "downvote");
								asyncOperations.execute(FrontPageActivity.this);
							} else {
								returnList.get(po).put("vote", "0");
								adapter.notifyDataSetChanged();

								asyncOperations = new AsynchronousOptions(
										FrontPageActivity.this, "t3_"
												+ returnList.get(po).get("id"),
										redditSUPreferences, "rescind");
								asyncOperations.execute(FrontPageActivity.this);
							}
						}

					} else {
						// open options menu
						if (li.getChildAt(po - li.getFirstVisiblePosition()) != null) {
							thisQuick.show(li.getChildAt(po
									- li.getFirstVisiblePosition()));
							thisQuick.setListViewPosition(po);
							thisQuick.setAnimStyle(QuickAction.ANIM_REFLECT);
						}
					}
				} else if (e2.getX() - e1.getX() > swipeMinDistance
						&& Math.abs(velocityX) > swipeThresholdVelocity) {

					// adding swipe to vote as a preference - jc - may 10 2012
					if (redditSUPreferences.getBoolean("stv", false)) {
						if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
							Toast.makeText(FrontPageActivity.this,
									"log in for this action", Toast.LENGTH_LONG)
									.show();
						} else {
							if (returnList.get(po).get("vote").matches("0")) {
								returnList.get(po).put("vote", "1");
								adapter.notifyDataSetChanged();

								asyncOperations = new AsynchronousOptions(
										FrontPageActivity.this, "t3_"
												+ returnList.get(po).get("id"),
										redditSUPreferences, "upvote");
								asyncOperations.execute(FrontPageActivity.this);
							}

							else {
								returnList.get(po).put("vote", "0");
								adapter.notifyDataSetChanged();

								asyncOperations = new AsynchronousOptions(
										FrontPageActivity.this, "t3_"
												+ returnList.get(po).get("id"),
										redditSUPreferences, "rescind");
								asyncOperations.execute(FrontPageActivity.this);
							}
						}

					} else {
						// open options menu - jc - may 10 2012
						if (li.getChildAt(po - li.getFirstVisiblePosition()) != null) {
							thisQuick.show(li.getChildAt(po
									- li.getFirstVisiblePosition()));
							thisQuick.setListViewPosition(po);
							thisQuick.setAnimStyle(QuickAction.ANIM_REFLECT);
						}
					}

				}
			} catch (Exception ex) {
				// just in case - jc
			}

			return false;
		}
	}

}