package com.l1fan.ane.ht;

import org.json.JSONException;
import org.json.JSONObject;
import org.mobile.newhtloginsdk.billing.GooglePay;
import org.mobile.newhtloginsdk.billing.GooglePay.IHPayHandler;
import org.mobile.newhtloginsdk.billing.IabResult;
import org.mobile.newhtloginsdk.billing.PurchaseInfo;
import org.mobile.newhtloginsdk.utils.LoginUtils;
import org.mobile.newhtloginsdk.utils.NetWorkUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;



public class GPActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setLayoutParams(new LayoutParams(-1, -1));
		tv.setGravity(Gravity.CENTER);
		tv.setText("正在跳转...");
		setContentView(tv);

		try {
			JSONObject data = new JSONObject(getIntent().getExtras().getString("payData"));
			pay(data);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
	}
	
	private void pay(JSONObject data) {
		String productid = data.optString(SDK.PID);
		String purchasePayload = data.optString(SDK.EXT);
		GooglePay.buyPurchase(this, productid, purchasePayload, new IHPayHandler() {

			@Override
			public void onIabPurchaseFinished(IabResult result,
					PurchaseInfo purchase) {
				if (result.isSuccess()) {
					GooglePay.consumePurchase(purchase);
					System.out.println("purchase is :"+purchase);
					NetWorkUtils.sendServer(SDK.mAppId, LoginUtils.getUid(GPActivity.this), SDK.mCooServer,SDK.mCooUid,purchase.getOriginalJson(), purchase.getSignature(), "");
					SDK.context.dispatchData(SDK.EVENT_PAY);
				}else{
					SDK.context.dispatchError(SDK.EVENT_PAY, ""+result.getMessage());
				}
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (GooglePay.onActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
		finish();
	}

}
