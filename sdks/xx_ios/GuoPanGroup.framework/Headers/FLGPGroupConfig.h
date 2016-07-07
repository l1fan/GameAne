//
//  FLGPGroupConfig.h
//  GuoPanGroup
//
//  Created by Hays on 15/9/21.
//  Copyright (c) 2015年 GuoPan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "FLGPGroupRootViewController.h"

@class FLGPGroupInfo;

typedef void (^FLGPNoLoginBlock)(NSString *warnWording);
typedef void (^FLGPLockAutoRotateBlock)(BOOL autoRotate);
typedef NSArray* (^FLGPLoadLocalVideoInfoBlock)();
typedef void (^FLGPBindPhoneBlock)(NSString *warnWording);

// 以后用到其他再新增
typedef NS_ENUM(NSUInteger, FLGPProductID) {
    FLGPProductID_None = 0,
    FLGPProductID_XXGameAssistant = 101,
    FLGPProductID_GPGAME_SDK = 104,
};

// SDK使用时C++编译需要更改为GNU++11和libc++
@interface FLGPGroupConfig : NSObject

@property (nonatomic, readonly) CGSize groupPanelSize;  // 小组界面view的size

#pragma mark - 初始化参数

@property (nonatomic) BOOL statusBarHidden; // 设置panel center获取的status bar状态
@property (nonatomic) BOOL isLockBgViewTap; // 是否锁定点击空白处不收起

@property (nonatomic, copy) FLGPNoLoginBlock        noLoginBlock; // 触发登录态失效or没有登录情况下执行
@property (nonatomic, copy) FLGPLockAutoRotateBlock lockAutoRotateBlock; // 锁定旋转，视频全屏播放时使用 required

// deprecated, 此需求暂时去掉
@property (nonatomic, copy) FLGPLoadLocalVideoInfoBlock loadLocalVideoInfoBlock; // 加载本地视频block，返回FLGPVideoInfo数组，外部使用转换为FLGPVideoInfo，不赋值则表示不支持视频发帖
@property (nonatomic, copy) FLGPBindPhoneBlock          bindPhoneBlock; // 提醒绑定手机block

@property (nonatomic, strong) UIView                    *globalBackgroundView; // rootVC 里的bgView
@property (nonatomic, strong) UIViewController          *rootVC; // rootVC;
@property (nonatomic, weak) FLGPGroupRootViewController *groupVC; // 内部使用，当前显示中的小组页

@property (nonatomic, strong) FLGPGroupInfo *group; // 当前小组，内部使用
// 用户登录信息
@property (nonatomic, readonly) NSInteger   uin;
@property (nonatomic, readonly) NSString    *loginKey;
@property (nonatomic, readonly) NSString    *nickName;
@property (nonatomic, readonly) UIImage     *headIcon;
@property (nonatomic, readonly) BOOL        isLogin;

// 初始化XXGroupProto::UserInfo基础信息参数
@property (nonatomic) NSInteger         channelId; // channel id
@property (nonatomic) FLGPProductID     productId; // product id
@property (nonatomic, copy) NSString    *version; // version
@property (nonatomic, copy) NSString    *buildNo; // build no, optional param
@property (nonatomic, copy) NSString    *deviceId; // device id, danmuku use, optional param
@property (nonatomic, copy) NSString    *secureKey; // 加密/解密key，不设置则使用默认果盘的key，SDK有专用的加密key
@property (nonatomic, copy) NSString    *remoteUUID; // 钩子从center读取的uuid

// 项目相关
@property (nonatomic, copy) NSString    *bundlePath; // 资源文件所在路径，不设置图片读不了
@property (nonatomic, copy) NSString    *emotionLibPath; // 表情库路径

// ServerAPI
@property (nonatomic, copy) NSString    *serverAPIForBBSData;
@property (nonatomic, copy) NSString    *serverAPIForBBSPosts;
@property (nonatomic, copy) NSString    *serverAPIForBBSGroup;
@property (nonatomic, copy) NSString    *serverAPIForBBSMessageCenter;

+ (instancetype)sharedInstance;

// 请求未付回复消息数量；finish：YES/NO (请求成功/失败)；count：未读回复消息数量
- (void)asyncRequestReplyUnReadCountCompletion:(void(^)(BOOL finish, NSInteger count))completion;

// 更新登录信息，uin设置0/loginKey设置空为注销登录or登录态失效
- (void)updateLoginInfoWithUin:(NSInteger)uin loginKey:(NSString *)loginKey nickName:(NSString *)nickName headIcon:(UIImage *)image;

// 外部设置小组基础信息
- (void)setGroupInfo:(NSInteger)fid groupName:(NSString *)groupName;

// 旋转响应
- (void)orientationDidChange:(NSNotification *)notification;

// 锁定点击空白处不收起
+ (void)backgroundEventTap;

// 手势需要过滤的类判断，YES表示view是需要响应的类
+ (BOOL)gestureFilterClassWithTouchView:(UIView *)view;

@end
