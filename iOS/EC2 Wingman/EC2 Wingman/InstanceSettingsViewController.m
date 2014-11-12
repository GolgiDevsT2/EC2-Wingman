//
//  InstanceSettingsViewController.m
//  EC2 Wingman
//
//  Created by Brian Kelly on 06/11/2014.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import "InstanceSettingsViewController.h"
#import "GolgiStuff.h"

@implementation InstanceSettingsViewController
@synthesize instanceId;
@synthesize instanceName;
@synthesize nameLabel;
@synthesize orangeSwitch;
@synthesize redSwitch;


+ (NSString *)orangeKeyFromInstanceId:(NSString *)instId
{
    return [NSString stringWithFormat:@"%@:ORANGE-SUPPRESS", instId];
}

+ (NSString *)redKeyFromInstanceId:(NSString *)instId
{
    return [NSString stringWithFormat:@"%@:RED-SUPPRESS", instId];
}

+ (BOOL)hasCustomSettings:(NSString *)instId
{
    int v = 0;
    
    
    v += [GolgiStore getIntegerForKey:[InstanceSettingsViewController orangeKeyFromInstanceId:instId] withDefault:0];
    v += [GolgiStore getIntegerForKey:[InstanceSettingsViewController redKeyFromInstanceId:instId] withDefault:0];

    return (v != 0) ? true : false;
}


- (NSString *)orangeKey
{
    return [InstanceSettingsViewController orangeKeyFromInstanceId:instanceId];
}

- (NSString *)redKey
{
    return [InstanceSettingsViewController redKeyFromInstanceId:instanceId];
}

- (void)saveState
{
    [GolgiStore deleteIntegerForKey:[self orangeKey]];
    [GolgiStore deleteIntegerForKey:[self redKey]];
    
    if(orangeSwitch.on){
        [GolgiStore putInteger:1 forKey:[self orangeKey]];
    }
    if(redSwitch.on){
        [GolgiStore putInteger:1 forKey:[self redKey]];
    }
}


- (IBAction)donePressed:(id)sender
{
    [self saveState];
    
    [self dismissViewControllerAnimated:true completion:^{
        NSLog(@"And relax");
    }];
    NSLog(@"DONE");
}


- (void)viewWillAppear:(BOOL)animated
{
    ViewController *vc = [GolgiStuff getInstance].viewController;
    
    instanceName = vc.currentInstanceName;
    instanceId = vc.currentInstanceId;
    
    nameLabel.text = instanceName;
    
    orangeSwitch.on = ([GolgiStore getIntegerForKey:[self orangeKey] withDefault:0] != 0) ? true : false;
    redSwitch.on = ([GolgiStore getIntegerForKey:[self redKey] withDefault:0] != 0) ? true : false;
    
    
    
}

@end
