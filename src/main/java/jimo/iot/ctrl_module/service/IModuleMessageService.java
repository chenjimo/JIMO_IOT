package jimo.iot.ctrl_module.service;

import jimo.iot.ctrl_module.entity.ModuleMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 *  1、更新模块状态
 *  2、获取模块的信息
 *  3、更新模块的信息
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
public interface IModuleMessageService extends IService<ModuleMessage> {
    /***
     * 更新模块状态
     * @param moduleId
     * @param status
     */
    boolean updateModuleStatus(Integer moduleId, Integer status);

    /***
     * 获取模块的信息
     * @param moduleId
     * @return
     */
    ModuleMessage getModuleMessage(Integer moduleId);

    /***
     * 更新模块全部信息
     * @param moduleMessage
     * @return
     */
    boolean updateModuleAllMessage(ModuleMessage moduleMessage);

    /***
     * 获取全部模块的ID
     * @return
     */
    List<Integer> getModuleIds();
}
