//
//  InstanceSettingsViewController.h
//  EC2 Wingman
//
//  Created by Brian Kelly on 06/11/2014.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface InstanceSettingsViewController : UIViewController
@property NSString *instanceId;
@property NSString *instanceName;

@property IBOutlet UILabel *nameLabel;
@property IBOutlet UISwitch *orangeSwitch;
@property IBOutlet UISwitch *redSwitch;

+ (BOOL)hasCustomSettings:(NSString *)instId;
- (IBAction)donePressed:(id)sender;

@end
