//
//  PPAppPlatformKitDelegate.h
//  PPAppPlatformKit
//
//  Created by keven on 14-10-8.
//  Copyright (c) 2014年 广州铁人网络科技有限公司. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "PPAppPlatformKitPublicDefine.h"

typedef void (^tokenVerifyingSuccessCallBack) (BOOL isSuccess);

@protocol PPAppPlatformKitDelegate <NSObject>
@required

/**
 *  动态库加载成功回调 （注意：登录必须在该回调之后调用）
 */
- (void)ppDylibLoadSucceedCallBack;

/**
 *  余额充足时直接购买后执行该回调（注意：余额不足时，需充值购买，不执行该回调）
 *
 *  @param paramPPPayResultCode 购买状态码
 */
- (void)ppPayResultCallBack:(PPPayResultCode)resultCode;

/**
 *  登录成功回调
 *
 *  @param paramStrToKenKey 字符串token
 *  @param block            回调block === 验证成功 block(YES) || 验证失败（NO）
 */
- (void)ppLoginSuccessCallBack:(NSString *)paramStrToKenKey callBack:(tokenVerifyingSuccessCallBack)block;

/**
 *  用户中心显示完成回调
 */
- (void)ppCenterDidShowCallBack;

/**
 *  用户中心关闭完成回调
 */
- (void)ppCenterDidCloseCallBack;

/**
 *  注销后回调
 */
- (void)ppLogOffCallBack;

@end
