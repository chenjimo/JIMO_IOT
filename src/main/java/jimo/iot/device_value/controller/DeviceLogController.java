package jimo.iot.device_value.controller;


import jimo.iot.ctrl_module.entity.ModuleMessage;
import jimo.iot.ctrl_module.service.impl.ModuleLogServiceImpl;
import jimo.iot.device_value.entity.DeviceLog;
import jimo.iot.device_value.service.impl.DeviceLogServiceImpl;
import jimo.iot.info.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author JIMO
 * @since 2023-11-15
 */
@RestController
@RequestMapping("/device")
public class DeviceLogController {
    @Resource
    DeviceLogServiceImpl deviceLogService;

    @Resource
    ModuleLogServiceImpl moduleLogService;


    private static final String success = "恭喜您，操作成功！";
    private static final String error = "抱歉，操作失败，请重试！";

    @PostMapping("/log")
    public Message insert(DeviceLog deviceLog) {
        return deviceLogService.insert(deviceLog) ? new Message(200, success, null) : new Message(500, error, null);
    }

    /***
     * 通过设备id获取设备日志
     * @param deviceLog
     * @return
     */
    @GetMapping("/log")
    public List<DeviceLog> getLogs(DeviceLog deviceLog) {
        return deviceLogService.getDeviceLogsByDeviceId(deviceLog.getDeviceId());
    }

    /***
     * 通过模块id获取对应的状态最新信息！
     * 先判断模块是否上线！上线再查询最近一条数据上传
     * @return map 根据名字对应的参数掺入信息
     */
    @GetMapping("/log/moduleId")
    public Message getLog(ModuleMessage message) {
        HashMap map = new HashMap<String, Object>();
        boolean b = false;//判断数据是否刷新成功
        //先判断模块是否离线！
        if (!moduleLogService.downModuleById(message.getId())) {
            switch (message.getId()) {
                case 1://模式状态、雨滴状态、当前晾晒模式.先判断是否为空特殊处理后再进行填写
                    DeviceLog deviceLogsByIdOne = deviceLogService.getDeviceLogsByIdOne(17);//是否寻光0-100
                    DeviceLog deviceLogsByIdOne1 = deviceLogService.getDeviceLogsByIdOne(16);//模式状态标识0~7
                    DeviceLog deviceLogsByIdOne2 = deviceLogService.getDeviceLogsByIdOne(14);//雨滴信号0-100
                    DeviceLog deviceLogsByIdOne3 = deviceLogService.getDeviceLogsByIdOne(15);
                    if (deviceLogsByIdOne2 != null && deviceLogsByIdOne1 != null && deviceLogsByIdOne != null && deviceLogsByIdOne3 != null) {
                        b = true;
                        boolean findSunOnOline = Integer.parseInt(deviceLogsByIdOne.getValue()) == 100;
                        int patternOline = Integer.parseInt(deviceLogsByIdOne1.getValue());
                        map.put("waterV", Integer.parseInt(deviceLogsByIdOne2.getValue()) == 100);
                        map.put("rackV", Integer.parseInt(deviceLogsByIdOne3.getValue()));
                        int status = 0;
                        if (patternOline == 1 || patternOline == 3 || patternOline == 5 || patternOline == 7) {
                            if (findSunOnOline) {//智能寻光晾晒中
                                status = 1;
                            } else {//智能反面晾晒中
                                status = 2;
                            }
                        } else {
                            status = 0;
                        }
                        map.put("spinV", status);
                        if (patternOline == 4 || patternOline == 5 || patternOline == 6 || patternOline == 7) {
                            status = 1;
                        } else {
                            status = 0;
                        }
                        map.put("smartWater", status);
                    }
                    break;
                case 2://水位信息
                    DeviceLog deviceLogsByIdOne6 = deviceLogService.getDeviceLogsByIdOne(8);//waterV
                    if (deviceLogsByIdOne6 != null){
                        b  = true;
                        map.put("waterV",deviceLogsByIdOne6.getValue());
                    }
                    break;
                case 3://最新的模式信息、门窗信息、
                case 4:
                    DeviceLog deviceLogsByIdOne4 = deviceLogService.getDeviceLogsByIdOne(11);//DoorStatus
                    DeviceLog deviceLogsByIdOne5 = deviceLogService.getDeviceLogsByIdOne(12);//pattern
                    if (deviceLogsByIdOne4 != null && deviceLogsByIdOne5 != null) {
                        b = true;
                        map.put("DoorStatus",deviceLogsByIdOne4.getValue());
                        map.put("pattern",deviceLogsByIdOne5.getValue());
                    }
                    break;
            }
        }
        return b ? new Message(200, success, map) : new Message(400, error, null);
    }

    /***
     * 通过设备id获取设备日志,并限制数量
     * @param deviceLog
     * @return
     */
    @GetMapping("/logs")
    public List<DeviceLog> getLogs(DeviceLog deviceLog, Integer jt) {
        return deviceLogService.getDeviceLogsByDeviceIdAndJT(deviceLog.getDeviceId(), jt);
    }

    /***
     * 详细的完善求每日平均值的周时间的数值JSON
     * @param deviceLog getDeviceId
     * @param jt 条数限制
     * @param avg 平均方式（1：按小时、2：按天、3：按月）
     */
    @GetMapping("/logs/avg")
    public List<Map<String, Object>> getLogsByAVG(DeviceLog deviceLog, Integer jt, Integer avg) {
        return deviceLogService.getDeviceLogByAVG(deviceLog.getDeviceId(), jt, avg);
    }
}
