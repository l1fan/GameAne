package com.l1fan.ane.kuaiyong;

import org.json.JSONException;
import org.json.JSONObject;

import com.anguotech.sdk.bean.PayInfo;
import com.anguotech.sdk.interfaces.PayCallBack;
import com.anguotech.sdk.manager.AnGuoManager;
import com.anguotech.xsdk.AGPlatFormManager;
import com.anguotech.xsdk.bean.AGPayInfos;
import com.anguotech.xsdk.bean.AGRolerInfo;
import com.anguotech.xsdk.bean.AGUserInfo;
import com.anguotech.xsdk.config.AppConfig;
import com.anguotech.xsdk.listener.AGExitListener;
import com.anguotech.xsdk.listener.AGInitListener;
import com.anguotech.xsdk.listener.AGPayListener;
import com.anguotech.xsdk.listener.AGUserListener;
import com.l1fan.ane.SDKContext;

public class SDK extends SDKContext {

	public void init() throws JSONException {
		regLifecycle();
		AGPlatFormManager.getInstance().SetUserListener(getActivity(), new AGUserListener() {
			
			@Override
			public void onLogout() {
				dispatchData(EVENT_LOGOUT, "logout success");
			}
			
			@Override
			public void onLoginSuccess(AGUserInfo loginInfo) {
				JSONObject json = new JSONObject();
				try {
					json.put(TOKEN, loginInfo.getToken());
					dispatchData(EVENT_LOGIN, json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onLoginFailed(String reason) {
				dispatchError(EVENT_LOGIN, reason);
			}
			
			@Override
			public void onLoginCancel() {
				dispatchError(EVENT_LOGIN, CODE_ERR_CANCEL, "login cancel");
			}
		} );
		
		
		AGPlatFormManager.getInstance().Init(getActivity(), new AGInitListener() {
			
			@Override
			public void onSuccess() {
				dispatchData(EVENT_INIT, "init success");
			}
			
			@Override
			public void onFail() {
				dispatchError(EVENT_INIT, "init failed");
			}
		});
	}


	public void userLogin() {
		AGPlatFormManager.getInstance().Login(getActivity());
	}

	public void userLogout() {
		AGPlatFormManager.getInstance().Logout(getActivity());
	}
	
	public void destroy(){
		AGPlatFormManager.getInstance().Exit(getActivity(), new AGExitListener() {
			
			@Override
			public void onThirdExiterProvide() {
				dispatchError(EVENT_EXIT,"failed");
			}
			
			@Override
			public void onExit() {
				dispatchData(EVENT_EXIT,"success");
			}
		});
	}

	public void pay() throws Exception {
		PayInfo payInfo = new PayInfo();
		JSONObject json = getJsonData();
		payInfo.setFee(json.optInt(AMOUNT) / 100);
		payInfo.setGame(AppConfig.GAMEID);
		payInfo.setSubject(json.optString(PNAME));
		payInfo.setDealSeq(json.optString(ORDER_ID));
		payInfo.setGameUserServer(json.optString(GAMESVR,"0"));
//		payInfo.setUid(json.optString("uid"));
//		payInfo.setNotifyUrl(json.optString("notifyUrl"));
//		payInfo.setPkgid(json.optString("packId"));

		AnGuoManager.Instance().Pay(payInfo, new PayCallBack() {

			@Override
			public void onCancel() {
				dispatchError(EVENT_PAY, CODE_ERR_CANCEL, "cancel");
			}

			@Override
			public void onFail(Object arg0) {
				dispatchError(EVENT_PAY, String.valueOf(arg0));
			}

			@Override
			public void onSuccess(Object arg0) {
				dispatchData(EVENT_PAY, String.valueOf(arg0));
			}

		});
	}
	
	public void submitGameInfo() throws JSONException{
		AGRolerInfo roler = new AGRolerInfo();
		JSONObject json = getJsonData();
		roler.setRole_Id(json.optString(ROLEID,"1"));
		roler.setRole_Name(json.optString(ROLENAME,"AGsb"));
		roler.setRole_Grade(json.optString(ROLELEVEL,"0"));
		roler.setRole_Balance(json.optString(json.optString("roleBalance","0")));
		roler.setRole_Vip(json.optString("vip","0"));
		roler.setRole_UserParty(json.optString(PARTYNAME,"无"));
		roler.setServer_Name(json.optString(GAMESVR,"svrname"));
		roler.setServer_Id(json.optString("gameServerId","0"));
		AGPlatFormManager.getInstance().SetRoleData(getActivity(), AGRolerInfo.AG_CREATE_ROLE, roler);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		AGPlatFormManager.getInstance().OnPause(getActivity());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AGPlatFormManager.getInstance().OnResume(getActivity());
	}
	
	
	/**
	 * SB API
	 * @throws Exception
	 */
	public void pay2() throws Exception{
		AGPayInfos payInfo = new AGPayInfos();
		JSONObject json = getJsonData();
		payInfo.set_proDes(json.optString(PNAME));
		payInfo.set_orderId(json.optString(ORDER_ID));
		payInfo.set_proAmount(1);
		payInfo.set_proId(json.optString(PID));
		payInfo.set_proName(json.optString(PNAME));
		payInfo.set_proPrice(json.optInt(AMOUNT));
		payInfo.set_userServer("1");
		payInfo.set_uid(json.optString(UID,"xxxx"));
		payInfo.set_exchangeRate(1);
		payInfo.set_gameMoneyName("...");
		
		AGPlatFormManager.getInstance().Pay(getActivity(), payInfo, new AGPayListener() {
			
			@Override
			public void onSuccess(Object paramObject) {
				
			}
			
			@Override
			public void onFail(Object paramObject) {
				
			}
			
			@Override
			public void onCancel() {
				
			}
		});
	}
}
