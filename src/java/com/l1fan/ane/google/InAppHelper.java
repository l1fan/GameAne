package com.l1fan.ane.google;

import com.android.vending.billing.IInAppBillingService;
import com.l1fan.ane.SDKContext;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class InAppHelper {

	private InAppHelper(){}
	
	private static InAppHelper helper;
	
	public static InAppHelper getHelper(){
		if (helper == null) {
			helper = new InAppHelper();
		}
		return helper;
	}
	
	SDKContext context;
	IInAppBillingService billService;

	ServiceConnection sc = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			billService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			billService = IInAppBillingService.Stub.asInterface(service);
			try {
				int response = billService.isBillingSupported(3, context.getActivity().getPackageName(), "inapp");
				if (response != 0) {
					context.dispatchError(SDKContext.EVENT_INIT, "不支持google play v3支付");
					return;
				}
				context.dispatchData(SDKContext.EVENT_INIT);
			} catch (RemoteException e) {
				e.printStackTrace();
				context.dispatchError(SDKContext.EVENT_INIT, "检测google play支付支持时发生错误");
			}
		}
	};
	
	public void bind(){
		Activity activity = context.getActivity();
		Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		serviceIntent.setPackage("com.android.vending");
		if (!activity.getPackageManager().queryIntentServices(serviceIntent, 0).isEmpty()) {
			activity.bindService(serviceIntent, sc, Context.BIND_AUTO_CREATE);
		} else {
			context.dispatchError(SDKContext.EVENT_INIT, "没有查找到google play应用内支付服务框架");
		}
	}

	public void unbind(){
		if (billService != null) {
			context.getActivity().unbindService(sc);
		}
	}
}
