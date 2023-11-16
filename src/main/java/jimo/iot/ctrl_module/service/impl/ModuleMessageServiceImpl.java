package jimo.iot.ctrl_module.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.entity.ModuleMessage;
import jimo.iot.ctrl_module.mapper.ModuleMessageMapper;
import jimo.iot.ctrl_module.service.IModuleMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Service
public class ModuleMessageServiceImpl extends ServiceImpl<ModuleMessageMapper, ModuleMessage> implements IModuleMessageService {

    /***
     * 更新模块状态
     * @param moduleId
     * @param status
     */
    @Override
    public boolean updateModuleStatus(Integer moduleId, Integer status) {
        return baseMapper.updateById(new ModuleMessage(moduleId, status)) > 0;
    }

    /***
     * 获取模块的信息
     * @param moduleId
     * @return
     */
    @Override
    public ModuleMessage getModuleMessage(Integer moduleId) {
        return baseMapper.selectById(moduleId);
    }

    /***
     * 更新模块全部信息
     * @param moduleMessage
     * @return
     */
    @Override
    public boolean updateModuleAllMessage(ModuleMessage moduleMessage) {
        return baseMapper.updateById(moduleMessage) > 0;
    }

    /***
     * 获取全部模块的ID
     * @return
     */
    @Override
    public List<Integer> getModuleIds() {
        List list = new ArrayList<Integer>();
        baseMapper.selectObjs(Wrappers.<ModuleMessage>lambdaQuery().select(ModuleMessage::getId)).forEach(
                list::add
        );
        return list;
    }
}
