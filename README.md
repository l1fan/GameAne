# Ane Game SDK

一个方便AIR手游快速接入国内各种Android/iOS渠道的项目

## 支持渠道

|渠道				|id					|Android| iOS|
|----------|------------|-------|---|
|奇虎360		|`qihoo360`   |Y			|		|
|UC					|`uc`				|Y			|		|
|小米				|`xiaomi`		|Y			|		|
|安智				|`anzhi`		|Y			|		|
|百度				|`baidu`		|Y			|		|
|当乐				|`downjoy`	|Y			|		|
|金立				|`gionee`		|Y			|		|
|海马				|`haima`		|Y			|		|
|华为				|`huawei`		|Y			|		|
|快用				|`kuaiyong`	|Y			|Y	|
|联想				|`lenovo`		|Y			|		|
|OPPO				|`oppo`			|Y			|		|
|搜狗				|`sogou`		|Y			|		|
|同步推			|`tongbutui`|Y			|Y	|
|豌豆荚			|`wandoujia`|Y			|		|
|拇指玩			|`muzhiwan`	|Y			|		|
|应用汇			|`appchina`	|Y			|		|
|魅族				|`meizu`		|Y			|		|
|优酷				|`youku`		|Y			|		|
|iTools			|`itools`		|Y			|Y	|
|爱思助手			|`i4`				|			|Y	|
|果盘叉叉助手	|`xx`				|			|Y	 |
|xy助手			|`xy`				|			|Y	  |
|pp助手			|`pp`				|			|Y	 |
|乐8助手			|`le8`			|			|Y	|
|i苹果			|`iiapple`	|			|Y		|



## 目录结构

	ane -- ane目录
	build -- 构建脚本及配置文件
	demo -- 示例程序
	sdks -- android及ios sdk原生资源及库
	src -- java端及as端源码


## 构建
项目使用[Apache Ant](http://ant.apache.org)构建。Flex SDK路径在`build/build.config`中配置。在`build`目录下执行

	ant -Did=渠道ID

---
示例:

	ant -Did=xiaomi

生成封装了小米原生SDK的ANE，生成的ane位于目录`ane/xiaomi`中

---
	ant -Did=xiaomi android

使用生成的ANE打包Demo程序，生成apk。将`android`改为`ios`即生成ipa。查看[Demo测试参数](https://github.com/l1fan/GameAne/wiki/Demo%E5%8F%82%E6%95%B0)。  

---
**如果Demo打包出错，请使用`build/other`目录下的`adt.jar`替换Flex SDK目录下的`lib/adt.jar`**[原因及其他解决办法](./build/other/)

>sdks下的目录含_ios后缀的表明渠道支持iOS平台。编译出的ane也会同时支持android和ios平台

> 测试环境为Flex4.6 with AIR20, Ant 1.9.1+, JDK7。欢迎测试反馈


## 文档

- [ANE使用文档](https://github.com/l1fan/GameAne/wiki/%E4%BD%BF%E7%94%A8ANE)
- [ANE配置文件说明](https://github.com/l1fan/GameAne/wiki/%E9%85%8D%E7%BD%AE%E6%B8%A0%E9%81%93)


## 开发
ActionScript中每调用一次方法，即为向ANE发送一个Action。在渠道SDK中对应一个公开的无参方法。
在java中通过`getAction()`获取操作,`getData()`、`getJsonData()` 方法获取数据
