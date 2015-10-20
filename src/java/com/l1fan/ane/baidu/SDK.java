package com.l1fan.ane.baidu;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.gamesdk.BDGameSDK;
import com.baidu.gamesdk.BDGameSDKSetting;
import com.baidu.gamesdk.BDGameSDKSetting.Domain;
import com.baidu.gamesdk.BDGameSDKSetting.Orientation;
import com.baidu.gamesdk.IResponse;
import com.baidu.gamesdk.ResultCode;
import com.baidu.platformsdk.PayOrderInfo;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	public void init() throws JSONException {

		BDGameSDKSetting mBDGameSDKSetting = new BDGameSDKSetting();
		JSONObject data = getJsonData();
		mBDGameSDKSetting.setAppID(data.optInt(APPID));// APPID设置
		mBDGameSDKSetting.setAppKey(data.optString(APPKEY));// APPKEY设置
		mBDGameSDKSetting.setDomain(Domain.RELEASE);// 设置为正式模式

		int ori = data.optInt(ORIENTATION, 1);
		if (ori == 0) {
			mBDGameSDKSetting.setOrientation(Orientation.LANDSCAPE);// 设置为横屏
		} else {
			mBDGameSDKSetting.setOrientation(Orientation.PORTRAIT);// 设置为竖屏
		}

		BDGameSDK.init(getActivity(), mBDGameSDKSetting, new IResponse<Void>() {

			@Override
			public void onResponse(int resultCode, String resultDesc, Void arg2) {
				switch (resultCode) {
				case ResultCode.INIT_SUCCESS:
					dispatchData(EVENT_INIT, resultDesc);
					break;
				case ResultCode.INIT_FAIL:
				default:
					dispatchError(EVENT_INIT, resultDesc);
				}
			}
		});

		BDGameSDK.setSuspendWindowChangeAccountListener(listener);
		BDGameSDK.setSessionInvalidListener(new IResponse<Void>() {
			
			@Override
			public void onResponse(int resultCode, String resultDesc, Void arg2) {
				if(resultCode == ResultCode.SESSION_INVALID){ 
					dispatchData(EVENT_LOGOUT, resultDesc);
				}
			}
		});
	}

	private IResponse<Void> listener = new IResponse<Void>() {

		@Override
		public void onResponse(int resultCode, String resultDesc, Void arg2) {
			switch (resultCode) {
			case ResultCode.LOGIN_SUCCESS:
				JSONObject json = new JSONObject();
				try {
					json.put(UID, BDGameSDK.getLoginUid());
					json.put(TOKEN, BDGameSDK.getLoginAccessToken());
					dispatchData(EVENT_LOGIN, json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case ResultCode.LOGIN_FAIL:
				dispatchError(EVENT_LOGIN, resultDesc);
				break;
			case ResultCode.LOGIN_CANCEL: // TODO 操作前后的登录状态没变化
				break;
			}
		}
	};

	public void userLogin() {
		BDGameSDK.login(listener);
	}

	public void pay() throws JSONException {
		JSONObject json = getJsonData();
		PayOrderInfo payOrderInfo = new PayOrderInfo();
		payOrderInfo.setCooperatorOrderSerial(json.optString(ORDER_ID)); // CP订单号
		payOrderInfo.setProductName(json.optString(PNAME)); // 商品名称
		payOrderInfo.setTotalPriceCent(json.optLong(AMOUNT));// 以分为单位,long类型
		payOrderInfo.setRatio(json.optInt("ratio", 1)); // 兑换比例,此时不生效
		payOrderInfo.setExtInfo(json.optString(EXT));// 该字段在支付通知中原样返回,不超过500个字符
		BDGameSDK.pay(payOrderInfo, null, new IResponse<PayOrderInfo>() {

			@Override
			public void onResponse(int resultCode, String resultDesc, PayOrderInfo arg2) {
				switch (resultCode) {
				case ResultCode.PAY_SUCCESS:// 支付成功
					dispatchData(EVENT_PAY);
					break;
				case ResultCode.PAY_CANCEL:// 订单支付取消
					dispatchError(EVENT_PAY, CODE_ERR_CANCEL, resultDesc);
					break;
				case ResultCode.PAY_FAIL:
				case ResultCode.PAY_SUBMIT_ORDER:// 订单已经提交,支付结果未知
													// (比如:已经请求了,但是查询超时)
					dispatchError(EVENT_PAY, resultDesc);
					break;
				}

			}
		});
	}
	
	public void toolBarShow(){
		BDGameSDK.showFloatView(getActivity());
	}
	
	public void toolBarHide(){
		BDGameSDK.closeFloatView(getActivity());
	}

	public void userLogout() {
		BDGameSDK.logout();
	}

	public void destory() {
		BDGameSDK.destroy();
	}

}
