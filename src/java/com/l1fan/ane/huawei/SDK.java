package com.l1fan.ane.huawei;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.huawei.pay.plugin.IHuaweiPay;
import com.android.huawei.pay.plugin.IPayHandler;
import com.android.huawei.pay.plugin.MobileSecurePayHelper;
import com.huawei.gamebox.buoy.sdk.inter.UserInfo;
import com.huawei.opensdk.OpenSDK;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {
	
	private String mAppId;

	public void init() throws JSONException {
		JSONObject init = getJsonData();
		mAppId = init.optString(APPID);
		String cpId = init.optString(CPID);
		String appKey = init.optString(APPKEY);
		
		OpenSDK.init(getActivity(), mAppId, cpId , appKey, new UserInfo() {
			
			@Override
			public void dealUserInfo(HashMap<String, String> userInfo) {
				if (userInfo == null) {
					dispatchError(EVENT_LOGIN, "login fail");
				}else if ("1".equals(userInfo.get("logiStatus"))) {
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
	}
	
	public void userLogin() {
		OpenSDK.start();
	}
	
	public void pay() throws JSONException {
		//TODO: unfinished
		JSONObject json = getJsonData();
        Map<String, Object> payInfo = new HashMap<String, Object>();
		// 必填字段，不能为null或者""
        payInfo.put(PayParams.AMOUNT, json.opt(AMOUNT));
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.PRODUCT_NAME, json.opt(PNAME));
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.REQUEST_ID, json.opt(ORDER_ID));
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.PRODUCT_DESC, json.opt(PNAME));
        // 必填字段，不能为null或者""，请填写自己的公司名称
        payInfo.put(PayParams.USER_NAME, "华为软件技术有限公司");
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.APPLICATION_ID, mAppId);
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.USER_ID, "");
        // 必填字段，不能为null或者""
        payInfo.put(PayParams.SIGN, "");
        
        // 必填字段，不能为null或者""，此处写死X6
        payInfo.put(PayParams.SERVICE_CATALOG, "X6");
        
        // 调试期可打开日志，发布时注释掉
        payInfo.put(PayParams.SHOW_LOG, true);
        
        // 设置支付界面横竖屏，默认竖屏
        payInfo.put(PayParams.SCREENT_ORIENT, 1);
		
		IHuaweiPay payHelper = new MobileSecurePayHelper();
		payHelper.startPay(getActivity(), payInfo, new IPayHandler() {
			
			@Override
			public void onFinish(Map<String, String> arg0) {
				
			}
		});
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
