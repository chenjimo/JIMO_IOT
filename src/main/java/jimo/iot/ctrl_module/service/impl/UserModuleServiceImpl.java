package jimo.iot.ctrl_module.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.entity.UserModule;
import jimo.iot.ctrl_module.mapper.UserModuleMapper;
import jimo.iot.ctrl_module.service.IUserModuleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Service
public class UserModuleServiceImpl extends ServiceImpl<UserModuleMapper, UserModule> implements IUserModuleService {

    /***
     * 写入模块与用户的关系
     * @param moduleId
     * @param userId
     * @param power
     * @return
     */
    @Override
    public boolean setUserModule(Integer moduleId, Integer userId, Integer power) {
        return baseMapper.insert(new UserModule(moduleId, userId, power)) > 0;
    }

    /***
     * 读取模块与用户的关系
     * @param moduleId
     * @param userId
     * @return
     */
    @Override
    public boolean getUserModule(Integer moduleId, Integer userId) {
        return baseMapper.selectOne(Wrappers.<UserModule>lambdaQuery()
                .eq(UserModule::getModuleId,moduleId).eq(UserModule::getUserId,userId)) != null;
    }
}
