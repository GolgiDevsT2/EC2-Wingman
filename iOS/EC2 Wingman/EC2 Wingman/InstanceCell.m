//
//  InstanceCell.m
//  EC2 Wingman
//
//  Created by Brian Kelly on 10/30/14.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import "InstanceCell.h"

@implementation InstanceCell
@synthesize instanceId;
@synthesize nameLabel;
@synthesize cpuLabel;
@synthesize stateLabel;
@synthesize theViewController;
@synthesize customisedView;


- (IBAction)itemClicked:(id)sender
{
    NSLog(@"Clicked");
    theViewController.currentInstanceName = nameLabel.text;
    theViewController.currentInstanceId = instanceId;
    [theViewController performSegueWithIdentifier:@"InstanceSettings" sender:theViewController];
}

- (IBAction)itemPreClick:(id)sender
{
    UIButton *btn = sender;

    btn.backgroundColor = [UIColor colorWithRed:0.0f green:0.0f blue:0.0f alpha:0.2f];
    
    NSLog(@"Item Pre-Click");
}
- (IBAction)itemPostClick:(id)sender
{
    UIButton *btn = sender;
    
    btn.backgroundColor = [UIColor colorWithRed:0.0f green:0.0f blue:0.0f alpha:0.0f];
    
    NSLog(@"Item Post-Click");
}


- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
