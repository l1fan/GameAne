//
//  ZHPayDefines.h
//  ZHAccountSDK
//
//  Created by changye on 14-4-16.
//  Copyright (c) 2014年 haima. All rights reserved.
//  SDKVer 1.3.1


#ifndef ZHAccountSDK_ZHAccountDefines_h
#define ZHAccountSDK_ZHAccountDefines_h

#pragma mark - 信息类 **********************************************************

/**
 *	@brief 用户的基础信息（登录后获得）
 */
@interface ZHPayUserInfo : NSObject

@property (readonly, copy) NSString *userId;         /* 用户Id，唯一标识 */
@property (readonly, copy) NSString *validateToken;  /* 登录验证token，可用于服务器登陆有效性验证，一分钟后失效，详见文档 */

@property (readonly, copy) NSString *userName;       /* 用户名，1.3.0后将废弃，未使用过的开发者不要使用，已经使用过的开发者可继续使用 */
@end

/**
 *  @brief 订单信息，用于支付
 */
@interface ZHPayOrderInfo : NSObject

@property (nonatomic, copy) NSString *orderId;				    /* 订单号，必须保证唯一 */
@property (nonatomic, copy) NSString *productName;				/* 商品名称，不可为空 */
@property (nonatomic, copy) NSString *gameName;                 /* 游戏名，可精确到服或区，不可为空 例如：大掌门A区 */
@property (nonatomic, copy) NSString *userParams;               /* 开发者自定义参数，服务器异步通知时会原样回传，不超过255个字符 可为空*/
@property double productPrice;                                  /* 商品价格，单位：元 需>0 double类型*/
@end

#pragma mark - 账号操作等回调协议 *************************************************************

@protocol ZHPayDelegate <NSObject>

@optional

/**
 *  @brief 登录成功回调
 *
 *  @param userInfo 成功返回用户信息
 */
- (void)ZHPayLoginSuccess:(ZHPayUserInfo *)userInfo;

/**
 *  @brief 登录失败回调
 *
 *  @param errorInfo 失败返回错误信息
 */
- (void)ZHPayLoginFailed:(NSDictionary *)errorInfo;

/**
 *  @brief 登录取消 回调
 */
- (void)ZHPayLoginCancel;

/**
 *  @brief 用户注销成功回调
 *
 *  @note  接收后您必须将自己的App注销并切换到登录前画面
 */
- (void)ZHPayDidLogout;

/**
 *  @brief 检查更新回调
 *
 *  @param isSuccess 检查成功返回YES   
 *  @param update    发现新版为YES
 *  @param force     本次是否是强制更新，强更为YES
 *
 *  @note  sdk会进行相应提示，开发者收到回调后不用再次提示用户。
 */
- (void)ZHPayCheckUpdateFinish:(BOOL)isSuccess shouldUpdate:(BOOL)update isForceUpdate:(BOOL)force;

/**
 *  @brief SDK界面出现
 *
 *  @note  如登录，账号信息界面等出现时，均会回调此方法。建议判断是否在游戏中途并将游戏临时暂停
 */
- (void)ZHPayViewIn;

/**
 *  @brief SDK界面退出
 *
 *  @note  如登录，账号信息界面等消失时，均会回调此方法。
 */
- (void)ZHPayViewOut;

@end


#pragma mark - 支付结果回调协议 *************************************************************

@protocol ZHPayResultDelegate <NSObject>

typedef enum {
    
    kZHPayBalanceError,      /*余额不足*/
    
    kZHPayCreateOrderError,  /*订单创建错误*/
    
    kZHPayOldOrderError,     /*重复提交订单，请尝试更换订单号*/
    
    kZHPayNetworkingError,   /*网络不通畅（有可能已购买成功但客户端已超时,建议去自己服务器进行订单查询，以自己服务器为准）*/
    
    kZHPayServerError,       /*服务器错误*/
    
    kZHPayOtherError,        /*其他错误*/
    
}ZH_PAY_ERROR;

@optional

/**
 *	@brief	支付成功
 *
 *	@param 	orderInfo 订单信息
 *  @note   收到回调后，SDK服务器会异步通知开发者服务器，建议去自己服务器查询订单是否有效，金额是否正确。
 */
- (void)ZHPayResultSuccessWithOrder:(ZHPayOrderInfo *)orderInfo;

/**
 *	@brief	支付失败
 *
 *	@param 	orderInfo  订单信息
 *	@param 	errorType  错误类型，见ZH_PAY_ERROR
 */
- (void)ZHPayResultFailedWithOrder:(ZHPayOrderInfo *)orderInfo resultCode:(ZH_PAY_ERROR)errorType;

/**
 *	@brief  用户中途取消支付
 *
 *	@param	orderInfo  订单信息
 */
- (void)ZHPayResultCancelWithOrder:(ZHPayOrderInfo *)orderInfo;

@end


#pragma mark - 手动查询订单结果回调协议 *************************************************************

@protocol ZHPayCheckDelegate <NSObject>

typedef enum{
    
    kZHPayCheckStatusReady = 0, /* 待支付（已经创建第三方充值订单，但未支付）*/
    
    kZHPayCheckStatusSuccess,   /* 成功 */
    
    kZHPayCheckStatusExpired,   /* 过期失效 */
    
    kZHPayCheckStatusNotExist,  /* 订单不存在（或未完成支付流程） */
    
    kZHPayCheckStatusFail,      /* 支付失败 */
    
}ZHPayCheckStatusType;

@optional
/**
 *  @brief 查询订单完毕
 *
 *  @param orderId     订单号
 *  @param money       订单金额（单位：元）
 *  @param statusType  订单状态
 */
- (void)ZHCheckOrderFinishedWithOrder:(NSString *)orderId
                                money:(double)money
                               status:(ZHPayCheckStatusType)statusType;

/**
 *  @brief 查询订单失败（网络中断，或服务器返回错误）
 */
- (void)ZHCheckOrderDidFailed:(NSString*)orderId;

@end


#endif
