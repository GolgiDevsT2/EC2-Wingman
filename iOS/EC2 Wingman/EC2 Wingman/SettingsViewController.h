//
//  SettingsViewController.h
//  EC2 Wingman
//
//  Created by Brian Kelly on 10/31/14.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import "ViewController.h"

@interface SettingsViewController : UIViewController

@property IBOutlet UISwitch *orangeCPU;
@property IBOutlet UISwitch *redCPU;
@property IBOutlet UISwitch *statusCheck;
@property IBOutlet UISwitch *stateChange;
@property IBOutlet UISwitch *filteredNotiifications;
@property IBOutlet UISwitch *onlyNamedNotifications;

- (IBAction)donePressed:(id)sender;


@end
