package jimo.iot.device_value.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jimo.iot.device_value.entity.DeviceLog;
import jimo.iot.device_value.mapper.DeviceLogMapper;
import jimo.iot.device_value.service.IDeviceLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JIMO
 * @since 2023-11-15
 */
@Service
public class DeviceLogServiceImpl extends ServiceImpl<DeviceLogMapper, DeviceLog> implements IDeviceLogService {

    /***
     * 记录传感器数据
     * @param deviceLog
     * @return
     */
    @Override
    public boolean insert(DeviceLog deviceLog) {
        deviceLog.setTime(deviceLog.getTime()==null? LocalDateTime.now():deviceLog.getTime());
        return baseMapper.insert(deviceLog) > 0;
    }

    /***
     * 根据传感器的ID查询所有记录
     * @param deviceId
     * @return
     */
    @Override
    public List<DeviceLog> getDeviceLogsByDeviceId(Integer deviceId) {
        return baseMapper.selectList(Wrappers.<DeviceLog>lambdaQuery().eq(DeviceLog::getDeviceId,deviceId));
    }

    /***
     * 根据传感器的ID查询最近的几条
     * @param deviceId
     * @param jt
     * @return
     */
    @Override
    public List<DeviceLog> getDeviceLogsByDeviceIdAndJT(Integer deviceId, Integer jt) {
        return baseMapper.selectList(Wrappers.<DeviceLog>lambdaQuery()
                .eq(DeviceLog::getDeviceId, deviceId)
                .orderByDesc(DeviceLog::getTime) // 根据时间降序排序
                .last("LIMIT "+jt)); // 限制结果数量为最近的三十条

    }
}
