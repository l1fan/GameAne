//
//  FLGPPostsSendViewController.h
//  GuoPanGroup
//
//  Created by Hays on 15/10/12.
//  Copyright © 2015年 GuoPan. All rights reserved.
//

#import <GuoPanGroup/GuoPanGroup.h>

@class FLGPPostsInfo;
@protocol FLGPPostsSendDelegate;

@interface FLGPPostsSendViewController : FLGPBasePanelViewController

@property(nonatomic, weak) id<FLGPPostsSendDelegate> delegate;

//编辑帖子
- (instancetype)initWithPostsInfo:(FLGPPostsInfo *)postInfo;

- (instancetype)initWithSnapshot:(UIImage *)snapshot;

@end

@protocol FLGPPostsSendDelegate <NSObject>

@optional
- (void)gpPostsSendViewControllerDidSendPosts:(FLGPPostsSendViewController *)vc;

@end