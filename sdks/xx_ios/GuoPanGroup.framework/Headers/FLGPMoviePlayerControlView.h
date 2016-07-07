//
//  GPMoviePlayerControlView.h
//  GPVideoPlayer
//
//  Created by Hays on 14/12/29.
//  Copyright (c) 2014年 Personal. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MediaPlayer/MediaPlayer.h>

@class FLGPMoviePlayerControlView;

@protocol FLGPMoviePlayerControlViewDelegate <NSObject>

@required
/**
 *  点击播放按钮时播放状态
 *
 *  @param controlView GPMoviePlayerControlView
 *
 *  @return MPMoviePlaybackState
 */
- (MPMoviePlaybackState)moviePlayerControlViewShouldDoPlayActionWithState:(FLGPMoviePlayerControlView *)controlView;

- (void)moviePlayerControlView:(FLGPMoviePlayerControlView *)controlView didDoPlayAction:(BOOL)isPlay;

- (BOOL)moviePlayerControlViewShouldUpdateTracker:(FLGPMoviePlayerControlView *)controlView;

- (void)moviePlayerControlView:(FLGPMoviePlayerControlView *)controlView didUpdateTracker:(CGFloat)progress;

- (BOOL)moviePlayerControlViewShouldZoomWithFullScreenState:(FLGPMoviePlayerControlView *)controlView;

- (void)moviePlayerControlView:(FLGPMoviePlayerControlView *)controlView didZoomAction:(BOOL)isZoomIn;

@end

@interface FLGPMoviePlayerControlView : UIView

@property (nonatomic, weak) id<FLGPMoviePlayerControlViewDelegate> delegate;

@property (nonatomic, strong) UILabel *title;

- (void)updateCacheTrack:(CGFloat)progress;

- (void)updateTracker:(CGFloat)progress;

- (void)resetPlayBtn:(BOOL)isPlaying;

- (void)updatePlaybackTime:(NSTimeInterval)time;

- (void)setControllerEnabled:(BOOL)enabled;

- (void)setZoomBtnEnable:(BOOL)enabled;

- (void)setPlayBtnEnable:(BOOL)enabled;

- (void)setTopViewEnabled:(BOOL)enabled;

- (void)startToolbarTimer;

- (void)stopToolbarTimer;

- (void)startLoadingAnimate;

- (void)stopLoadingAnimate;

- (void)showController;

- (void)dismissController;

- (BOOL)isLoadAnimating;

- (void)setHasZoomBtn:(BOOL)hasZoom;

@end
