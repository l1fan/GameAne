package com.l1fan.ane.htc;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jolo.account.Jolo.onAccountResult;
import com.jolo.jolopay.JoloPay.onPayResult;
import com.jolo.sdk.JoloSDK;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	private String mGameCode;
	private String mUserId;
	protected String mSession;
	private String mJoloPubKey;
	private String mCPPriKey;
	private String mNotifyUrl;

	public void init() throws JSONException{
		
		JSONObject init = getJsonData();
		Bundle md = getMetaData();
		mGameCode = init.optString("gameCode",init.optString(APPID,md.getString("gameCode"))).replace("htc.", "");
		mJoloPubKey = init.optString("joloPublicKey",md.getString("joloPublicKey"));
		mCPPriKey = init.optString("cpPrivateKey",md.getString("cpPrivateKey"));
		mNotifyUrl = init.optString(NOTIFY_URL,md.getString(NOTIFY_URL));
		JoloSDK.initJoloSDK(getActivity(), mGameCode);
		JoloSDK.initCallBack(onAccount, onPay);
		dispatchData(EVENT_INIT);
	}
	
	private onAccountResult onAccount = new onAccountResult() {
		
		@Override
		public void onAccount(int resultCode, Intent data) {
			if (resultCode != Activity.RESULT_OK || data == null){
				dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL,"login cancel");
				return;
			}
			String userName = data.getStringExtra(JoloSDK.USER_NAME);
			mUserId = data.getStringExtra(JoloSDK.USER_ID);
			mSession = data.getStringExtra(JoloSDK.USER_SESSION);
			String accountSign = data.getStringExtra(JoloSDK.ACCOUNT_SIGN);
			String account = data.getStringExtra(JoloSDK.ACCOUNT);

			Log.d("gameane", "data = " + data.getExtras().toString());
			Log.d("gameane", "account = " + account +"; sign="+accountSign);
			
			if (account == null && accountSign == null) {
				dispatchError(EVENT_LOGIN, "签名验证失败，返回的签名信息为空");
				return;
			}
			
			if (RsaSign.doCheck(account, accountSign, mJoloPubKey)) {
				JSONObject login = new JSONObject();
				try {
					login.put(UID, mUserId);
					login.put(UNAME, userName);
					login.put(TOKEN, mSession);
					login.put("accountSign", accountSign);
					login.put("account", account);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dispatchData(EVENT_LOGIN, login);	
			}else{
				dispatchError(EVENT_LOGIN, "login account sign check error");
			}
				
		}
	};
	
	private onPayResult onPay = new onPayResult() {
		
		@Override
		public void onPay(int resultCode, Intent data) {
			if (resultCode != Activity.RESULT_OK || data == null){
				dispatchError(EVENT_PAY, CODE_ERR_CANCEL,"pay cancel");
				return;
			}
			
			String resultOrder = data.getStringExtra(JoloSDK.PAY_RESP_ORDER);
			String resultSign = data.getStringExtra(JoloSDK.PAY_RESP_SIGN);
			Log.d("gameane", "resultOrder = " + resultOrder);
			Log.d("gameane", "resultSign = " + resultSign);
			if (RsaSign.doCheck(resultOrder, resultSign, mJoloPubKey)) {
				ResultOrder or = new ResultOrder(resultOrder);
				int rcode = or.getResultCode(); // 返回码, == 200为支付成功
				String resultmsg = or.getResultMsg(); // 返回提示信息
				if (rcode == 200) {
					dispatchData(EVENT_PAY);
				}else{
					dispatchError(EVENT_PAY, ""+resultmsg);
				}
			} else {
				dispatchError(EVENT_PAY, "sign invalid");
			}
		}
	};

	
	public void userLogin(){
		JoloSDK.login(getActivity());
	}
	
	public void userLogout(){
		JoloSDK.logoff(getActivity());
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		Order order = new Order();
		order.setAmount(pay.optString(AMOUNT));
		order.setGameCode(mGameCode);
		order.setGameName(pay.optString(APPNAME, getAppName()));
		order.setGameOrderid(pay.optString(ORDER_ID));
		order.setNotifyUrl(pay.optString(NOTIFY_URL,mNotifyUrl));
		order.setProductID(pay.optString(PID));
		order.setProductName(pay.optString(PNAME));
		order.setProductDes(pay.optString(PDESC,pay.optString(PNAME)));
		order.setSession(mSession);
		order.setUsercode(mUserId);
		String orderJson = order.toJsonOrder();
		String sign = RsaSign.sign(orderJson, mCPPriKey);
		
		JoloSDK.startPay(getActivity(), orderJson, sign);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		JoloSDK.releaseJoloSDK();
	}

}

