#ANE详细使用说明

- [奇虎360](#qihoo360)
- [海马](#haima)
- [爱思助手](#i4)

## <span id="qihoo360">奇虎360</span>

SDK及文档地址[点此](http://dev.360.cn/wiki/index/id/73)  

集成版本 v1.1.8(402).  

使用时注意替换Demo配置文件`ANEDemo.xml`中的参数为正式参数:  
`QHOPENSDK_APPID`、`QHOPENSDK_APPKEY`的值为在360申请的appid和appkey  
`QHOPENSDK_PRIVATEKEY`的值为`md5(app_secret +”#”+ app_key)`     
`QHOPENSDK_WEIXIN_APPID` 用于微信分享，如使用需要到微信开放平台申请ID  

登录结果中token即为access_token，需要通过服务端换取用户ID。查看[获取用户信息](http://dev.360.cn/wiki/index/id/67)  
支付时需要加上以下三个属性:  
`pay.qihooUid` 奇虎用户ID
`pay.uname`  用户名
`pay.uid`    用户id
`pay.notifyUrl` 通知地址
查看[支付结果通知服务端接口](http://dev.360.cn/wiki/index/id/68)


`uploadScore`      上传积分
`destroy`          销毁
`antiAddiction`	   防沉迷
`realNameRegister` 实名注册


## <span id="qihoo360">海马</span>

查看[联运平台](http://pay.haima.me)，支持android和iOS平台。  
需要通过海马审核才能使用SDK，否则会显示应用被禁用

Android海马SDK版本为v1.1.4
iOS海马SDK版本v1.3.6


*初始化时可以使用以下参数

- `appId`   iOS必须传此参数来设置appid。 android可以在配置文件中设置，也可以通过此参数设置，同时设置时优先取此参数。
- `debugMode`  测试更新功能，总会弹出更新窗口
- `ifErrorType`   1. 表示更新检测失败时,什么都不显示 2. 表示显示确定按钮 3. 表示显示确定和取消按钮。 用于强制更新时是否能跳过

## <span id="i4">爱思助手</span>
iOS SDK版本v2.1.0  

修改配置文件, As2中的2修改为在爱思后台申请的appId.

	<key>CFBundleURLSchemes</key>
	<array>
		<string>As2</string>
	</array>


其他
TODO
