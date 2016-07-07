//
//  FLGPBaseViewController.h
//  GuoPanGroup
//
//  Created by Hays on 15/9/21.
//  Copyright (c) 2015年 GuoPan. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FLGPBaseViewController : UIViewController

@property (nonatomic) BOOL autoRotate;
@property (nonatomic) NSInteger supportedIOs;
@property (nonatomic) BOOL isRunningAnimation;
@property (nonatomic) BOOL shouldDisplayMainControl;
@property (nonatomic) UIInterfaceOrientation lastestOrientation; // update in view will disappear
@property (nonatomic, copy) void(^dismissBlock)(FLGPBaseViewController *vc);

- (void)dismissViewControllerAction:(id)sender;

@end
