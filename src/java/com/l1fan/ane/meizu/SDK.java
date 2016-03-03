package com.l1fan.ane.meizu;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.l1fan.ane.SDKContext;
import com.meizu.gamecenter.sdk.LoginResultCode;
import com.meizu.gamecenter.sdk.MzAccountInfo;
import com.meizu.gamecenter.sdk.MzBuyInfo;
import com.meizu.gamecenter.sdk.MzGameCenterPlatform;
import com.meizu.gamecenter.sdk.MzLoginListener;
import com.meizu.gamecenter.sdk.MzPayListener;
import com.meizu.gamecenter.sdk.PayResultCode;

public class SDK extends SDKContext {
	
	private String mAppId;
	private String mUid;
	private String mAppSecret;

	public void init() throws JSONException {
		JSONObject init = getJsonData();
		Bundle md = getMetaData();
		mAppId = init.optString(APPID,String.valueOf(md.get(APPID)));
		String appKey =  init.optString(APPKEY,md.getString(APPKEY));
		mAppSecret = init.optString(APPSECRET,md.getString(APPSECRET));
		
		MzGameCenterPlatform.init(getActivity(), mAppId, appKey);
		
		dispatchData(EVENT_INIT);
	}
	
	public void userLogin(){
		MzGameCenterPlatform.login(getActivity(), new MzLoginListener() {
			

			@Override
			public void onLoginResult(int code, MzAccountInfo uInfo, String errorMsg) {
				switch (code) {
				case LoginResultCode.LOGIN_SUCCESS:
					JSONObject data = new JSONObject();
					try {
						mUid = uInfo.getUid();
						data.put(UID, mUid);
						data.put(TOKEN, uInfo.getSession());
						data.put(UNAME, uInfo.getName());
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					dispatchData(EVENT_LOGIN, data);
					break;
					
				case LoginResultCode.LOGIN_ERROR_CANCEL:
					dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL, "login cancel");
					break;
				default:
					dispatchError(EVENT_LOGIN, "loign fail:"+errorMsg+":"+code);
					break;
				}
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject pay = getJsonData();
		String orderId = pay.optString(ORDER_ID);
	    String price = String.valueOf(pay.optInt(AMOUNT)/100.00);
	    String ext = pay.optString(EXT);
	    String pname = pay.optString(PNAME);
	    String pid = pay.optString(PID);
	    long ts = System.currentTimeMillis();
	    String orderStr = String.format("app_id="+mAppId+"&buy_amount=1&cp_order_id="+orderId+"&create_time="+ts+"&pay_type=0&product_body="+pname+"&product_id="+pid+"&product_per_price="+price+"&product_subject="+pname+"&product_unit=&total_price="+price+"&uid="+mUid+"&user_info="+ext+":"+mAppSecret);
	    String sign = md5(orderStr);
	    
		MzBuyInfo info = new MzBuyInfo();
		info.setBuyCount(1);
	    info.setCpUserInfo(ext);
	    info.setOrderAmount(price);
	    info.setOrderId(orderId);
	    info.setPerPrice(price);
	    info.setProductBody(pname);
	    info.setProductId(pid);
	    info.setProductSubject(pname);
	    info.setCreateTime(ts);
	    info.setPayType(0);
	    info.setPerPrice(price);
	    info.setSign(sign);
	    info.setSignType("md5");
	    info.setAppid(mAppId);
	    info.setUserUid(mUid);
	    
		MzGameCenterPlatform.payOnline(getActivity(), info, new MzPayListener() {
			
			@Override
			public void onPayResult(int code, MzBuyInfo buyInfo, String errorMsg) {
				switch (code) {
				case PayResultCode.PAY_SUCCESS:
					dispatchData(EVENT_PAY);
					break;
				case PayResultCode.PAY_ERROR_CANCEL:
					dispatchError(EVENT_PAY, CODE_ERR_CANCEL, "pay cancel");
					break;
				default:
					dispatchError(EVENT_PAY, "pay fail:"+errorMsg+":"+code);
					break;
				}
			}
		});
	}
	
	public void userLogout(){
		MzGameCenterPlatform.logout(getActivity());
	}
	
	public static final String md5(final String s) {
	    final String MD5 = "MD5";
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance(MD5);
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuilder hexString = new StringBuilder();
	        for (byte aMessageDigest : messageDigest) {
	            String h = Integer.toHexString(0xFF & aMessageDigest);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

}
