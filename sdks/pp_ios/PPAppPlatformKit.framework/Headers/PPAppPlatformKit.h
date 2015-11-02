//
//  PPAppPlatformKit.h
//  PPAppPlatformKit
//
//  Created by panos on 14-9-16.
//  Copyright (c) 2014年 广州铁人网络科技有限公司. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "PPAppPlatformKitDelegate.h"
#import "PPAppPlatformKitPublicDefine.h"

@interface PPAppPlatformKit : NSObject

/**
 *  游戏默认充值金额，大于等于1的整数 ，可选（默认10）
 */
@property (nonatomic, assign) int rechargeAmount;

/**
 *  是否开启调试日志，可选（默认不开启）
 */
@property (nonatomic, assign) BOOL isOpenNSlogData;

/**
 *  浮标位置，可选 (默认CGPoint(30,30))
 */
@property (nonatomic, assign) CGPoint buoyPosition;

/**
 *  单例 初始化PPAppPlatformKit
 */
+ (instancetype)share;

/**
 *  游戏基本信息（必须设置，且在startPPSDK方法之前设置）
 *
 *  @param delegate SDK代理[强引用]
 *  @param appId    游戏AppId
 *  @param appKey   游戏appKey
 */
- (void)setupWithDelegate:(id<PPAppPlatformKitDelegate>)delegate
                    appId:(int)appId
                   appKey:(NSString *)appKey;

/**
 *  开启SDK加载流程（只允许调用一次,已做限制）
 */
- (void)startPPSDK;

/**
 *  登录
 *  1、若已经登录，则不做任何处理
 *  2、若设置了自动登录，则直接执行登录
 *  3、若未设置自动登录，则弹出登录页面
 */
- (void)login;

/**
 *  弹出SDK主页，若未登录,弹出登录页面(可选)
 */
- (void)showSDKCenter;

/**
 *  注销登录，回调ppLogOffCallBack
 */
- (void)logout;

/**
 *  获取当前用户登录状态
 *
 *  @return 0为未登录，1为登录
 */
- (int)loginState;

/**
 *  处理支付宝客户端唤回后的回调数据
 *  处理支付宝客户端通过URL启动App时传递的数据,需要在 application:openURL:sourceApplication:annotation:或者application:handleOpenURL中调用。
 *  需同时在URLTypes里面添加urlscheme 为teiron + AppId,
 *
 *  @param paramURL 启动App的URL
 */
- (void)alixPayResult:(NSURL *)paramURL;

/**
 *  兑换道具，账户余额必须大于道具金额，否则失败且无回调
 *
 *  @param paramPrice     商品价格，价格必须大于等于1的int型
 *  @param paramBillNo    商品订单号，订单号长度勿超过30位，无特殊符号
 *  @param paramBillTitle 商品名称
 *  @param paramRoleId    角色id，若无请填写0
 *  @param paramZoneId    开发者中心后台配置的分区id,若无请填写0
 */
- (void)exchangeGoods:(int)paramPrice
               BillNo:(NSString *)paramBillNo
            BillTitle:(NSString *)paramBillTitle
               RoleId:(NSString *)paramRoleId
               ZoneId:(int)paramZoneId;

@end
