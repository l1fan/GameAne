#!/bin/bash

# remove air prefix of package id
export AIR_NOANDROIDFLAIR=true

# ant -q -Did=qihoo360
# ant -q -Did=uc
# ant -q -Did=xiaomi
# ant -q -Did=anzhi 
# ant -q -Did=baidu
# ant -q -Did=downjoy
# ant -q -Did=gionee
# ant -q -Did=haima
# ant -q -Did=huawei
# ant -q -Did=kuaiyong
# ant -q -Did=lenovo
# ant -q -Did=oppo
# ant -q -Did=sogou
# ant -q -Did=tongbutui
# ant -q -Did=wandoujia

ant -q -Did=qihoo360 
ant -q -Did=qihoo360 android
adb install -r ../bin/qihoo360_demo.apk 
