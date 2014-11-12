//
//  GolgiStuff.m
//  EC2 Wingman
//
//  Created by Brian Kelly on 10/30/14.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import "AppDelegate.h"
#import "GolgiStuff.h"
#import "GOLGI_KEYS.h"
#import <AudioToolbox/AudioToolbox.h>


static GolgiStuff *instance = nil;

@implementation GolgiStuff
@synthesize viewController;
@synthesize currentList;

+ (GolgiStuff *)getInstance
{
    if(instance == nil){
        instance = [[GolgiStuff alloc] init];
    }
    
    return instance;
}

- (void)displayAlertWithTitle:(NSString *)title andContent:(NSString *)content
{
    UILocalNotification* localNotification = [[UILocalNotification alloc] init];
    localNotification.alertBody = content;
    [[UIApplication sharedApplication] scheduleLocalNotification:localNotification];
    AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
}

// GOLGI
//********************************* Registration ***************************
//
// Setup handling of inbound SendMessage methods and then Register with Golgi
//
- (void)doGolgiRegistration
{
    //
    // Do this before registration because on registering, there may be messages queued
    // up for us that would arrive and be rejected because there is no handler in place
    //
    
    // [TapTelegraphSvc registerSendMessageRequestReceiver:self];
    
    //
    // and now do the main registration with the service
    //
    
    
    // [Golgi setOption:@"USE_DEV_CLUSTER" withValue:@"0"];
    
    /*
    [PushButtonSvc registerButtonPushedRequestHandler:^(id<PushButtonButtonPushedResultSender> resultSender, PushData *pushData) {
        NSLog(@"Received request");
        [resultSender success];
        if(viewController != nil){
            [viewController requestReceived];
        }
        
        UILocalNotification* localNotification = [[UILocalNotification alloc] init];
        localNotification.alertBody = [NSString stringWithFormat:@"Received Request: %@", [pushData getData]];
        [[UIApplication sharedApplication] cancelAllLocalNotifications];
        [[UIApplication sharedApplication] scheduleLocalNotification:localNotification];
        NSLog(@"D");
        AudioServicesPlaySystemSound(kSystemSoundID_Vibrate);
        NSLog(@"E");
        
    }];
     */
    
    
    [WingmanSvc registerRaiseOrangeCpuAlarmRequestHandler:^(id<WingmanRaiseOrangeCpuAlarmResultSender> resultSender, NSString *instName, NSInteger cpu) {
        [self displayAlertWithTitle:@"Orange CPU" andContent:[NSString stringWithFormat:@"%@ orange CPU alert: %ld%%", instName, (long)cpu]];
        [resultSender success];
    }];
    
    [WingmanSvc registerRaiseRedCpuAlarmRequestHandler:^(id<WingmanRaiseRedCpuAlarmResultSender> resultSender, NSString *instName, NSInteger cpu) {
        [self displayAlertWithTitle:@"Red CPU" andContent:[NSString stringWithFormat:@"%@ red CPU alert: %ld%%", instName, (long)cpu]];
        [resultSender success];
    }];
    
    [WingmanSvc registerRaiseStateChangeAlarmRequestHandler:^(id<WingmanRaiseStateChangeAlarmResultSender> resultSender, NSString *instName, NSInteger oldState, NSInteger newState) {
        [self displayAlertWithTitle:@"State Change" andContent:[NSString stringWithFormat:@"%@ state change", instName]];
        [resultSender success];
    }];
    
    
    
    
    NSData *pushId = [AppDelegate getPushId];
    
    if(pushId.length > 0){
#ifdef DEBUG
        [Golgi setDevPushToken:pushId];
#else
        [Golgi setProdPushToken:pushId];
#endif
    }
    NSString *instanceId = [AppDelegate getInstanceId];
    if(instanceId.length > 0){
        [Golgi registerWithDevId:GOLGI_DEV_KEY
                           appId:GOLGI_APP_KEY
                          instId:instanceId
                andResultHandler:^(NSString *errorText) {
                    if(errorText != nil){
                        NSLog(@"Golgi Registration: FAIL => '%@'", errorText);
                    }
                    else{
                        NSLog(@"Golgi Registration: PASS");
                    }
                }];
    }
}

-(void) housekeep:(NSTimer *)theTimer
{
    time_t now = time(NULL);
    
    if(!listInFlight && (now - lastUpdate) >= 60){
        listInFlight = true;
        [WingmanSvc sendListUsingResultHandler:^(InstanceList *iList, WingmanListExceptionBundle *exBundle)
                                                {
                                                    listInFlight = false;
                                                    lastUpdate = time(NULL);
                                                    if(exBundle == nil){
                                                        currentList = iList;
                                                        if(viewController != nil && inFg){
                                                            [viewController updateList:iList];
                                                        }
                                                    }
                                                    NSLog(@"List exception bundle: %@", exBundle);
                                                }
                            withTransportOptions:stdGto
                                andDestination:@"SERVER"];
    }
    else{
        if(viewController != nil && inFg){
            [viewController updateStatus];
        }

    }
}


- (void)enterForeground
{
    if(hkTimer == nil){
        hkTimer = [NSTimer scheduledTimerWithTimeInterval: 1.0f
                                                   target: self
                                                 selector: @selector(housekeep:)
                                                 userInfo: nil
                                                  repeats: YES];

    }
    inFg = true;
    
}

- (void)enterBackground
{
    if(hkTimer != nil){
        [hkTimer invalidate];
        hkTimer = nil;
    }
    inFg = false;
    
}


- (GolgiStuff *)init
{
    self = [super init];
    viewController = nil;
    
    
    stdGto = [[GolgiTransportOptions alloc] init];
    [stdGto setValidityPeriodInSeconds:3600];
    
    NSString *instanceId = [AppDelegate getInstanceId];
    if(instanceId.length == 0){
        srand(getpid());
        NSMutableString *str = [[NSMutableString alloc] init];
        for(int i = 0; i < 20; i++){
            int ch = 'A' + (rand() % ('z' - 'A'));
            [str appendFormat:@"%c", ch];
        }
        [AppDelegate setInstanceId:str];
    }
    
    currentList = [[InstanceList alloc] init];

    
    
    [self doGolgiRegistration];
    
    return self;
}

@end
