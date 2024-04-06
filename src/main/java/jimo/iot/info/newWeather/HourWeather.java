package jimo.iot.info.newWeather;

import lombok.Data;

@Data
public class HourWeather {
    private String hours;               // 小时
    private String wea;                 // 天气情况
    private String wea_img;             // 天气图片
    private String tem;                 // 温度
    private String win;                 // 风向
    private String win_speed;           // 风力
    private String vis;                 // 能见度
    private String aqinum;              // 空气质量指数
    private String aqi;                 // 空气质量等级

    // Getters and setters
    // Constructors
    // Other methods
}
