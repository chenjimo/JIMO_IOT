package jimo.iot.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import jimo.iot.controller.SmartController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
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
    private SmartController sendConfig;
    /***
     * 每天的凌晨一点四十五分执行一次！
     */
    @Scheduled(cron = "0 45 1 * * ?")
    public void dataTreating() throws IOException {
        System.out.println("在 "+ DateUtil.localDateTimeToString(LocalDateTime.now())+" 执行了方法-->>'0 45 1 * * ?'");
        sendConfig.accept("0 45 1 * * ?");
    }
    /***
     * 每天的中午十二点执行一次！
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void scheduledNoon() throws IOException {
        System.out.println("在 "+DateUtil.localDateTimeToString(LocalDateTime.now())+" 执行了方法-->>'0 0 12 * * ?'");
        sendConfig.accept("0 0 12 * * ?");
    }
    /***
     * 每三小时执行一次！
     */
    @Scheduled(cron = "0 0 */3 * * ?")
    public void weatherSensing() throws IOException {
        System.out.println("在 "+DateUtil.localDateTimeToString(LocalDateTime.now())+" 执行了方法-->>'0 0 */3 * * ?'");
        sendConfig.accept("0 0 */3 * * ?");
    }
    /***
     * 每天的晚上十一点十分点执行一次！
     */
    @Scheduled(cron = "0 10 23 * * ?")
    public void oderClear() throws IOException {
        System.out.println("在 "+DateUtil.localDateTimeToString(LocalDateTime.now())+" 执行了方法-->>'0 10 23 * * ?'");
        sendConfig.accept("0 10 23 * * ?");
    }
//    /***
//     * 每天的下午六点点执行一次！
//     */
//    @Scheduled(cron = "0 0 18 * * ?")
//    public void scheduledHint() {
//        System.out.println("在 "+DateUtil.dateToString(new Date())+" 执行了方法。");
//        sendConfig.accept("0 0 18 * * ?");
//    }

    @Scheduled(cron = "0 0 */1 * * ?")//测试每小时一执行
    public void scheduledTest() throws IOException {
        System.out.println("在 "+DateUtil.localDateTimeToString(LocalDateTime.now())+" 执行了方法-->>0/10 * * * * ? ");
        sendConfig.accept("Test");
    }

}
