package com.rsu.jayanthsunchu.redditsuitedup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class AsynchronousOptions extends AsyncTask<Context, Integer, String> {
	Context ctx;
	String idForOperation;
	SharedPreferences sh;
	String operation;
	String vote = "";

	public AsynchronousOptions(Context act, String list, SharedPreferences sha,
			String op) {
		this.ctx = act;
		this.idForOperation = list;
		this.sh = sha;
		this.operation = op;

	}

	@Override
	protected String doInBackground(Context... arg0) {
		if (operation.matches("upvote")) {
			vote = FrontPageActivity.postVote(idForOperation, "1", sh);

		} else if (operation.matches("downvote")) {
			vote = FrontPageActivity.postVote(idForOperation, "-1", sh);
		} else if (operation.matches("rescind")) {
			vote = FrontPageActivity.postVote(idForOperation, "0", sh);
		} else if (operation.matches("save")) {
			vote = FrontPageActivity.saveOrUnsave(idForOperation,
					Constants.CONST_SAVE_URL, sh);
		} else if (operation.matches("unsave")) {
			vote = FrontPageActivity.saveOrUnsave(idForOperation,
					Constants.CONST_UNSAVE_URL, sh);
		}
		return "complete";
	}

	protected void onPostExecute(String result) {

	}

	protected void onPreExecute() {

	}

}
