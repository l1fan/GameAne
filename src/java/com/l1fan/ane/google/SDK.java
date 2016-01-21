package com.l1fan.ane.google;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.RemoteException;

import com.google.ads.conversiontracking.AdWordsAutomatedUsageReporter;
import com.google.ads.conversiontracking.AdWordsConversionReporter;
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

	public void track() throws JSONException{
		JSONObject track = getJsonData();
		String cid = track.optString("conversionId");
		AdWordsAutomatedUsageReporter.enableAutomatedUsageReporting(getActivity(), cid);
		AdWordsConversionReporter.reportWithConversionId(getActivity(),
				cid, track.optString("label"), track.optString("value"),track.optString("currencyCode"), track.optBoolean("isRepeatable"));
	}

	@Override
	public void dispose() {
		super.dispose();
		InAppHelper.getHelper().unbind();
	}
}
