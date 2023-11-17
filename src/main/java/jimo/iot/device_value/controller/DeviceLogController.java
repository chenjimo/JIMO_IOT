package jimo.iot.device_value.controller;


import jimo.iot.device_value.entity.DeviceLog;
import jimo.iot.device_value.service.impl.DeviceLogServiceImpl;
import jimo.iot.info.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
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

    private static final String success = "恭喜您，操作成功！";
    private static final String error = "抱歉，操作失败，请重试！";
    @PostMapping("/log")
    public Message insert(DeviceLog deviceLog){
        return  deviceLogService.insert(deviceLog)? new Message(200,success,null) : new Message(500,error,null);
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
     * 通过设备id获取设备日志,并限制数量
     * @param deviceLog
     * @return
     */
    @GetMapping("/logs")
    public List<DeviceLog> getLogs(DeviceLog deviceLog,Integer jt) {
        return deviceLogService.getDeviceLogsByDeviceIdAndJT(deviceLog.getDeviceId(),jt);
    }
}
