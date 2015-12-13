package com.l1fan.ane.google;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.RemoteException;

import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	public void init() {
		InAppHelper.getHelper().context = this;
		InAppHelper.getHelper().bind();
	}

	public void pay() throws RemoteException, IllegalStateException, JSONException, SendIntentException {
		Activity activity = getActivity();
		Intent intent = new Intent(activity, InAppActivity.class);
		intent.putExtra("payData", getData());
		activity.startActivity(intent);
	}

	@Override
	public void dispose() {
		super.dispose();
		InAppHelper.getHelper().unbind();
	}
}
