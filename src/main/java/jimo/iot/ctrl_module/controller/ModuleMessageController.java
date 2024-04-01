package jimo.iot.ctrl_module.controller;


import jimo.iot.ctrl_module.entity.ModuleMessage;
import jimo.iot.ctrl_module.service.impl.ModuleLogServiceImpl;
import jimo.iot.ctrl_module.service.impl.ModuleMessageServiceImpl;
import jimo.iot.info.Message;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
public class ModuleMessageController {
    @Resource
    ModuleMessageServiceImpl moduleMessageService;
    @Resource
    ModuleLogServiceImpl moduleLogService;
    private static final String success = "恭喜您，操作成功！";
    private static final String error = "抱歉，操作失败，请重试！";

    /***
     * 获取一个模块信息
     * @param moduleId
     * @return
     */
    @CrossOrigin(origins = "null", maxAge =-1)//用于解决前端的跨域资源共享问题
    @GetMapping("/message")
    public Message getModuleMessage(Integer moduleId) {
        moduleMessageService.updateModuleStatus(moduleId,
                moduleLogService.upModuleById(moduleId) || !moduleLogService.downModuleById(moduleId)?1:0);
        return new Message(200,success, moduleMessageService.getModuleMessage(moduleId));
    }

    /***
     * 获取全部模块信息
     * @return
     */
    @GetMapping("/messages")
    public Message getModuleMessages() {
        List moduleMessageList = new ArrayList<ModuleMessage>();
        moduleMessageService.getModuleIds().forEach(
                moduleId ->  {
                    moduleMessageService.updateModuleStatus(moduleId,
                            moduleLogService.upModuleById(moduleId) || !moduleLogService.downModuleById(moduleId)?1:0);
                    moduleMessageList.add(moduleMessageService.getModuleMessage(moduleId));
                }
        );
        return new Message(200,success, moduleMessageList);
    }
}
