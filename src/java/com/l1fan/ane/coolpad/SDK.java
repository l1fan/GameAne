package com.l1fan.ane.coolpad;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.coolcloud.uac.android.api.Coolcloud;
import com.coolcloud.uac.android.api.ErrInfo;
import com.coolcloud.uac.android.api.auth.OAuth2.OnAuthListener;
import com.coolcloud.uac.android.common.Params;
import com.iapppay.interfaces.authentactor.AccountBean;
import com.iapppay.interfaces.callback.IPayResultCallback;
import com.iapppay.sdk.main.CoolPadPay;
import com.iapppay.utils.RSAHelper;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {
	
	private String mAppId;
	private String mAppKey;
	private Coolcloud mCool;
	protected String mOpenId;
	protected String mToken;
	private String mPrivateKey;
	private String mPublicKey;

	public void init() throws JSONException{
		JSONObject jd = getJsonData();
		Bundle md = getMetaData();
		
		mAppId = jd.optString(APPID,md.getString(APPID,"")).replace("coolpad.", "");
		mAppKey = jd.optString(APPKEY,md.getString(APPKEY));
		mPrivateKey = jd.optString("privateKey",md.getString("privateKey"));
		mPublicKey = jd.optString("publicKey",md.getString("publicKey"));
		int ori = jd.optInt(ORIENTATION,md.getInt(ORIENTATION,1));
		
		Activity act = getActivity();
		CoolPadPay.init(act, ori, mAppId);
		dispatchData(EVENT_INIT);
	}
	
	public void userLogin(){
		
		mCool = Coolcloud.createInstance(getActivity(), mAppId, mAppKey);
		mCool.login(getActivity(), "/user/getuserinfo", new OnAuthListener() {
			
			@Override
			public void onError(ErrInfo err) {
				dispatchError(EVENT_LOGIN, err.getError()+err.getMessage());
			}
			
			@Override
			public void onDone(Object arg) {
				System.out.println(arg.toString());
				Bundle result = (Bundle)arg;
				JSONObject data = new JSONObject();
				mToken = result.getString(Params.ACCESS_TOKEN);
				mOpenId = result.getString(Params.OPEN_ID);
				try {
					data.put(UID, mOpenId);
					data.put(TOKEN, mToken);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dispatchData(EVENT_LOGIN, data);
			}
			
			@Override
			public void onCancel() {
				dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL, "login cancel");
			}
		});
		
	}
	
	public void userLogout(){
		mCool.logout(getActivity(), new OnAuthListener() {
			
			@Override
			public void onError(ErrInfo err) {
				dispatchError(EVENT_LOGOUT, "logout fail:"+err.getError()+err.getMessage());
			}
			
			@Override
			public void onDone(Object arg0) {
				dispatchData(EVENT_LOGOUT);
				
			}
			
			@Override
			public void onCancel() {
				dispatchError(EVENT_LOGOUT, CODE_ERR_CANCEL,"logout cancel");
			}
		});
	}
	
	public void pay() throws JSONException{
		
		
		JSONObject pay = getJsonData();

		AccountBean account = CoolPadPay.buildAccount(getActivity(), mToken, mAppId, mOpenId);
		
		String genUrl;
		if (pay.has("transid") || pay.has("transId")) {
			genUrl = "transid="+pay.optString("transid",pay.optString("transId"))+"&appid="+mAppId;
		}else{
			String uid = pay.optString(UID);
			String priKey = pay.optString("privateKey",mPrivateKey);
			genUrl = genUrl(mAppId,uid,pay.optString(EXT),priKey,pay.optInt(PID),pay.optInt(AMOUNT)/100.00,pay.optString(ORDER_ID),pay.optString(NOTIFY_URL));
		}
	
		CoolPadPay.startPay(getActivity(), genUrl, account, new IPayResultCallback() {

			@SuppressWarnings("deprecation")
			@Override
			public void onPayResult(int resultCode, String signValue, String resultInfo) {
				switch (resultCode) {
				case CoolPadPay.PAY_SUCCESS:
					if (TextUtils.isEmpty(signValue)) {
						dispatchError(EVENT_PAY, "pay fail: sign value is null");
					}else{
						int transdataLast = signValue.indexOf("&sign=");
						String transdata = URLDecoder.decode(signValue.substring("transdata=".length(), transdataLast));
						int signLast = signValue.indexOf("&signtype=");
						String sign = URLDecoder.decode(signValue.substring(transdataLast+"&sign=".length(),signLast));
						String signtype = signValue.substring(signLast+"&signtype=".length());
						boolean isSign = false;
						try {
							isSign = RSAHelper.verify(transdata, mPublicKey, sign);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (signtype.equals("RSA") && isSign) {
							dispatchData(EVENT_PAY);
						}else{
							dispatchError(EVENT_PAY, "pay fail: verify sign error");
						}
					}
					break;
				default:
					dispatchError(EVENT_PAY, "pay fail:"+resultCode+":"+resultInfo);
					break;
				}				
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	private String genUrl(String appid, String appuserid, String cpprivateinfo, String appPrivateKey, int waresid, double price, String cporderid,String url) {
		String json = "";

		JSONObject obj = new JSONObject();
		try {
			obj.put("appid", appid);
			obj.put("waresid", waresid);
			obj.put("cporderid", cporderid);
			obj.put("price", price);
			obj.put("appuserid", appuserid);

			
			/*CP私有信息，选填*/
			String cpprivateinfo0 = cpprivateinfo;
			if(!TextUtils.isEmpty(cpprivateinfo0)){
				obj.put("cpprivateinfo", cpprivateinfo0);
			}	
			
			/*支付成功的通知地址。选填。如果客户端不设置本参数，则使用服务端配置的地址。*/
			String notifyurl0 = url;
			if(!TextUtils.isEmpty(notifyurl0)){
				obj.put("notifyurl", notifyurl0);
			}			
			json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sign = "";
		try {
			sign = RSAHelper.signForPKCS1(json, appPrivateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "transdata=" + URLEncoder.encode(json) + "&sign=" + URLEncoder.encode(sign) + "&signtype=" + "RSA";
	}
}
