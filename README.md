# Ane Game SDK 

一个方便AIR手游快速接入国内各种Android/iOS渠道的项目

## 支持渠道

- 奇虎360(qihoo360)
- UC(uc)
- 小米(xiaomi)
- 安智(anzhi)
- 百度(baidu)
- 当乐(downjoy)
- 金立(gionee)
- 海马(haima)
- 华为(huawei)
- 快用(kuaiyong)
- 联想(lenovo)
- OPPO(oppo)
- 搜狗(sogou)
- 同步推(tongbutui)
- 豌豆荚(wandoujia)
- ...

>括号中字符为本项目规划的渠道ID

## 目录结构

	ane -- ane目录
	build -- 构建脚本及配置文件
	demo -- 示例程序
	sdks -- android及ios sdk原生资源及库
	src -- java端及as端源码


## 构建
项目使用[Apache Ant](http://ant.apache.org)构建。Flex SDK路径在`build/build.config`中配置。在`build`目录下执行
 
	ant -Did=渠道ID

示例:

	ant -Did=xiaomi
	
生成封装了小米原生SDK的ANE，生成的ane位于目录`ane/xiaomi`中

	ant -Did=xiaomi android

使用生成的ANE打包Demo程序，生成apk。将`android`改为`ios`即生成ipa
> sdks下的目录含_ios后缀的表明渠道支持iOS平台。编译出的ane也会同时支持android和ios平台

**如果Demo打包出错，请使用`build/other`目录下的`adt.jar`替换Flex SDK目录下的`lib/adt.jar`**[原因及其他解决办法](./build/other/)

测试环境为Flex4.6 with AIR19. Ant 1.9.1+。欢迎测试反馈


## ANE使用
ANE对所有渠道封装成了通用的方法和事件。主要包括`初始化`,`登录`,`注销`,`支付`

### 事件
事件通过StatusEvent回传，其中`event.code`为响应事件，包括:
	
	HeTuGameSDK.SDK_HETU_INIT  初始化事件
	HeTuGameSDK.SDK_HETU_LOGIN  登录事件，会返回登录的结果。如`uid`,`token`
	HeTuGameSDK.SDK_HETU_LOGOUT  注销事件
	HeTuGameSDK.SDK_HETU_PAY     支付事件

`event.level`为返回的json数据，当所做操作成功时， 数据带data属性， 否则返回error。如:

	{"data":"success"}    成功，数据仅为一个success的字符串
	{"error":{"code":11,"message":"pay cancel"}}    失败，返回详细的失败信息
	{"data":{"uid":"xxx","token":"xxx"}}    登录成功后返回，用户ID和Token


### 方法
创建SDK对象，指定渠道并设置事件监听

	var sdk:HeTuGameSDK = new HeTuGameSDK('com.l1fan.ane.渠道ID');
	sdk.addEventListener(StatusEvent.STATUS,statusHandler);

初始化，主要包含的属性 `appId`,`appKey`,`appSecret`。更多属性可查看[ANE详细使用文档](./ane)
 
	var init:Object = new Object();
	init.appId = "xxx";
	init.appKey = "xxx";
	sdk.init(JSON.stringify(init));

用户登录
	
	sdk.userLogin();

用户退出

	sdk.userLogout();

支付，更多属性查看[ANE详细使用文档](./ane)

	var pay:Object = new Object();
	pay.amount = 100;       //支付金额，以分为单位
	pay.pname = "100元宝";  //支付商品名
	pay.pid = "4001";       //支付商品ID
	pay.orderId = "xxx";    //订单号
	pay.notifyUrl = "xxx";   //支付结果通知URL地址
	pay.ext = "xxx";        //扩展参数，会原样返回给服务端
	sdk.pay(JSON.stringify(pay));


### 其他
	
	sdk.call(action,data);

各个渠道都有一些特殊方法可能需要调用，可以通过`call`的方式进行，其中`action`为要执行的操作，`data`为参数。


## 开发
ActionScript中每调用一次方法，即为向ANE发送一个Action。在java中对应渠道SDK中一个公开的无参方法。




