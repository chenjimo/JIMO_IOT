package jimo.iot.info.newWeather;

import lombok.Data;

@Data
public class Alarm {
    private String alarm_type;          // 预警类型
    private String alarm_level;         // 预警等级
    private String alarm_title;         // 预警标题
    private String alarm_content;       // 预警内容

    // Getters and setters

    public Alarm() {
    }

    public Alarm(String alarm_type, String alarm_level, String alarm_title, String alarm_content) {
        this.alarm_type = alarm_type;
        this.alarm_level = alarm_level;
        this.alarm_title = alarm_title;
        this.alarm_content = alarm_content;
    }
    // Constructors
    // Other methods
}
