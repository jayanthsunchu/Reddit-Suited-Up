package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FrontPageListAdapter extends BaseAdapter {
	ArrayList<HashMap<String, String>> returnArrayList;
	Activity context;
	LayoutInflater ourInflater;
	ImageManager imageManager;
	public int count = 1;

	public FrontPageListAdapter(Activity ctx,
			ArrayList<HashMap<String, String>> list) {
		this.context = ctx;

		this.returnArrayList = list;

		ourInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageManager = new ImageManager(ctx.getApplicationContext());
	}

	@Override
	public int getCount() {

		return returnArrayList.size();
	}

	@Override
	public Object getItem(int position) {

		return returnArrayList.get(position);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {

		ViewHolder vh;

		if (convertView == null) {
			convertView = ourInflater.inflate(R.layout.frontpagelistlayout,
					null);
			vh = new ViewHolder();
			vh.txt = (TextView) convertView.findViewById(R.id.txtTitle);
			vh.txtComments = (TextView) convertView.findViewById(R.id.comments);
			vh.txtVotes = (TextView) convertView.findViewById(R.id.votes);
			vh.imgPreview = (ImageView) convertView
					.findViewById(R.id.txtPreviewImage);
			vh.previewButton = (ImageView) convertView
					.findViewById(R.id.upButton);
			vh.previewLayout = (LinearLayout) convertView
					.findViewById(R.id.previewLayout);
			vh.previewButtonLayout = (LinearLayout) convertView
					.findViewById(R.id.previewButtonLayout);
			vh.gifView = (WebView) convertView.findViewById(R.id.gifView);

			vh.txtSelftext = (TextView) convertView
					.findViewById(R.id.txtSelfText);

			vh.txtprogressview = (TextView) convertView.findViewById(R.id.tV1);
			vh.webviewprogress = (ProgressBar) convertView
					.findViewById(R.id.pB1);
			vh.visitedLinkImage = (ImageView) convertView
					.findViewById(R.id.visitedLinkImage);
			vh.voteImage = (ImageView) convertView.findViewById(R.id.voteImage);
			convertView.setTag(vh);
		} else {

			vh = (ViewHolder) convertView.getTag();

		}

		if (returnArrayList.get(position).get("url").endsWith("png")
				|| returnArrayList.get(position).get("url").endsWith("jpg")
				|| returnArrayList.get(position).get("url").endsWith("jpeg")
				|| (returnArrayList.get(position).get("domain")
						.matches("imgur.com") && !returnArrayList.get(position)
						.get("url").contains("imgur.com/a"))
				|| returnArrayList.get(position).get("domain")
						.matches("quickmeme.com")
				|| returnArrayList.get(position).get("url").endsWith("gif")) {
			Drawable d = context.getResources().getDrawable(
					R.drawable.imagelinkgray);
			vh.previewButton.setBackgroundDrawable(d);
		} else if (!returnArrayList.get(position).get("selftext").matches("")) {
			Drawable d = context.getResources().getDrawable(
					R.drawable.textlinkgray);
			vh.previewButton.setBackgroundDrawable(d);

		} else {
			Drawable d = context.getResources().getDrawable(
					R.drawable.openlinkgray);
			vh.previewButton.setBackgroundDrawable(d);
		}

		if (returnArrayList.get(position).get("vote").matches("1")
				|| returnArrayList.get(position).get("likes").matches("true")) {
			vh.voteImage.setBackgroundResource(0);
			vh.voteImage.setBackgroundResource(R.drawable.upvoteg);
			vh.voteImage.setVisibility(0);
			// vh.txt.setBackgroundResource(R.drawable.orangelayout);
		} else if (returnArrayList.get(position).get("vote").matches("-1")
				|| returnArrayList.get(position).get("likes").matches("false")) {
			vh.voteImage.setBackgroundResource(0);
			vh.voteImage.setBackgroundResource(R.drawable.downvoteg);
			vh.voteImage.setVisibility(0);

			// vh.txt.setBackgroundResource(R.drawable.bluelayout);
		} else if (returnArrayList.get(position).get("vote").matches("0")
				|| returnArrayList.get(position).get("likes").matches("null")) {
			vh.voteImage.setBackgroundResource(0);

			vh.voteImage.setVisibility(8);

			// vh.txt.setBackgroundResource(0);
		} else {
			vh.voteImage.setBackgroundResource(0);

			vh.voteImage.setVisibility(8);
			// vh.txt.setBackgroundResource(0);
		}

		vh.txt.setText(returnArrayList.get(position).get("title"));

		if (returnArrayList.get(position).get("clicked").matches("true")) {
			vh.visitedLinkImage.setVisibility(0);
		} else {
			vh.visitedLinkImage.setVisibility(8);
		}

		// DateUtils for getting relative time span - i love it - May 08 2012 -
		// JC

		vh.txtComments.setText(returnArrayList.get(position).get("subreddit")
				+ "|"
				+ returnArrayList.get(position).get("num_comments")
				+ " comments"
				+ "|"
				+ returnArrayList.get(position).get("author")
				+ "|"
				+ DateUtils.getRelativeTimeSpanString((long) Float
						.parseFloat(returnArrayList.get(position).get(
								"created_utc")) * 1000));

		if (returnArrayList.get(position).get("saved").matches("true")) {
			if (returnArrayList.get(position).get("over_18").matches("true"))
				vh.txtVotes.setText(Html.fromHtml(returnArrayList.get(position)
						.get("score")
						+ "("
						+ returnArrayList.get(position).get("ups")
						+ "|"
						+ returnArrayList.get(position).get("downs")
						+ ")"
						+ " | "
						+ "(saved)"
						+ " | "
						+ "<font color='#FF0000'>NSFW</font>"));
			else
				vh.txtVotes.setText(returnArrayList.get(position).get("score")
						+ "(" + returnArrayList.get(position).get("ups") + "|"
						+ returnArrayList.get(position).get("downs") + ")"
						+ " | " + "(saved)");
		} else {
			if (returnArrayList.get(position).get("over_18").matches("true"))
				vh.txtVotes.setText(Html.fromHtml(returnArrayList.get(position)
						.get("score")
						+ "("
						+ returnArrayList.get(position).get("ups")
						+ "|"
						+ returnArrayList.get(position).get("downs")
						+ ")"
						+ " | " + "<font color='#FF0000'>NSFW</font>"));
			else
				vh.txtVotes.setText(returnArrayList.get(position).get("score")
						+ "(" + returnArrayList.get(position).get("ups") + "|"
						+ returnArrayList.get(position).get("downs") + ")");
		}

		vh.curl = returnArrayList.get(position).get("url");
		vh.currentDomain = returnArrayList.get(position).get("domain");
		vh.selfText = returnArrayList.get(position).get("selftext");
		vh.selfTextHtml = returnArrayList.get(position).get("selftext_html");
		if (!returnArrayList.get(position).get("selftext_html").matches("null")) {

			vh.txtSelftext.setText(Html.fromHtml(returnArrayList.get(position)
					.get("selftext")));

		} else {
			vh.txtSelftext.setText("");
		}
		vh.currentPosition = position;
		vh.previewButton.setTag(vh);
		vh.previewButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewHolder holderOnClick = (ViewHolder) v.getTag();
				if (holderOnClick.previewLayout.getVisibility() == 8) {
					if (holderOnClick.curl.endsWith("png")
							|| holderOnClick.curl.endsWith("jpg")
							|| holderOnClick.curl.endsWith("jpeg")
							|| (holderOnClick.currentDomain
									.matches("imgur.com") && !holderOnClick.curl
									.contains("imgur.com/a"))
							|| holderOnClick.currentDomain
									.matches("quickmeme.com")) {
						holderOnClick.gifView.clearHistory();
						holderOnClick.gifView.clearView();
						holderOnClick.gifView.setVisibility(8);// To clear the
															// webview before
															// loading another
															// one.
						if (holderOnClick.currentDomain.matches("imgur.com")
								&& holderOnClick.curl.contains("imgur.com/a")) {
							// Imgur Galleries Opening as a link.
							returnArrayList.get(holderOnClick.currentPosition)
									.put("clicked", "true");
							FrontPageListAdapter.this.notifyDataSetChanged();
							Intent comments = new Intent(v.getContext(),
									CommentsAndLink.class);
							comments.putExtra("url", holderOnClick.curl);
							comments.putExtra(
									"id",
									returnArrayList.get(
											holderOnClick.currentPosition).get(
											"id"));
							comments.putExtra(
									"author",
									returnArrayList.get(
											holderOnClick.currentPosition).get(
											"author"));
							comments.putExtra(
									"selftext",
									returnArrayList.get(
											holderOnClick.currentPosition).get(
											"selftextun"));
							comments.putExtra(
									"title",
									returnArrayList.get(
											holderOnClick.currentPosition).get(
											"title"));
							comments.putExtra(
									"score",
									returnArrayList.get(
											holderOnClick.currentPosition).get(
											"score"));
							comments.putExtra(
									"saved",
									returnArrayList.get(
											holderOnClick.currentPosition).get(
											"saved"));
							comments.putExtra(
									"vote",
									returnArrayList.get(
											holderOnClick.currentPosition).get(
											"vote"));
							// finish();
							v.getContext().startActivity(comments);
						} else if (holderOnClick.currentDomain
								.matches("imgur.com")
								&& !holderOnClick.curl.contains("imgur.com/a")) {
							holderOnClick.imgPreview.setTag(holderOnClick.curl
									+ ".png");
							imageManager
									.displayImage(holderOnClick.curl + ".png",
											context, holderOnClick.imgPreview);
							holderOnClick.previewLayout.setVisibility(0);
						} else if (holderOnClick.currentDomain
								.matches("quickmeme.com")) {
							String[] splitUrl = holderOnClick.curl.split("/");

							holderOnClick.imgPreview.setTag("http://i.qkme.me/"
									+ splitUrl[splitUrl.length - 1].replace(
											"/", "") + ".jpg");
							imageManager.displayImage(
									"http://i.qkme.me/"
											+ splitUrl[splitUrl.length - 1]
													.replace("/", "") + ".jpg",
									context, holderOnClick.imgPreview);
							holderOnClick.previewLayout.setVisibility(0);
						} else {
							holderOnClick.imgPreview.setTag(holderOnClick.curl);
							imageManager.displayImage(holderOnClick.curl,
									context, holderOnClick.imgPreview);
							holderOnClick.previewLayout.setVisibility(0);
						}
						holderOnClick.imgPreview.setVisibility(0);
					} else if (!holderOnClick.selfText.matches("")) {
						holderOnClick.gifView.clearHistory();
						holderOnClick.gifView.clearView();
						holderOnClick.gifView.setVisibility(8);// To clear the
															// webview before
															// loading another
															// one.
						holderOnClick.previewLayout.setVisibility(0);
						holderOnClick.txtSelftext.setVisibility(0);
						holderOnClick.imgPreview.setVisibility(8);
					} else if (holderOnClick.curl.endsWith("gif")) {
						// handled progress bar, clearing cache, disable zoom -
						// May 08 2012 - JC
						// Need to work on a better progress bar
						holderOnClick.gifView.clearHistory();
						holderOnClick.gifView.clearView(); // To clear the
															// webview before
															// loading another
															// one.
						holderOnClick.imgPreview.setVisibility(8);
						// ProgressDialog webViewProgress = new ProgressDialog(v
						// .getContext());
						//
						// webViewProgress.setCancelable(false);
						// webViewProgress.setMessage("loading gif");
						// webViewProgress.show();
						holderOnClick.previewLayout.setVisibility(0);
						holderOnClick.gifView.getSettings()
								.setLoadWithOverviewMode(true);
						holderOnClick.gifView.getSettings().setUseWideViewPort(
								true);

						holderOnClick.gifView.loadUrl(holderOnClick.curl);
						holderOnClick.gifView.setVisibility(0);
						holderOnClick.gifView.setTag(holderOnClick);
						holderOnClick.gifView.getSettings()
								.setLoadWithOverviewMode(true);
						holderOnClick.gifView.getSettings().setUseWideViewPort(
								false);
						holderOnClick.gifView.getSettings()
								.setBuiltInZoomControls(false);
						// holderOnClick.gifView.setOnTouchListener(null);
						// holderOnClick.gifView
						// .setWebViewClient(new WebViewClient() {
						// @Override
						// public void onPageFinished(WebView view,
						// String url) {
						// ViewHolder pd = (ViewHolder) view
						// .getTag();
						// super.onPageFinished(view, url);
						// if (pd.isShowing())
						// pd.dismiss();
						// view.setVisibility(0);
						//
						// }
						// });
						// handling webview progress bar better - jc - may 20
						// 2012
						holderOnClick.gifView
								.setWebChromeClient(new WebChromeClient() {
									public void onProgressChanged(WebView view,
											int progress) {
										ViewHolder pd = (ViewHolder) view
												.getTag();
										if (progress < 100
												&& pd.webviewprogress
														.getVisibility() == ProgressBar.GONE) {
											pd.webviewprogress
													.setVisibility(ProgressBar.VISIBLE);
											pd.txtprogressview
													.setVisibility(View.VISIBLE);
										}
										pd.webviewprogress
												.setProgress(progress);
										if (progress == 100) {
											pd.webviewprogress
													.setVisibility(ProgressBar.GONE);
											pd.txtprogressview
													.setVisibility(View.GONE);
										}
									}
								});
					} else {

						returnArrayList.get(holderOnClick.currentPosition).put(
								"clicked", "true");
						FrontPageListAdapter.this.notifyDataSetChanged();
						Intent comments = new Intent(v.getContext(),
								CommentsAndLink.class);
						comments.putExtra("url", holderOnClick.curl);
						comments.putExtra(
								"id",
								returnArrayList.get(
										holderOnClick.currentPosition)
										.get("id"));
						comments.putExtra(
								"author",
								returnArrayList.get(
										holderOnClick.currentPosition).get(
										"author"));
						comments.putExtra(
								"selftext",
								returnArrayList.get(
										holderOnClick.currentPosition).get(
										"selftextun"));
						comments.putExtra(
								"title",
								returnArrayList.get(
										holderOnClick.currentPosition).get(
										"title"));
						comments.putExtra(
								"score",
								returnArrayList.get(
										holderOnClick.currentPosition).get(
										"score"));
						comments.putExtra(
								"saved",
								returnArrayList.get(
										holderOnClick.currentPosition).get(
										"saved"));
						comments.putExtra(
								"vote",
								returnArrayList.get(
										holderOnClick.currentPosition).get(
										"vote"));
						// finish();
						v.getContext().startActivity(comments);
					}
				} else {
					holderOnClick.previewLayout.setVisibility(8);
					holderOnClick.txtSelftext.setVisibility(8);
					holderOnClick.imgPreview.setVisibility(8);
					holderOnClick.gifView.setVisibility(8);

				}

			}

		});

		// if (returnArrayList.get(position).get("url").endsWith("png")
		// || returnArrayList.get(position).get("url").endsWith("jpg")
		// || returnArrayList.get(position).get("url").endsWith("jpeg")) {
		// vh.imgPreview.setTag(returnArrayList.get(position).get("url"));
		// imageManager.displayImage(returnArrayList.get(position).get("url"),
		// context, vh.imgPreview);
		//
		// } else {
		// vh.imgPreview.setImageResource(R.drawable.link);
		//
		// }

		if (count == 0) {
			vh.previewLayout.setVisibility(8);

		} else {
			vh.previewLayout.setVisibility(0);
		}

		return convertView;
	}

	public class ViewHolder {
		TextView txt;
		TextView txtComments;
		TextView txtVotes;
		ImageView imgPreview;
		ImageView previewButton;
		LinearLayout previewLayout;
		String curl;
		LinearLayout previewButtonLayout;
		String currentDomain;
		String selfText;
		TextView txtSelftext;
		String selfTextHtml;
		int currentPosition;
		WebView gifView;
		// Added progress bar - may 20 2012 - jc
		ProgressBar webviewprogress;
		TextView txtprogressview;
		ImageView visitedLinkImage;
		ImageView voteImage;
	}

}
