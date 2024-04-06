package jimo.iot.info.newWeather;

import lombok.Data;

@Data
public class Warning {
    private String safecode;    // 安全码，后台可修改，用于通信验证
    private String title;       // 预警标题，如：河北省霸州市发布雷电黄色预警
    private String type;        // 预警类型，如：雷电
    private String level;       // 预警等级，如：黄色
    private String text;        // 具体内容
    private String time;        // 发布时间
    private int pushlevel;      // 推送等级，1：省级预警，2：市级预警，3：区县级预警
    private String cityid;      // 城市ID
    private String province;    // 省份
    private String city;        // 城市名称
    private String area;        // 区县名称

    public Warning() {
    }

    // Getters and setters
}
