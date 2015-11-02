//
//  LDMobile.h
//
//  Created by LD on 15-08-03.
//  Copyright (c) 2015年 L8. All rights reserved.
//  Ver 1.0.6


#ifndef LDMobileSDK_LDMobileSDK_h
#define LDMobileSDK_LDMobileSDK_h

#pragma mark - 支持旋转方向 *************************************************************

typedef enum {
    kLDOrientationMaskPortraitAndDown, /* 竖向（两个方向）*/
    kLDOrientationMaskPortrait,        /* 竖向（Home键在下）*/
    kLDOrientationMaskLandscape,       /* 横向（两个方向）*/
    kLDOrientationMaskLandscapeLeft,   /* 横向（Home键在左）*/
    kLDOrientationMaskLandscapeRight   /* 横向（Home键在右）*/
}LDSupportOrientation;

#pragma mark - 用户及订单信息 **********************************************************

/**
 *	@brief 用户信息
 */
@interface LDMobileUser : NSObject

@property (readonly, copy) NSString *uid;        /* 用户Id，唯一标识 */
@property (readonly, copy) NSString *token;      /* 登录验证token，一分钟后失效，详见文档 */
@end

/**
 *  @brief 订单信息
 */
@interface LDMobileOrder : NSObject

@property (nonatomic, copy) NSString *orderSerial;			    /* 订单号，需保证唯一，不超过64个字符*/
@property (nonatomic, copy) NSString *orderName;				/* 订单标题/商品名称，不超过255个字符，不可为空 */
@property (nonatomic, copy) NSString *body;                     /* 游戏名/商品详情，不超过400字符，不可为空 */
@property (nonatomic, copy) NSString *userParam;                /* 开发者自定义参数，不超过255个字符，可为空，服务器异步通知时会原样回传 */
@property double price;                                         /* 商品价格，单位：元 需>0 double类型*/
@end

#pragma mark - 登陆等回调方法 *************************************************************

@protocol LDMobileDelegate <NSObject>

@optional

/**
 *  @brief 登录成功回调
 *
 *  @param user 成功返回用户信息
 */
- (void)LDMobileDidLogin:(LDMobileUser *)user;

/**
 *  @brief 登录取消回调
 */
- (void)LDMobileLoginDidCancel;

/**
 *  @brief 注销成功回调
 *
 *  @note  接收后您需要将自己的游戏注销并切换到登录前画面
 */
- (void)LDMobileDidLogout;

/**
 *  @brief 检查更新回调
 *
 *  @param hasNewVersion    有新版为YES
 *  @param isMustUpdate     强更为YES
 *
 *  @note  sdk会自动提示，开发者不用做任何操作
 */
- (void)LDMobileCheckNewVersion:(BOOL)hasNewVersion mustUpdate:(BOOL)isMustUpdate;

@end

#pragma mark - 支付结果回调方法 *************************************************************

@protocol LDMobileOrderDelegate <NSObject>

typedef enum {
    kLDMoneyError,        /*余额不够*/
    kLDOrderInfoError,    /*订单创建错误*/
    kLDSameSerialError,   /*重复的订单号*/
    kLDNetworkError,      /*网络超时（可能已付款,建议去自己服务器进行订单查询，以自己服务器为准）*/
    kLDServerError,       /*服务器错误*/
    kLDOtherError,        /*其他错误*/
}LDErrorType;

@optional

/**
 *	@brief	支付成功
 *
 *	@param 	order 订单信息
 *  @note   收到回调后，建议去自己服务器查询订单是否有效，金额是否匹配。
 */
- (void)LDMobilePaySuccessOfOrder:(LDMobileOrder *)order;

/**
 *	@brief	支付失败
 *
 *	@param 	order  订单信息
 *	@param 	error  错误类型，见LDErrorType
 */
- (void)LDMobilePayFailedOfOrder:(LDMobileOrder *)order errorCode:(LDErrorType)error;

/**
 *	@brief  用户中途取消支付
 *
 *	@param	order  订单信息
 */
- (void)LDMobilePayCancelOfOrder:(LDMobileOrder *)order;


typedef enum{
    kLDOrderCreated = 0, /* 未支付 */
    kLDOrderSuccess,     /* 支付完成 */
    kLDOrderExpired,     /* 过期失效 */
    kLDOrderNotExist,    /* 订单不存在（或未完成支付流程） */
    kLDOrderFail,        /* 支付失败 */
}LDOrderResult;

/**
 *  @brief 查询订单完毕
 *
 *  @param orderSerial     订单号
 *  @param result          订单结果
 *  @param price           订单金额（单位：元）
 */
- (void)LDMobileCheckOrderDone:(NSString *)orderSerial
                        result:(LDOrderResult)result
                         price:(double)price;

/**
 *  @brief 查询订单失败（网络中断，或服务器返回错误）
 */
- (void)LDMobileCheckOrderFailed:(NSString*)orderSerial;

@end


#endif
