package jimo.iot.ctrl_module.service;

import jimo.iot.ctrl_module.entity.ModuleLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 *  1、一个为心跳记录的函数
 *  2、判断模块上线的函数
 *  3、判断模块下线的函数
 *  4、模块的总在线时长统计在一起（小时、天数、最近连续工作时长（可进行提示休息等等））最后开发！
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
public interface IModuleLogService extends IService<ModuleLog> {
    /***
     * 写入心跳记录，根据ID和time
     * @param moduleLog
     */
    boolean insertHeartBeatLog(ModuleLog moduleLog);

    /***
     * 模块上线判断
     * @param id
     * @return
     */
    boolean upModuleById(Integer id);

    /***
     * 模块下线判断
     * @param id
     * @return
     */
    boolean downModuleById(Integer id);

}
