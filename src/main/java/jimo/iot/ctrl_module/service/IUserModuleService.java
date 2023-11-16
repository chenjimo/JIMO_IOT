package jimo.iot.ctrl_module.service;

import jimo.iot.ctrl_module.entity.UserModule;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 *  1、写入模块与用户的关系
 *  3、读取模块与用户的关系
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
public interface IUserModuleService extends IService<UserModule> {
    /***
     * 写入模块与用户的关系
     * @param moduleId
     * @param userId
     * @return
     */
    public boolean setUserModule(Integer moduleId, Integer userId,Integer power);

    /***
     * 读取模块与用户的关系
     * @param moduleId
     * @param userId
     * @return
     */
    public boolean getUserModule(Integer moduleId, Integer userId);

}
