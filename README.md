# Ane Game SDK

一个方便AIR手游快速接入国内各种Android/iOS渠道的项目

## 支持渠道

|渠道				|id					|Android| iOS|版本(Android/iOS)|
|----------|------------|-------|---|---|------------|
|奇虎360		|`qihoo360`   |Y			|		|v1.1.8(402) |
|UC					|`uc`				|Y			|		|v3.5.3.1 |
|小米				|`xiaomi`		|Y			|		|v4.4.33 |
|安智				|`anzhi`		|Y			|		|v3.5.2 |
|百度				|`baidu`		|Y			|		|v3.5.2 |
|当乐				|`downjoy`	|Y			|		|v4.2 |
|金立				|`gionee`		|Y			|		|v3.0.7.o |
|海马				|`haima`		|Y			|Y	|v1.1.7/v1.3.7|
|华为				|`huawei`		|Y			|		|v3.31.70.305 |
|快用				|`kuaiyong`	|Y			|Y	|v2.0.3/v2.2.3 |
|联想				|`lenovo`		|Y			|		|v2.6.1 |
|OPPO				|`oppo`			|Y			|		|v2.0.0 |
|搜狗				|`sogou`		|Y			|		| v1.4.30 |
|同步推			|`tongbutui`|Y			|Y	|v4.2.0  |
|豌豆荚			|`wandoujia`|Y			|		|v4.0.7		|
|拇指玩			|`muzhiwan`	|Y			|		|v3.0.9		|
|应用汇			|`appchina`	|Y			|		|v6.2.0		|
|魅族				|`meizu`		|Y			|		|v2.1			|
|优酷				|`youku`		|Y			|		|v1.9.6		|
|卓易				|`droi`			|Y			|		|v2.1.5		|
|虫虫				|`cc`				|Y			|		|v1.4.0 |
|靠谱				|`kaopu`		|Y			|		|v5.2  |
|机锋       |`gfan`     |Y     |    |v4.2   |
|4399      |`m4399`    |Y     |    |v2.7.0.3 |
|泡椒       |`paojiao`  |Y     |    |v3.5.4  |
|TT语音     |`tt`				|Y		|			|v1.0.0-C108|
|iTools			|`itools`		|			|Y	|	v2.5.0	|
|爱思助手			|`i4`				|			|Y	|v2.1.0		|
|果盘叉叉助手	|`xx`				|Y			|Y	 |v2.1.1/v2.1.0	|
|xy助手			|`xy`				|			|Y	  |v2.2.1		|
|pp助手			|`pp`				|			|Y	 |vS155D1611|
|乐8助手			|`le8`			|			|Y	| v1.0.7 |
|i苹果			|`iiapple`	|			|Y		| v1.2.9|



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
