//
//  FLGPPanelNavBar.h
//  GuoPanGroup
//
//  Created by Hays on 15/9/24.
//  Copyright (c) 2015年 GuoPan. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol FLGPPanelNavBarDelegate;

typedef NS_OPTIONS(NSUInteger, FLGPPanelNavBarRightItem) {
    FLGPPanelNavBarRightItem_None = 1 << 0,
    FLGPPanelNavBarRightItem_More = 1 << 1, // 更多
    FLGPPanelNavBarRightItem_Join = 1 << 2, // 加入小组
    FLGPPanelNavBarRightItem_Message = 1 << 3, // 回复通知中心
    FLGPPanelNavBarRightItem_Post = 1 << 4, // 发帖
    FLGPPanelNavBarRightItem_Word = 1 << 5, // 纯文本按钮，当设置纯文本按钮后，右侧按钮只有一个按钮
};

@interface FLGPPanelNavBar : UIView

@property (nonatomic, weak) id<FLGPPanelNavBarDelegate> delegate;

@property (nonatomic, strong) NSString *title;

@property (nonatomic, readonly) UIButton *backBtn;

@property (nonatomic) FLGPPanelNavBarRightItem rightItems;
@property (nonatomic, strong) NSString *rightItemWording; // use by rightItems is FLGPPanelNavBarRightItem_Word
@property (nonatomic, strong) UIColor *rightItemWordingColor; // 默认白色

- (instancetype)initWithPanelView:(UIView *)panelView;

- (void)showRedPointForRightItem:(FLGPPanelNavBarRightItem)rightItem;

@end

@protocol FLGPPanelNavBarDelegate <NSObject>

- (void)panelNavBarDidClickLeftButton:(FLGPPanelNavBar *)navBar;

- (void)panelNavBarDidClickRightButton:(FLGPPanelNavBar *)navBar rightItem:(FLGPPanelNavBarRightItem)rightItem;

@end