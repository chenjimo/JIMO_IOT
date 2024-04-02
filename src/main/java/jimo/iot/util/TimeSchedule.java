package jimo.iot.util;


import jimo.iot.controller.SendConfig;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/***
 * 定时消息类
 * 1、长期没有响应的指令失败处理，并备注！
 * 2、定时刷新未来数据情况进行根据模块状态以及多种模式进行区分，报警或者下发控制指令！
 * 3、其他。
 */
@Component
public class TimeSchedule {
    @Resource
    private SendConfig sendConfig;
    /***
     * 每天的上午七点执行一次！
     */
    @Scheduled(cron = "0 0 7 * * ?")
    public void scheduledMorning() {
        System.out.println("在 "+ DateUtil.localDateTimeToString(LocalDateTime.now())+" 执行了方法-->>'0 0 7 * * ?'");
        sendConfig.accept("0 0 7 * * ?");
    }
    /***
     * 每天的中午十二点执行一次！
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void scheduledNoon() {
        System.out.println("在 "+DateUtil.localDateTimeToString(LocalDateTime.now())+" 执行了方法-->>'0 0 12 * * ?'");
        sendConfig.accept("0 0 12 * * ?");
    }
    /***
     * 每天的晚上九点执行一次！
     */
    @Scheduled(cron = "0 0 21 * * ?")
    public void scheduledEvening() {
        System.out.println("在 "+DateUtil.localDateTimeToString(LocalDateTime.now())+" 执行了方法-->>'0 0 21 * * ?'");
        sendConfig.accept("0 0 21 * * ?");

    }
    /***
     * 每天的晚上十点执行一次！
     */
    @Scheduled(cron = "0 0 22 * * ?")
    public void scheduledOlg() {
        System.out.println("在 "+DateUtil.localDateTimeToString(LocalDateTime.now())+" 执行了方法-->>'0 0 22 * * ?'");
        sendConfig.accept("0 0 22 * * ?");

    }
//    /***
//     * 每天的下午六点点执行一次！
//     */
//    @Scheduled(cron = "0 0 18 * * ?")
//    public void scheduledHint() {
//        System.out.println("在 "+DateUtil.dateToString(new Date())+" 执行了方法。");
//        sendConfig.accept("0 0 18 * * ?");
//    }

    @Scheduled(cron = "0 0 */1 * * ?")//测试每10秒一执行
    public void scheduledTest() {
        System.out.println("在 "+DateUtil.localDateTimeToString(LocalDateTime.now())+" 执行了方法-->>0/10 * * * * ? ");
        sendConfig.accept("Test");
    }

}
