package com.l1fan.ane.anzhi;

import org.json.JSONException;
import org.json.JSONObject;

import com.anzhi.usercenter.sdk.AnzhiUserCenter;
import com.anzhi.usercenter.sdk.inter.AnzhiCallback;
import com.anzhi.usercenter.sdk.inter.InitSDKCallback;
import com.anzhi.usercenter.sdk.item.CPInfo;
import com.l1fan.ane.SDKContext;

/**
 * init.appKey
 * init.appSecret
 * 
 * test 
 *  Appkey = "1378375366Az26xatNyDOD5EM6D2ys";
 * AppSecret = "ug2KMdLi2JSr4naOE48XmL3h";
 *
 */
public class SDK extends SDKContext implements AnzhiCallback {

    public void init() throws JSONException{
    	CPInfo info = new CPInfo();
    	JSONObject data = getJsonData();
    	info.setAppKey(data.optString(APPKEY));
    	info.setSecret(data.optString(APPSECRET));
    	info.setGameName(data.optString(APPNAME,getAppName()));
    	info.setChannel("AnZhi");
    	
    	AnzhiUserCenter anzhi = AnzhiUserCenter.getInstance();
    	anzhi.azinitSDK(getActivity(), info, new InitSDKCallback() {
			
			@Override
			public void ininSdkCallcack() {
				dispatchData(EVENT_INIT);
			}
		});

    	/**0 横屏,1 竖屏,4 根据物理感应来选择方向 */
    	anzhi.setActivityOrientation(data.optInt(ORIENTATION,1));
    	anzhi.setOpendTestLog(data.optBoolean(SDKLOG,false));
    	anzhi.setCallback(this);
    }
    
	@Override
	public void onCallback(CPInfo cpInfo, String result) {
		try {
			JSONObject json = new JSONObject(result);
			String key = json.optString("callback_key");
			if ("key_pay".equals(key)) {
				int code = json.optInt("code"); 
				if (code == 200) {
					dispatchData(EVENT_PAY);
				}else {
					dispatchError(EVENT_PAY, json.optString("desc"));
				}
			}else if("key_logout".equals(key)){
				dispatchData(EVENT_LOGOUT);
			}else if ("key_login".equals(key)) {
				int code = json.optInt("code");
				if (code == 200) {
					JSONObject data = new JSONObject();
					data.put(UID, json.getString("uid"));
					data.put(TOKEN, json.getString("sid"));
					data.put(UNAME, json.getString("login_name"));
					dispatchData(EVENT_LOGIN, data);
				}else{
					dispatchError(EVENT_LOGIN, json.optString("desc"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    
	public void toolBarHide(){
		AnzhiUserCenter.getInstance().dismissFloaticon();
	}
	
	public void toolBarShow(){
		AnzhiUserCenter.getInstance().showFloaticon();
	}
    
    public void userLogin(){
    	AnzhiUserCenter.getInstance().login(getActivity(), true);
    }
    
    public void userLogout(){
    	AnzhiUserCenter.getInstance().logout(getActivity());
    }
    
    public void anzhi() throws Exception{
    	sdkcall(AnzhiUserCenter.getInstance());
    }
    
    public void pay() throws JSONException{
    	JSONObject json = getJsonData();
    	float price = (float) (json.optDouble(AMOUNT)/100);
    	String payDesc = json.optString(PNAME);
    	String cbInfo = json.optString(ORDER_ID);
    	AnzhiUserCenter.getInstance().pay(getActivity(), 0, price, payDesc, cbInfo);
    }


}
