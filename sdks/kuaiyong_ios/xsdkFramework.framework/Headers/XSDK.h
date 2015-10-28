//
//  XSDK.h
//  XSDK
//
//  Created by lujunfeng on 15/3/30.
//  Copyright (c) 2015年 yrtd. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol XSDKDelegate <NSObject>
@optional

/**
 *  @method-(void)XSDKinitCallBack
 *  游戏初始化结束后回调
 **/
-(void)XSDKCheckUpdateCallBack;

/**
 *  @method-(void)XSDKloginCallBack:(NSString *)tokenKey
 *  用户登录回调
 *  @param  tokenKey
 **/
-(void)XSDKLoginCallBack:(NSString *)tokenKey;

/**
 *  @method-(void)XSDKLogOutCallBack:(NSString *)guid
 *  注销方法回调
 *  @param  guid
 **/
-(void)XSDKLogOutCallBack:(NSString *)guid;

/**
 *  @methodXSDKGameLoginCallback:(NSString *)username password:(NSString *)password resultBlock:(void (^)(NSString * type))resultBlock
 *  游戏账号登陆回调
 **/
-(void)XSDKGameLoginCallback:(NSString *)username password:(NSString *)password resultBlock:(void (^)(NSString * typeMessage))resultBlock;

/**
 *游戏账号登陆成功回调
 **/
-(void)XSDKGameLoginSuc;


/**
 *  @method - (void)XSDKPayCallback
 *  支付回调
 *  @param isUnfinished
 *  客户端获得的支付结果，CP客户端应在收到此回调后发起服务器验证，获得最终结果
 */
-(void)XSDKPayCallback:(BOOL)isClientSuccess;

@end

@interface XSDK : NSObject
@property(weak,nonatomic)id<XSDKDelegate> xsdkDelegate;

+(id)instanceXSDK;

/**
 * 设置appKey
 * @param appKey
 * 游戏开发商添加游戏后获得的appKey
 **/
-(void)setAppKey:(NSString*)appKey;

/**
 * 设置是否只支持iphone
 **/
-(void)setOnlySupportIPhone:(BOOL)isOnlySupportIPhone;
/**
 * 设置是否只支持游戏账号登陆（默认不支持）
 **/
-(void)setISSupportGameNo:(BOOL)isSupportGameNo;

/**
 * 初始化数据，检查更新
 **/
-(void)checkUpdate;

/**
 * 登陆，如果之前有登录的用户，直接登录
 **/
-(void)login;

/**
 * 注销
 **/
-(void)logout;

/**
 *  游戏账号登陆，找回密码等错误时，联系客服方式
 */
-(void)setGameContact:(NSString *)contact;

/**
 显示支付
 @param dealseq
 订单号
 @param fee
 金额
 @param payID
 游戏开发商添加游戏后获得的payID
 @param gamesvr
 多个通告地址的选择设置
 @param gameUserServer
 所在区服名称（不能为空）
 @param subject
 商品名称
 **/
-(void)payWithDealSeq:(NSString *)dealseq andFee:(NSString *)fee andPayID:(NSString *)payID andGamesvr:(NSString *)gamesvr andGameUserServer:(NSString *)gameUserServer andSubject:(NSString *)subject;

/**
 处理openurl回调，参数对应AppDelegate的 -(BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation 回调
 **/
-(BOOL)handleApplication:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation;
@end
