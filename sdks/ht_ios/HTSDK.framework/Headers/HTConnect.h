//
//  HTConnect.h
//  GSDK
//
//  Created by 王璟鑫 on 16/8/9.
//  Copyright © 2016年 王璟鑫. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "HTAssistiveTouch.h"
@interface HTConnect : NSObject

@property (nonatomic,strong)NSString*gameCenterLoginType;


/*
 *在- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:方法中调用此方法,并输入你们自己的appID
    countryNumber是手机号的国家代码 如中国是86 (+86 只需写86即可无需写+)
 */
+(void)initSDKwithAppID:(NSString*)AppID andPhoneCountryNumber:(NSString*)countryNumber;



/**
 *  展示HTSDK UI并接收登录信息回调
 *
 *  @param infoBlack 回调信息
 */
+(void)showHTSDKwithLoginInfo:(void(^)(NSDictionary*loginInfo,NSDictionary*thridLoginInfo))infoBlack;

/**
 *  切换账号
 */
+(void)changeAccount:(void(^)(NSDictionary*accountInfo,NSDictionary*facebookInfo))changeAccountBlock;

/**
 *  显示和隐藏悬浮窗,登录成功后会自动显示悬浮窗.
 */
+(void)showAssistiveTouch;
+(void)hideAssistiveTouch;

/**
 *  统计接口
 *
 *  @param type       类型log或者reg
 *  @param version    游戏版本
 *  @param coo_server 合作伙伴服务器id
 *  @param coo_uid    合作伙伴用户UID
 */
+(void)StatisticsInterfacelogOrRegType:(NSString*)type version:(NSString*)version channel:(NSString*)channel coo_server:(NSString*)coo_server coo_uid:(NSString*)coo_uid success:(void(^)(id response))success failure:(void(^)(id error))failure;

/**
 *  获取商品ID列表
 *
 *  @param server  现在默认是1后续版本会变
 *  @param success 请求成功回调
 *  @param error   请求失败回调
 */
+(void)getProductsIDwithServer:(NSString*)server ifSuccess:(void(^)(NSArray* response))success orError:(void(^)(NSError*error))error;

/**
 *  faceBook分享
 *
 *  @param url               链接
 *  @param imageURL          图片链接
 *  @param contentTitle      标题
 */
+(void)shareToFacebookWithURL:(NSString*)url imageURL:(NSString*)imageURL contentTitle:(NSString*)contentTitle contentDescription:(NSString*)contentDescription shareInfoBlock:(void(^)(NSDictionary *shareInfoDic))shareInfoBlock;
/**
 *  faceBook邀请
 *
 *  @param title             标题
 *  @param msg               详细内容
 */
+ (void)inviteFB:(NSString *)title message:(NSString *)msg inviteInfoBlock:(void(^)(NSDictionary*inviteInfoDic))inviteInfoBlock;

/*
 切换账号，修改密码游戏重新登录
 
 */

+ (void)gameRestart:(void(^)(NSDictionary *dic))restarBlock;




//内部实现的一些方法,无需调用
+(instancetype)shareConnect;
@property(nonatomic,copy)void(^sharFB)(NSDictionary*);
@property(nonatomic,copy)void(^inviteFB)(NSDictionary*);
@property(nonatomic,copy)void(^changeAccount)(NSDictionary*account,NSDictionary*faceboolInfo);
@property (nonatomic,copy)void(^loginBackBlock)(NSDictionary*loginInfo,NSDictionary*FaceBookInfo);
@property(nonatomic,copy)void(^changePassword)(NSDictionary *password);
@property (nonatomic,strong)HTAssistiveTouch*mywindow;

@end
