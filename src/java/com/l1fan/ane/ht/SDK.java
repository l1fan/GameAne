package com.l1fan.ane.ht;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.GameRequestDialog.Result;
import com.ht.htloginsdk.bean.LoginBean;
import com.ht.htloginsdk.utils.HTSdk;
import com.ht.htloginsdk.utils.HeTuCallback;
import com.ht.htloginsdk.utils.HtFacebookInviteCallback;
import com.ht.htloginsdk.utils.HtFacebookShareCallback;
import com.ht.htloginsdk.utils.HtLoginManager;
import com.ht.htloginsdk.utils.LoginUtils;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	public void init() throws JSONException {
		JSONObject json = getJsonData();
		Bundle md = getMetaData();
		HTSdk.sdkInitialize(getActivity(), json.optString(APPID, md.getString(APPID,"")), json.optString("channel",md.getString("channel","")));
		HtLoginManager.getInstance().setLoginListener(new HeTuCallback<LoginBean>() {
			
			@Override
			public void onSuccess(LoginBean b) {
				if (b.getCode() == 0) {
					JSONObject obj = new JSONObject();
					try {
						obj.put(UID, b.getData().getUid());
						obj.put(UNAME, b.getData().getName());
						obj.put(TOKEN, b.getData().getToken());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, obj);
				}else{
					dispatchError(EVENT_LOGIN, b.getCode()+":"+b.getMsg());
				}
			}
			
			@Override
			public void onFailure(Throwable paramThrowable, boolean paramBoolean) {
				paramThrowable.printStackTrace();
				dispatchError(EVENT_LOGIN, "login failed");
			}
		});
		
		HtLoginManager.getInstance().setFacebookInviteCallbackListener(new HtFacebookInviteCallback<GameRequestDialog.Result>() {
			
			@Override
			public void onSuccess(Result paramResult) {
				dispatchData("FB_INVITE");
			}
			
			@Override
			public void onError(FacebookException paramFacebookException) {
				dispatchError("FB_INVITE", paramFacebookException.getMessage());
			}
		});
		
		HtLoginManager.getInstance().setFacebookShareCallbackListener(new HtFacebookShareCallback<Sharer.Result>() {
			
			@Override
			public void onSuccess(com.facebook.share.Sharer.Result paramResult) {
				dispatchData("FB_SHARE",paramResult.getPostId());
			}
			
			@Override
			public void onError(FacebookException paramFacebookException) {
				dispatchError("FB_SHARE", paramFacebookException.getMessage());
			}
		});
		
		dispatchData(EVENT_INIT);
	}
	
	public void userLogin(){
		LoginUtils.startLoginSdk(getActivity());
	}
	
	public void invite() throws JSONException{
		JSONObject json = getJsonData();
		String title = json.optString("title");
		String msg = json.optString("message");
		LoginUtils.facebookInvite(getActivity(), title, msg);
	}
	
	public void share() throws JSONException{
		JSONObject json = getJsonData();
		String captionStr = json.optString("title");
		String desStr = json.optString("message");
		String linkString = json.optString("linkurl");
		String pictureString = json.optString("pictureurl");
		LoginUtils.doShare(getActivity(), captionStr, desStr, linkString, pictureString);
	}
	
	public void pay(){
		LoginUtils.startTopUp(getActivity());
	}
	
	/**
	 * 统计接口需在登录成功后，创建完角色时调用
	 */
	public void track() throws JSONException{
		JSONObject json = getJsonData();
		String type = json.optString("type");
		String version = json.optString("version");
		String coo_server = json.optString("coo_server");
		String coo_uid = json.optString("coo_uid");
		LoginUtils.bindStatistics(getActivity(), type, version, coo_server, coo_uid);
	}
}
