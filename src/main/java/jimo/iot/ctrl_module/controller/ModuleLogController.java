package jimo.iot.ctrl_module.controller;


import jimo.iot.ctrl_module.entity.ModuleLog;
import jimo.iot.ctrl_module.service.impl.ModuleLogServiceImpl;
import jimo.iot.info.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
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
    @PostMapping("/log")
    public Message insert(ModuleLog moduleLog){
        moduleLog.setTime(LocalDateTime.now());
        return  moduleLogService.insertHeartBeatLog(moduleLog)? new Message(200,success,null) : new Message(500,error,null);
    }
}
