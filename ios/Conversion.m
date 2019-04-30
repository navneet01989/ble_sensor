//
//  objC.m
//  BLETemperatureReaderSwift
//
//  Created by ITS on 29/6/17.
//  Copyright © 2017 Cloud City. All rights reserved.
//

#import "Conversion.h"
@implementation Conversion
RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(convertGPSData:(int)index payload:(nonnull NSString *)payload callback:(RCTResponseSenderBlock)callback)
{
  NSData *value = [[NSData alloc] initWithBase64EncodedString:payload options:0];
  switch (index) {
    case 0: {
      char scratchVal[value.length];
      int16_t ambTemp;
      int16_t objTemp;
      float tObj;
      //Ambient temperature first
      [value getBytes:&scratchVal length:value.length];
      ambTemp = ((scratchVal[2] & 0xff)| ((scratchVal[3] << 8) & 0xff00));
      //Then object temperature
      objTemp = ((scratchVal[0] & 0xff)| ((scratchVal[1] << 8) & 0xff00));
      objTemp >>= 2;
      tObj = ((float)objTemp) * 0.03125;
      
      self.objectTemperature = tObj;
      self.ambientTemperature = ambTemp / 128.0f;
      callback(@[[NSNull null], [NSString stringWithFormat:@"%0.1f",self.ambientTemperature]]);
    }
      break;
    case 1:{
      char scratchVal[value.length];
      [value getBytes:&scratchVal length:value.length];
      UInt16 hum;
      hum = (scratchVal[2] & 0xff) | ((scratchVal[3] << 8) & 0xff00);
      self.humidity = (float)((float)hum/(float)65535) * 100.0f;
      callback(@[[NSNull null], [NSString stringWithFormat:@"%0.1f",(float)self.humidity]]);
    }
      break;
    case 2: {
      if (value.length < 4) {
        callback(@[@"Custom error", [NSNull null]]);
      } else {
        char scratchVal[value.length];
        [value getBytes:&scratchVal length:value.length];
        uint32_t pres = (scratchVal[3] & 0xff) | ((scratchVal[4] << 8) & 0xff00) | ((scratchVal[5] << 16) & 0xff0000);
        self.airPressure =  (float)pres / 100.0f;
        callback(@[[NSNull null], [NSString stringWithFormat:@"%0.1f",(float)self.airPressure]]);
      }
      
    }
      break;
    case 3:{
      uint8_t byteArray[[value length]];
      [value getBytes:&byteArray length:value.length];
      NSMutableString *byteArrayString = [[NSMutableString alloc]init];
      [byteArrayString appendString:[NSString stringWithFormat:@"%d",byteArray[0]]];
      callback(@[[NSNull null], byteArrayString]);
    }
      break;
    case 4: {
      char scratchVal[value.length];
      [value getBytes:&scratchVal length:value.length];
      uint32_t l = 0;
      l = ((scratchVal[2] & 0xFF) << 8) | (scratchVal[3] & 0xFF);
      NSMutableString *byteArrayString = [[NSMutableString alloc]init];
      [byteArrayString appendString:[NSString stringWithFormat:@"%d-",scratchVal[0]]];
      [byteArrayString appendString:[NSString stringWithFormat:@"%d-",scratchVal[1]]];
      [byteArrayString appendString:[NSString stringWithFormat:@"%0.0f",(float)l]];
      callback(@[[NSNull null], byteArrayString]);
    }
      break;
    case 5: {
      uint8_t byteArray[[value length]];
      [value getBytes:&byteArray length:value.length];
      NSMutableString *byteArrayString = [[NSMutableString alloc]init];
      [byteArrayString appendString:[NSString stringWithFormat:@"%d:",byteArray[0]]];
      [byteArrayString appendString:[NSString stringWithFormat:@"%d:",byteArray[1]]];
      [byteArrayString appendString:[NSString stringWithFormat:@"%d",byteArray[2]]];
      NSDateFormatter *formatter = [self dateFormatter];
      [formatter setDateFormat:@"HH:mm:ss"];
      [formatter setTimeZone:[NSTimeZone timeZoneWithAbbreviation:@"UTC"]];
      NSDate *sourceDate = [formatter dateFromString:byteArrayString];
      [formatter setTimeZone:[NSTimeZone systemTimeZone]];
      [formatter setDateFormat:@"HH:mm:ss"];
      NSString* localTime = [formatter stringFromDate:sourceDate];
      callback(@[[NSNull null], [NSString stringWithFormat:@"%@",localTime]]);
    }
      break;
    case 6: {
      if (value.length < 4){
        callback(@[@"Custom error", [NSNull null]]);
      } else {
        char scratchVal[value.length];
        [value getBytes:&scratchVal length:value.length];
        uint32_t l = 0;
        l |= scratchVal[0] & 0xFF;
        l <<= 8;
        l |= scratchVal[1] & 0xFF;
        l <<= 8;
        l |= scratchVal[2] & 0xFF;
        l <<= 8;
        l |= scratchVal[3] & 0xFF;
        self.gps =  (float)l / 10000000;
        callback(@[[NSNull null], [NSString stringWithFormat:@"%0.8f",(float)self.gps]]);
      }
    }
      break;
    case 7: {
      if (value.length < 4){
        callback(@[@"Custom error", [NSNull null]]);
      } else {
        char scratchVal[value.length];
        [value getBytes:&scratchVal length:value.length];
        uint32_t l = 0;
        l |= scratchVal[0] & 0xFF;
        l <<= 8;
        l |= scratchVal[1] & 0xFF;
        l <<= 8;
        l |= scratchVal[2] & 0xFF;
        l <<= 8;
        l |= scratchVal[3] & 0xFF;
        self.gps =  (float)l / 10000000;
        callback(@[[NSNull null], [NSString stringWithFormat:@"%0.8f",(float)self.gps]]);
      }
    }
      break;
    case 8: {
      if (value.length < 4){
        callback(@[@"Custom error", [NSNull null]]);
      } else {
        char scratchVal[value.length];
        [value getBytes:&scratchVal length:value.length];
        uint32_t l = 0;
        l |= scratchVal[0] & 0xFF;
        l <<= 8;
        l |= scratchVal[1] & 0xFF;
        l <<= 8;
        l |= scratchVal[2] & 0xFF;
        l <<= 8;
        l |= scratchVal[3] & 0xFF;
        self.gps =  (float)l / 10000000;
        callback(@[[NSNull null], [NSString stringWithFormat:@"%0.8f",(float)self.gps]]);
      }
    }
      break;
    case 9: {
      if (value.length < 4){
        callback(@[@"Custom error", [NSNull null]]);
      } else {
        char scratchVal[value.length];
        [value getBytes:&scratchVal length:value.length];
        uint32_t l = 0;
        l |= scratchVal[0] & 0xFF;
        l <<= 8;
        l |= scratchVal[1] & 0xFF;
        l <<= 8;
        l |= scratchVal[2] & 0xFF;
        l <<= 8;
        l |= scratchVal[3] & 0xFF;
        self.gps =  (float)l / 10000000;
        callback(@[[NSNull null], [NSString stringWithFormat:@"%0.8f",(float)self.gps]]);
      }
    }
      break;
    case 10: {
      if (value.length < 4){
        callback(@[@"Custom error", [NSNull null]]);
      } else {
        char scratchVal[value.length];
        [value getBytes:&scratchVal length:value.length];
        uint32_t l = 0;
        l |= scratchVal[0] & 0xFF;
        l <<= 8;
        l |= scratchVal[1] & 0xFF;
        l <<= 8;
        l |= scratchVal[2] & 0xFF;
        l <<= 8;
        l |= scratchVal[3] & 0xFF;
        self.gps =  (float)l / 10000000;
        callback(@[[NSNull null], [NSString stringWithFormat:@"%0.8f",(float)self.gps]]);
      }
    }
      break;
    case 11:
      if (value.length < 18) {
        callback(@[@"Custom error", [NSNull null]]);
      } else {
        char vals[value.length];
        [value getBytes:&vals length:value.length];
        
        Point3D gyroPoint;
        gyroPoint.xx = ((float)((int16_t)((vals[0] & 0xff) | (((int16_t)vals[1] << 8) & 0xff00)))/ (float) 32768) * 255 * 1;
        gyroPoint.yy = ((float)((int16_t)((vals[2] & 0xff) | (((int16_t)vals[3] << 8) & 0xff00)))/ (float) 32768) * 255 * 1;
        gyroPoint.zz = ((float)((int16_t)((vals[4] & 0xff) | (((int16_t)vals[5] << 8) & 0xff00)))/ (float) 32768) * 255 * 1;
        self.gyro = gyroPoint;
        
        Point3D accPoint;
        accPoint.xx = (((float)((int16_t)((vals[6] & 0xff) | (((int16_t)vals[7] << 8) & 0xff00)))/ (float) 32768) * 8) * 1;
        accPoint.yy = (((float)((int16_t)((vals[8] & 0xff) | (((int16_t)vals[9] << 8) & 0xff00))) / (float) 32768) * 8) * 1;
        accPoint.zz = (((float)((int16_t)((vals[10] & 0xff) | (((int16_t)vals[11] << 8) & 0xff00)))/ (float) 32768) * 8) * 1;
        self.acc = accPoint;
        
        Point3D magPoint;
        magPoint.xx = (((float)((int16_t)((vals[12] & 0xff) | (((int16_t)vals[13] << 8) & 0xff00))) / (float) 32768) * 4912);
        magPoint.yy = (((float)((int16_t)((vals[14] & 0xff) | (((int16_t)vals[15] << 8) & 0xff00))) / (float) 32768) * 4912);
        magPoint.zz = (((float)((int16_t)((vals[16] & 0xff) | (((int16_t)vals[17] << 8) & 0xff00))) / (float) 32768) * 4912);
        self.mag = magPoint;
        
        NSArray *myColors;
        myColors = [NSArray arrayWithObjects: [NSNumber numberWithFloat:self.acc.xx], [NSNumber numberWithFloat:self.acc.yy], [NSNumber numberWithFloat:self.acc.zz], [NSNumber numberWithFloat:self.mag.xx], [NSNumber numberWithFloat:self.mag.yy], [NSNumber numberWithFloat:self.mag.zz], [NSNumber numberWithFloat:self.gyro.xx], [NSNumber numberWithFloat:self.gyro.yy], [NSNumber numberWithFloat:self.gyro.zz], nil];
        callback(@[[NSNull null], myColors]);
      }
      
      
      break;
    default:
      break;
  }
}
- (NSDateFormatter *)dateFormatter
{
  static NSDateFormatter *formatter;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    formatter = [NSDateFormatter new];
  });
  return formatter;
}
@end
