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
- iTools(itools)
- 爱思助手(i4)
- 果盘叉叉助手(xx)
- xy助手(xy)
- pp助手(pp)
- 乐8助手(le8)
- i苹果(iiapple)
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

使用生成的ANE打包Demo程序，生成apk。将`android`改为`ios`即生成ipa。查看[Demo测试参数](https://github.com/l1fan/GameAne/wiki/Demo%E5%8F%82%E6%95%B0)
**如果Demo打包出错，请使用`build/other`目录下的`adt.jar`替换Flex SDK目录下的`lib/adt.jar`**[原因及其他解决办法](./build/other/)

> sdks下的目录含_ios后缀的表明渠道支持iOS平台。编译出的ane也会同时支持android和ios平台
> 测试环境为Flex4.6 with AIR19. Ant 1.9.1+ JDK1.7。欢迎测试反馈


## 文档

- [ANE使用文档](https://github.com/l1fan/GameAne/wiki/%E4%BD%BF%E7%94%A8ANE)
- [ANE配置文件说明](https://github.com/l1fan/GameAne/wiki/%E9%85%8D%E7%BD%AE%E6%B8%A0%E9%81%93)


## 开发
ActionScript中每调用一次方法，即为向ANE发送一个Action。在渠道SDK中对应一个公开的无参方法。
在java中通过`getAction()`获取操作,`getData()`、`getJsonData()` 方法获取数据





