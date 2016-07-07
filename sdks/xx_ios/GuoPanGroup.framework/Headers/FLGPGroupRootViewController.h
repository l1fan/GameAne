//
//  FLGPGroupRootViewController.h
//  GuoPanGroup
//
//  Created by Hays on 15/9/21.
//  Copyright (c) 2015年 GuoPan. All rights reserved.
//

#import "FLGPBasePanelTableViewController.h"

@interface FLGPGroupRootViewController : FLGPBasePanelTableViewController

/*
 * 显示小组模块首页
 * @param rootVC 悬浮窗根ViewController
 * @param bgView 显示的view
 */
+ (void)showGroupViewControllerWithBGView:(nonnull UIView *)bgView
                                   rootVC:(nonnull UIViewController *)rootVC
                               completion:(nullable dispatch_block_t)completion
                             dismissBlock:(nullable void(^)(BOOL shouldDisplayMainControl))dismissBlock;

/*
 * 显示小组模块首页
 * @param navigationControllerClass 外部自定义navigation Controller，非null时，显示小组模块使用传入的navigationController来创建显示
 */
+ (void)showGroupViewControllerWithBGView:(nonnull UIView *)bgView
                                   rootVC:(nonnull UIViewController *)rootVC
                navigationControllerClass:(nullable Class)navigationControllerClass
                               completion:(nullable dispatch_block_t)completion
                             dismissBlock:(nullable void(^)(BOOL shouldDisplayMainControl))dismissBlock;

@end
