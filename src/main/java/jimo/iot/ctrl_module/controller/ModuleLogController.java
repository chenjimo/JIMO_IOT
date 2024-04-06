package jimo.iot.ctrl_module.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.entity.ModuleLog;
import jimo.iot.ctrl_module.entity.ModuleMessage;
import jimo.iot.ctrl_module.service.impl.ModuleLogServiceImpl;
import jimo.iot.info.Message;
import jimo.iot.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/module")
public class ModuleLogController {
    @Resource
    ModuleLogServiceImpl moduleLogService;
    private static final String success = "恭喜您，操作成功！";
    private static final String error = "抱歉，操作失败，请重试！";

    /***
     * 传感器的心跳验证！
     * @param moduleLog
     * @return
     */
    @PostMapping("/log")
    public Message insert(ModuleLog moduleLog) {
        moduleLog.setTime(LocalDateTime.now());
        return moduleLogService.insertHeartBeatLog(moduleLog) ? new Message(200, success, null) : new Message(500, error, null);
    }

    /***
     * 获取模块在线时长
     * @param moduleLog
     * @return
     */
    @GetMapping("/times")
    public Message getModuleOnlineTimeInfo(ModuleLog moduleLog) {
        Map<Integer, Object> moduleTime = moduleLogService.getModuleTime(moduleLog.getModuleId());
        String message = null;
        if (moduleTime != null) {//不为空则标识曾经有过信息
            boolean isOnline = moduleTime.get(0) instanceof Boolean ? (Boolean) moduleTime.get(0) : false;
            long allIntervalTime = (long) moduleTime.getOrDefault(1, 0L);
            LocalDateTime nearFastTime = (LocalDateTime) moduleTime.getOrDefault(2, LocalDateTime.now());
            LocalDateTime nearLastTime = (LocalDateTime) moduleTime.getOrDefault(3, LocalDateTime.now());
            long nearIntervalTime = (long) moduleTime.getOrDefault(4, 0L);
            if (isOnline) {//在线
                message = "上线时间'" + DateUtil.localDateTimeToString(nearFastTime) + "'，连续在线'" + DateUtil.formatDuration(nearIntervalTime) + "'，总在线'" + DateUtil.formatDuration(allIntervalTime) + "'";
            } else {
                message = "下线时间'" + DateUtil.localDateTimeToString(nearLastTime) + "'，上次在线'" + DateUtil.formatDuration(nearIntervalTime) + "'，总在线'" + DateUtil.formatDuration(allIntervalTime) + "'";
            }
        }
        return new Message(moduleTime == null ? 400 : 200, moduleTime == null ? error : success, message);
    }

    /***
     * 根据模块ID获取智能模块的记录信息,可以通过算法实现对智能感知模式在线时间的计算！
     * @param moduleMessage
     * @return
     */
    @GetMapping("/log/smart")
    public List<ModuleLog> getModuleLogByModuleId(ModuleMessage moduleMessage, Integer jt) {
        jt = jt == null || jt < 0 ? 0 : jt;
        return moduleLogService.getBaseMapper().selectList(Wrappers.<ModuleLog>lambdaQuery()
                .eq(ModuleLog::getModuleId, moduleMessage.getId())
                .orderByDesc(ModuleLog::getTime)
                .last("LIMIT " + jt));
    }
}
