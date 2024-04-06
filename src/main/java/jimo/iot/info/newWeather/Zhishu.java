package jimo.iot.info.newWeather;

import lombok.Data;

@Data
public class Zhishu {
    private Advice chuanyi;              // 穿衣指数
    private Advice daisan;               // 带伞指数
    private Advice ganmao;               // 感冒指数
    private Advice chenlian;             // 晨练指数
    private Advice ziwaixian;            // 紫外线指数
    private Advice liangshai;            // 晾晒指数
    private Advice kaiche;               // 开车指数
    private Advice xiche;                // 洗车指数
    private Advice lvyou;                // 旅游指数
    private Advice diaoyu;               // 钓鱼指数

    // Getters and setters
    // Constructors
    // Other methods
}
