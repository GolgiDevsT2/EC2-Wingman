//
//  GolgiStuff.h
//  EC2 Wingman
//
//  Created by Brian Kelly on 10/30/14.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ViewController.h"
#import "libGolgi.h"
#import "WingmanSvcWrapper.h"


@interface GolgiStuff : NSObject
{
    GolgiTransportOptions *stdGto;
    NSTimer *hkTimer;
    time_t lastUpdate;
    BOOL listInFlight;
    BOOL inFg;
}

@property ViewController *viewController;
@property InstanceList *currentList;

+ (GolgiStuff *)getInstance;
- (void)enterForeground;
- (void)enterBackground;



@end
