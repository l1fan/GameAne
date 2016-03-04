package com.l1fan.ane.huawei;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.android.huawei.pay.plugin.IHuaweiPay;
import com.android.huawei.pay.plugin.IPayHandler;
import com.android.huawei.pay.plugin.MobileSecurePayHelper;
import com.android.huawei.pay.plugin.PayParameters;
import com.android.huawei.pay.util.HuaweiPayUtil;
import com.android.huawei.pay.util.Rsa;
import com.huawei.gamebox.buoy.sdk.impl.BuoyOpenSDK;
import com.huawei.gamebox.buoy.sdk.inter.UserInfo;
import com.huawei.gamebox.buoy.sdk.util.BuoyConstant;
import com.huawei.gamebox.buoy.sdk.util.DebugConfig;
import com.huawei.hwid.openapi.OpenHwID;
import com.huawei.opensdk.OpenSDK;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {
	
	private String mAppId;
	private String mPayId;
	private String mPrivateKey;

	public void init() throws JSONException {
		JSONObject init = getJsonData();
		Bundle md = getMetaData();
		mAppId = init.optString(APPID,md.getString(APPID,"")).replace("hw.", "");
		String cpId = init.optString(CPID,md.getString(CPID,"")).replace("hw.", "");
		String appKey = init.optString(APPKEY,init.optString("buoSecret",md.getString("buoSecret")));
		
		mPayId = init.optString("payId",md.getString("payId")).replace("hw.", "");
		mPrivateKey = init.optString("privateKey",md.getString("privateKey"));
		int ret = OpenSDK.init(getActivity(), mAppId, cpId , appKey, new UserInfo() {
			
			@Override
			public void dealUserInfo(HashMap<String, String> userInfo) {
				if (userInfo == null) {
					dispatchError(EVENT_LOGIN, "login fail");
				}else if ("1".equals(userInfo.get("loginStatus"))) {
					JSONObject data = new JSONObject();
					try {
						data.put(TOKEN, userInfo.get("accesstoken"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					dispatchData(EVENT_LOGIN, data);
				}
			}
		});
		
		DebugConfig.setLog(true);
		
		if (ret == 0) {
			dispatchData(EVENT_INIT);
		}else{
			dispatchError(EVENT_INIT, "init failed");
		}
		
		regLifecycle();
		IntentFilter filter = new IntentFilter(BuoyConstant.CHANGE_USER_LOGIN_ACTION);
		getActivity().registerReceiver(receiver, filter);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
	        String action = intent.getAction();
	        if (BuoyConstant.CHANGE_USER_LOGIN_ACTION.equals(action)){
	            Bundle bundle = intent.getBundleExtra(BuoyConstant.GAMEBOX_EXTRA_DATA);
	            int value = 0;
	            if(null != bundle)
	            {
	                value = bundle.getInt(BuoyConstant.KEY_GAMEBOX_CHANGEUSERLOGIN);
	            }
	            if (BuoyConstant.VALUE_CHANGE_USER == value)
	            {   
	                dispatchData(EVENT_LOGOUT);
	            }
			}

		}
	};
	
	public void userLogin() {
		OpenSDK.start();
	}
	
	public void pay() throws JSONException {
		DebugConfig.setLog(true);

		JSONObject json = getJsonData();

        Map<String, String> params = new HashMap<String, String>();
        // 必填字段，不能为null或者""，请填写从联盟获取的支付ID
        params.put(PayParams.USER_ID, json.optString("payId",mPayId));
        // 必填字段，不能为null或者""，请填写从联盟获取的应用ID
        params.put(PayParams.APPLICATION_ID, mAppId);
        // 必填字段，不能为null或者""，单位是元，精确到小数点后两位，如1.00
        params.put(PayParams.AMOUNT, String.valueOf(json.optInt(AMOUNT) / 100.00));
        // 必填字段，不能为null或者""，道具名称
        params.put(PayParams.PRODUCT_NAME, json.optString(PNAME));
        // 必填字段，不能为null或者""，道具描述
        params.put(PayParams.PRODUCT_DESC, json.optString(PNAME));
        // 必填字段，不能为null或者""，最长30字节，不能重复，否则订单会失败
        params.put(PayParams.REQUEST_ID, json.optString(ORDER_ID));
        
        String noSign = HuaweiPayUtil.getSignData(params);
        System.out.println("nosign==>"+noSign);
        String sign = Rsa.sign(noSign, json.optString("privateKey",mPrivateKey));
        System.out.println("sign==>"+sign);
		
        Map<String, Object> payInfo = new HashMap<String, Object>();
		// 必填字段，不能为null或者""
        payInfo.put(PayParams.AMOUNT, String.valueOf(json.optInt(AMOUNT) / 100.00));
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.PRODUCT_NAME, json.optString(PNAME));
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.REQUEST_ID, json.optString(ORDER_ID));
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.PRODUCT_DESC, json.opt(PNAME));
        // 必填字段，不能为null或者""，请填写自己的公司名称
        payInfo.put(PayParams.USER_NAME, getAppName());
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.APPLICATION_ID, mAppId);
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.USER_ID, json.optString("payId",mPayId));
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.SIGN, json.optString("sign",sign));
        
        // 必填字段，不能为null或者""，此处写死X6
        payInfo.put(PayParams.SERVICE_CATALOG, "X6");
        
        // 调试期可打开日志，发布时注释掉
        payInfo.put(PayParams.SHOW_LOG, true);
        
        // 设置支付界面横竖屏，默认竖屏
        payInfo.put(PayParams.SCREENT_ORIENT, 1);
		System.out.println("payinfo==>"+payInfo.toString());
		IHuaweiPay payHelper = new MobileSecurePayHelper();
		payHelper.startPay(getActivity(), payInfo, new IPayHandler() {
			
			@Override
			public void onFinish(Map<String, String> payResp) {
	            if ("0".equals(payResp.get(PayParameters.returnCode))){
	                if ("success".equals(payResp.get(PayParameters.errMsg))){
	                	dispatchData(EVENT_PAY);
	                }
	            }else{
	            	dispatchError(EVENT_PAY, payResp.get(PayParameters.errMsg));
	            }
			}
		});
	}
	
	@Override
	protected void onResume() {
		BuoyOpenSDK.getIntance().showSmallWindow(getActivity());
	}
	
	@Override
	protected void onPause() {
		BuoyOpenSDK.getIntance().hideSmallWindow(getActivity()); 
		BuoyOpenSDK.getIntance().hideBigWindow(getActivity());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		OpenHwID.releaseResouce();
		BuoyOpenSDK.getIntance().destroy(getActivity());
	}
	
	public void userLogout(){
		
	}
}

interface PayParams
{
    public static final String USER_ID = "userID";
    
    public static final String APPLICATION_ID = "applicationID";
    
    public static final String AMOUNT = "amount";
    
    public static final String PRODUCT_NAME = "productName";
    
    public static final String PRODUCT_DESC = "productDesc";
    
    public static final String REQUEST_ID = "requestId";
    
    public static final String USER_NAME = "userName";
    
    public static final String SIGN = "sign";
    
    public static final String NOTIFY_URL = "notifyUrl";
    
    public static final String SERVICE_CATALOG = "serviceCatalog";
    
    public static final String SHOW_LOG = "showLog";
    
    public static final String SCREENT_ORIENT = "screentOrient";
    
    public static final String SDK_CHANNEL = "sdkChannel";
    
    public static final String URL_VER = "urlver";
}
