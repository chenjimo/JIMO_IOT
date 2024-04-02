package jimo.iot.controller;

import org.springframework.stereotype.Component;

/**
 * <p>
 * 服务实现类
 * 定时的核心业务实现！！！
 * 超级无敌大类，这个业务差点给俺干到土里面！！！
 * </p>
 *
 * @author JIMO
 * @since 2022-08-07
 */
@Component
public class SendConfig{

    /**
     * Performs this operation on the given argument.
     *
     * @param s the input argument
     */
    public void accept(String s) {
        switch (s) {
            case "0 0 7 * * ?":
                scheduledMorning();
                break;
            case "0 0 12 * * ?":
                scheduledNoon();
                break;
            case "0 0 21 * * ?":
                scheduledEvening();
                break;
            case "0 0 22 * * ?":
                scheduledOlg();
                break;
            case "Test":
                scheduledTest();
                break;
        }
    }

    /***
     * 每天的上午七点执行一次！
     * power 1、3、5、7
     */

    public void scheduledMorning() {

    }

    /***
     * 每天的中午十二点执行一次！
     * power 2、3、6、7
     */

    public void scheduledNoon() {

    }

    /***
     * 每天的晚上九点执行一次！
     * power 4、5、6、7
     */

    public void scheduledEvening() {

    }

    /***
     * 每天的晚上十点执行一次！
     */

    public void scheduledOlg() {

    }


    /***
     * 测试每10秒一执行
     */

    public void scheduledTest() {
    }


}
