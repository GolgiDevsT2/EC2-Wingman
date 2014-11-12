//
//  ViewController.h
//  EC2 Wingman
//
//  Created by Brian Kelly on 10/30/14.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WingmanSvcWrapper.h"

@interface ViewController : UIViewController <UITableViewDataSource,UITableViewDelegate,UITextFieldDelegate>
{
    NSArray *fullList;
    NSArray *liveList;
    time_t lastUpdate;
}
 
@property IBOutlet UITextField *filterTextField;
@property IBOutlet UITableView *theTableView;
@property IBOutlet UILabel *statusLabel;
@property NSString *currentInstanceName;
@property NSString *currentInstanceId;

+ (BOOL)filterMatchesName:(NSString *)name;

- (IBAction)settingsPressed:(id)sender;

- (void)updateList:(InstanceList *)iList;
- (void)updateStatus;
@end

