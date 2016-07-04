package com.l1fan.ane.tongbutui;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.l1fan.ane.SDKContext;
import com.tongbu.sdk.bean.TbOrderInfo;
import com.tongbu.sdk.bean.TbUser;
import com.tongbu.sdk.configs.TbPayInfo;
import com.tongbu.sdk.configs.TbPlatform;
import com.tongbu.sdk.configs.TbPlatformSettings;
import com.tongbu.sdk.configs.TbToolBarPlace;
import com.tongbu.sdk.listener.OnCheckPayListener;
import com.tongbu.sdk.listener.OnInitFinishedListener;
import com.tongbu.sdk.listener.OnLeavePlatformListener;
import com.tongbu.sdk.listener.OnLoginFinishedListener;
import com.tongbu.sdk.listener.OnPayProcessListener;
import com.tongbu.sdk.listener.OnSwitchAccountListener;
import com.tongbu.sdk.widget.TbFloatToolBar;

public class SDK extends SDKContext {

	private TbFloatToolBar toolBar;

	public void init() throws JSONException {
		TbPlatformSettings settings = new TbPlatformSettings();
		JSONObject json = getJsonData();
		Bundle md = getMetaData();
		settings.setAppId(json.optString(APPID,md.getString(APPID)).replace("tb.", ""));
		settings.setOrient(json.optInt(ORIENTATION,md.getInt(ORIENTATION, 1)));
		settings.setContinueWhenCheckUpdateFailed(true);
		
		TbPlatform.getInstance().tbInit(getActivity(), settings, new OnInitFinishedListener() {

			@Override
			public void onInitFinished(int flag) {
				switch (flag) {
				case OnInitFinishedListener.FLAG_NORMAL:
					dispatchData(EVENT_INIT);
					break;
				case OnInitFinishedListener.FLAG_FORCE_CLOSE:
					dispatchError(EVENT_INIT, "force close");
					break;
				case OnInitFinishedListener.FLAG_CHECK_FAILED:
					dispatchError(EVENT_INIT, "check failed");
					break;
				default:
					break;
				}
			}
		});
		
		toolBar = TbFloatToolBar.create(getActivity(), TbToolBarPlace.POSITION_TOP_LEFT);
	}

	public void userLogin() {
		TbPlatform.getInstance().tbLogin(getActivity(), new OnLoginFinishedListener() {

			@Override
			public void onLoginFinished(boolean success, TbUser paramTbUser) {
				if (success) {
					
					dispatchLoginData(paramTbUser);
					toolBar.show();
					TbPlatform.getInstance().setOnSwitchAccountListener(new OnSwitchAccountListener() {
						
						@Override
						public void onSwitchAccountFinished(int flag, TbUser user) {
							switch (flag) {
							case SWITCH_ACCOUNT_SUCCESS:
								dispatchLoginData(user);
								break;
							case SWITCH_ACCOUNT_LOGOUT:
								dispatchData(EVENT_LOGOUT);
								break;
							default:
								break;
							}
						}
					});
				}
			}
			
			public void dispatchLoginData(TbUser user){
				JSONObject json = new JSONObject();
				try {
					json.put(UID, user.uid);
					json.put(TOKEN, user.session);
					json.put(UNAME, user.name);
					json.put("account", user.account);
					dispatchData(EVENT_LOGIN, json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void pay() throws JSONException{
		JSONObject json = getJsonData();
		TbPayInfo payInfo = new TbPayInfo();
		final String orderId = json.optString(ORDER_ID);
		payInfo.setSerial(orderId);
		payInfo.setNeedPayRMB(json.optInt(AMOUNT)/100);
		payInfo.setPayDescription(json.getString(PNAME));
		TbPlatform.getInstance().tbUniPayForCoin(getActivity(), payInfo, new OnPayProcessListener() {
			
			@Override
			public void onBuyGoodsDidSuccessWithOrder(String paramString) {
				dispatchData(EVENT_PAY,"success:"+paramString);
			}
			
			@Override
			public void onBuyGoodsDidStartRechargeWithOrder(String paramString) {
               
                
               final OnCheckPayListener listener = new OnCheckPayListener() {
					
					@Override
					public void CheckOrderFinished(TbOrderInfo info) {
						switch (info.code) {
						case 1:
						case 3:
							dispatchData(EVENT_PAY, "success:"+info.code+":"+info.message);
							break;
						default:
							dispatchError(EVENT_PAY, "fail:"+info.code+":"+info.message);
							break;
						}
					}
				};
				
                TbPlatform.getInstance().tbLeavedSDKPlatform(new OnLeavePlatformListener() {
					
					@Override
					public void onLeaveFinished(int type, String paramString) {
						switch (type) {
						case TBPlatformLeavedFromUserPay:
							TbPlatform.getInstance().tbCheckPaySuccess(getActivity(), orderId, listener);
							break;
						default:
							break;
						}
					}
				});

			}

			@Override
			public void onBuyGoodsDidFailedWithOrder(String paramString, int paramInt) {
				dispatchError(EVENT_PAY, paramInt + ":"+ paramString);
			}
			
			@Override
			public void onBuyGoodsDidCancelByUser(String paramString) {
				dispatchError(EVENT_PAY, CODE_ERR_CANCEL, "cancel");
			}
		});
		
		
	}

	public void userLogout() {
		TbPlatform.getInstance().tbLogout(getActivity(), 1);
		dispatchData(EVENT_LOGOUT);
		toolBar.hide();
	}

	@Override
	public void dispose() {
		super.dispose();
		TbPlatform.getInstance().tbDestroy(getActivity());
		if(toolBar != null) toolBar.recycle();
	}
	
}
