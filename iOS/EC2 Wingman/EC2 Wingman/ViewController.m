//
//  ViewController.m
//  EC2 Wingman
//
//  Created by Brian Kelly on 10/30/14.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import "ViewController.h"
#import "InstanceCell.h"
#import "GolgiStuff.h"
#import "WingmanSvcWrapper.h"
#import "InstanceSettingsViewController.h"

@implementation ViewController
@synthesize theTableView;
@synthesize statusLabel;
@synthesize filterTextField;
@synthesize currentInstanceId;
@synthesize currentInstanceName;

+ (BOOL)filterMatchesName:(NSString *)name
{
    BOOL result = false;
    NSString *f = [GolgiStore getStringForKey:@"FILTER" withDefault:@""];
    
    f = [[f lowercaseString] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    
    if(f.length == 0){
        result = true;
    }
    else{
        name = [name lowercaseString];
        if([name rangeOfString:f].location != NSNotFound){
            result = true;
        }
    }

    return result;
}

- (void)runFilterUsingString:(NSString *)f
{
    NSMutableArray *a = [[NSMutableArray alloc] init];
    for(int i = 0; i < fullList.count; i++){
        InstanceData *iData = [fullList objectAtIndex:i];
        if([ViewController filterMatchesName:[iData getName]]){
            [a addObject:iData];
        }
    }
    liveList = a;
    
    [theTableView reloadData];
}

- (void)runFilter
{
    NSString  *f = filterTextField.text;
    [self runFilterUsingString:f];
}



- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string
{
    NSLog(@"Text: '%@'", filterTextField.text);
    NSString *f = filterTextField.text;
    f = [f stringByReplacingCharactersInRange:range withString:string];
    [GolgiStore deleteStringForKey:@"FILTER"];
    [GolgiStore putString:f forKey:@"FILTER"];
    [self runFilterUsingString:f];
    
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return NO;
}


- (void)updateList:(InstanceList *)_iList
{
    
    lastUpdate = time(NULL);
    
    
    fullList = [[_iList getIList] sortedArrayUsingComparator:^NSComparisonResult(id a, id b) {
        InstanceData *first = (InstanceData *)a;
        InstanceData *second = (InstanceData *)b;
        int s1 = [a getState] & 0xff;
        int s2 = [b getState] & 0xff;
        

        if(s1 == s2 && s1 == 16){
            //
            // Running
            //
            return (NSComparisonResult)([second getCpuUsage] - [first getCpuUsage]);
        }
        else if(s1 == 16){
            return (NSComparisonResult)-1;
        }
        else if(s2 == 16){
            return (NSComparisonResult)1;
        }
        else{
            return [[first getName] compare:[second getName]];
        }
    }];
    
    [self runFilter];

    
}

- (void)updateStatus
{
    NSString *str;
    if(lastUpdate == 0){
        str = @"Never";
    }
    else{
        int diff = (int)(time(NULL) - lastUpdate);
        if(diff < 5){
            str = @"Just Now";
        }
        else if(diff < 120){
            str = [NSString stringWithFormat:@"%d seconds ago", diff];
        }
        else{
            str = [NSString stringWithFormat:@"%d minutes ago", (diff / 60)];
        }
    }
    
    statusLabel.text = [NSString stringWithFormat:@"Last Updated: %@", str];
    
}



- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return liveList.count;
}

- (UITableViewCell *)tableView:(UITableView *)tView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSInteger cpuUsage;
    InstanceData *iData = [liveList objectAtIndex:indexPath.row];
    
    InstanceCell *cell;
    
    int s = [iData getState] & 0xff;
    
    
    if(s == 16){
        // Running
        cell = [tView dequeueReusableCellWithIdentifier:@"RunningCell" forIndexPath:indexPath];
    
        cpuUsage = [iData getCpuUsage];
        cell.cpuLabel.text = [NSString stringWithFormat:@"%ld%%", (long)cpuUsage];
        
        cell.cpuView.cpuUsage = cpuUsage;
        
        [cell.cpuView setNeedsDisplay];
    }
    else{
        cell = [tView dequeueReusableCellWithIdentifier:@"NonRunningCell" forIndexPath:indexPath];
        NSString *str;
        switch(s){
            case 0:
                str = @"PENDING";
                break;
            case 16:
                str = @"RUNNING";
                break;
            case 32:
                str = @"SHUTTING-DOWN";
                break;
            case 48:
                str = @"TERMINATED";
                break;
            case 64:
                str = @"STOPPING";
                break;
            case 80:
                str = @"STOPPED";
                break;
            default:
                str = [NSString stringWithFormat:@"UKNOWN STATE %d", s];
                break;
        }
        cell.stateLabel.text = str;
        
    }
    cell.theViewController = self;
    
    cell.nameLabel.text = [iData getName];
    cell.instanceId = [iData getInstanceId];
    
    if([InstanceSettingsViewController hasCustomSettings:cell.instanceId]){
        cell.customisedView.hidden = false;
    }
    else{
        cell.customisedView.hidden = true;
    }

    
    return cell;
}

- (IBAction)settingsPressed:(id)sender
{
    [self performSegueWithIdentifier:@"Settings" sender:self];
}

- (void)viewWillAppear:(BOOL)animated
{
    [GolgiStuff getInstance].viewController = self;
    filterTextField.text = [GolgiStore getStringForKey:@"FILTER" withDefault:@""];
    [theTableView reloadData];
}


-(void)viewDidDisappear:(BOOL)animated
{
    [GolgiStuff getInstance].viewController = nil;
}



- (void)viewDidLoad {
    [super viewDidLoad];
    [GolgiStuff getInstance].viewController = self;
    [self updateList:[GolgiStuff getInstance].currentList];
    
    theTableView.delaysContentTouches = NO;

    // Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
