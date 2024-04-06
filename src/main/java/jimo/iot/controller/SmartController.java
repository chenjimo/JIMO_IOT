package jimo.iot.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jimo.iot.ctrl_module.controller.OderMessageController;
import jimo.iot.ctrl_module.entity.OderLog;
import jimo.iot.ctrl_module.service.IModuleMessageService;
import jimo.iot.ctrl_module.service.IOderLogService;
import jimo.iot.ctrl_module.service.IOderMessageService;
import jimo.iot.device_value.entity.DeviceLog;
import jimo.iot.device_value.service.IDeviceLogService;
import jimo.iot.info.Message;
import jimo.iot.info.newWeather.Alarm;
import jimo.iot.info.newWeather.WeatherInfo;
import jimo.iot.info.newWeather.WeatherRequest;
import jimo.iot.info.send.SendEmail;
import jimo.iot.util.APIUtil;
import jimo.iot.util.DateUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 智能感知模块的方法控制。
 * 定时的核心业务实现！！！
 * 超级无敌大类，这个业务差点给俺干到土里面！！！
 * </p>
 *
 * @author JIMO
 * @since 2024-04-03
 */
@Component
public class SmartController {
    @Value("${jimo.alter.openSmart}")
    private boolean openSmart;
    @Resource
    private APIUtil apiUtil;
    @Resource
    private SendEmail sendEmail;
    @Resource
    private IModuleMessageService moduleMessageService;
    @Resource
    private IOderLogService oderLogService;
    @Resource
    private IDeviceLogService deviceLogService;
    @Resource
    private IOderMessageService oderMessageService;
    @Resource
    private OderMessageController oderMessageController;

    /**
     * 需要定时的任务有：
     * 1、定时中断未执行任务
     * 2、天气感知
     * 3、数据库的信息整理，优化数据！（V1.3版本后再优化）
     *
     * @param s the input argument
     */
    public void accept(String s) throws IOException {
        if (openSmart) {//开关标识
            switch (s) {
                case "0 45 1 * * ?":
                    dataTreating();
                    break;
                case "0 0 24 * * ?":
                    scheduledNoon();
                    break;
                case "Test":
                    scheduledTest();
                    break;
                default: //非系统默认的智能模式任务："0 0 */3 * * ?"、"0 10 11 * * ?"
                    SmartModule(s);//智能模块的定时任务处理分流
                    break;
            }
        } else {
            System.out.println("暂未开启智能感知：openSmart:" + false);
        }
    }

    /***
     * 关于智能模块的处理有一层开关是数据库的模式标识是否打开需要再进行一次认证！
     * 前提一定要把第一层的智能定时任务开关打开，才会执行到这一层！
     * @param s 定时标识
     */
    public void SmartModule(String s) throws IOException {
        //智能模块的ID为5,注意区分mode和status的标识意图区别，status是一个不太及时的状态刷新标识，而mode是模块的模式标识（1为开启，0为关闭）！
        if (moduleMessageService.getModuleMessage(5).getMode() == 1) {
            if (Objects.equals(s, "0 0 */3 * * ?")) {
                weatherSensing();
            } else if (Objects.equals(s, "0 10 23 * * ?")) {
                oderClear();
            }
        }
    }

    /***
     * 每天的凌晨一点四十五分执行一次！
     * 0 45 1 * * ?
     * 进行数据库的数据分析整理处理！
     */

    public void dataTreating() {

    }

    /***
     * 每天的中午十二点执行一次！
     * power 2、3、6、7
     */

    public void scheduledNoon() {

    }

    /***
     * 每三小时执行一次！
     * 天气感知处理
     * 又做了一层智能指令的开关处理目的是为了细化管理！
     * 前提一定要把第一层的智能定时任务开关打开，且智能模块的开关也打开，才会执行到这一层！
     */
    public void weatherSensing() throws IOException {
        //智能天气感知指令的ID是17，状态为1时代表可用，0停用，-1删除。这里的处理方式不同于其他指令主要受status判断进行执行！
        if (oderMessageService.readOderMessage(17).getStatus() == 1) {
            WeatherRequest weatherRequest = new WeatherRequest();
            //这里应该添加一些对用户的模块信息的一些处理，添加一些参数（位置信息等）进去请求或者限制。这里就按默认的配置进行处理。以后有壮大的需求了在进行扩展！
            String weatherJSON = apiUtil.getNewWeatherByHourly(weatherRequest);
            //根据参数信息进行转为WeatherInfo，做好异常的处理！errcode存在这个字段则代表有异常
            if (JSONObject.fromObject(weatherJSON).get("errcode") == null && JSONObject.fromObject(weatherJSON).get("error") == null) {
                ObjectMapper objectMapper = new ObjectMapper();
                WeatherInfo weatherInfo = objectMapper.readValue(weatherJSON, WeatherInfo.class);
                Object alarmInfoObj = weatherInfo.getAlarm();
                // 检查 alarm 对象的类型。天气图片地址：http://127.0.0.1:9288/static/img/cherry/yun.png
                if (alarmInfoObj instanceof Map) {
                    // 判断是否为 Map 类型
                    Map<String, String> alarmInfo = (Map<String, String>) alarmInfoObj;
                    //转化为类
                    Alarm alarm = new Alarm(alarmInfo.get("alarm_type"), alarmInfo.get("alarm_level"), alarmInfo.get("alarm_title"), alarmInfo.get("alarm_content"));
                    //将数据传给专用的方法进行处理
                    smartRack(weatherInfo);
                    smartWatchdog(alarm, null);
                } else if (alarmInfoObj instanceof List) {
                    // 如果 alarm 是 List 类型
                    List<Map<String, String>> alarmInfoList = (List<Map<String, String>>) alarmInfoObj;
                    // 创建存储 Alarm 对象的列表
                    List<Alarm> alarms = new ArrayList<>();
                    for (Map<String, String> alarmInfo : alarmInfoList) {
                        String alarmType = alarmInfo.get("alarm_type");
                        String alarmLevel = alarmInfo.get("alarm_level");
                        String alarmTitle = alarmInfo.get("alarm_title");
                        String alarmContent = alarmInfo.get("alarm_content");
                        // 创建并添加 Alarm 对象到列表中
                        Alarm alarm = new Alarm(alarmType, alarmLevel, alarmTitle, alarmContent);
                        alarms.add(alarm);
                    }
                    //将数据传给专用的方法进行处理
                    smartRack(weatherInfo);
                    smartWatchdog(null, alarms);
                } else {
                    // 如果 alarm 是其他类型
                    System.out.println("天气信息参数alarm是其他类型,不兼容转化。源JSON：" + weatherJSON);
                }
            } else {//进入这里代表存在异常，导致信息无法被正常的读取到！
                System.out.println("获取天气信息异常：src/main/java/jimo/iot/controller/SmartController.java-142,weatherJSON=" + weatherJSON);
            }


        }
    }

    /***
     * 每天的晚上十一点时分执行一次！
     * 0 10 11 * * ?
     * 终止24小时以前未执行的指令！
     * 又做了一层智能指令的开关处理目的是为了细化管理！
     * 前提一定要把第一层的智能定时任务开关打开，且智能模块的开关也打开，才会执行到这一层！
     */
    public void oderClear() {
        //智能指令感知指令的ID是16，状态为1时代表可用，0停用，-1删除。这里的处理方式不同于其他指令主要受status判断进行执行！
        if (oderMessageService.readOderMessage(16).getStatus() == 1) {
            //中断失败24小时以前未执行的指令，并写入备注信息。
            Integer i = oderLogService.setPastDueOderAll(24, "AUTO:中断过期指令");
            System.out.println("AUTO:中断过期指令,一共成功处理" + i + "条指令");
        }
    }

    /***
     * 测试每小时一执行
     */

    public void scheduledTest() {
    }

    /***
     * 专门用于处理智能晾衣架的未来天气感知处理方法。
     * @param weatherInfo 主要未来三小时下雨和大风情况获取！
     */
    private void smartRack(WeatherInfo weatherInfo) {
        LocalTime now = LocalTime.now();
        LocalTime futureHour = now.plusHours(4);
        AtomicInteger BeforeHour = new AtomicInteger();
        AtomicBoolean isSYF = new AtomicBoolean(false);
        AtomicReference<String> wea = new AtomicReference<>();
        AtomicReference<String> winSpeed = new AtomicReference<>();
        weatherInfo.setHours(weatherInfo.getHours().subList(0,12));//这里的数据获取前半段的数据就足够使用了，同时避免后续的12小时制干扰！
        weatherInfo.getHours().forEach(hourWeather -> {
            LocalTime time = LocalTime.parse(hourWeather.getHours());
            //在有效时间内的信息分析
            if (time.isAfter(now) && time.isBefore(futureHour)) {
                //查到最近一次的下雨情况或大风情况
                if ((isRainy(hourWeather.getWea()) || shouldTakeClothes(hourWeather.getWin_speed())) && !isSYF.get()) {
                    isSYF.set(true);
                    wea.set(hourWeather.getWea());
                    winSpeed.set(hourWeather.getWin_speed());
                } else {
                    if (!isSYF.get()) {
                        BeforeHour.getAndIncrement();//几小时后会发生
                    }
                }
            }
        });
        if (isSYF.get()) {//存在需要收衣服的情况
            LocalDateTime now1 = LocalDateTime.now();
            LocalDateTime localDateTime = now1.plusHours(BeforeHour.get());
            //判断是否在线，不在线则不处理默认没开启，如有极端天气会在智能门窗中进行提醒！
            OderLog oderLog = new OderLog();
            oderLog.setOderId(4);
            oderLog.setWriteTime(localDateTime);
            oderLog.setBz("AUTO:wea=" + wea.get() + ",winSpeed=" + winSpeed.get());
            Message message = oderMessageController.writeOderMessage(oderLog);
            //给用户邮件提醒
            sendEmail.alterEmail("智能感知模块", "智能感知触发-收衣服AUTO:wea=" + wea.get() + ",winSpeed=" + winSpeed.get() +
                    "，下雨时间：" + DateUtil.localDateTimeToString(localDateTime.plusHours(BeforeHour.get())) +
                    ";指令下发情况：" + message.getResult(), now1);
        }
    }

    /***
     * 判断是否下雨的情况
     * @param condition
     * @return
     */
    public static boolean isRainy(String condition) {
        // 检查天气状况字符串是否包含有雨滴信息
        return condition.contains("大雨到大暴雨") ||
                condition.contains("暴雨") ||
                condition.contains("雷阵雨") ||
                condition.contains("雨夹雪") ||
                condition.contains("中雨") ||
                condition.contains("小雨") ||
                condition.contains("暴雨到大暴雨") ||
                condition.contains("大到暴雨") ||
                condition.contains("大雨") ||
                condition.contains("扬沙转小雨") ||
                condition.contains("雨夹雪转中雨") ||
                condition.contains("中雨转雨夹雪") ||
                condition.contains("大暴雨") ||
                condition.contains("大到暴雪转大雪") ||
                condition.contains("中雨转暴雨") ||
                condition.contains("暴雨转中雨") ||
                condition.contains("中雨转小雨") ||
                condition.contains("雷阵雨转小雨") ||
                condition.contains("小雨转中雨") ||
                condition.contains("暴雨转小雨") ||
                condition.contains("小雨转暴雨") ||
                condition.contains("小雨转大雨") ||
                condition.contains("中雨转雷阵雨") ||
                condition.contains("大雨转小雨");
    }

    /***
     * 判断风级是否需要收衣服（默认大于三级）
     * @param s
     * @return
     */
    public static boolean shouldTakeClothes(String s) {
        // 提取数字
        String[] parts = s.split("级");
        if (parts.length == 2) {
            try {
                int speed = Integer.parseInt(parts[0]);
                // 判断风速是否需要收衣服
                return speed > 3;
            } catch (NumberFormatException e) {
                // 如果无法解析数字，则默认不需要收衣服
                return false;
            }
        }
        // 默认情况下不需要收衣服
        return false;
    }

    /***
     * 专门用于处理智能门窗（安防）的未来天气感知处理方法。后续可以根据更详细的数据进行处理扩大自定义程度!
     * @param alarm 预警信息
     * @param alarms 多个预警信息
     */
    private void smartWatchdog(Alarm alarm, List<Alarm> alarms) {
        if (alarm == null) {
            //这里简化处理了只进行循环报警，应该优化为后续的叠加在一起处理更为合适！
            alarms.forEach(alarm1 -> {
                SmartWatchdog(alarm);
            });
        } else {
            SmartWatchdog(alarm);
        }
    }

    // 需要紧闭门窗的预警类型列表
    private static final List<String> NEED_CLOSE_WINDOWS_WARNINGS = Arrays.asList(
            "台风", "暴雨", "暴雪", "寒潮", "大风",
            "沙尘暴", "高温", "干旱", "雷电", "冰雹",
            "霜冻", "大雾", "霾", "道路结冰", "海上大雾",
            "雷暴大风", "持续低温", "浓浮尘", "龙卷风", "低温冻害",
            "海上大风", "低温雨雪冰冻", "强对流", "臭氧", "大雪",
            "强降雨", "强降温", "雪灾", "森林（草原）火险", "雷暴",
            "严寒", "沙尘", "海上雷雨大风", "海上雷电", "海上台风",
            "低温", "寒冷", "灰霾", "雷雨大风", "森林火险",
            "降温", "道路冰雪", "干热风", "空气重污染", "冰冻"
    );

    // 方法用于判断给定的预警类型字符串是否包含需要紧闭门窗的预警类型
    public static boolean isNeedCloseWindowsWarning(String warningType) {
        for (String warning : NEED_CLOSE_WINDOWS_WARNINGS) {
            if (warningType.contains(warning)) {
                return true;
            }
        }
        return false;
    }

    //严重程度进行排比
    public static int getWarningSeverity(String input) {
        // 定义预警颜色与严重程度的映射关系
        Map<String, Integer> severityMap = new HashMap<>();
        severityMap.put("蓝色", 1);
        severityMap.put("黄色", 2);
        severityMap.put("橙色", 3);
        severityMap.put("红色", 4);
        severityMap.put("白色", 0); // 白色预警不计入严重程度
        // 如果预警颜色在映射中存在，则更新最大严重程度
        if (severityMap.containsKey(input)) {
            return severityMap.get(input);
        }
        return 0;
    }

    public void SmartWatchdog(Alarm alarm) {
        //不为空则代表存在预警信息！
        if (alarm.getAlarm_type() != null && !alarm.getAlarm_type().isEmpty() && alarm.getAlarm_level() != null && !alarm.getAlarm_level().isEmpty()) {
            //对预警信息进行分类并结合模式处理！
            DeviceLog deviceLog = deviceLogService.getDeviceLogsByIdOne(12);
            if (isNeedCloseWindowsWarning(alarm.getAlarm_type())) {//过滤掉不需要关闭门窗的预警
                if (deviceLog != null) {
                    //根据模式的不同对应不同的响应
                    if (deviceLog.getValue().equals("1")) {
                        Message message1, message2 = null;
                        if (getWarningSeverity(alarm.getAlarm_level()) > 2) {//比较严重在进行关窗户
                            OderLog oderLog = new OderLog();
                            oderLog.setOderId(7);
                            oderLog.setBz("pattern1,AUTO:alarm_level=" + alarm.getAlarm_level() + ",alarm_type=" + alarm.getAlarm_type());
                            message1 = oderMessageController.writeOderMessage(oderLog);
                            oderLog.setOderId(2);
                            message2 = oderMessageController.writeOderMessage(oderLog);
                        } else {//仅闭门
                            OderLog oderLog = new OderLog();
                            oderLog.setOderId(7);
                            oderLog.setBz("pattern1,AUTO:alarm_level=" + alarm.getAlarm_level() + ",alarm_type=" + alarm.getAlarm_type());
                            message1 = oderMessageController.writeOderMessage(oderLog);
                        }
                        //存在失误才进行报警！
                        if (message1.getStatus() != 200 || (message2 != null && message2.getStatus() != 200)) {
                            sendEmail.alterEmail("智能感知模块", "智能感知触发-天气预警pattern1-AUTO:" + alarm.getAlarm_title() +
                                    "“" + alarm.getAlarm_type() + alarm.getAlarm_level() + "”预警，详情‘" +
                                    alarm.getAlarm_content() + "’指令下发异常信息:message1=" + message1
                                    + (message2 != null ? ",message2=" + message2 : ""), LocalDateTime.now());
                        }
                    } else if (deviceLog.getValue().equals("2")) {
                        Message message1, message2 = null;
                        if (getWarningSeverity(alarm.getAlarm_level()) > 2) {//比较严重在进行关窗户
                            OderLog oderLog = new OderLog();
                            oderLog.setOderId(7);
                            oderLog.setBz("pattern2,AUTO:alarm_level=" + alarm.getAlarm_level() + ",alarm_type=" + alarm.getAlarm_type());
                            message1 = oderMessageController.writeOderMessage(oderLog);
                            oderLog.setOderId(2);
                            message2 = oderMessageController.writeOderMessage(oderLog);
                            //恶劣天气进行报警
                            sendEmail.alterEmail("智能感知模块", "智能感知触发-天气预警pattern2-AUTO:" + alarm.getAlarm_title() +
                                    "“" + alarm.getAlarm_type() + alarm.getAlarm_level() + "”预警，详情‘" +
                                    alarm.getAlarm_content() + "’指令下发执行信息:message1=" + message1
                                    + (message2 != null ? ",message2=" + message2 : ""), LocalDateTime.now());
                        } else {//仅闭门
                            OderLog oderLog = new OderLog();
                            oderLog.setOderId(7);
                            oderLog.setBz("pattern2,AUTO:alarm_level=" + alarm.getAlarm_level() + ",alarm_type=" + alarm.getAlarm_type());
                            message1 = oderMessageController.writeOderMessage(oderLog);
                            //仅在指令下发异常报警
                            if (message1.getStatus() != 200) {
                                sendEmail.alterEmail("智能感知模块", "智能感知触发-天气预警pattern2-AUTO:" + alarm.getAlarm_title() +
                                        "“" + alarm.getAlarm_type() + alarm.getAlarm_level() + "”预警，详情‘" +
                                        alarm.getAlarm_content() + "’指令执行异常信息:message1=" + message1, LocalDateTime.now());
                            }
                        }
                    } else if (deviceLog.getValue().equals("3")) {
                        Message message1 = null;
                        //提醒但不执行！除非很严重
                        if (getWarningSeverity(alarm.getAlarm_level()) > 3) {//比较严重在进行关窗户
                            OderLog oderLog = new OderLog();
                            oderLog.setOderId(7);
                            oderLog.setBz("pattern3,AUTO:alarm_level=" + alarm.getAlarm_level() + ",alarm_type=" + alarm.getAlarm_type());
                            message1 = oderMessageController.writeOderMessage(oderLog);
                        }
                        //在指令下发异常报警
                        if (message1 != null && message1.getStatus() != 200) {
                            sendEmail.alterEmail("智能感知模块", "智能感知触发-天气预警pattern3-AUTO:" + alarm.getAlarm_title() +
                                    "“" + alarm.getAlarm_type() + alarm.getAlarm_level() + "”预警，详情‘" +
                                    alarm.getAlarm_content() + "’指令执行异常信息:message1=" + message1, LocalDateTime.now());
                        } else {
                            sendEmail.alterEmail("智能感知模块", "智能感知触发-天气预警pattern3-AUTO:" + alarm.getAlarm_title() +
                                    "“" + alarm.getAlarm_type() + alarm.getAlarm_level() + "”预警，详情‘" +
                                    alarm.getAlarm_content() + "’", LocalDateTime.now());
                        }
                    }
                } else {
                    //设备没上线就简单提醒一下即可给用户邮件提醒
                    sendEmail.alterEmail("智能感知模块", "智能感知触发-天气预警AUTO:" + alarm.getAlarm_title() +
                            "“" + alarm.getAlarm_type() + alarm.getAlarm_level() + "”预警，您的智能门窗（安防）模块未上线-注意紧闭门窗保护好个人财产！详情‘" +
                            alarm.getAlarm_content() + "’", LocalDateTime.now());
                }
            } else {
                //设备没上线就简单提醒一下即可给用户邮件提醒
                sendEmail.alterEmail("智能感知模块", "智能感知触发-天气预警AUTO:" + alarm.getAlarm_title() +
                        "“" + alarm.getAlarm_type() + alarm.getAlarm_level() + "”预警，详情‘" +
                        alarm.getAlarm_content() + "’", LocalDateTime.now());
            }
        }
    }

}
