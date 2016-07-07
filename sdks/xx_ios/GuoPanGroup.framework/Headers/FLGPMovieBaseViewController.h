//
//  GPMovieBaseViewController.h
//  GPVideoPlayer
//
//  Created by Hays on 14/12/30.
//  Copyright (c) 2014年 GuoPan. All rights reserved.
//

#import "FLGPMoviePlayerController.h"
#import "FLGPBaseViewController.h"

@interface FLGPMovieBaseViewController : FLGPBaseViewController <FLGPMoviePlayerControllerDelegate>

@property (nonatomic, strong) FLGPMoviePlayerController *movie;

- (void)moviePlayerShowMessage:(NSString *)message;

- (void)moviePlayerWillEnterFullscreen:(FLGPMoviePlayerController *)controller;

- (void)moviePlayerDidEnterFullscreen:(FLGPMoviePlayerController *)controller;

- (void)moviePlayerWillExitFullscreen:(FLGPMoviePlayerController *)controller;

- (void)moviePlayerDidExitFullscreen:(FLGPMoviePlayerController *)controller;

@end
