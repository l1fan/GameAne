package com.l1fan.ane.kaopu;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build;
import android.os.Bundle;

import com.kaopu.supersdk.api.KPSuperSDK;
import com.kaopu.supersdk.callback.KPAuthCallBack;
import com.kaopu.supersdk.callback.KPGetCheckUrlCallBack;
import com.kaopu.supersdk.callback.KPLoginCallBack;
import com.kaopu.supersdk.callback.KPLogoutCallBack;
import com.kaopu.supersdk.callback.KPPayCallBack;
import com.kaopu.supersdk.model.UserInfo;
import com.kaopu.supersdk.model.params.PayParams;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	public void init(){
		final Activity ctx = getActivity();
        final HashMap<String, String> configData = new HashMap<String, String>();
        configData.put("gameName", getAppName());
		KPSuperSDK.auth(ctx, configData, new KPAuthCallBack() {
			
			@Override
			public void onAuthSuccess() {
				KPSuperSDK.startGuide(ctx);
				KPSuperSDK.showFloatView(ctx);
				lifeCycle();
				dispatchData(EVENT_INIT);
				
                KPSuperSDK.registerLogoutCallBack(new KPLogoutCallBack() {
					
					@Override
					public void onSwitch() {
						dispatchData(EVENT_LOGOUT);
					}
					
					@Override
					public void onLogout() {
						dispatchData(EVENT_LOGOUT);
					}
				});
                

			}
			
			@Override
			public void onAuthFailed() {
				dispatchError(EVENT_INIT, "init failed");
			}
				
		});
	}
	
	public void userLogin(){
		KPSuperSDK.login(getActivity(), new KPLoginCallBack() {
			
			@Override
			public void onLoginSuccess(UserInfo uInfo) {
				
				final JSONObject data = new JSONObject();
				try {
					data.put(UID, uInfo.getUserid());
					data.put(UNAME, uInfo.getUsername());
					data.put(TOKEN, uInfo.getToken());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
                KPSuperSDK.setGetCheckUrlCallBack(new KPGetCheckUrlCallBack() {
					
					@Override
					public void onGetCheckUrlSuccess(String url) {
						try {
							data.put("checkUrl", url);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dispatchData(EVENT_LOGIN,data);
					}
					
					@Override
					public void onGetCheckUrlFailed() {
						dispatchError(EVENT_LOGIN, "login verify failed");
					}
				});
                
				KPSuperSDK.getCheckUrl();
			}
			
			@Override
			public void onLoginFailed() {
				dispatchError(EVENT_LOGIN, "login failed");
			}
			
			@Override
			public void onLoginCanceled() {
				dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL, "login canceled");
			}
		});
	}
	
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		PayParams params = new PayParams();
		params.setAmount(pay.optInt(AMOUNT)/100.00);
		params.setGamename(getAppName());
		params.setGameserver(pay.optString("gameServer","0"));
		params.setRolename(pay.optString("roleName"," "));
		params.setOrderid(pay.optString(ORDER_ID));
		params.setCustomPrice(pay.optBoolean("isCustomPrice",true));
		params.setCurrencyname(pay.optString("currency"," "));
		params.setProportion(pay.optDouble("proprotion",1));
		params.setCustomText(pay.optString(PNAME));
		KPSuperSDK.pay(getActivity(), params, new KPPayCallBack() {
			
			@Override
			public void onPaySuccess() {
				dispatchData(EVENT_PAY);
			}
			
			@Override
			public void onPayFailed() {
				dispatchError(EVENT_PAY, "pay failed");
			}
			
			@Override
			public void onPayCancle() {
				dispatchError(EVENT_PAY, CODE_ERR_CANCEL, "pay cancel");
			}
		});
	}
	
	public void userLogout(){
		KPSuperSDK.logoutAccount();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		KPSuperSDK.release();
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
						KPSuperSDK.showFloatView(arg0);
					}

					@Override
					public void onActivityPaused(Activity arg0) {
						KPSuperSDK.closeFloatView(arg0);
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
