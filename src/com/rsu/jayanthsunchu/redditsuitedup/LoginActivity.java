package com.rsu.jayanthsunchu.redditsuitedup;

import java.util.ArrayList;
import java.util.HashMap;

import com.rsu.jayanthsunchu.redditsuitedup.R;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LoginActivity extends Activity {
	EditText txtUserName;
	EditText txtPassWord;
	Button loginButton;
	RedditorDB reddDb;
	SharedPreferences redditShares;
	SharedPreferences.Editor redditShEditor;

	ArrayAdapter<String> accountList;
	AuthenticatePage authPage;
	public ProgressDialog progressDialog;
	ArrayList<HashMap<String, String>> accountsListMap = new ArrayList<HashMap<String, String>>();
	AccountListAdapter accountsListAdapter;

	String[] noAccounts = { "No Accounts Added" };
	String[] accountFromDb = new String[] {};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (reddDb != null)
			reddDb.closeDb();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		redditShares = this.getSharedPreferences(Constants.PREFS_NAME, 0);
		redditShEditor = redditShares.edit();
		reddDb = new RedditorDB(this);
		setContentView(R.layout.login_dialog);
		progressDialog = new ProgressDialog(LoginActivity.this);
		ListView accounts = (ListView) findViewById(R.id.accountsList);
		if (redditShares.getInt("loggedinornot", 0) == 0) {
			accountList = new ArrayAdapter<String>(this,
					R.layout.accountlistitem, noAccounts);
			accounts.setAdapter(accountList);
		} else {

			accountsListAdapter = new AccountListAdapter(LoginActivity.this,
					accountsListMap);
			accounts.setAdapter(accountsListAdapter);
			accountFromDb = reddDb.getAllRows();
			for (int i = 0; i < accountFromDb.length; i++) {
				String[] splitText = accountFromDb[i].split(",");
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("name", splitText[0]);
				hashMap.put("current", splitText[1]);

				accountsListMap.add(hashMap);
				accountsListAdapter.notifyDataSetChanged();

			}

		}
		accounts.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {

				reddDb.removeDefaultsAndUpdate(accountsListMap.get(position)
						.get("name").trim());
				redditShEditor.putInt("changedornot", 1);
				redditShEditor.commit();
				if (reddDb.getCount() > 0) {

					String[] current = reddDb.getCurrentUser();

					// currentCookie = current[1].trim();

					redditShEditor.putString("currentusername",
							current[0].trim());
					redditShEditor.commit();
					redditShEditor.putString("redditsession", current[1].trim());
					redditShEditor.commit();
					redditShEditor.putString("usermodhash", current[2].trim());
					redditShEditor.commit();
				}
				setResult(Constants.CONST_RESULT_CODE2);
				finish();

			}

		});

		txtUserName = (EditText) findViewById(R.id.userNameField);

		txtPassWord = (EditText) findViewById(R.id.passWordField);
		loginButton = (Button) findViewById(R.id.loginButton);

		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!txtUserName.getText().toString().trim().matches("")
						&& !txtPassWord.getText().toString().trim().matches("")) {
					int existingCount = reddDb
							.checkExistingUserNames(txtUserName.getText()
									.toString().trim());
					if (existingCount == 0) {
						//Added condition to check for spaces in the username/password which is causing
						//force closes - jc - May 16 2012
						if (txtUserName.getText().toString().trim()
								.matches(".*\\s+.*")
								|| txtPassWord.getText().toString().trim()
										.matches(".*\\s+.*")) {
							Toast.makeText(v.getContext(),
									"Remove the spaces in Username/Password fields.",
									Toast.LENGTH_LONG).show();
						} else {
							authPage = new AuthenticatePage(txtUserName
									.getText().toString().trim(), txtPassWord
									.getText().toString().trim(), reddDb,
									LoginActivity.this, progressDialog);
							authPage.execute(LoginActivity.this);
						}
					} else {
						txtUserName.setText("");
						txtPassWord.setText("");
						Toast.makeText(v.getContext(),
								"Existing Account, Choose from below.",
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(v.getContext(),
							"Please enter username and password.",
							Toast.LENGTH_LONG).show();
				}
			}

		});
	}

}
