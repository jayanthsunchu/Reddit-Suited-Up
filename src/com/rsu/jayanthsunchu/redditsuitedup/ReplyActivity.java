package com.rsu.jayanthsunchu.redditsuitedup;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReplyActivity extends Activity {
	TextView txtCommentText;
	Button postComment;
	EditText commentReply;
	
	ReplyCommentAsyncTask replyTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.replylayout);
		setUpViews();
	}
	
	@Override
	protected void onPause() {
		super.onPause();

	}

	private void setUpViews() {
		txtCommentText = (TextView) findViewById(R.id.txtParentComment);
		commentReply = (EditText) findViewById(R.id.commentField);
		postComment = (Button) findViewById(R.id.submitComment);
		Bundle br = getIntent().getExtras();
		if (br != null) {
			txtCommentText.setText(Html.fromHtml(Mdown.getHtml(br
					.getString("text"))));
			postComment.setTag(br);
			postComment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle brr = (Bundle) v.getTag();
					if (commentReply.getText().toString().trim().matches("")) {
						Toast.makeText(v.getContext(),
								"Enter the text to post as a comment.",
								Toast.LENGTH_LONG).show();
					} else {
						replyTask = new ReplyCommentAsyncTask(v.getContext(),
								commentReply.getText().toString().trim(), brr
										.getString("id"), brr
										.getBoolean("flag"), ReplyActivity.this, brr);
						replyTask.execute(v.getContext());
					}
				}
			});
		}

	}
}
