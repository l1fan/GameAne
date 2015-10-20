#其他库文件说明

由于AIR调用aapt打包apk时强制了资源本地化检测，如果资源不符合要求会打包失败。如SDK直接在布局中使用字符串，而非引用strings.xml中资源。解决的办法有两种

1. 如果资源可以修改，按Android的本地化建议将资源内容改为引用的方式。
2. 修改AIR的adt打包工具，去掉强制检测。此处的`adt.jar`从AIR18 SDK中提取修改，可以直接覆盖使用

###如何修改打包工具
找到Flex SDK路径下lib/adt.jar文件，解压该jar包。找到com/adobe/air/apk/APKOutputStream.class文件，使用十六进制编辑器打开。搜索字符`-z`,将其修改为`-v`。保存之后将新文件压缩回jar包。可使用如下命令更新jar包
	
	jar uvf adt.jar ./com/adobe/air/apk/APKOutputStream.class 

###Android依赖库
编译Android代码时需要下面两个库

	android.jar
	android-support-annotations.jar
	
均从Android SDK(API22)中取出，从其他地方取得的库文件请注意与官方文件做Hash校验。
如果安装有Android SDK可以修改build.xml中`android.classpath`为SDK中文件路径



