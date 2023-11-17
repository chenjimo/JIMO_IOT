package jimo.iot.device_value.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.service.impl.ModuleMessageServiceImpl;
import jimo.iot.device_value.entity.DeviceMessage;
import jimo.iot.device_value.mapper.DeviceMessageMapper;
import jimo.iot.device_value.service.IDeviceMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JIMO
 * @since 2023-11-15
 */
@Service
public class DeviceMessageServiceImpl extends ServiceImpl<DeviceMessageMapper, DeviceMessage> implements IDeviceMessageService {

    @Resource
    ModuleMessageServiceImpl moduleMessageService;
    /***
     * 根据ID获取传感器的报警阈值。
     * @param deviceId
     * @return
     */
    @Override
    public DeviceMessage getDeviceMessageMinAndMax(Integer deviceId) {
        return baseMapper.selectOne(Wrappers.<DeviceMessage>lambdaQuery().eq(DeviceMessage::getId, deviceId));
    }

    /***
     * 设置传感器的报警阈值。
     * @param deviceMessage
     * @return
     */
    @Override
    public boolean updateDeviceMessage(DeviceMessage deviceMessage) {
        return baseMapper.updateById(deviceMessage) > 0;
    }

    /***
     * 根据模块分组进行展示传感器数据。
     * @return
     */
    @Override
    public Map<String, List<DeviceMessage>> getDeviceMessageByModuleGroup() {
        Map map = new HashMap<String,List<DeviceMessage>>();
        baseMapper.selectObjs(Wrappers.<DeviceMessage>lambdaQuery().groupBy(DeviceMessage::getModuleId).select(DeviceMessage::getModuleId))
                .forEach(
                        moduleId   -> {
                            List list = new ArrayList<DeviceMessage>();
                            baseMapper.selectList(Wrappers.<DeviceMessage>lambdaQuery().eq(DeviceMessage::getModuleId, moduleId)).
                                    forEach(list::add);
                            map.put(moduleMessageService.getModuleMessage((Integer) moduleId).getName(),list);
                        }
                );
        return map;
    }

    /***
     * 获取传感器的单位信息（直接获取全部也可）
     * @param deviceId
     */
    @Override
    public String getUnit(Integer deviceId) {
        return baseMapper.selectById(deviceId).getUnit();
    }
}
