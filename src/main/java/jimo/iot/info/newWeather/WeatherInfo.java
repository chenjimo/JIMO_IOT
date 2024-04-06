package jimo.iot.info.newWeather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class WeatherInfo {
    private String cityid;              // 城市ID
    private String date;                // 日期
    private String week;                // 星期几
    private String update_time;         // 更新时间
    private String city;                // 城市名称
    private String cityEn;              // 城市英文名称
    private String country;             // 国家
    private String countryEn;           // 国家英文名称
    private String wea;                 // 天气情况
    private String wea_img;             // 天气图片
    private String tem;                 // 温度
    private String tem1;                // 最高温度
    private String tem2;                // 最低温度
    private String win;                 // 风向
    private String win_speed;           // 风力
    private String win_meter;           // 风速
    private String humidity;            // 湿度
    private String visibility;          // 能见度
    private String pressure;            // 气压
    private String air;                 // 空气质量指数
    private String air_pm25;            // PM2.5指数
    private String air_level;           // 空气质量等级
    private String air_tips;            // 空气质量建议
//    @JsonIgnore
    private Object alarm;                // 预警信息,如果只有一条数据存入index0（事先做一层判断）
    private String rain_pcpn;           // 降水量
    private String uvIndex;             // 紫外线指数
    private String uvDescription;       // 紫外线描述
    private String wea_day;             // 白天天气
    private String wea_day_img;         // 白天天气图片
    private String wea_night;           // 夜晚天气
    private String wea_night_img;       // 夜晚天气图片
    private String sunrise;             // 日出时间
    private String sunset;              // 日落时间
    private List<HourWeather> hours;    // 小时天气信息
    private Aqi aqi;                    // 空气质量指数信息
    private Zhishu zhishu;              // 生活指数

    // Getters and setters
    // Constructors
    // Other methods
}
