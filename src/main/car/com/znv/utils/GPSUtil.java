package com.znv.utils;

/**
 * GPS转换
 * Created by MaHuiming on 2018/5/9.
 */
public class GPSUtil {

    private static final double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
    private static final double PI = 3.1415926535897932384626;
    private static final double a = 6378245.0;
    private static final double ee = 0.00669342162296594323;

    public static void main(String[] args) {
        //121.5191574,31.1806507 派出所地址高德
        //高德转百度
        /*double[] hm2 = GPSUtil.gcj02Tobd09(121.5191574,31.1806507);
        System.out.println("百度坐标：" + hm2[0] + "," + hm2[1]);

        //百度转高德
        double[] hm = GPSUtil.bd09Togcj02(121.5256912608269,31.18645045657703);
        System.out.println("高德坐标：" + hm[0] + "," + hm[1]);

        //百度转高德
        hm = GPSUtil.bd09Togcj02(121.5256912608269,31.18645045657703);
        //高德转84
        hm = GPSUtil.gcj02Towgs84(hm[0],hm[1]);
        System.out.println("高德转84坐标：" +hm[0] + "," + hm[1]);
        //84转高德
        hm = GPSUtil.gps84Togcj02(hm[0],hm[1]);
        System.out.println("84转高德坐标：" +hm[0] + "," + hm[1]);
        hm = GPSUtil.bd09Togps84(121.5256912608269,31.18645045657703);
        System.out.println("百度转84坐标：" +hm[0] + "," + hm[1]);*/
        double[] hm2 = GPSUtil.gcj02Towgs84(121.509877,31.199328);
        System.out.println("高德转84坐标：" + hm2[0] + "," + hm2[1]);

    }

    /**
     * 百度(BD-09)转高德火星坐标系 (GCJ-02)
     *
     * @param bd_lng GPS经度
     * @param bd_lat GPS纬度
     * @return
     */
    public static double[] bd09Togcj02(double bd_lng, double bd_lat) {
        double[] gd_lat_lon = new double[2];
        double x = bd_lng - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        gd_lat_lon[0] = z * Math.cos(theta);
        gd_lat_lon[1] = z * Math.sin(theta);
        return gd_lat_lon;
    }

    /**
     * 高德火星坐标系 (GCJ-02)转百度(BD-09)
     *
     * @param gd_lng GPS经度
     * @param gd_lat GPS纬度
     * @return
     */
    private static double[] gcj02Tobd09(double gd_lng, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double x = gd_lng, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }

    /**
     * 高德火星坐标系 (GCJ-02)转84
     * @param lng GPS经度
     * @param lat GPS纬度
     * @return
     */
    public static double[] gcj02Towgs84(double lng, double lat) {
        double[] gps = transform(lat, lng);
        double lontitude = lng * 2 - gps[1];
        double latitude = lat * 2 - gps[0];
        return new double[]{lontitude,latitude};
    }

    /**
     * 84转高德火星坐标系 (GCJ-02)
     * @param lng GPS经度
     * @param lat GPS纬度
     * @return
     */
    public static double[] gps84Togcj02(double lng,double lat) {
        double dLon = transformLon(lng - 105.0, lat - 35.0);
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        double mgLon = lng + dLon;
        double mgLat = lat + dLat;
        return new double[]{mgLon,mgLat};
    }

    /**
     * 84转百度
     * @param lat
     * @param lon
     * @return
     */
    public static double[] gps84Tobd09(double lat,double lon){
        double[] gcj02 = gps84Togcj02(lat,lon);
        double[] bd09 = gcj02Tobd09(gcj02[0],gcj02[1]);
        return bd09;
    }

    /**
     * 百度转84
     * @param lat
     * @param lon
     * @return
     */
    public static double[] bd09Togps84(double lat,double lon){
        double[] gcj02 = bd09Togcj02(lat, lon);
        double[] gps84 = gcj02Towgs84(gcj02[0], gcj02[1]);
        //保留小数点后六位
        gps84[0] = retain7(gps84[0]);
        gps84[1] = retain7(gps84[1]);
        return gps84;
    }

    /**
     * 转换纬度
     * @param lng
     * @param lat
     * @return
     */
    public static double transformLat(double lng, double lat) {
        double ret =
        -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 转换经度
     * @param lng
     * @param lat
     * @return
     */
    public static double transformLon(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 转换经纬度
     * @param lat
     * @param lng
     * @return
     */
    public static double[] transform(double lat, double lng) {
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLon = transformLon(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat + dLat;
        double mgLon = lng + dLon;
        return new double[]{mgLat,mgLon};
    }

    /**
     * 保留小数点后六位
     * @param num
     * @return
     */
    public static double retain7(double num){
        String result = String .format("%.7f", num);
        return Double.valueOf(result);
    }
}
