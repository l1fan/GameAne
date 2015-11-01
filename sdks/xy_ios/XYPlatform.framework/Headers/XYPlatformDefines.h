//
//  XYPlatformDefines.h
//  XYPlatform
//
//  Created by Eason on 28/04/2014.
//  Copyright (c) 2014 www.xyzs.com. All rights reserved.
//

#ifndef XYPlatformDemo_XYPlatformDefines_h
#define XYPlatformDemo_XYPlatformDefines_h

#pragma mark - Notification 相关 key ---------------------------------------

extern NSString* const kXYPlatformErrorKey;    /*noti userinfo 错误码Key */
extern NSString* const kXYPlatformErrorMsg;    /*noti userinfo errmsg key */

#pragma mark - Notification -----------------------------------------------

extern NSString* const kXYPlatformInitDidFinishedNotification;   //初始化成功
extern NSString* const kXYPlatformLogoutNotification;            //注销
extern NSString* const kXYPlatformLoginNotification;             //登录
extern NSString* const kXYPlatformSetLoginNotification;          //设置登陆信息，用于注册跳转到安全中心
extern NSString* const kXYPlatformGuestTurnOfficialNotification; //游客转正注册账号
extern NSString* const kXYPlatformLeavedNotification;            //离开平台

#pragma mark -- enum



/**
 * @brief 离开平台的类型
 */
typedef enum {
    
    XYPlatformLeavedDefault = 0,    /* 离开未知页（预留状态）*/
    XYPlatformLeavedFromLogin,      /* 离开登录页面        */
    XYPlatformLeavedFromRegister,   /* 离开注册页面        */
    XYPlatformLeavedFromPayment,    /* 离开充值页面        */
    XYPlatformLeavedFromSNSCenter,  /* 离开各种中心        */
    
} XYPlatformLeavedType;


/**
 *@brief ToolBar 位置
 */
typedef enum {
    
	XYToolBarAtTopLeft = 1,   /* 左上 */
    
	XYToolBarAtTopRight,      /* 右上 */
    
    XYToolBarAtMiddleLeft,    /* 左中 */
    
	XYToolBarAtMiddleRight,   /* 右中 */
    
	XYToolBarAtBottomLeft,    /* 左下 */
    
	XYToolBarAtBottomRight,   /* 右下 */
    
}XYToolBarPlace;


#pragma mark-- checkPayOrder 协议


/**
 * @brief 支付错误码
 */
typedef enum {
    kPayNoError             =0,     // 无错误
    kPayParamsError         =1,     // 参数错误
    kPayPlayerNotExitError  =2,     // 玩家不存在
    kPayServerNotExitError  =3,     // 游戏服不存在
    kPayOrderIsExitError    =4,     // 订单已存在
    kPayExtraError          =5,     // 透传信息错误
    kPaySignError           =6,      //签名验证错误
    kPayOtherError          =8      // 其他错误
}XY_PAY_ERROR;


/**
 *@brief 充值订单状态
 */
typedef enum {
    
    XYPayOrderStatus_NoPay              = 0,  // 未支付
    XYPayOrderStatus_Shipping           = 1,  // 正在发货
    XYPayOrderStatus_Success            = 2,  // 支付成功，发货成功
    XYPayOrderStatus_PayFailed          = 3,  // 支付失败
    XYPayOrderStatus_Repairing          = 4,  // 补单中
    XYPayOrderStatus_Repaired           = 5,  // 补单完成，发货成功
    XYPayOrderStatus_ShipInvalid        = 8,  // 发货无返回或返回无法解析
    XYPayOrderStatus_Payed_ShipFailed   = 9   // 支付成功，发货失败
    
}XYCheckPayOrderStatus;


/**
 *@brief 查看充值订单状态 回调协议
 */

@protocol XYCheckPayOrderDelegate <NSObject>

@optional

/**
 * @brief 查询订单完成
 *
 * @param orderId 订单号码
 *
 * @param amount  订单金额
 *
 * @param status  该笔订单信息，是否支付发货完成等 参考 XYCheckPayOrderStatus
 */
- (void) XYCheckedOrderFinishedWithOrder:(NSString *) orderId
                                  amount:(NSString *) amount
                                  status:(XYCheckPayOrderStatus) status;


/**
 * @brief 查询订单失败
 * 
 * @param orderId 订单号码
 *
 * @param etc 错误码 参考 XY_PAY_ERROR
 *
 * @param errMsg 错误信息
 */

- (void) XYCkeckOrderDidFailed:(NSString *) orderId
                     errorCode:(XY_PAY_ERROR) etc   
                      errorMsg:(NSString *) errMsg;


@end



#pragma mark-- 支付 协议
/**
 *@brief 支付 返回协议
 */

@protocol XYPayDelegate <NSObject>

@optional

/**
 * @brief 支付成功
 * 该方法在用户完成充值过程， 在充值成功界面点击“确定”时回调，该界面会显示"充值成功，返回游戏，自动发放道具...."
 */
- (void) XYPaySuccessWithOrder:(NSString *) orderId;


/**
 * @brief 支付失败
 *  该方法在用户完成充值过程，充值失败界面点击“确定”时回调，该界面会显示“充值失败，请返回游戏重试或者联系客服”
 *  支付失败可能是 1、用户支付问题 2、支付成功，但回调游戏方服务器问题
 */
- (void) XYPayFailedWithOrder:(NSString *) orderId;

/**
 * @brief 用户点击充值界面右上角叉叉取消充值
 * 注： 1、orderId若为空，则说明用户在选择“充值方式”界面上未点击“确认”进行下一步支付，用户取消点击右上角取消按钮取消支付
 *     2、orderId若不为空，则说明已生成订单号，用户在支付过程中点击界面右上角叉叉取消
 *     3、该回调不能说明充值成功或失败，若orderId不为空，用户可能会在支付宝支付完毕回调到商户的时间内点击界面右上角叉叉取消
 *        开发者应在该回调中调用sdk查询订单接口或者请求游戏服务端获取该笔订单是否成功
 */
- (void) XYPayDidCancelByUser:(NSString *)orderId;




@end




#pragma mark- 一般错误码 －－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－

#define XY_PLATFORM_NO_ERROR                                0       /**< 没有错误,成功 */
#define XY_PLATFORM_ERROR_UNKNOWN                           -1       /**< 未知错误 */
#define XY_PLATFORM_ERROR_NETWORK                           -2       /**< 网络错误 */
#define XY_PLATFORM_ERROR_PAY_ORDERID_NIL                   -3       /**< 查询订单，orderId为空 */
#define XY_PLATFORM_ERROR_NOT_INIT                          -99      /**< 平台未初始化 */

#define XY_PLATFORM_ERROR_ACCOUNT_EMPTY                     1       /**< 用户名位空， 用户名6-20位字符和数字组合 */
#define XY_PLATFORM_ERROR_UID_INVALID                       2       /**< UID不能为空 */
#define XY_PLATFORM_ERROR_PASSWORD_INVALID                  3       /**< 密码为空或不合法 ， 密码是6-16位字符 */
#define XY_PLATFORM_ERROR_ACCOUNT_INVALID                   4       /**< 用户名格式不正确， 用户名6-20位字母和数字组合 */
#define XY_PLATFORM_ERROR_ACCOUNT_EXIST                     5       /**< 用户名已存在 */
#define XY_PLATFORM_ERROR_ACCOUNT_NO_EXIST                  6       /**< 无此用户 */
#define XY_PLATFORM_ERROR_ACCOUNT_OR_PASSWORD               7       /**< 帐号或密码错误 */
#define XY_PLATFORM_ERROR_PASSWORD_DIFF                     8       /**< 两次密码不一致 */
#define XY_PLATFORM_ERROR_SIGN                              9       /**< sign错误 */
#define XY_PLATFORM_ERROR_BEYOND_SEND_OUT                   10      /**< 超出发送限制 */
#define XY_PLATFORM_ERROR_PARAMETERS                        11      /**< 参数错误 */
#define XY_PLATFORM_ERROR_BINDED_MOBILE_OR_EMAIL            12      /**< 手机号或邮箱已绑定 */
#define XY_PLATFORM_ERROR_LACK_PARAMETERS                   13      /**< 缺少参数 */
#define XY_PLATFORM_ERROR_OLD_PASSWORD_EMPTY                14      /**< 原密码位空 */
#define XY_PLATFORM_ERROR_MOBILE_NO_CONFORM                 15      /**< 手机号码不对应 */
#define XY_PLATFORM_ERROR_MOBILE_INVALID                    16      /**< 手机号错误 */
#define XY_PLATFORM_ERROR_EMAIL_INVALID                     17      /**< 邮箱错误 */
#define XY_PLATFORM_ERROR_CAPTCHA                           18      /**< 验证码错误 */
#define XY_PLATFORM_ERROR_APP_NO_INFORMATION                19      /**< 无此app信息 */
#define XY_PLATFORM_ERROR_NO_APPID                          20      /**< 缺少 appid */
#define XY_PLATFORM_ERROR_NOT_IN_WHITELIST                  21      /**< 不在白名单内 */
#define XY_PLATFORM_ERROR_PAY_AMOUNT_LESS_1RMB              22      /**< 充值金额不能少于一元 */
#define XY_PLATFORM_ERROR_PAY_CHANNEL_NO_TOKEN              23      /**< 支付渠道token获取失败 */
#define XY_PLATFORM_ERROR_ONLY_BIND_ONE_MOBILE_OR_EMAIL     24      /**< 只能绑定一个手机或邮箱，先解绑再绑其他 */


#define XY_PLATFORM_ERROR_DATA_PROCESSING                   996     /**< 数据处理错误 */
#define XY_PLATFORM_ERROR_TOKEN_OUTDATE                     997     /**< token过期 */
#define XY_PLATFORM_ERROR_TOKEN_NOT_AUTH                    998     /**< token验证失败 */
#define XY_PLATFORM_ERROR_CAPTCHA_NOT_AUTH                  999     /**< 验证码校验失败 */



#endif
