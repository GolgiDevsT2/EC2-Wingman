//
//  AppDelegate.h
//  EC2 Wingman
//
//  Created by Brian Kelly on 10/30/14.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GolgiStuff.h"
#import "ViewController.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate>
{
    GolgiStuff *golgiStuff;
}

@property (strong, nonatomic) UIWindow *window;

+ (NSString *)getInstanceId;
+ (void)setInstanceId:(NSString *)instanceId;
+ (NSData *)getPushId;
+ (void)setPushId:(NSData *)pushId;


@end

