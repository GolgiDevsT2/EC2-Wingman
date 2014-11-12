//
//  CPUView.m
//  EC2 Wingman
//
//  Created by Brian Kelly on 10/30/14.
//  Copyright (c) 2014 Golgi. All rights reserved.
//

#import "CPUView.h"

@implementation CPUView
@synthesize cpuUsage;


- (CGColorRef) colorFromHex:(NSInteger)hexColor
{
    int alpha = ((hexColor >> 24) & 0xff);
    int r = ((hexColor >> 16) & 0xff);
    int g = ((hexColor >> 8) & 0xff);
    int b = ((hexColor >> 0) & 0xff);
    
    
    return [[UIColor colorWithRed:(r/255.0f) green:(g/255.0f) blue:(b/255.0f) alpha:(alpha/255.0f)] CGColor];
}


// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    
    // NSLog(@"drawRect() %f,%f,%f,%f", rect.origin.x, rect.origin.y, rect.size.width, rect.size.height);
    CGRect r1 = CGRectMake(0.0f, 0.0f, (rect.size.width * cpuUsage)/100.0f, rect.size.height);
    CGColorRef color;
    
    CGContextRef ctx = UIGraphicsGetCurrentContext();
    CGContextSaveGState(ctx);
    CGContextAddRect(ctx, r1);
    
    if(cpuUsage >= 80){
        color = [self colorFromHex:0xffcc0000];
    }
    else if(cpuUsage >= 50){
        color = [self colorFromHex:0xffdd7700];
    }
    else{
        color = [self colorFromHex:0xff00aa00];
    }

    CGContextSetFillColor(ctx, CGColorGetComponents(color));
    CGContextFillPath(ctx);
    CGContextRestoreGState(ctx);
    CGContextSaveGState(ctx);
    CGContextSetFillColor(ctx, CGColorGetComponents([self colorFromHex:0xee000000]));
    
    CGContextAddRect(ctx, rect);
    
    for(int i = 0; i <= 100; i += 10){
        CGFloat x = (i * rect.size.width) / 100.0f;
        CGContextMoveToPoint(ctx, x, 0.0f);
        CGContextAddLineToPoint(ctx, x, rect.size.height);
    }
    
    for(int i = 5; i < 100; i += 10){
        CGFloat x = (i * rect.size.width) / 100.0f;
        CGContextMoveToPoint(ctx, x, rect.size.height/2.0);
        CGContextAddLineToPoint(ctx, x, rect.size.height);
    }

    CGContextStrokePath(ctx);
    
    
    CGContextRestoreGState(ctx);

}


@end
