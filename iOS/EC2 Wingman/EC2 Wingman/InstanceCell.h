//
//  InstanceCell.h
//  EC2 Wingman
//
//  Created by Brian Kelly on 10/30/14.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ViewController.h"
#import "CPUView.h"
@interface InstanceCell : UITableViewCell

@property NSString *instanceId;
@property IBOutlet UILabel *nameLabel;
@property IBOutlet UILabel *cpuLabel;
@property IBOutlet CPUView *cpuView;
@property IBOutlet UILabel *stateLabel;
@property IBOutlet UIView *customisedView;
@property ViewController *theViewController;

- (IBAction)itemClicked:(id)sender;
- (IBAction)itemPreClick:(id)sender;
- (IBAction)itemPostClick:(id)sender;


@end
