//
//  GPMoviePlayerController.h
//  GPVideoPlayer
//
//  Created by Hays on 14/12/29.
//  Copyright (c) 2014年 GuoPan. All rights reserved.
//

#import <MediaPlayer/MediaPlayer.h>

@class FLGPMoviePlayerController, FLGPMoviePlayerControlView;

@protocol FLGPMoviePlayerControllerDelegate <NSObject>

@optional

- (void)moviePlayerWillEnterFullscreen:(FLGPMoviePlayerController *)controller;
- (void)moviePlayerDidEnterFullscreen:(FLGPMoviePlayerController *)controller;

- (void)moviePlayerWillExitFullscreen:(FLGPMoviePlayerController *)controller;
- (void)moviePlayerDidExitFullscreen:(FLGPMoviePlayerController *)controller;

- (void)moviePlayerTimeout:(FLGPMoviePlayerController *)controller;

- (void)moviePlayerLoadVideoFail:(FLGPMoviePlayerController *)controller;

- (void)moviePlayerShowMessage:(NSString *)message; // 显示提示信息delegate

- (void)moviePlayerDidClickPlayButton:(FLGPMoviePlayerController *)controller;
- (void)moviePlayerDidFinishPlayed:(FLGPMoviePlayerController *)controller;

@required
- (UIInterfaceOrientation)interfaceOrientationForMoviePlayer;

@end

@interface FLGPMoviePlayerController : MPMoviePlayerController

@property (nonatomic) CGRect frame;

@property (nonatomic, weak) id<FLGPMoviePlayerControllerDelegate> delegate;

@property (nonatomic) BOOL gpFullScreen;

@property (nonatomic, readonly, getter=isPreparing) BOOL preparing; // 是否加载视频当中

@property (nonatomic, readonly) FLGPMoviePlayerControlView *controlView;

- (instancetype)initWithContentURL:(NSURL *)url frame:(CGRect)frame delegate:(id<FLGPMoviePlayerControllerDelegate>)delegate;

- (void)setupControllerWithTitle:(NSString *)title;

- (void)setupControllerWithTitle:(NSString *)title withFullScreen:(BOOL)fullScreen;

- (void)setInterfaceOrientationWithFullScreen:(UIInterfaceOrientation)toInterfaceOrientation;

- (void)registNotifications;

- (void)removeNotifications;

- (void)startTrackTimer;

- (void)stopTrackTimer;

- (void)resetUI;

- (void)setGpFullScreen:(BOOL)gpFullScreen;

- (void)setHasFullScreen:(BOOL)hasFullScreen;

@end
