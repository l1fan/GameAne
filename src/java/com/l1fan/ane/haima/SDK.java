package com.l1fan.ane.haima;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build;
import android.os.Bundle;

import com.haima.loginplugin.ZHErrorInfo;
import com.haima.loginplugin.ZHPayUserInfo;
import com.haima.loginplugin.callback.OnCheckUpdateListener;
import com.haima.loginplugin.callback.OnLoginCancelListener;
import com.haima.loginplugin.callback.OnLoginListener;
import com.haima.payPlugin.callback.OnCheckOrderListener;
import com.haima.payPlugin.callback.OnPayCancelListener;
import com.haima.payPlugin.callback.OnPayListener;
import com.haima.payPlugin.infos.ZHPayOrderInfo;
import com.haima.plugin.haima.HMPay;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	public void init() throws JSONException {
		/**
		 * HMPay.init应用程序初始化，在入口activity的oncreate中调用
		 * （请勿将demo中的appid放入游戏中测试，一个appid仅对应一个程序，
		 * 在平台填写的包名必须与程序包名一致，appid或者包名不正确会提示应用程序被禁用， 获取appid请联系商务人员）
		 *
		 * @param context
		 *            上下文
		 * @param isLandscape
		 *            是否是横屏
		 * @param appid
		 *            应用程序分发的应用标识 传入null 表示从manifest中获取
		 * @param checkUpdateListener
		 *            检查更新回调接口
		 * @param isTestMode
		 *            是否是测试模式 (测试模式下会弹出窗口)
		 * @param ifErrorType
		 *            如果检查更新失败 需要的提示 详见本页下方检查更新失败回调
		 * @return boolean 初始化失败返回false 参数不正确
		 */
		registerLifeCycle();
		JSONObject json = getJsonData();
		boolean isTest = json.optBoolean(DEBUGMODE, false);
		int errTyep = json.optInt("ifErrorType", 1);
		boolean init = HMPay.init(getActivity(), false, json.optString(APPID, null), updateListener, isTest, errTyep);
		if (init) {
			dispatchData(EVENT_INIT);
		}else{
			dispatchError(EVENT_INIT, "haima init error");
		}
		HMPay.setLoginListener(loginListener);
		HMPay.setLoginCancelListener(new OnLoginCancelListener() {

			@Override
			public void onLoginCancel() {
				dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL,"login cancel");
			}
		});
		HMPay.setPayCancelListener(new OnPayCancelListener() {

			@Override
			public void onPayCancel() {
				dispatchError(EVENT_PAY, CODE_ERR_CANCEL,"pay cancel");
			}
		});
		
	}

	private OnCheckUpdateListener updateListener = new OnCheckUpdateListener() {

		@Override
		public void onCheckUpdateSuccess(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
			JSONObject json = new JSONObject();
			try {
				json.put("isNeedUpdate", paramBoolean1);
				json.put("isForceUpdate", paramBoolean2);
				json.put("isTestMode", paramBoolean3);
				dispatchData(EVENT_UPDATE, json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onCheckUpdateFailed(ZHErrorInfo paramZHErrorInfo, int paramInt) {
			dispatchError(EVENT_UPDATE, paramZHErrorInfo.desc);
		}
	};

	private OnLoginListener loginListener = new OnLoginListener() {

		@Override
		public void onLoginSuccess(ZHPayUserInfo paramZHPayUserInfo) {
			JSONObject json = new JSONObject();
			try {
				json.put(UID, paramZHPayUserInfo.uid);
				json.put(TOKEN, paramZHPayUserInfo.loginToken);
				json.put("udid", paramZHPayUserInfo.udid);
				dispatchData(EVENT_LOGIN, json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onLoginFailed(ZHErrorInfo info) {
			dispatchError(EVENT_LOGIN, info.desc);
		}

		@Override
		public void onLogOut() {
			dispatchData(EVENT_LOGOUT);
		}
	};
	
	@SuppressLint("NewApi")
	private void registerLifeCycle() {
		if (Build.VERSION.SDK_INT < 14) {
			return;
		}
		Activity activity = getActivity();
		activity.getApplication().registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

			@Override
			public void onActivityStopped(Activity activity) {

			}

			@Override
			public void onActivityStarted(Activity activity) {

			}

			@Override
			public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
			}

			@Override
			public void onActivityResumed(Activity activity) {
				if (activity.equals(activity)) {
					HMPay.onResume(activity);
				}
			}

			@Override
			public void onActivityPaused(Activity activity) {
				if (activity.equals(activity)) {
					HMPay.onPause();
				}
			}

			@Override
			public void onActivityDestroyed(Activity activity) {

			}

			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

			}
		});
	}

	public void userLogin() {
		HMPay.login(getActivity(), loginListener);
	}
	

	public void userLogout() {
		HMPay.logOut();
	}

	public void switchAccount() {
		HMPay.switchAccount(getActivity());
	}

	@Override
	public boolean isSupportUserCenter() {
		return true;
	}


	public void userCenter() {
		HMPay.startUserCenter(getActivity());
	}

	public boolean isLogined() {
		return HMPay.isLogined();
	}

	public void setLogEnable() {
		HMPay.setLogEnable(Boolean.valueOf(getData()));
	}

	public void checkOrder() {
		final String event = getAction();
		HMPay.checkOrder(getData(), new OnCheckOrderListener() {

			@Override
			public void onCheckOrderSuccess(String paramString, float paramFloat, int paramInt) {
				JSONObject json = new JSONObject();
				try {
					json.put("orderId", paramString);
					json.put("amount", paramFloat * 100);
					json.put("status", paramInt);
					dispatchData(event, json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onCheckOrderFailed(String paramString, ZHErrorInfo paramZHErrorInfo) {
				dispatchError(event, paramZHErrorInfo.desc);
			}
		});
	}

	public void pay() throws JSONException {
		ZHPayOrderInfo info = new ZHPayOrderInfo();
		JSONObject pay = getJsonData();
		info.gameName = pay.optString(APPNAME, getAppName());
		info.goodName = pay.optString(PNAME);
		info.goodPrice = pay.optInt(AMOUNT) / 100;
		info.orderNo = pay.optString(ORDER_ID);
		info.showUrl = pay.optString("showUrl");
		info.userParam = pay.optString(EXT);
		HMPay.pay(info, getActivity(), new OnPayListener() {

			@Override
			public void onPaySuccess(ZHPayOrderInfo paramZHPayOrderInfo) {
				dispatchData(EVENT_PAY);
			}

			@Override
			public void onPayFailed(ZHPayOrderInfo paramZHPayOrderInfo, ZHErrorInfo paramZHErrorInfo) {
				dispatchError(EVENT_PAY, paramZHErrorInfo.desc);
			}
		});
	}

	public void checkUpdate() {
		HMPay.checkUpdate(getActivity(), updateListener, Boolean.valueOf(getData()),
				HMPay.CHECKUPDATE_FAILED_SHOW_CANCLEANDSURE);
	}

}
