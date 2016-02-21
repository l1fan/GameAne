package com.l1fan.ane;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build;
import android.os.Bundle;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;

/**
 * init
 * userLogin
 * pay
 * userLogout
 *
 */
public class SDKContext extends FREContext implements FREFunction {
	
	public static final String TAG = "GamePaySDK-ANE";

	public static final int CODE_ERR = 10;
	public static final int CODE_ERR_UNSUPPORTED = 11;
	public static final int CODE_ERR_PARAMETER = 12;
	public static final int CODE_ERR_SDK = 13;
	public static final int CODE_ERR_CANCEL = 14;
	public static final int CODE_ERR_INVALID = 15;


	/** status event code */
	public static final String EVENT_INIT = "SDK_HeTu_Init";
	public static final String EVENT_UPDATE = "SDK_HeTu_Update";
	public static final String EVENT_LOGIN = "SDK_HeTu_Login";
	public static final String EVENT_LOGOUT = "SDK_HeTu_Logout";
	public static final String EVENT_PAY = "SDK_HeTu_Pay";
	public static final String EVENT_ERROR = "SDK_HeTu_Error";
	public static final String EVENT_EXIT = "SDK_HeTu_Exit";
	
	
	/****** json members *******/
	public static final String APPID = "appId";
	public static final String CPID = "cpId";
	public static final String APPKEY = "appKey";
	public static final String APPSECRET = "appSecret";
	public static final String ORIENTATION = "orientation";
	public static final String SDKLOG = "sdkLog";
	public static final String DEBUGMODE = "debugMode";
	public static final String APPNAME = "appName";
	public static final String TOKEN = "token";
	public static final String UID = "uid";
	public static final String PNAME = "pname";
	public static final String PDESC = "pdesc";
	public static final String PID = "pid";
	public static final String UNAME = "uname";
	public static final String EXT = "ext";
	public static final String AMOUNT = "amount";
	public static final String NOTIFY_URL = "notifyUrl";
	public static final String ORDER_ID = "orderId";
	/**************/

	private String mAction;
	private String mData;

	@Override
	public void dispose() {
		System.out.println("sdk context dispose");
	}
	
	public SDKContext(){
	}

	/**
	 * send event message to as(StatusEvent)
	 * 
	 * 发送事件回as
	 * 
	 * @param event event code
	 * @param level
	 */
	private void dispatchEvent(String code, String level) {
		System.out.println("code->"+code+" level->"+level);
		dispatchStatusEventAsync(code, level);
	}

	public void dispatchData(String event, Object data){
		dispatchEvent(event, dataJson(data));;
	}
	
	public void dispatchData(String event){
		dispatchData(event, "success");;
	}
	
	public void dispatchError(String event, int code ,String message){
		dispatchEvent(event, errorJson(code, message));;
	}
	
	public void dispatchError(String event, String message){
		dispatchError(event, CODE_ERR, message);;
	}
	
	@Override
	public Map<String, FREFunction> getFunctions() {
		HashMap<String, FREFunction> map = new HashMap<String, FREFunction>();
		map.put("doAction", this);
		return map;
	}

	@Override
	public FREObject call(FREContext ctx, FREObject[] args) {
		try {
			mAction = args[0].getAsString();
			mData = args[1].getAsString();
		} catch (Exception e) {
			dispatchEvent(EVENT_ERROR, errorJson(CODE_ERR_PARAMETER,  "call method must have two parameters at least"));
			e.printStackTrace();
			return null;
		}

		System.out.println("action->" + mAction + " data->" + mData);

		try {
			Method method = getClass().getMethod(actionWithoutPrefix());
			return j2fre(method.invoke(this));
		} catch (NoSuchMethodException e) {
			dispatchEvent(EVENT_ERROR, errorJson(CODE_ERR_UNSUPPORTED, "unspported action:" + mAction));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			dispatchEvent(EVENT_ERROR, errorJson(CODE_ERR_SDK, mAction +" action internal error , check catlog"));
			e.printStackTrace();
		} catch (FREWrongThreadException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/** remove action's prefix.
	 * 
	 * 一般java中不会有包含下划线的方法名，在可以用下划线可以为前缀和方法名的分隔
	 * 
	 * @return actionName without prefix
	 */
	@SuppressLint("DefaultLocale")
	private String actionWithoutPrefix() {
		String[] arr = mAction.split("_");
		String name = arr[arr.length - 1];
		String methodName = name.substring(0, 1).toLowerCase() + name.substring(1);
		return methodName;
	}


	/**
	 * get data
	 * @return
	 * @throws JSONException
	 */
	protected JSONObject getJsonData() throws JSONException {
		if (mData == "") {
			return new JSONObject();
		}
		return new JSONObject(mData);
	}

	/**
	 * get raw data string from actionscript
	 * @return
	 */
	protected String getData() {
		return mData;
	}
	
	/**
	 * get action string from actionscript
	 * @return
	 */
	protected String getAction() {
		return mAction;
	}
	
	public boolean isSupportUserLogout() {
		return true;
	}

	public boolean isSupportUserCenter() {
		return false;
	}
	
	protected Object sdkcall(Object obj) throws Exception{
		Method method = obj.getClass().getMethod(mData);
		return method.invoke(obj);
	}
	
	public String getAppName(){
		Activity activity = getActivity();
		return activity.getString(activity.getApplicationInfo().labelRes);
	}

	/**
	 * java object to as object
	 * 
	 * @param java
	 *            object
	 * @return
	 * @throws FREWrongThreadException
	 */
	private FREObject j2fre(Object obj) throws FREWrongThreadException {
		if (obj instanceof String) {
			return FREObject.newObject((String) obj);
		} else if (obj instanceof Boolean) {
			return FREObject.newObject((Boolean) obj);
		} else if (obj instanceof Integer) {
			return FREObject.newObject((Integer) obj);
		} else if (obj instanceof Double || obj instanceof Float) {
			return FREObject.newObject((Double) obj);
		}
		return null;
	}

	public String errorJson(int code, String message) {
		JSONObject error = new JSONObject();
		JSONObject json = new JSONObject();
		try {
			error.put("code", code);
			error.put("message", message);
			json.put("error", error);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public String dataJson(Object value) {
		JSONObject json = new JSONObject();
		try {
			json.put("data", value);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json.toString();
	}
	
	/**
	 * 返回json对象中key对应的值。可以输入多个key值，按顺序查找，只要匹配到就直接返回，后面的不再查询。
	 * @param json
	 * @param keys 多个key值，注意顺序
	 * @return 不存在时返回null
	 */
	protected Object jsonOpt(JSONObject json, String... keys){
		for (String key : keys) {
			if (json.has(key)) {
				return json.opt(key);
			}
		}
		return null;
	}
	
	/**
	 * activity life cycle api14
	 */
	protected void onResume() {
		
	}
	
	/**
	 * activity life cycle，api14
	 */
	protected void onPause(){
		
	}
	
	/**
	 * register activity life cycle, then {@link #onResume()} and {@link #onPause()} override will be called
	 */
	protected void regLifecycle() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return;
		}
		final Activity activity = getActivity();
		activity.getApplication().registerActivityLifecycleCallbacks(
				new ActivityLifecycleCallbacks() {

					@Override
					public void onActivityStopped(Activity arg0) {

					}

					@Override
					public void onActivityStarted(Activity arg0) {

					}

					@Override
					public void onActivitySaveInstanceState(Activity arg0,
							Bundle arg1) {
					}

					@Override
					public void onActivityResumed(Activity arg0) {
						if (arg0.equals(activity)) {
							onResume();
						}
					}

					@Override
					public void onActivityPaused(Activity arg0) {
						if (arg0.equals(activity)) {
							onPause();
						}
					}

					@Override
					public void onActivityDestroyed(Activity arg0) {

					}

					@Override
					public void onActivityCreated(Activity arg0, Bundle arg1) {

					}
				});		
	}
}
