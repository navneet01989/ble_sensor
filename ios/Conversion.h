//
//  objC.h
//  BLETemperatureReaderSwift
//
//  Created by ITS on 29/6/17.
//  Copyright © 2017 Cloud City. All rights reserved.
//


#import <UIKit/UIKit.h>
#import <React/RCTBridgeModule.h>

@interface Conversion : NSObject<RCTBridgeModule>

typedef struct Point3D_ {
    CGFloat xx,yy,zz;
} Point3D;

@property Point3D acc;
@property Point3D gyro;
@property Point3D mag;
@property CGFloat airPressure;
@property CGFloat gps;
@property CGFloat humidity;
@property CGFloat objectTemperature;
@property CGFloat ambientTemperature;
- (NSDateFormatter *)dateFormatter;
@end
