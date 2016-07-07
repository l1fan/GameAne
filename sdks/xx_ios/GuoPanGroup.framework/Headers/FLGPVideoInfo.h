//
//  FLGPVideoInfo.h
//  GuoPanGroup
//
//  Created by Hays on 15/10/19.
//  Copyright © 2015年 GuoPan. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FLGPVideoInfo : NSObject

@property(nonatomic, strong) NSString *videoId;
@property(nonatomic, strong) NSString *url;
@property(nonatomic, strong) NSString *videoPath;
@property(nonatomic, strong) NSString *thumPath;
@property(nonatomic) FLGPVideoQuality quality;
@property(nonatomic, strong) NSString *size;

@property(nonatomic, strong, readonly) NSString *sizeString; // 用于cell显示

@end
