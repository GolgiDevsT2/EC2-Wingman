//
//  SettingsViewController.m
//  EC2 Wingman
//
//  Created by Brian Kelly on 10/31/14.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import "SettingsViewController.h"
#import "libGolgi.h"

#define ORANGE_CPU_KEY @"orangeCPU"
#define RED_CPU_KEY @"redCPU"
#define STATUS_CHECK_KEY @"statusCheck"
#define STATE_CHANGE_KEY @"stateChange"
#define FILTERED_NFNS_KEY @"filteredNotifications"
#define ONLY_NAMED_NFNS_KEY @"onlyNamedNotifications"

@interface SettingsViewController ()

@end

@implementation SettingsViewController
@synthesize orangeCPU;
@synthesize redCPU;
@synthesize statusCheck;
@synthesize stateChange;
@synthesize filteredNotiifications;
@synthesize onlyNamedNotifications;


- (void)saveState
{
    [GolgiStore deleteIntegerForKey:ORANGE_CPU_KEY];
    [GolgiStore deleteIntegerForKey:RED_CPU_KEY];
    [GolgiStore deleteIntegerForKey:STATUS_CHECK_KEY];
    [GolgiStore deleteIntegerForKey:STATE_CHANGE_KEY];
    [GolgiStore deleteIntegerForKey:FILTERED_NFNS_KEY];
    [GolgiStore deleteIntegerForKey:ONLY_NAMED_NFNS_KEY];
    
    [GolgiStore putInteger:orangeCPU.on forKey:ORANGE_CPU_KEY];
    [GolgiStore putInteger:redCPU.on forKey:RED_CPU_KEY];
    [GolgiStore putInteger:statusCheck.on forKey:STATUS_CHECK_KEY];
    [GolgiStore putInteger:stateChange.on forKey:STATE_CHANGE_KEY];
    [GolgiStore putInteger:filteredNotiifications.on forKey:FILTERED_NFNS_KEY];
    [GolgiStore putInteger:onlyNamedNotifications.on forKey:ONLY_NAMED_NFNS_KEY];
}

- (IBAction)switchChanged:(id)sender
{
    NSLog(@"Switch changed");
    
    [self saveState];
    
}

- (void)viewWillAppear:(BOOL)animated
{
    NSLog(@"Settings will appear");
    orangeCPU.on = ([GolgiStore getIntegerForKey:ORANGE_CPU_KEY withDefault:1] != 0) ? true : false;
    redCPU.on = ([GolgiStore getIntegerForKey:RED_CPU_KEY withDefault:1] != 0) ? true : false;
    statusCheck.on = ([GolgiStore getIntegerForKey:STATUS_CHECK_KEY withDefault:1] != 0) ? true : false;
    stateChange.on = ([GolgiStore getIntegerForKey:STATE_CHANGE_KEY withDefault:1] != 0) ? true : false;
    filteredNotiifications.on = ([GolgiStore getIntegerForKey:FILTERED_NFNS_KEY withDefault:0] != 0) ? true : false;
    onlyNamedNotifications.on = ([GolgiStore getIntegerForKey:ONLY_NAMED_NFNS_KEY withDefault:0] != 0) ? true : false;

}

 - (IBAction)donePressed:(id)sender
{
    [self saveState];

    [self dismissViewControllerAnimated:true completion:^{
        NSLog(@"And relax");
    }];
    NSLog(@"DONE");
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
