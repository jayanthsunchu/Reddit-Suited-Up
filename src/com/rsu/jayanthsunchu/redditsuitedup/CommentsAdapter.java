package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.Visibility;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rsu.jayanthsunchu.redditsuitedup.QuickAction.OnActionItemClickListener;

public class CommentsAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int COMMENTUPVOTE = 0;
	private int COMMENTDOWNVOTE = 1;
	private int COMMENTUSER = 2;
	private int COMMENTREPLY = 3;
	private int TOGGLE = 4;
	Node<HashMap<String, String>> arrayList2;
	ArrayList<HashMap<String, String>> arrayList;
	ViewHolder currentViewHolder;
	public Activity context;
	Random ranGenerator = new Random();
	int checking = 1;
	AsynchronousOptions asyncOperations;
	final QuickAction quickAction;
	ArrayList<LinearLayout> linearList = new ArrayList<LinearLayout>();
	SharedPreferences redditSUPreferences;
	public final String postAuthor;

	public CommentsAdapter(Activity context,
			ArrayList<HashMap<String, String>> arrayList,
			Node<HashMap<String, String>> arrayList2, String a) {
		super();
		this.postAuthor = a;
		this.context = context;

		this.arrayList = arrayList;

		this.arrayList2 = arrayList2;

		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.linearList = loadAllLayouts();
		redditSUPreferences = context.getSharedPreferences(
				Constants.PREFS_NAME, 0);
		ActionItem nextItem = new ActionItem(COMMENTUPVOTE, "", context
				.getResources().getDrawable(R.drawable.upvotewhite));
		ActionItem prevItem = new ActionItem(COMMENTUSER, "user", context
				.getResources().getDrawable(R.drawable.userwhite));

		ActionItem infoItem = new ActionItem(COMMENTREPLY, "reply", context
				.getResources().getDrawable(R.drawable.reply));
		ActionItem eraseItem = new ActionItem(COMMENTDOWNVOTE, "", context
				.getResources().getDrawable(R.drawable.downvotewhite));
		
		ActionItem toggleVisibility = new ActionItem(TOGGLE, "", context.getResources().getDrawable(R.drawable.hideorshow));

		// create QuickAction. Use QuickAction.VERTICAL or
		// QuickAction.HORIZONTAL param to define layout
		// orientation

		quickAction = new QuickAction(context, QuickAction.HORIZONTAL, true);

		quickAction.addActionItem(nextItem);
		quickAction.addActionItem(prevItem);

		quickAction.addActionItem(infoItem);
		quickAction.addActionItem(eraseItem);
		quickAction.addActionItem(toggleVisibility);

		quickAction.setOnActionItemClickListener(actionItemClick);

	}

	private OnActionItemClickListener actionItemClick = new OnActionItemClickListener() {

		@Override
		public void onItemClick(QuickAction source, int pos, int actionId) {
			if (actionId == COMMENTUPVOTE) {
				if (source.getRespectiveView() != null) {
					if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
						Toast.makeText(context, "log in for this action",
								Toast.LENGTH_LONG).show();
					} else {
						TextView txtSource = (TextView) source
								.getRespectiveView();
						// added for a better upvote/downvote graphic - jc - may
						// 28 2012
						BitmapDrawable dd = (BitmapDrawable) context
								.getResources().getDrawable(R.drawable.upvoteg);
						dd.setBounds(0, 0, txtSource.getWidth(),
								dd.getIntrinsicHeight());

						dd.setGravity(Gravity.LEFT);

						txtSource.setCompoundDrawables(null, dd, null, null);

						// txtSource
						// .setBackgroundResource(R.drawable.orangelayout);
						asyncOperations = new AsynchronousOptions(source
								.getRespectiveView().getContext(), "t1_"
								+ source.getCommentId(), source
								.getRespectiveView().getContext()
								.getSharedPreferences(Constants.PREFS_NAME, 0),
								"upvote");
						asyncOperations.execute(source.getRespectiveView()
								.getContext());
					}
				}
			}

			else if (actionId == COMMENTDOWNVOTE) {
				if (source.getRespectiveView() != null) {
					if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
						Toast.makeText(context, "log in for this action",
								Toast.LENGTH_LONG).show();
					} else {
						TextView txtSource = (TextView) source
								.getRespectiveView();
						// Bitmap b =
						// BitmapFactory.decodeResource(context.getResources(),
						// context.getResources().getDrawable(R.drawable.downvoteg));
						// added for a better upvote/downvote graphic - jc - may
						// 28 2012
						BitmapDrawable dd = (BitmapDrawable) context
								.getResources().getDrawable(
										R.drawable.downvoteg);
						dd.setBounds(0, 0, txtSource.getWidth(),
								dd.getIntrinsicHeight());

						dd.setGravity(Gravity.LEFT);

						txtSource.setCompoundDrawables(null, dd, null, null);
						// txtSource.setBackgroundResource(R.drawable.bluelayout);
						asyncOperations = new AsynchronousOptions(source
								.getRespectiveView().getContext(), "t1_"
								+ source.getCommentId(), source
								.getRespectiveView().getContext()
								.getSharedPreferences(Constants.PREFS_NAME, 0),
								"downvote");
						asyncOperations.execute(source.getRespectiveView()
								.getContext());
					}
				}
			}

			else if (actionId == COMMENTUSER) {
				if (source.getRespectiveView() != null) {
					Intent profile = new Intent(source.getRespectiveView()
							.getContext(), ProfileActivity.class);
					profile.putExtra("username", source.getCommentAuthor()
							.trim());

					source.getRespectiveView().getContext()
							.startActivity(profile);
				}
			}

			else if (actionId == COMMENTREPLY) {
				if (source.getRespectiveView() != null) {
					// Toast.makeText(
					// source.getRespectiveView().getContext(),
					// "Feature yet to be added. Please check for the updates.",
					// Toast.LENGTH_LONG).show();
					if (redditSUPreferences.getInt("loggedinornot", 0) == 0) {
						Toast.makeText(context, "log in for this action",
								Toast.LENGTH_LONG).show();
					} else {
						Intent in = new Intent(context, ReplyActivity.class);
						in.putExtra("id", "t1_" + source.getCommentId());
						in.putExtra("text", source.getCommentText());
						in.putExtra("flag", false);

						context.startActivityForResult(in,
								Constants.CONST_REFRESH_COMMENT_CODE);
						context.overridePendingTransition(
								R.anim.slide_top_to_bottom,
								R.anim.shrink_from_top);
					}
				}
			}
			else if(actionId == TOGGLE){
				if(source.getRespectiveView() != null){
					TextView current = (TextView)source.getRespectiveView();
					LinearLayout linCurrent = (LinearLayout)current.getParent();
					LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) linCurrent
							.getLayoutParams();
					if (lp.height != 30) {
						
				
						linCurrent.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT, 30));
					} else {
						
						linCurrent.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT));

					}
				}
			}
		}

	};

	public ArrayList<LinearLayout> loadAllLayouts() {
		ArrayList<LinearLayout> returnList = new ArrayList<LinearLayout>();

		List<Node<HashMap<String, String>>> presentList = arrayList2
				.getChildren();
		for (int i = 0; i < arrayList2.getNumberOfChildren(); i++) {

			TextView txt = new TextView(context);

			LinearLayout presentLayout = new LinearLayout(context);
			presentLayout.setOrientation(1);

			// presentLayout.setBackgroundResource(R.drawable.rounded_corners);
			presentLayout.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			txt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			// txt.setTextColor(Color.WHITE);
			// Handling Op's comments - jc - may 16 2012
			String author = "";

			if (arrayList.get(i).get("author").trim()
					.matches(postAuthor.trim()))
				author = "<font color='#0000FF'><b>"
						+ arrayList.get(i).get("author") + "</b></font>";
			else
				author = "<font color='#686868'><b>"
						+ arrayList.get(i).get("author") + "</b></font>";

			String returnText = author + " " + "<font color='#686868'><b>"
					+ "(" + arrayList.get(i).get("ups") + "|"
					+ arrayList.get(i).get("downs") + ")" + "</b></font>"
					+ "<br></br>" + arrayList.get(i).get("body");
			txt.setText(Html.fromHtml(returnText));
			txt.setTag(new String[] { arrayList.get(i).get("id"),
					arrayList.get(i).get("links"),
					arrayList.get(i).get("author") });

			txt.setOnClickListener(commentClickListener);
			//txt.setOnLongClickListener(toggleCommentListener);
			presentLayout.addView(txt);

			presentLayout = recursiveFuckFunction(presentList.get(i)
					.getChildren(), presentLayout, i, null, 5, txt);

			returnList.add(presentLayout);
		}
		return returnList;
	}

	private OnClickListener commentClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String[] tag = (String[]) v.getTag();
			quickAction.setLinkList(tag[1], context);
			quickAction.setCommmentId(tag[0]);
			quickAction.setCommentAuthor(tag[2]);
			quickAction.show(v);
			quickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
		}

	};

	private OnLongClickListener toggleCommentListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			TextView current = (TextView) v;
			LinearLayout linCurrent = (LinearLayout) current.getParent();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) linCurrent
					.getLayoutParams();
			if (lp.height != 35) {

				linCurrent.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT, 35));
			} else {

				linCurrent.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));

			}
			

			return false;
		}

	};

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.commentsadapterlayout,
					null);

			holder = new ViewHolder();

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}
		try {
			holder.text = new LinearLayout(context);

			holder.text = (LinearLayout) convertView
					.findViewById(R.id.mainCommentLayout);

			holder.text.removeAllViews();

			holder.text.addView(linearList.get(position));
		} catch (IllegalStateException ex) {
			holder.text.removeAllViews();
		}

		return convertView;
	}

	public LinearLayout recursiveFuckFunction(
			List<Node<HashMap<String, String>>> node,
			LinearLayout presentLayout, int position, ViewHolder holder,
			int padding, TextView text) {

		if (node.size() > 0) {
			// lp.addRule(LinearLayout.BELOW, text.getId());

			for (int i = 0; i < node.size(); i++) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

				LinearLayout currentLayout = new LinearLayout(context);
				currentLayout.setLayoutParams(lp);
				currentLayout.setOrientation(1);

				Node<HashMap<String, String>> currentNode = node.get(i);

				// currentLayout.setLayoutParams(lp2);
				TextView txt = new TextView(context);
				txt.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
				// txt.setTextColor(Color.BLACK);

				if (currentNode.getData().containsKey("body")) {

					// Handling Op's comments - jc - may 16 2012
					String author = "";

					if (currentNode.getData().get("author").trim()
							.matches(postAuthor))
						author = "<font color='#0000FF'><b>"
								+ currentNode.getData().get("author")
								+ "</b></font>";
					else
						author = "<font color='#686868'><b>"
								+ currentNode.getData().get("author")
								+ "</b></font>";

					String returnText = author + " "
							+ "<font color='#686868'><b>" + "("
							+ currentNode.getData().get("ups") + "|"
							+ currentNode.getData().get("downs") + ")"
							+ "</b></font>" + "<br></br>"
							+ currentNode.getData().get("body");
					txt.setText(Html.fromHtml(returnText));
				} else
					txt.setText("nothing here");
				// txt.setId(i+1);
				txt.setPadding(10 + padding, 0, 0, 0);
				txt.setTag(new String[] { currentNode.getData().get("id"),
						currentNode.getData().get("links"),
						currentNode.getData().get("author") });

				txt.setOnClickListener(commentClickListener);
				//txt.setOnLongClickListener(toggleCommentListener);
				currentLayout.addView(txt);
				// currentLayout.setId(ranGenerator.nextInt());
				if (currentNode.getNumberOfChildren() > 0) {
					currentLayout = recursiveFuckFunction(
							currentNode.getChildren(), currentLayout, 0, null,
							padding + 5, txt);

				}

				// presentLayout.addView(currentLayout, i);
				presentLayout.addView(currentLayout);
				// currentLayout.setOnClickListener(commentClickListener);

			}

		}

		return presentLayout;

	}

	static class ViewHolder {
		LinearLayout text;
	}

}
