//
//  PPAppPlatformKit.h
//  PPAppPlatformKit
//
//  Created by mac on 14/8/5.
//  Copyright (c) 2014年 TR. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PPAppPlatformKitDelegate.h"
#import "TRBollView.h"

@class TRGlobalConfig;
@interface PPAppPlatformKitDylib : NSObject

@property (nonatomic, assign) BOOL isSDKCenterShowing;//是否正在显示 [默认NO];
@property (nonatomic, weak) id<PPAppPlatformKitDelegate> platformKitDelegate; //静态库代理

@property (nonatomic, weak) TRMainViewController *mainViewController;
@property (nonatomic, strong) TRBollView *bollView;

/**
 *  初始化动态库
 *
 *  @return PPAppPlatformKitDylib 单利
 */
+(instancetype)share;

/**
 *  设置基本信息
 *
 *  @param SDKConfig 游戏基本配置
 */
- (void)initSDKWithConfig:(TRGlobalConfig*)SDKConfig;

/**
 *  弹出SDK主页，若未登录,弹出登录页面
 */
- (void)showSDKCenter;

/**
 *  登录
 *  1、若已经登录，则不做任何处理
 *  2、若设置了自动登录，则直接执行登录
 *  3、若未设置自动登录，则弹出登录页面
 */
- (void)login TR_DYLIB_AVAILABLE_SDK(__DYLIB_1_5_2_1);

/**
 *  获取用户登录状态
 *
 *  @return 0为未登录，1为登录
 */
- (int)getLoginState;

/**
 * @brief     注销用户，清除自动登录状态
 * @return    无返回
 */
- (void)logout;

/**
 * @brief     显示登录成功后欢迎界面
 * @return    无返回
 */
- (void)showLoginSuccessView;

/**
 * @brief  处理支付宝客户端唤回后的回调数据
 * @param  paramURL     启动App的URL
 * @return 无返回
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

/**
 *  设置浮标
 *
 *  @param origin    浮标卯点坐标
 *  @param isShow    是否显示
 */
- (void)setupBollView:(CGPoint)origin
                 show:(BOOL)isShow;

/**
 *  更改浮标位置和展开方向
 *
 *  @param center    浮标原点坐标
 */
- (void)exchangeBollView:(CGPoint)origin;

/**
 *  显示或隐藏浮标
 *
 *  @param isShow (yes显示，no隐藏)
 */
- (void)showBollView:(BOOL)isShow;


/**
 *  展开或收起浮标
 *
 *  @param isStretch (yes展开，no收起)
 */
- (void)stretchBollView:(BOOL)isStretch;

/**
 *  获取动态库代码中设置的数字版本号
 *
 *  @return 动态库数字版本号
 */
- (int)getDylibDigitVersion;

/**
 *  private method (私有方法)
 *
 *  获取动态库配置状态（0：测试状态， 1：正式状态）
 */
- (int)privateDylibContext;

@end


