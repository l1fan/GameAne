package com.l1fan.ane;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

/**
 * ANE Entry
 * 
 */
public class SDKExtension implements FREExtension {


	@Override
	public FREContext createContext(String aneId) {
		System.out.println("create GamePaySDK:" + aneId);
		try {
			return ((FREContext) Class.forName(aneId + ".SDK").newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void dispose() {
		System.out.println("GamePaySDK dispose");
	}

	@Override
	public void initialize() {
		System.out.println("GamePaySDK initialize");
	}

	
}


