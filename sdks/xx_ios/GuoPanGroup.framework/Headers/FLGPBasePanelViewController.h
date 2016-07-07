//
//  FLGPBasePanelViewController.h
//  GuoPanGroup
//
//  Created by Hays on 15/9/21.
//  Copyright (c) 2015年 GuoPan. All rights reserved.
//

#import "FLGPBaseViewController.h"
#import "FLGPPanelNavBar.h"

@interface FLGPBasePanelViewController : FLGPBaseViewController<FLGPPanelNavBarDelegate>

@property (nonatomic, strong) UIView *panelView;

@property (nonatomic, strong) FLGPPanelNavBar *navBar;

- (void)panelNavBarDidClickLeftButton:(FLGPPanelNavBar *)navBar;

- (void)panelNavBarDidClickRightButton:(FLGPPanelNavBar *)navBar rightItem:(FLGPPanelNavBarRightItem)rightItem;

@end
