package jimo.iot.info.newWeather;

import lombok.Data;

@Data
public class Aqi {
    private String update_time;         // 更新时间
    private String air;                 // 空气质量指数
    private String air_level;           // 空气质量等级
    private String air_tips;            // 空气质量建议
    private String pm25;                // PM2.5指数
    private String pm25_desc;           // PM2.5等级描述
    private String pm10;                // PM10指数
    private String pm10_desc;           // PM10等级描述
    private String o3;                  // 臭氧指数
    private String o3_desc;             // 臭氧等级描述
    private String no2;                 // 二氧化氮指数
    private String no2_desc;            // 二氧化氮等级描述
    private String so2;                 // 二氧化硫指数
    private String so2_desc;            // 二氧化硫等级描述
    private String co;                  // 一氧化碳指数
    private String co_desc;             // 一氧化碳等级描述
    private String kouzhao;             // 口罩建议
    private String yundong;             // 运动建议
    private String waichu;              // 外出建议
    private String kaichuang;           // 开窗建议
    private String jinghuaqi;           // 净化器建议

    // Getters and setters
    // Constructors
    // Other methods
}
