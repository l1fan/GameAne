//
//  AsInfoKit.h
//  AsSDKDemo
//
//  Created by i4.cn on 14-3-13.
//  Copyright (c) 2014年 i4.cn. All rights reserved.
//

#import <Foundation/Foundation.h>

@class UIViewController;

@interface AsInfoKit : NSObject

/**
 *  appId
 */
@property (nonatomic, assign) int appId;
/**
 *  appKey
 */
@property (nonatomic, retain) NSString *appKey;
/**
 *  设置为 YES 时，控制台会显示网络请求的数据；设置为 NO 时，控制台不会有网络请求的数据显示
 */
@property (nonatomic, assign) BOOL logData;
/**
 *  设置为 YES 时，关闭充值功能；设置为 NO 时，打开充值功能
 */
@property (nonatomic, assign) BOOL closeRecharge;
/**
 *  设置为 YES 时，为充值并兑换道具，爱思平台服务器会给游戏方服务器发送兑换道具成功或失败的消息；设置为 NO 时，为先打开爱思充值页面，给账户充值，将人民币兑换为爱思币
 */
@property (nonatomic, assign) BOOL longComet;
@property (nonatomic, assign) BOOL verifyingUpdate;
/**
 *  关闭充值功能的提示语
 */
@property (nonatomic, retain) NSString *closeRechargeAlertMessage;
/**
 *  根视图控制器
 */
@property (nonatomic, assign) UIViewController *rootViewController;
/**
 *  是否隐藏登录关闭按键
 */
@property (nonatomic, assign) BOOL isHiddenCloseButtonOnAsLoginView;
/**
 *  是否使用银联wap支付
 */
@property (nonatomic, assign) BOOL isWapUnionpay;
/**
 *  退出登录时是否退出游戏
 */
@property (nonatomic, assign) BOOL isExitGame;

/**
 *  返回一个静态单例对象
 *
 *  @return 单例对象
 */
+ (AsInfoKit *)sharedInstance;

/**
 *  第三方支付结果处理,2.0新增
 *
 *  @param url               url
 *  @param sourceApplication sourceApplication
 */
- (void)payResult:(NSURL *)url sourceApplication:(NSString *)sourceApplication;

@end