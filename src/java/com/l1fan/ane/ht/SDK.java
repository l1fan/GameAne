package com.l1fan.ane.ht;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.mobile.newhtloginsdk.activity.TopUpActivity;
import org.mobile.newhtloginsdk.bean.LoginResultBean;
import org.mobile.newhtloginsdk.bean.ProductListBean;
import org.mobile.newhtloginsdk.billing.BillingBroadcastReceiver.IabBroadcastListener;
import org.mobile.newhtloginsdk.billing.GooglePay;
import org.mobile.newhtloginsdk.billing.GooglePay.InitQueryHandler;
import org.mobile.newhtloginsdk.billing.IabResult;
import org.mobile.newhtloginsdk.interfaces.HeTuCallback;
import org.mobile.newhtloginsdk.utils.FacebookUtils;
import org.mobile.newhtloginsdk.utils.FacebookUtils.FacebookCallbackResult;
import org.mobile.newhtloginsdk.utils.HtLoginManager;
import org.mobile.newhtloginsdk.utils.HtLoginSdk;
import org.mobile.newhtloginsdk.utils.LoginUtils;
import org.mobile.newhtloginsdk.utils.NetWorkUtils;
import org.mobile.newhtloginsdk.utils.NetWorkUtils.ProductListHandler;
import org.xutils.common.Callback.CancelledException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.GameRequestDialog.Result;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext implements IabBroadcastListener {

	static String mAppId;
	static String mCooServer = "";
	static String mCooUid = "";
	static String mChannel;
	static SDKContext context;

	public void init() throws JSONException {
		context = this;
		JSONObject json = getJsonData();
		Bundle md = getMetaData();
		mAppId = json.optString(APPID, String.valueOf(md.getInt(APPID,0)));
		mChannel = json.optString("channel",md.getString("channel",""));
		
		
		HtLoginSdk.getInstance().sdkInitialize(getActivity(), mAppId, mChannel);
		HtLoginManager.getInstance().registerCallback(new HeTuCallback<LoginResultBean>() {
			
			@Override
			public void onSuccess(LoginResultBean info) {
				System.out.println("login result:"+info);
				if (info.getCode() == 0) {
					if (info.getData() == null) {
						dispatchData(EVENT_LOGOUT, info.getMsg());
					}else{
						JSONObject obj = new JSONObject();
						try {
							obj.put(UID, info.getData().getUid());
							obj.put(UNAME, info.getData().getName());
							obj.put(TOKEN, info.getData().getToken());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dispatchData(EVENT_LOGIN, obj);
					}
				}else if (info.getCode() == 2 || info.getCode() == 3) {
					dispatchData(EVENT_LOGOUT, info.getMsg());
				}else {
					dispatchError(EVENT_LOGIN, info.getCode()+":"+info.getMsg());
				}
			}
			
			@Override
			public void onFinished() {
				System.out.println("login finish");
			}
			
			@Override
			public void onError(Throwable arg0, boolean arg1) {
				arg0.printStackTrace();
				dispatchError(EVENT_LOGIN, "login failed");
			}
			
			@Override
			public void onCancelled(CancelledException arg0) {
				dispatchError(EVENT_LOGIN, "login cancel");

			}
		});
		
		FacebookUtils.getInstance().onCreate();
		
		GooglePay.init(getActivity(),this, new InitQueryHandler() {
			
			@Override
			public void onIabSetupFinished(IabResult result) {
				System.out.println("--> GooglePay.init finished ");
				System.out.println(result.toString());
			}
		});
		
		
        
		dispatchData(EVENT_INIT);

	}
	
	public void productList(){
		NetWorkUtils.productList(mAppId, getActivity().getPackageName(), mChannel, mCooServer, new ProductListHandler() {

			@Override
			public void onError(Throwable paramThrowable, boolean paramBoolean) {
				dispatchError("HT_PLIST", paramThrowable.getMessage());
			}
			
			@Override
			public void onFinished() {
				
			}

			@Override
			public void onCancelled(CancelledException arg0) {
				dispatchData("HT_PLIST", "cancel");					
			}

			@Override
			public void onSuccess(ArrayList<ProductListBean> list) {
				dispatchData("HT_PLIST", JSON.toJSON(list));					
			}
		});
	}
	
	public void userLogin(){
		LoginUtils.startLoginSdk(getActivity());
	}
	
	public void invite() throws JSONException{
		JSONObject json = getJsonData();
		String title = json.optString("title");
		String msg = json.optString("message");
		FacebookUtils.getInstance().facebookInvite(getActivity(), title, msg, new FacebookCallbackResult<GameRequestDialog.Result>() {
			
			@Override
			public void onSuccess(Result paramT) {
				dispatchData("FB_INVITE");
			}
			
			@Override
			public void onError(FacebookException paramFacebookException) {
				dispatchError("FB_INVITE", paramFacebookException.getMessage());
			}
			
			@Override
			public void onCancel() {
				dispatchError("FB_INVITE", "invite cancel");
			}
		});
	}
	
	public void share() throws JSONException{
		JSONObject json = getJsonData();
		String captionStr = json.optString("title");
		String desStr = json.optString("message");
		String linkString = json.optString("linkurl");
		String pictureString = json.optString("pictureurl");
		FacebookUtils.getInstance().facebookShare(getActivity(), captionStr, desStr, linkString, pictureString, new FacebookCallbackResult<Sharer.Result>() {
			
			@Override
			public void onSuccess(com.facebook.share.Sharer.Result paramResult) {
				dispatchData("FB_SHARE",paramResult.getPostId());
			}
			
			@Override
			public void onError(FacebookException paramFacebookException) {
				dispatchError("FB_SHARE", paramFacebookException.getMessage());
			}
			
			@Override
			public void onCancel() {
				dispatchError("FB_SHARE", "share cancel");
			}
		});
	}
	
	public void pay() throws JSONException{

		Activity entryActivity = getActivity();
		Intent intent = new Intent(entryActivity,GPActivity.class);
		intent.putExtra("payData", getData());
		intent.putExtra("appId", mAppId);
		intent.putExtra("cooServer", mCooServer);
		entryActivity.startActivity(intent);
	}
	
	public void pay3rd(){
		getActivity().startActivity(new Intent(getActivity(),TopUpActivity.class));
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
		mCooServer = coo_server;
		mCooUid = coo_uid;
		NetWorkUtils.bindStatistics(getActivity(), type, version, coo_server, coo_uid);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		GooglePay.onDestroy(getActivity());
	}

	@Override
	public void receivedBroadcast() {

	}
}
