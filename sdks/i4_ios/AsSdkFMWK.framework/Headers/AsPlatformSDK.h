//
//  AsPlatformSDK.h
//  AsPlatformSDK
//
//  Created by i4.cn on 14-2-20.
//  Copyright (c) 2014年 i4.cn. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, AsPageCode) {
    /**
     * 关闭接口为登录页面
     */
    AsLoginViewPageCode	= 1,
    /**
     * 关闭接口为注册
     */
    AsRegisterViewPageCode = 2
};

typedef NS_ENUM(NSInteger, AsPayResultCode) {
    /**
     * 购买成功
     */
	AsPayResultCodeSucceed	= 0,
    /**
     * 购买失败
     */
    AsPayResultCodeFailed = 1,
    /**
     * 支付结果未知，需要去服务端查询
     */
    AsPayResultCodeUnknown	= 6
};

/**
 * @protocol  AsPlatformSDKDelegate
 * @brief   SDK接口回调协议
 */
@protocol AsPlatformSDKDelegate <NSObject>

@required

/**
 * @brief   支付结果回调
 * @param   INPUT   paramAsPayResultCode       接口返回的结果编码
 * @return  无返回
 */
- (void)asPayResultCallBack:(AsPayResultCode)paramPayResultCode;

/**
 * @brief   验证更新成功后
 * @noti    分别在非强制更新点击取消更新和暂无更新时触发回调用于通知弹出登录界面
 * @return  无返回
 */
- (void)asVerifyingUpdatePassCallBack;


/**
 * @brief   登录成功回调
 * @param   INPUT   paramToken       字符串token
 * @return  无返回
 */
- (void)asLoginCallBack:(NSString *)paramToken;


/**
 * @brief   关闭登录页面后的回调
 * @param   INPUT   paramAsPageCode       接口返回的页面编码
 * @return  无返回
 */
- (void)asCloseLoginViewCallBack:(AsPageCode)paramPageCode;


/**
 * @brief   注销后的回调
 * @return  无返回
 */
- (void)asLogOffCallBack;

@end

@interface AsPlatformSDK : NSObject


@property (nonatomic, assign) id<AsPlatformSDKDelegate> delegate;

/**
 * @brief     初始化SDK信息
 * @return    AsPlatformSDK    生成的AsPlatformSDK对象实例
 */
+ (AsPlatformSDK *)sharedInstance;


/**
 * @brief     弹出爱思登录页面
 * @return    无返回
 */
- (void)showLogin;


/**
 * @brief     弹出爱思中心页面
 * @return    无返回
 */
- (void)showCenter;


/**
 * @brief     兑换道具
 * @noti      只有余额大于道具金额时候才有客户端回调。余额不足的情况取决与LongComet参数，LongComet = YES，则为充值兑换。回调给服务端，LongComet = NO ，则只是打开充值界面
 * @param     INPUT paramPrice      商品价格，价格必须为大于等于1的int类型
 * @param     INPUT paramBillNo     商品订单号，订单号长度请勿超过30位，参有特殊符号
 * @param     INPUT paramBillTitle  商品名称
 * @param     INPUT paramRoleId     角色id，回传参数，若无请填0
 * @param     INPUT paramZoneId     无效参数，传0即可
 * @return    无返回
 */
- (void)exchangeGoods:(int)paramPrice BillNo:(NSString *)paramBillNo BillTitle:(NSString *)paramBillTitle RoleId:(NSString *)paramRoleId ZoneId:(int)paramZoneId;


/**
 * @brief     SDK检查游戏版本更新
 * @return    无返回
 */
- (void)checkGameUpdate;

/**
 * @brief   注销后的回调
 * @return  无返回
 */
- (void)asLogout;

/**
 * @brief     获取当前用户id
 * @return    返回当前用户id
 */
- (u_int64_t)currentUserId;


/**
 * @brief     获取当前用户名
 * @return    返回当前用户名
 */
- (NSString *)currentUserName;

/*--------------------2.1.0新增接口-------------------------
 *  @return   返回当SDK版本
 */
- (NSString *)currentSDKVersion;

/**
 *  关闭充值功能的回调
 */
-(void) asCloseChargeCallBack;
/**
 *  显示浮动菜单
 */
- (void)showFloatIcon;

/**
 *  隐藏浮动菜单
 */
- (void)hideFloatIcon;

@end
