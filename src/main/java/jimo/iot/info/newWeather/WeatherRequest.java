package jimo.iot.info.newWeather;

import lombok.Data;

/***
 * 这个是用于天气API请求的的参数类！
 * 具体的天气位置参数JSON 数据中缺少了这些额外的信息，只提供了基本的天气数据，包括城市、日期、天气情况、温度、风向、风速、湿度、能见度、气压和空气质量等。
 * 1、JSON 数据中缺少了这些额外的信息，只提供了基本的天气数据，包括城市、日期、天气情况、温度、风向、风速、湿度、能见度、气压和空气质量等。
 * 2、JSON 数据中的 air_level 和 air_tips 字段包含了关于空气质量的建议信息。
 * 3、JSON 数据中的 alarm 字段包含了预警信息，包括了类型、级别、标题和内容等。
 * 4、 JSON 数据中的 uvIndex 和 uvDescription 字段提供了紫外线指数和对应的描述信息。
 * 如果没有预警的情况下，alarm参数不是list格式！！！注意处理一下
 */
@Data
public class WeatherRequest {
    private String appid;       // 用户的 appid，注册开发账号后获取
    private String appsecret;   // 用户的 appsecret
    private String version;     // 接口版本标识，固定值为 v62，每个接口的 version 值都不一样
    private String adcode;      // 国家统计局城市ID，可选参数，参考全国统计用区划代码表
    private String cityid;      // 城市ID，可选参数，参考城市ID列表。cityid优先级最高。
    private String city;        // 城市名称，可选参数，不要带市和区，如青岛、铁西
    private String province;    // 所在省，可选参数，如果担心城市重名可传此参数，不要带省和市，如山东、上海
    private String ip;          // IP地址，可选参数，查询IP所在城市天气
    private String lng;         // 经度，可选参数，如119.545023（需额外开通lbs权限，500/年，2000/5年）
    private String lat;         // 纬度，可选参数，如36.044254
    private String point;       // 坐标体系，默认百度坐标，如使用高德坐标，请传参 gaode
    private String callback;    // jsonp参数，可选参数，如 jQuery.Callbacks
    private String vue;         // 跨域参数，可选参数，如果使用 react、vue、angular，请填写值为1
    private int unescape;       // 是否转义中文，可选参数，如果希望 json 不被 unicode，直接输出中文，请传参1

    // Getters and setters
}
