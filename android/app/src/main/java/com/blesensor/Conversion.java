package com.blesensor;




import static java.lang.Math.pow;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by its on 28/2/18.
 */

public class Conversion extends ReactContextBaseJavaModule {
    private static final String DURATION_SHORT_KEY = "SHORT";
    private static final String DURATION_LONG_KEY = "LONG";

    public Conversion(ReactApplicationContext reactContext) {
        super(reactContext);
    }
    @Override
    public String getName() {
        return "Conversion";
    }
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
        constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
        return constants;
    }
    @ReactMethod
    public void convertGPSData(int index, String payload, Callback callBack) {
        switch (index) {
            case 0:
                convertTemp(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 1:
                convertHumid(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 2:
                convertPressure(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 3:
                convertFix(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 4:
                convertDate(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 5:
                convertTime(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 6:
                convertLatLng(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 7:
                convertLatLng(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 8:
                convertSpeed(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 9:
                convertCourse(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 10:
                convertSpeed(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
            case 11:
                convertGyro(Base64.decode(payload, Base64.DEFAULT), callBack);
                break;
        }
    }

    private void convertGyro(byte[] value, Callback callBack) {
        if(value != null){
            Point3D v0;
            Point3D v1;
            Point3D v2;
            final float SCALE0 = (float) 4096.0;

            int x0 = (value[7]<<8) + value[6];
            int y0 = (value[9]<<8) + value[8];
            int z0 = (value[11]<<8) + value[10];

            v0 = new Point3D(((x0 / SCALE0) * -1), y0 / SCALE0, ((z0 / SCALE0)*-1));



            final float SCALE1 = (float) 128.0;

            int x1 = (value[1]<<8) + value[0];
            int y1 = (value[3]<<8) + value[2];
            int z1 = (value[5]<<8) + value[4];

            v1 = new Point3D(x1 / SCALE1, y1 / SCALE1, z1 / SCALE1);



            final float SCALE2 = (float) (32768 / 4912);
            if (value.length >= 18) {
                int x2 = (value[13]<<8) + value[12];
                int y2 = (value[15]<<8) + value[14];
                int z2 = (value[17]<<8) + value[16];
                v2 = new Point3D(x2 / SCALE2, y2 / SCALE2, z2 / SCALE2);
            }else{
                v2 =  new Point3D(0,0,0);
            }
//            callBack.invoke(new Object[]{null, "ACC: "+String.format("X:%.2fG, Y:%.2fG, Z:%.2fG", v0.x, v0.y, v0.z)+
//                    "\n"+
//                    "GYRO: "+String.format("X:%.2f°/s, Y:%.2f°/s, Z:%.2f°/s", v1.x, v1.y, v1.z)+
//                    "\n"+
//                    "MAGNETO: "+String.format("X:%.2fuT, Y:%.2fuT, Z:%.2fuT", v2.x, v2.y, v2.z)});
            WritableArray array = new WritableNativeArray();
            array.pushString(String.format("%.2f", v0.x));
            array.pushString(String.format("%.2f", v0.y));
            array.pushString(String.format("%.2f", v0.z));
            array.pushString(String.format("%.2f", v1.x));
            array.pushString(String.format("%.2f", v1.y));
            array.pushString(String.format("%.2f", v1.z));
            array.pushString(String.format("%.2f", v2.x));
            array.pushString(String.format("%.2f", v2.y));
            array.pushString(String.format("%.2f", v2.z));
            callBack.invoke(new Object[]{null, array});

        }
    }

    private void convertCourse(byte[] payload, Callback callBack) {
        Log.d("debug", payload[0] + "" + payload[1] + "" + payload[2] + "" + payload[3]);
        long val = unsignedIntToLong(payload);
        Point3D v = new Point3D(val, 0, 0);
        double course = v.x;
        callBack.invoke(new Object[]{null, course});
    }

    private void convertSpeed(byte[] payload, Callback callBack) {
        long val = unsignedIntToLong(payload);
        Point3D v = new Point3D(val, 0, 0);
        double speed = v.x/1000;
        callBack.invoke(new Object[]{null, speed});
    }

    private void convertTime(byte[] value, Callback callBack) {
        long val = getUnsignedInt(value);
        Point3D v = new Point3D(val, 0, 0);
        final StringBuilder stringBuilder = new StringBuilder(value.length);
        for(byte byteChar : value) {
            stringBuilder.append(addPadding(2,String.valueOf(hexToLong(String.format("%02X", byteChar)))));
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        TimeZone utcZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat.setTimeZone(utcZone);

        int interval = 2;
        char separator = ':';

        StringBuilder sb = new StringBuilder(stringBuilder);

        for(int i = 0; i < stringBuilder.length() / interval; i++) {
            sb.insert(((i + 1) * interval) + i, separator);
        }
        String timeFormat = sb.toString();

        try {
            Date myDate = simpleDateFormat.parse(timeFormat);
            TimeZone tz = TimeZone.getDefault();
            if(tz.getID().equals("Asia/Singapore")){
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            }else{
                simpleDateFormat.setTimeZone(tz);
            }
            String formattedDate = simpleDateFormat.format(myDate);
            callBack.invoke(new Object[]{null, formattedDate});
        }catch (ParseException e){
            e.printStackTrace();
        }
    }
    public static long getUnsignedInt(byte[] data) {
        long result = 0;

        for (int i = 0; i < data.length; i++) {
            result += data[i] << 8 * (data.length - 1 - i);
        }
        return result;
    }
    private void convertDate(byte[] payload, Callback callBack) {
        long val = unsignedIntToLong(payload);
        Point3D v = new Point3D(val, 0, 0);
        final StringBuilder stringBuilder = new StringBuilder(payload.length);
        for(int i=0;i< payload.length;i++){
            if(i < 2 ){
                String formatting = String.format("%02X", payload[i]);
                stringBuilder.append(addPadding(2,String.valueOf(hexToLong(formatting))) + "-");
            }
        }
        int concateYear = ((payload[2] & 0xFF) << 8) | (payload[3] & 0xFF);
        stringBuilder.append(hexToLong(String.format("%02X", concateYear)));
        callBack.invoke(new Object[]{null, stringBuilder.toString()});
    }
    public String addPadding(int length, String text) {
        StringBuilder sb = new StringBuilder();

        // First, add (length - 'length of text') number of '0'
        for (int i = length - text.length(); i > 0; i--) {
            sb.append('0');
        }

        // Next, add string itself
        sb.append(text);
        return sb.toString();
    }
    private long hexToLong(String hex) {
        return Long.parseLong(hex, 16);
    }
    private void convertFix(byte[] payload, Callback callBack) {
        callBack.invoke(new Object[]{null, String.valueOf(payload[0])});
    }

    private static final double PA_PER_METER = 12.0;
    private void convertPressure(byte[] value, Callback callBack) {
        if(value != null){
            Point3D v;
            if (value.length > 4) {
                Integer val = twentyFourBitUnsignedAtOffset(value, 2);
                v = new Point3D((double) val / 100.0, 0, 0);
            }
            else {
                int mantissa;
                int exponent;
                Integer sfloat = shortUnsignedAtOffset(value, 2);

                mantissa = sfloat & 0x0FFF;
                exponent = (sfloat >> 12) & 0xFF;

                double output;
                double magnitude = pow(2.0f, exponent);
                output = (mantissa * magnitude);
                v = new Point3D(output / 100.0f, 0, 0);
            }
            double h = (v.x)
                    / PA_PER_METER;
            h = (double) Math.round(-h * 10.0) / 10.0;
//            callBack.invoke(new Object[]{null, String.format("%.1f mBar %.1f meter", v.x / 100, h)});

            WritableArray array = new WritableNativeArray();
            array.pushString(String.format("%.1f", v.x / 100));
            array.pushString(String.format("%.1f", h));
            callBack.invoke(new Object[]{null, array});
        }
    }
    private static Integer twentyFourBitUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer mediumByte = (int) c[offset+1] & 0xFF;
        Integer upperByte = (int) c[offset + 2] & 0xFF;
        return (upperByte << 16) + (mediumByte << 8) + lowerByte;
    }

    private void convertHumid(byte[] payload, Callback callBack) {
        Point3D v;
        int a = shortUnsignedAtOffset(payload, 2);
        a = a - (a % 4);

        //v = new Point3D((-6f) + 125f * (a / 65535f), 0, 0);
        v = new Point3D(100f * (a / 65535f), 0, 0);
        callBack.invoke(new Object[]{null, String.format("%.1f", v.x)});
    }
    private void convertLatLng(byte[] payload, Callback callBack){
        long val = unsignedIntToLong(payload);
        Point3D v = new Point3D(val, 0, 0);
        double latitude = v.x/10000000;
        callBack.invoke(new Object[]{null, latitude});
    }
    private void convertTemp(byte[] payload, Callback callBack){
        double ambient = extractAmbientTemperature(payload);
        double target = extractTargetTemperature(payload, ambient);
        double targetNewSensor = extractTargetTemperatureTMP007(payload);
        Point3D v = new Point3D(ambient, target, targetNewSensor);
//        callBack.invoke(new Object[]{null, String.format("%.1f°C", v.x)});
        callBack.invoke(new Object[]{null, String.format("%.1f", v.x)});
    }
    private double extractTargetTemperatureTMP007(byte [] v) {
        int offset = 0;
        return shortUnsignedAtOffset(v, offset) / 128.0;
    }
    private double extractTargetTemperature(byte [] v, double ambient) {
        Integer twoByteValue = shortSignedAtOffset(v, 0);

        double Vobj2 = twoByteValue.doubleValue();
        Vobj2 *= 0.00000015625;

        double Tdie = ambient + 273.15;

        double S0 = 5.593E-14; // Calibration factor
        double a1 = 1.75E-3;
        double a2 = -1.678E-5;
        double b0 = -2.94E-5;
        double b1 = -5.7E-7;
        double b2 = 4.63E-9;
        double c2 = 13.4;
        double Tref = 298.15;
        double S = S0 * (1 + a1 * (Tdie - Tref) + a2 * pow((Tdie - Tref), 2));
        double Vos = b0 + b1 * (Tdie - Tref) + b2 * pow((Tdie - Tref), 2);
        double fObj = (Vobj2 - Vos) + c2 * pow((Vobj2 - Vos), 2);
        double tObj = pow(pow(Tdie, 4) + (fObj / S), .25);

        return tObj - 273.15;
    }
    private double extractAmbientTemperature(byte [] v) {
        int offset = 2;
        return shortUnsignedAtOffset(v, offset) / 128.0;
    }
    private Integer shortUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer upperByte = (int) c[offset+1] & 0xFF;
        return (upperByte << 8) + lowerByte;
    }
    private static Integer shortSignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer upperByte = (int) c[offset+1]; // // Interpret MSB as signed
        return (upperByte << 8) + lowerByte;
    }
    public static final long unsignedIntToLong(byte[] b)
    {
        long l = 0;
        l |= b[0] & 0xFF;
        l <<= 8;
        l |= b[1] & 0xFF;
        l <<= 8;
        l |= b[2] & 0xFF;
        l <<= 8;
        l |= b[3] & 0xFF;
        return l;
    }
}
