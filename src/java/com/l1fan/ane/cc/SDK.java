package com.l1fan.ane.cc;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;

import com.l1fan.ane.SDKContext;
import com.lion.ccpay.CCPaySdk;
import com.lion.ccpay.CCPaySdk$Stats;
import com.lion.ccpay.login.LoginListener;
import com.lion.ccpay.pay.PayListener;
import com.lion.ccpay.pay.vo.PayResult;
import com.lion.ccpay.user.vo.LoginResult;

public class SDK extends SDKContext {
	
	class LoginOutBroadcastReceiver extends BroadcastReceiver {

		public static final String LOGINOUT_ACTION = "CCPAY_LOGINOUT_ACTION";

		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("action-->"+intent.toString());
			if (LOGINOUT_ACTION.equals(intent.getAction()) && intent.getData().getHost().equals(context.getPackageName())) {
				dispatchData(EVENT_LOGOUT);
			}
		}
	}

	private LoginOutBroadcastReceiver receiver;
	
	public void init() throws NameNotFoundException {
		CCPaySdk.getInstance().init(getActivity());
		regReceiver();
		lifeCycle();
		dispatchData(EVENT_INIT);
	}
	
	private void regReceiver() throws NameNotFoundException{
		Activity context = getActivity();
		IntentFilter filter = new IntentFilter(LoginOutBroadcastReceiver.LOGINOUT_ACTION);
		Object appid = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.get("app_id");
		filter.addDataScheme(String.valueOf(appid));
		receiver = new LoginOutBroadcastReceiver();
		context.registerReceiver(receiver, filter);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		getActivity().unregisterReceiver(receiver);
	}
	
	public void userLogin(){
		CCPaySdk.getInstance().login(getActivity(), new LoginListener() {
			
			@Override
			public void onComplete(LoginResult result) {
				if (result.isSuccess) {
					JSONObject data = new JSONObject();
					try {
						data.put(UID, result.userId);
						data.put(UNAME, result.displayName);
						data.put(TOKEN, result.token);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, data);
				}else{
					dispatchError(EVENT_LOGIN, "login failed");
				}
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		String amount = pay.optString(AMOUNT,null);
		if (amount != null) {
			int a = Integer.valueOf(amount);
			if (a == 0) {
				amount = null;
			}else{
				amount = String.valueOf(a/100.00);
			}
		}
		CCPaySdk.getInstance().pay(getActivity(), pay.optString(PID), amount ,pay.optString(ORDER_ID), new PayListener() {
			
			@Override
			public void onComplete(PayResult result) {
				switch (result.statusCode) {
				case "0000":
					dispatchData(EVENT_PAY);
					break;
				case "0003":
					dispatchError(EVENT_PAY, CODE_ERR_CANCEL, "pay cancel:"+result.msg);
					break;
				default:
					dispatchError(EVENT_PAY, "pay error:"+result.msg);
					break;
				}
			}
		});
	}
	
	private void lifeCycle() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return;
		}
		getActivity().getApplication().registerActivityLifecycleCallbacks(
				new ActivityLifecycleCallbacks() {

					@Override
					public void onActivityStopped(Activity arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onActivityStarted(Activity arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onActivitySaveInstanceState(Activity arg0,
							Bundle arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onActivityResumed(Activity arg0) {
						CCPaySdk$Stats.onResume(arg0);
					}

					@Override
					public void onActivityPaused(Activity arg0) {
						CCPaySdk$Stats.onPause(arg0);
					}

					@Override
					public void onActivityDestroyed(Activity arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onActivityCreated(Activity arg0, Bundle arg1) {
						// TODO Auto-generated method stub

					}
				});		
	}
}
