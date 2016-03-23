package com.l1fan.swc
{
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	import flash.events.EventDispatcher;
	import flash.events.StatusEvent;
	import flash.utils.ByteArray;
	
	/**
	 * 回调
	 * StatusEvent
	 * code : 对应 静态变量里的类型
	 * level : 对应回传内容
	 */
	public class HeTuGameSDK extends EventDispatcher
	{
		/** SDK完成Init回调 **/
		public static const SDK_HETU_INIT:String = "SDK_HeTu_Init";
		/** SDK 错误 **/
		public static const SDK_HETU_ERROR:String = "SDK_HeTu_Error";
		/** SDK切换Debug模式 **/
		public static const SDK_HETU_DEBUG:String = "SDK_HeTu_Debug";
		/** SDK自动更新回调 **/
		public static const SDK_HETU_UPDATE:String = "SDK_HeTu_Update";
		/** SDK完成用户登录回调 **/
		public static const SDK_HETU_LOGIN:String = "SDK_HeTu_Login";
		/** SDK完成用户注销回调 **/
		public static const SDK_HETU_LOGOUT:String = "SDK_HeTu_Logout";
		/** SDK完成用户注销回调 **/
		public static const SDK_HETU_USERCENTER:String = "SDK_HeTu_UserCenter";
		/** SDK完成用户购买回调 **/
		public static const SDK_HETU_PAY:String = "SDK_HeTu_Pay";
		/** SDK **/
		public static const SDK_HETU_TOOLBAR_SHOW:String = "SDK_HeTu_ToolBarShow";
		/** SDK **/
		public static const SDK_HETU_TOOLBAR_HIDE:String = "SDK_HeTu_ToolBarHide";
		
		/** SDK仓库 **/
		private static var lib:Object = new Object();
		/**
		 * 通过 extId 返回已经初始化的 SDK
		 * @return
		 */
		public static function getSDK(extId:String):HeTuGameSDK
		{
			if (lib.hasOwnProperty(extId))
			{
				return lib[extId] as HeTuGameSDK;
			}
			return null;
		}
		
		/**
		 * 从库中清理SDK,以便内存回收
		 * @param	extId
		 * @return
		 */
		public static function delSDK(extId:String):Boolean
		{
			if (lib.hasOwnProperty(extId))
			{
				delete lib[extId];
				return true;
			}
			return false;
		}
		
		/** 实例 **/
		private var _sdk:ExtensionContext;
		/** SDK扩展ID **/
		private var _extId:String = "";
		
		/**
		 * 与extension id相同才能加载对应的ANE
		 * @param	extId		与extension.xml文件中的id相同才能加载对应的ANE
		 */
		public function HeTuGameSDK(extId:String)
		{
			this._extId = extId;
			var level:Object;
			var e:StatusEvent;
			if (extId)
			{
				if (lib.hasOwnProperty(extId))
				{
					level = new Object();
					level.message = "HeTuGameSDK 此SDK已经初始化成功 ID : " + extId;
					e = new StatusEvent(HeTuGameSDK.SDK_HETU_DEBUG, false, false, HeTuGameSDK.SDK_HETU_DEBUG, JSON.stringify(level));
					dispatchEvent(e);
				}
				else
				{
					try
					{
						_sdk = ExtensionContext.createExtensionContext(extId, extId);
						_sdk.addEventListener(StatusEvent.STATUS, statusHandler);
						lib[extId] = this;
						
					}
					catch (error:Error)
					{
						level = new Object();
						level.note = "HeTuGameSDK 初始化失败 ID : " + extId;
						level.name = error.name;
						level.errorID = error.errorID;
						level.message = error.message;
						e = new StatusEvent(HeTuGameSDK.SDK_HETU_DEBUG, false, false, HeTuGameSDK.SDK_HETU_DEBUG, JSON.stringify(level));
						dispatchEvent(e);
					}
				}
			}
			else
			{
				level = new Object();
				level.message = "HeTuGameSDK 初始化失败,请输入extId";
				e = new StatusEvent(HeTuGameSDK.SDK_HETU_DEBUG, false, false, HeTuGameSDK.SDK_HETU_DEBUG, JSON.stringify(level));
				dispatchEvent(e);
			}
		}
		
		/** 获取SDK **/
		public function get sdk():ExtensionContext
		{
			if (_sdk) return _sdk;
			var level:Object = new Object();
			level.message = "HeTuGameSDK SDK 未初始化 ID : " + _extId;
			var e:StatusEvent = new StatusEvent(HeTuGameSDK.SDK_HETU_DEBUG, false, false, HeTuGameSDK.SDK_HETU_DEBUG, JSON.stringify(level));
			dispatchEvent(e);
			return null;
		}
		/** 获取ANE的ID **/
		public function get extId():String { return _extId; }
		
		/**
		 * 转抛事件
		 * @param	e	事件
		 */
		private function statusHandler(e:StatusEvent):void
		{
			dispatchEvent(e);
		}
		
		/**
		 * 执行ANE内的方法
		 * @param	action		方法名称
		 * @param	data		参数
		 * @return
		 */
		public function call(action:String, data:String = ""):Object
		{
			if (_sdk)
			{
				return _sdk.call("doAction", action, data);
			}
			return null;
		}
		
		/** 初始化sdk **/
		public function init(data:String = ""):Object { return call("init", data); }
		/** 用户登录 **/
		public function userLogin(data:String = ""):Object { return call("userLogin", data); }
		/** 用户注销 **/
		public function userLogout(data:String = ""):Object { return call("userLogout", data); }
		/** 打开用户中心或平台 **/
		public function userCenter(data:String = ""):Object { return call("userCenter", data); }
		/** 购买虚拟货币 **/
		public function pay(data:String = ""):Object { return call("pay", data); }
		/** SDK是否支持用户中心 **/
		public function isSupportUserCenter():Boolean
		{
			var o:Object = call("isSupportUserCenter");
			if (o && int(o) == 1)
			{
				return true;
			}
			return false;
		}
		
		/** SDK是否支持用户注销 **/
		public function isSupportUserLogout():Boolean
		{
			var o:Object = call("isSupportUserLogout");
			if (!o)
			{
				return true;
			}
			if (o && int(o) == 1)
			{
				return true;
			}
			return false;
		}
	}
}