//
//  LDMobileSDK.h
//
//  Created by LD on 15-08-03.
//  Copyright (c) 2015年 L8. All rights reserved.
//  Ver:1.0.6

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "LDMobile.h"

#pragma mark - 平台初始化等 *******************************************************

@interface LDMobileSDK : NSObject

/**
 *  @brief 初始化sdk
 *
 *  @param appid 注册时分配的appId
 *  @param delegate 设置代理后可接收登录更新等相关回调
 *  @param orientation 设置SDK支持方向
 *  @param alertType 网络原因导致检查更新失败时是否可进入游戏 0:不提示检查失败，可直接进入游戏  1:提示需重新检查，不可进入游戏  2:提示检查失败，但可以点击忽略进入游戏
 */
+ (void)setAppId:(NSString *)appid
        delegate:(id<LDMobileDelegate>)delegate
supportOrientation:(LDSupportOrientation)orientation
   updateAlertType:(int)alertType;

/**
 *  @brief 是否已登录
 */
+ (BOOL)isUserLogined;

/**
 *  @brief 获取用户信息
 *
 *  @note  登录前该值为nil，登录后为已登录用户信息
 */
+ (LDMobileUser *)getCurrentUser;

/**
 *  @brief 设置日志输出
 *
 *  @param isPrint 默认为NO,不打印日志
 */
+ (void)setLogPrint:(BOOL)isPrint;

/**
 *  @brief 设置SDK支持旋转方向
 *
 *  @param 默认为 kLDOrientationMaskLandscape 横屏
 */
+ (void)setSupportRotateOrientation:(LDSupportOrientation)orientation;

/**
 *  @brief 选择区服后可上传所在大区/服名称,角色名称,玩家其他信息等，以便用户忘记时帮忙找回
 *
 *  @param params 字符串，任意格式组合，例如"风云二区|流浪之徒|3级"
 */
+ (void)setGameParams:(NSString *)params;

@end


#pragma mark - 登陆支付等相关方法 **************************************************

@interface LDMobileSDK(LDMobileLogin)

/**
 *  @brief  开始登录
 */
+ (void)beginLogin;

/**
 *  @brief  开始注销
 *
 *  @note   注销后会回调成功方法，之后可自行调用beginLogin重新登录
 */
+ (void)beginLogout;

/**
 *  @brief 切换用户
 *
 *  @param logoutCurrentUser 是否注销当前用户
 *  @note  sdk会弹出登录窗口
 */
+ (void)changeUser:(BOOL)logoutCurrentUser;

/**
 *  @brief  展示账户中心界面
 *
 *  @note   需先登录
 *  @result 未登录或有其他界面正在显示，将返回NO. 成功返回YES
 */
+ (BOOL)presentUserCenter;

/**
 *  @brief 刷新用户登录状态
 *
 *  @note  若检测到用户异地登录，SDK会回调LDMobileDidLogout方法，此时您需要将游戏退出
 */
+ (void)refreshUserStatus;

/**
 *  @brief 手动检查更新，检查完毕会有回调 (详见LDMobile.h)
 *
 *  @note  SDK初始化时会自动检查更新
 */
+ (void)checkNewVersion;

/**
 *  @brief  发起订单，未登录时会自动弹出登录框让用户先登录
 *
 *  @param  order 订单信息
 *  @result 若订单格式有误，返回NO. 无误返回YES.
 */
+ (BOOL)createOrder:(LDMobileOrder *)order withDelegate:(id<LDMobileOrderDelegate>)payResultDelegate;

/**
 *  @brief  手动检查订单是否成功
 *
 *  @param  orderSerial	支付订单号
 */
+ (void)checkOrder:(NSString *)orderSerial withDelegate:(id<LDMobileOrderDelegate>)checkOrderDelegate;

@end

