package com.dropbox.android.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
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
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), AuthActivity.class);
				intent.putExtra(AuthActivity.EXTRA_INPUT_APPNAME,
						com.dropbox.android.sample.Constants.APPNAME);
				intent.putExtra(AuthActivity.EXTRA_INPUT_AUTHTOKEN,
						com.dropbox.android.sample.Constants.AUTH_TOKEN);
				intent.putExtra(AuthActivity.EXTRA_INPUT_ACCOUNTNAME,
						com.dropbox.android.sample.Constants.ACCOUNTNAME);
				intent.putExtra(AuthActivity.EXTRA_INPUT_APPACCOUNTID,
						com.dropbox.android.sample.Constants.APP_ACCOUNT_ID);
				intent.putExtra(AuthActivity.EXTRA_INPUT_STORAGETYPE,
						StorageType.DROPBOX);
				startActivityForResult(intent, 500);
			}
		});

		Button link2 = (Button) findViewById(R.id.auth_button2);

		link2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					List<AppAccount> appAccounts = filestorage.getAppAccounts();
					List<UserAccount> userAccounts = filestorage
							.getUserAccounts(com.dropbox.android.sample.Constants.AUTH_TOKEN);
				} catch (Exception e) {
					Log.e(getClass().getName(), e.getMessage());
				}
			}
		});

		filestorage = new Filestorage(getApplicationContext(),
				Constants.APPNAME);
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
				Bundle responseBundle = data
						.getBundleExtra(AuthActivity.BUNDLE_RESPONSE);
				UserAccount accountRegistered = responseBundle
						.getParcelable(AuthActivity.EXTRA_OUTPUT_USERACCOUNT);
				Log.i(getClass().getName(), "AUTHENTICATION OK"
						+ accountRegistered.getId());
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
