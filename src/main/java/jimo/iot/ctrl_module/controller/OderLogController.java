package jimo.iot.ctrl_module.controller;


import jimo.iot.ctrl_module.entity.OderLog;
import jimo.iot.ctrl_module.entity.OderMessage;
import jimo.iot.ctrl_module.service.impl.ModuleMessageServiceImpl;
import jimo.iot.ctrl_module.service.impl.OderLogServiceImpl;
import jimo.iot.ctrl_module.service.impl.OderMessageServiceImpl;
import jimo.iot.ctrl_module.service.impl.UserMessageServiceImpl;
import jimo.iot.info.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 *  1、模块获取控制指令记录
 *  2、模块回复控制指令记录
 *  3、用户获取全部指令记录信息（分类为执行成功、等待执行中、与执行失败）
 *  4、用户撤回指令（status=-1）先判断指令未执行且未读取（及时查询类型）
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/oder")
public class OderLogController {

    @Resource
    OderLogServiceImpl oderLogService;
    @Resource
    OderMessageServiceImpl oderMessageService;
    @Resource
    UserMessageServiceImpl userMessageService;
    @Resource
    ModuleMessageServiceImpl moduleMessageService;
    private static final String success = "恭喜您，操作成功！";
    private static final String error = "抱歉，操作失败，请重试！";

    /***
     * 硬件模块获取指令的API
     * @param moduleId
     * @return
     */
    @GetMapping("/log")
    public Message get(Integer moduleId) {
        OderLog oderLog = oderLogService.readLog(moduleId);
        boolean b = oderLog != null;
        if (b) {
            OderMessage oderMessage = oderMessageService.readOderMessage(oderLog.getOderId());
            if ( oderMessage.getStatus() > 0){//判断指令是否还可以用
                oderLog.setOderMessage(oderMessage.getMessage());//写入指令内容
            }else {
                b=false ;
            }
        }
        //200是有内容的，300是空内容可表示没有指令
        return new Message(b?200:300,b?success:"暂无指令！",b?oderLog:null);
    }

    /***
     * 模块回复系统执行情况！
     * @param oderLog
     * @return
     */
    @PostMapping("/log")
    public  Message post(OderLog oderLog){
        boolean b = oderLogService.updateLog(oderLog.getId(), oderLog.getStatus());
        return new Message(b?200:500,b?success:error,null);
    }

    /***
     * （根据status和ReadTime分类为等待中、执行中、执行成功、与执行失败、已撤销、其他执行结果状态回馈。）
     * isReadTime=true示查询模块已经度过数据的记录（status执行中0、执行成功1-3-5、与执行失败2-4（邮箱提示4））
     * isReadTime=false示查询模块还未读取数据的记录（status等待中0、撤销-1）
     * @return
     */
    @GetMapping("/logs")
    public Message getLogs(boolean isReadTime,Integer status){
        List logs = new ArrayList<OderLog>();
                oderLogService.getLogs(isReadTime, status,null).forEach(
                oderLog -> {
                    oderLog.setOderName(oderMessageService.readOderMessage(oderLog.getOderId()).getName());
                    oderLog.setUserName(userMessageService.getUserMessage(oderLog.getUserId()).getName());
                    oderLog.setModuleName(moduleMessageService.getModuleMessage(oderLog.getModuleId()).getName());
                    logs.add(oderLog);
                }
        );
        return new Message(200,success,logs);
    }
    /***
     * （根据status和ReadTime分类为等待中、执行中、执行成功、与执行失败、已撤销、其他执行结果状态回馈。）
     * isReadTime=true示查询模块已经度过数据的记录（status执行中0、执行成功1-3-5、与执行失败2-4（邮箱提示4））
     * isReadTime=false示查询模块还未读取数据的记录（status等待中0、撤销-1）
     * @return 一个经过分页后的数据处理
     */
    @GetMapping("/logs/limit")
    public Message getLogsLimit(boolean isReadTime,Integer status,Integer limitNum){
        List logs = new ArrayList<OderLog>();
        oderLogService.getLogs(isReadTime, status,limitNum).forEach(
                oderLog -> {
                    oderLog.setOderName(oderMessageService.readOderMessage(oderLog.getOderId()).getName());
                    oderLog.setUserName(userMessageService.getUserMessage(oderLog.getUserId()).getName());
                    oderLog.setModuleName(moduleMessageService.getModuleMessage(oderLog.getModuleId()).getName());
                    logs.add(oderLog);
                }
        );
        return new Message(200,success,logs);
    }
    /***
     * 等待中(0)、执行中(1)、执行成功(2)、与执行失败(3)、已撤销（4）,判断是否可以撤销这条指令！
     * (大于0则表示不可以再撤销了)
     * @return
     */
    @PostMapping("/logs")
    public Message deleteOder(Integer oderLogId){
        String s = "撤回失败！";
        switch (oderLogService.isUpdate(oderLogId)) {
            case 0:
                s = oderLogService.updateLog(oderLogId, -1)?success:error;
                break;
            case 1:
                s = "该指令正在执行中，无法撤回！";
                break;
            case 2:
                s = "该指令已经执行成功，无法撤回！";
                break;
            case 3:
                s = "该指令已经执行失败，无法撤回！";
                break;
            case 4:
                s = "该指令已经撤销，无法撤回！";
                break;
            }
        return new Message(200,s,null);
    }
}
