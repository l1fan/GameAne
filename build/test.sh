#! /bin/bash

# sh test.sh anzhi android

# remove air prefix of package id
export AIR_NOANDROIDFLAIR=true

echo "test $1 $2"

appid=$1
# android or ios
apptype=$2 || "android"

build_ane()
{ 
  ant -q -Did=$appid
}

build_app(){
  ant -q -Did=$appid $apptype
}

install(){
  echo "安装apk install..."
  adb install -r ../bin/${appid}_demo.apk
}

case $apptype in
  android ) 
    build_app
    install
    ;; 
  ios ) build_app;;
  in ) install;;
  *) 
    apptype="android"
    build_ane
    build_app
    install
  ;;

esac
