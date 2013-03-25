/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package com.dropbox.android.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.AuthActivity;
import eu.trentorise.smartcampus.storage.Filestorage;
import eu.trentorise.smartcampus.storage.model.AppAccount;
import eu.trentorise.smartcampus.storage.model.StorageType;
import eu.trentorise.smartcampus.storage.model.UserAccount;

public class MyActivity extends Activity {

	private Filestorage filestorage;

	private ResAdapter resAdapter;
	private AppAccount appAccount;

	public String rid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		ListView view = (ListView) findViewById(R.id.topic_listview);
		resAdapter = new ResAdapter(this, R.layout.res);
		view.setAdapter(resAdapter);
		Button link = (Button) findViewById(R.id.auth_button);
		link.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 200);
			}
		});

		Button link1 = (Button) findViewById(R.id.auth_button1);
		link1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (appAccount == null) {
					Toast.makeText(MyActivity.this, "Read account first!", Toast.LENGTH_LONG).show();
					return;
				}
				filestorage.startAuthActivityForResult(
						MyActivity.this, 
						com.dropbox.android.sample.Constants.AUTH_TOKEN,
						com.dropbox.android.sample.Constants.ACCOUNTNAME,
						appAccount.getId(),
						StorageType.DROPBOX,
						500);
			}
		});

		Button link2 = (Button) findViewById(R.id.auth_button2);

		link2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AsyncTask<Void, Void, List<AppAccount>>() {
					@Override
					protected List<AppAccount> doInBackground(Void... params) {
						try {
							return filestorage.getAppAccounts(com.dropbox.android.sample.Constants.AUTH_TOKEN);
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					}
					@Override
					protected void onPostExecute(List<AppAccount> result) {
						if (result != null && result.size() > 0) {
							appAccount = result.get(0);
							Toast.makeText(MyActivity.this, "Account: "+appAccount.getAppAccountName(), Toast.LENGTH_LONG).show();
						}
						Toast.makeText(MyActivity.this, "No Accounts!", Toast.LENGTH_LONG).show();
					}
				}.execute();
			}
		});

		filestorage = new Filestorage(getApplicationContext(), Constants.APPNAME, Constants.APPTOKEN, Constants.HOST, Constants.SERVICE);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 500) {
			if (resultCode == Activity.RESULT_OK) {
				UserAccount accountRegistered = data.getParcelableExtra(Filestorage.EXTRA_OUTPUT_USERACCOUNT);
				Log.i(getClass().getName(), "AUTHENTICATION OK" + accountRegistered.getId());
			} else {
				Log.i(getClass().getName(), "AUTHENTICATION KO");
			}
		}

		if (requestCode == 10000) {
			if (resultCode == Activity.RESULT_OK) {
				Bitmap image = (Bitmap) data.getExtras().get("data");

				ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.JPEG, 100,
						byteArrayBitmapStream);
				try {
					filestorage
							.updateResource(
									com.dropbox.android.sample.Constants.AUTH_TOKEN,
									com.dropbox.android.sample.Constants.USER_ACCOUNT_ID,
									rid, byteArrayBitmapStream.toByteArray());
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (requestCode == 200) {
			if (resultCode == Activity.RESULT_OK) {
				try {
					Bitmap image = (Bitmap) data.getExtras().get("data");

					ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
					image.compress(Bitmap.CompressFormat.JPEG, 100,
							byteArrayBitmapStream);

					String rid = filestorage
							.storeResource(
									byteArrayBitmapStream.toByteArray(),
									"image/jpg",
									"image" + System.currentTimeMillis()
											+ ".jpg",
									com.dropbox.android.sample.Constants.AUTH_TOKEN,
									com.dropbox.android.sample.Constants.USER_ACCOUNT_ID);

					resAdapter.add(rid);
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ConnectionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
