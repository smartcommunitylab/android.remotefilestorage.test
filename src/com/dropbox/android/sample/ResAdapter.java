package com.dropbox.android.sample;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.Filestorage;
import eu.trentorise.smartcampus.storage.model.Resource;

public class ResAdapter extends ArrayAdapter<String> {

	private Activity context;
	private int layoutResourceId;

	public ResAdapter(Activity context, int textViewResourceId) {
		super(context, textViewResourceId, new ArrayList<String>());
		this.context = context;
		layoutResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		DataHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new DataHolder();
			holder.res_name = (TextView) row.findViewById(R.id.resname);
			holder.res_delete = (Button) row.findViewById(R.id.button1);
			holder.res_view = (Button) row.findViewById(R.id.button3);
			holder.res_update = (Button) row.findViewById(R.id.button2);
			row.setTag(holder);
		} else
			holder = (DataHolder) row.getTag();

		String res = getItem(position);
		holder.res_name.setText(res);
		holder.res_delete.setOnClickListener(new ResDeleteOnClickListener(res));
		holder.res_view.setOnClickListener(new ResViewOnClickListener(res,
				context.findViewById(R.id.imageView1)));
		holder.res_update.setOnClickListener(new ResUpdateOnClickListener(res,
				null));
		return row;
	}

	static class DataHolder {
		TextView res_name;
		Button res_delete;
		Button res_update;
		Button res_view;
	}

	class ResDeleteOnClickListener implements OnClickListener {

		String res;

		public ResDeleteOnClickListener(String res) {
			this.res = res;
		}

		@Override
		public void onClick(View v) {
			Filestorage storage = new Filestorage(getContext(), "smartcampus");
			try {
				storage.deleteResource(Constants.AUTH_TOKEN,
						Constants.USER_ACCOUNT_ID, res);
				remove(res);
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

	class ResViewOnClickListener implements OnClickListener {

		String res;
		View img;

		public ResViewOnClickListener(String res, View img) {
			this.res = res;
			this.img = img;
		}

		@Override
		public void onClick(View v) {
			Filestorage storage = new Filestorage(getContext(), "smartcampus");
			try {
				Resource r = storage.getResource(Constants.AUTH_TOKEN, res);
				ImageView view = (ImageView) img;
				view.setImageBitmap(BitmapFactory.decodeByteArray(
						r.getContent(), 0, r.getContent().length));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			remove(res);
		}
	}

	class ResUpdateOnClickListener implements OnClickListener {

		String res;
		View img;

		public ResUpdateOnClickListener(String res, View img) {
			this.res = res;
			this.img = img;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			((MyActivity) context).rid = res;
			context.startActivityForResult(intent, 10000);
		}
	}
}
