//
//  HTIAPManager.h
//  正式内购开整
//
//  Created by 王璟鑫 on 16/5/20.
//  Copyright © 2016年 王璟鑫. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <StoreKit/StoreKit.h>

@interface HTIAPManager : NSObject

@property (nonatomic, retain) SKProductsRequest *productRequest;
@property(nonatomic,strong)NSString*extra;

@property(nonatomic,copy)void(^IAPBlock)(NSString*status);



+(instancetype)defaultManager;

/**
 *  IAP支付
 *
 *  @param productId 传入商品ID
 */
- (void)requestProductWithId:(NSString *)productId;


//新接口
//- (void)requestProductWithId:(NSString *)productId withblock:(void(^)(NSString *str))block;
/**
 *  与服务器连接失败重连的定时器
 */
-(void)createTimer;



@end
