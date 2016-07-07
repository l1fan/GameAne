//
//  FLGPBasePanelTableViewController.h
//  GuoPanGroup
//
//  Created by Hays on 15/9/25.
//  Copyright (c) 2015年 GuoPan. All rights reserved.
//

#import "FLGPBasePanelViewController.h"

@class FLGPRefreshTableHeaderView, FLGPRefreshTableFooterView, FLGPLoadingActivityView, FLGPEmptyView;

@interface FLGPBasePanelTableViewController : FLGPBasePanelViewController

@property (nonatomic, readonly) FLGPRefreshTableHeaderView *refreshHeader;
@property (nonatomic, readonly) FLGPRefreshTableFooterView *refreshFooter;
@property (nonatomic, readonly) UILabel *noMoreLabel;
@property (nonatomic, readonly) FLGPLoadingActivityView *activityView;
@property (nonatomic, strong) FLGPEmptyView *emptyView;
@property (nonatomic, strong) UITableView *tableView;

@property (nonatomic, assign) BOOL isGroupedStyle;
@property (nonatomic) BOOL needRefreshView; // default yes
@property (nonatomic) BOOL needLoadMoreView; // default NO
@property (nonatomic) BOOL scrollsToTop;
@property (nonatomic) BOOL isLoading;
@property (nonatomic) BOOL hasMore;

- (void)setNorMoreLabelTitle:(NSString *)noMoreTitle;

- (void)setFrame:(CGRect)frame;

- (void)reconfigEmptyWithDataTitle:(NSString *)dataTitle dataDetail:(NSString *)dataDetail;

// 子类加载数据重载使用
- (void)loadData;

- (void)loadMore;

// type is FLGPEmptyViewType
- (void)showEmptyView:(BOOL)isShow type:(NSInteger)type;

// 刷新使用，无特别需要不需要重载，不需要下拉刷新view时重载次刷新方法
- (void)toggleToReload:(FLGPEmptyView *)emptyView;

- (void)doneLoadingTableViewData;

- (void)startInitLoading;

- (void)stopInitLoading;


@end
