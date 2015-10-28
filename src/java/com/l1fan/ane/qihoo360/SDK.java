package com.l1fan.ane.qihoo360;

import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.l1fan.ane.SDKContext;
import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;
import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;

public class SDK extends SDKContext {

	protected String mToken;

	/**
	 * 3.1 初始化接口【客户端调用】(必接)
	 */
	public void init() {
		Matrix.init(getActivity());
		dispatchData(EVENT_INIT);
	}

	/**
	 * 3.2.2.1 登录接口【客户端调用】(必接)
	 */
	public void userLogin() {
		Activity activity = getActivity();
		Intent intent = new Intent(activity, ContainerActivity.class);
		intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);
		intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, false);
		intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, true);
		intent.putExtra(ProtocolKeys.IS_SHOW_AUTOLOGIN_SWITCH, true);
		Matrix.execute(activity, intent, loginCallback);
	}

	private IDispatcherCallback loginCallback = new IDispatcherCallback() {

		@Override
		public void onFinished(String str) {
			try {
				JSONObject json = new JSONObject(str);
				int errno = json.optInt("errno", -1);
				if (errno == -1) {
					dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL, "login cancel");
				}else{
					JSONObject data = json.getJSONObject("data");
					mToken = data.getString("access_token");
					JSONObject obj = new JSONObject();
					obj.put(UID, "");
					obj.put(TOKEN, mToken);
					dispatchData(EVENT_LOGIN, obj);
				}
			} catch (JSONException e) {
				dispatchError(EVENT_LOGIN, e.getMessage());
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * 3.2.2.2 切换账号接口
	 */
	public void switchAccount() {
		Activity activity = getActivity();

		Intent intent = new Intent(activity, ContainerActivity.class);
		intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);
		intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, false);
		intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, true);
		intent.putExtra(ProtocolKeys.IS_SHOW_AUTOLOGIN_SWITCH, true);
		Matrix.invokeActivity(activity, intent, loginCallback);
	}

	/**
	 * 3.3.2.1 支付接口【客户端调用】(必接)
	 * 
	 * @throws JSONException
	 */
	public void pay() throws JSONException {
		Activity activity = getActivity();
		Intent intent = new Intent(activity, ContainerActivity.class);
		intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);
		Bundle bundle = new Bundle();
		JSONObject pay = getJsonData();
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, false);
		bundle.putString(ProtocolKeys.ACCESS_TOKEN, mToken);
		bundle.putString(ProtocolKeys.QIHOO_USER_ID, pay.optString("qihooUid"));
		bundle.putString(ProtocolKeys.AMOUNT, pay.optString(AMOUNT)); // 以分为单位
		bundle.putString(ProtocolKeys.PRODUCT_NAME, pay.optString(PNAME));
		bundle.putString(ProtocolKeys.PRODUCT_ID, pay.optString(PID,"0"));
		bundle.putString(ProtocolKeys.NOTIFY_URI, pay.optString(NOTIFY_URL));
		bundle.putString(ProtocolKeys.APP_NAME, pay.optString(APPNAME, getAppName()));
		bundle.putString(ProtocolKeys.APP_USER_NAME, pay.optString(UNAME));
		bundle.putString(ProtocolKeys.APP_USER_ID, pay.optString(UID));
		bundle.putString(ProtocolKeys.APP_ORDER_ID, pay.optString(ORDER_ID));
		bundle.putString(ProtocolKeys.APP_EXT_1, pay.optString(EXT));
		bundle.putString(ProtocolKeys.APP_EXT_2, pay.optString("ext2"));
		intent.putExtras(bundle);
		Matrix.invokeActivity(activity, intent, new IDispatcherCallback() {
			
			@Override
			public void onFinished(String data) {
				try {
					JSONObject json = new JSONObject(data);
					int code = json.optInt("error_code");
					String msg = json.optString("error_msg");
					switch (code) {
					case 0:
						dispatchData(EVENT_PAY, msg);
						break;
					case 4010201:
					case 4009911:
						dispatchError(EVENT_PAY, CODE_ERR_INVALID, msg);
					default:
						dispatchError(EVENT_PAY, msg);
					}
						
				} catch (JSONException e) {
					dispatchError(EVENT_PAY, e.getMessage());
					e.printStackTrace();
				}
				
			}
		});
	}

	/**
	 * 3.4.2.5 上传积分接口(必接)
	 * 
	 * @throws JSONException
	 */
	public void uploadScore() throws JSONException {
		JSONObject us = getJsonData();
		Intent intent = new Intent();
		intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_UPLOAD_SCORE);
		intent.putExtra(ProtocolKeys.SCORE, us.optString("strScore"));
		intent.putExtra(ProtocolKeys.TOPNID, us.optString("topnid"));
		Matrix.execute(getActivity(), intent, new QiHooCallback(getAction()));
	}

	/**
	 * 3.5 销毁接口【客户端调用】(必接) 以退出登录状态并释放资源。调用完该接口后,360SDK 又回到未初始化状态。
	 */
	public void destroy() {
		Matrix.destroy(getActivity());
	}

	/**
	 * 3.6.1 退出接口【客户端调用】(必接)
	 */
	public void userLogout() {
		Activity activity = getActivity();
		Bundle bundle = new Bundle();
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, false);
		bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_QUIT);
		Intent intent = new Intent(activity, ContainerActivity.class);
		intent.putExtras(bundle);
		Matrix.invokeActivity(activity, intent, new QiHooCallback(getAction()));
	}

	/**
	 * 3.6.3 防沉迷查询接口【客户端调用】(网游必接)
	 * 
	 * @throws JSONException
	 */
	public void antiAddiction() throws JSONException {
		JSONObject json = getJsonData();
		Bundle bundle = new Bundle();
		bundle.putString(ProtocolKeys.ACCESS_TOKEN, mToken);
		bundle.putString(ProtocolKeys.QIHOO_USER_ID, json.optString("qihooUid"));
		bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_ANTI_ADDICTION_QUERY);
		Intent intent = new Intent(getActivity(), ContainerActivity.class);
		intent.putExtras(bundle);
		Matrix.execute(getActivity(), intent, new QiHooCallback(getAction()));
	}

	/**
	 * 3.6.4 实名注册接口【客户端调用】(网游必接)
	 */
	public void realNameRegister() {
		Bundle bundle = new Bundle();
		bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, false);
		bundle.putString(ProtocolKeys.QIHOO_USER_ID, getData());
		bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_REAL_NAME_REGISTER);
		Intent intent = new Intent(getActivity(), ContainerActivity.class);
		intent.putExtras(bundle);
		Matrix.invokeActivity(getActivity(), intent, new QiHooCallback(getAction()));
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object matrix() throws Exception {
		Method method = Matrix.class.getMethod(getData(), Activity.class);
		return method.invoke(null, getActivity());
	}

	/**
	 * 回调
	 *
	 */
	class QiHooCallback implements IDispatcherCallback {

		private String event;

		public QiHooCallback(String event) {
			this.event = event;
		}

		@Override
		public void onFinished(String data) {
			try {
				JSONObject json = new JSONObject(data);
				dispatchData(event, json);
			} catch (JSONException e) {
				dispatchError(event, e.getMessage());
				e.printStackTrace();
			}
		}

	}

}
