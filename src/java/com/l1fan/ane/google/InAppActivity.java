package com.l1fan.ane.google;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.l1fan.ane.SDKContext;

public class InAppActivity extends Activity {

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
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (SendIntentException e) {
			e.printStackTrace();
		}

	}

	private void pay(final JSONObject pay) throws RemoteException, SendIntentException {
		System.out.println("starting... pay");
		test(pay);
		System.out.println("end... pay");
	}

	private void test(final JSONObject pay) throws RemoteException, SendIntentException {
		String pid = pay.optString(SDKContext.PID);
		String ext = pay.optString(SDKContext.EXT);

		Bundle buyIntentBundle = InAppHelper.getHelper().billService.getBuyIntent(3, getPackageName(), pid, "inapp",
				ext);
		System.out.println("buy intent bundle:" + buyIntentBundle + ",resp code:"
				+ buyIntentBundle.getInt("RESPONSE_CODE"));
		PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
		System.out.println("pending intent" + pendingIntent);
		startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0),
				Integer.valueOf(0), Integer.valueOf(0));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("activity result :" + resultCode);
		Bundle b = data.getExtras();
		for (String key : b.keySet()) {
			System.out.println("bundle data:" + key + ":" + b.get(key));
		}

		if (requestCode != 1001)
			return;
		SDKContext sdk = InAppHelper.getHelper().context;

		int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
		String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
		String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

		if (resultCode == Activity.RESULT_OK && responseCode == 0) {
			System.out.println("Successful resultcode from purchase activity.");
			System.out.println("Purchase data: " + purchaseData);
			System.out.println("Data signature: " + dataSignature);
			System.out.println("Extras: " + data.getExtras());

			if (purchaseData == null || dataSignature == null) {
				sdk.dispatchError(SDKContext.EVENT_PAY, "支付数据或签名为空");
				return;
			}

			JSONObject dispatchData = new JSONObject();

			try {
				final JSONObject p = new JSONObject(purchaseData);
				dispatchData.put("purchase", p);
				dispatchData.put("sign", dataSignature);
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							InAppHelper.getHelper().billService.consumePurchase(3, getPackageName(),
									p.getString("purchaseToken"));
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}).start();
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			sdk.dispatchData(SDKContext.EVENT_PAY, dispatchData);
		}else if(resultCode == Activity.RESULT_OK){
			sdk.dispatchError(SDKContext.EVENT_PAY, "支付发生错误，代码:"+responseCode);
		}else if (resultCode == Activity.RESULT_CANCELED) {
			sdk.dispatchError(SDKContext.EVENT_PAY, SDKContext.CODE_ERR_CANCEL,"用户取消");
		}else {
			sdk.dispatchError(SDKContext.EVENT_PAY, "其他错误，代码:"+responseCode);
		}

		finish();
	}

}
