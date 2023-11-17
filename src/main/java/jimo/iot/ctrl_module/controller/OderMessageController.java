package jimo.iot.ctrl_module.controller;


import jimo.iot.ctrl_module.entity.OderLog;
import jimo.iot.ctrl_module.entity.OderMessage;
import jimo.iot.ctrl_module.service.impl.ModuleLogServiceImpl;
import jimo.iot.ctrl_module.service.impl.OderLogServiceImpl;
import jimo.iot.ctrl_module.service.impl.OderMessageServiceImpl;
import jimo.iot.ctrl_module.service.impl.UserMessageServiceImpl;
import jimo.iot.info.Message;
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
 *  前端控制器
 *  1、获取全部指令信息
 *  2、修改指令信息
 *  3、用户发布执行指令
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/oder")
public class OderMessageController {

    @Resource
    OderMessageServiceImpl oderMessageService;
    @Resource
    OderLogServiceImpl oderLogService;
    @Resource
    ModuleLogServiceImpl moduleLogService;
    @Resource
    UserMessageServiceImpl userMessageService;

    private static final String success = "恭喜您，操作成功！";
    private static final String error = "抱歉，操作失败，请重试！";

    /***
     * 根据ByOrderModule分组进行获取指令的信息
     * @return
     */
    @GetMapping("/message")
    public Message getAllOderMessage() {
        Map<String, List<OderMessage>> stringListMap = oderMessageService.readOderMessagesByOrderModule();
      return new Message(stringListMap!=null?200:500, stringListMap!=null?success:error, stringListMap);
    }

    /***
     * 修改指令信息内容
     * @param oderMessage
     * @return
     */
    @PostMapping("/message")
    public Message updateOderMessage(OderMessage oderMessage) {
        boolean b = oderMessageService.writeOderMessage(oderMessage);
        return new Message(b?200:500, b?success:error, null);
    }

    /***
     * 用户发布执行指令(要判断对应的模块是否在线)
     * @param oderLog
     * @return
     */
    @PostMapping("/messages")
    public Message writeOderMessage(OderLog oderLog) {
        OderMessage oderMessage = oderMessageService.readOderMessage(oderLog.getOderId());
        if (oderMessage != null && oderMessage.getStatus() > 0){//判断该指令是否可用
            if (moduleLogService.upModuleById(oderMessage.getModuleId())){//判断该模块是否在线
                oderLog.setUserId(oderLog.getUserId()==null?2:oderLog.getUserId());//默认的指令输入者为test
                oderLog.setModuleId(oderMessage.getModuleId());//写入模块ID
                oderLog.setWriteTime(LocalDateTime.now().plusMinutes(Integer.parseInt(oderLog.getDelay())));//设置延时执行时间（为分钟）
                oderLogService.writeLog(oderLog);
                userMessageService.addOperationRecord(oderLog.getUserId());//为用户增加写入记录
                return new Message(200, "恭喜您，操作成功！", null);
            }else {
                return new Message(400, "该模块暂未上线，注意检测设备的网络连接，请重试！", null);
            }
        }else {
            return new Message(300, "指令不可用，请重试！", null);
        }
    }
}
