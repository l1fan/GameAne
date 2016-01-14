//
//  ZHPayPlatform.h
//  AccountTestDemo
//
//  Created by changye on 14-4-14.
//  Copyright (c) 2014年 haima. All rights reserved.
//  Ver:1.3.8

#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "ZHPayDefines.h"

#pragma mark - 平台初始化 *******************************************************

@interface ZHPayPlatform : NSObject

/**
 *  @brief 初始化sdk
 *
 *  @param appid 注册时分配的appId
 *  @param test  测试更新模式。为YES时，则不论版本号，肯定提示更新；
 *  @param delegate 设置代理后可接收登录更新等相关回调
 *  @param checkType 当检查更新失败时，控制是否允许跳过强制更新； 
 *                   0：不提示检查失败（直接跳过并进入游戏） 
 *                   1：不允许跳过（alert只有一个"重新检查"按钮） 
 *                   2：允许选择跳过更新（alert有两个按钮，一个“否”，一个“重新检查”）
 *
 *  @note  正式上线时请务必将test改为NO。若您需要设置强制更新，请登录海马后台设置
 *
 */
+ (void)initWithAppId:(NSString *)appid
         withDelegate:(id<ZHPayDelegate>)delegate
       testUpdateMode:(BOOL)test
 alertTypeCheckFailed:(int)checkType;

/**
 *  @brief 设置ZHPayDelegate
 */
+ (void)setZHPayDelegate:(id<ZHPayDelegate>)delegate;

/**
 *  @brief 设置日志输出
 *
 *  @param enable 默认为NO,不打印日志
 */
+ (void)setLogEnable:(BOOL)enable;

/**
 *  @brief 设置支持设备方向
 *
 *  @param orientation 默认为UIInterfaceOrientationMaskAll
 */
+ (void)setSupportOrientation:(UIInterfaceOrientationMask)orientation;

/**
 *  @brief 获取AppId
 *
 *  @note  初始化成功后才有值
 */
+ (NSString *)getAppId;

/**
 *  @brief 获取SDK版本号
 */
+ (NSString *)getSDKVersion;

/**
 *  @brief 为解决银联设置支持设备方向
 */
+ (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window;

/**
 *  @brief 为解决银联设置支持设备方向
 */
+ (BOOL)shouldAutorotate;

@end


#pragma mark - 平台登录相关方法 **************************************************

@interface ZHPayPlatform(ZHPayLogin)

/**
 *  @brief 是否已登录
 */
+ (BOOL)isLogined;

/**
 *  @brief  开始登录
 */
+ (void)startLogin;
/**
 *  @brief  静默登录(无登录视图)
 */
+ (void)startLoginBackground;
/**
 *  @brief  开始注销
 *
 *  @note   注销后会回调成功方法，之后可自行调用startLogin重新登录
 */
+ (void)startLogout;

/**
 *  @brief 直接切换账号
 *
 *  @param shouldLogout 是否注销当前用户
 *  @note  sdk会弹出登录窗口
 */
+ (void)switchAccount:(BOOL)shouldLogout;

/**
 *  @brief  进入用户中心界面
 *
 *  @note   需先登录
 *  @result 未登录或有其他界面正在显示，将返回NO. 成功返回YES
 */
+ (BOOL)showUserCenter;

/**
 *  @brief 手动检查更新，检查完毕会有回调 (详见ZHPayDefines.h)
 *
 *  @note  sdk初始化时会自动检查更新，仅在您的设置菜单中若有检查更新功能时，可调此API
 */
+ (void)checkUpdate;

/**
 *  @brief 用户异地登录检查，建议在游戏重要节点检查
 *
 *  @note  若检测到用户异地登录，SDK会回调ZHPayDidLogout方法，此时您需要将游戏退出；没有异地登录时不会有任何回调。
 */
+ (void)checkLoginStatus;
@end


#pragma mark - 用户信息相关方法 **************************************************

@interface ZHPayPlatform(ZHPayInformation)

/**
 *  @brief 获取登录账号信息
 *
 *  @note  登录前该值为nil，登录后该值为当前登录的用户信息
 */
+ (ZHPayUserInfo *)getUserInfo;

@end


#pragma mark - 支付API *****************************************************

@interface ZHPayPlatform(ZHPayOrder)

/**
 *  @brief  发起订单请求，未登录时会自动弹出登录框让用户先登录
 *
 *  @param  orderInfo 订单信息
 *  @result 若订单格式有误，返回NO. 无误返回YES.
 */
+ (BOOL)startPay:(ZHPayOrderInfo *)orderInfo delegate:(id<ZHPayResultDelegate>)payResultDelegate;

/**
 *  @brief  查询订单是否成功
 *
 *  @param  orderId	支付订单号
 */
+ (void)checkPayOrder:(NSString *)orderId delegate:(id<ZHPayCheckDelegate>)checkOrderDelegate;

@end





