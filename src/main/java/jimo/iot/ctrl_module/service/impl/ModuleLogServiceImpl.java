package jimo.iot.ctrl_module.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.entity.ModuleLog;
import jimo.iot.ctrl_module.mapper.ModuleLogMapper;
import jimo.iot.ctrl_module.service.IModuleLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jimo.iot.device_value.entity.DeviceLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Service
public class ModuleLogServiceImpl extends ServiceImpl<ModuleLogMapper, ModuleLog> implements IModuleLogService {

    @Value("${jimo.message.heartbeat}")
    private Integer heartbeat;//设备的心跳时间

    /***
     * 写入心跳记录，根据ID和time
     * @author JIMO
     * @since 2024-03-31
     * @param moduleLog
     */
    @Override
    public boolean insertHeartBeatLog(ModuleLog moduleLog) {
        boolean b = true;
        if (moduleLog.getModuleId() == 3) {
            b = baseMapper.insert(moduleLog) > 0;
            moduleLog.setModuleId(4);
            moduleLog.setBz("随ID：3同步更新在线!");
        } else if (moduleLog.getModuleId() == 4) {
            b = baseMapper.insert(moduleLog) > 0;
            moduleLog.setModuleId(3);
            moduleLog.setBz("随ID：4同步更新在线!");
        }
        return baseMapper.insert(moduleLog) > 0 && b;
    }

    /***
     * 模块上线判断(心跳时间在30内就算上线)
     * @param id
     * @return
     */
    @Override
    public boolean upModuleById(Integer id) {
        ModuleLog moduleLog = baseMapper.selectOne(
                Wrappers.<ModuleLog>lambdaQuery()
                        .eq(ModuleLog::getModuleId, id)
                        .orderByDesc(ModuleLog::getTime)
                        .last("LIMIT 1")); // 添加限制只查询一条数据
        if (moduleLog == null) {
            return false;
        }
        // 计算相差的秒数
        long secondsDifference = Duration.between(moduleLog.getTime(), LocalDateTime.now()).getSeconds();
        return secondsDifference <= heartbeat + 5;//给5S的缓冲时间，防止网络拥堵等情况
    }

    /***
     * 模块下线判断(60S内无心跳)
     * @param id
     * @return
     */
    @Override
    public boolean downModuleById(Integer id) {
        ModuleLog moduleLog = baseMapper.selectOne(
                Wrappers.<ModuleLog>lambdaQuery()
                        .eq(ModuleLog::getModuleId, id)
                        .orderByDesc(ModuleLog::getTime)
                        .last("LIMIT 1"));
        if (moduleLog == null) {
            return true;
        }
        // 计算相差的秒数
        long secondsDifference = Duration.between(moduleLog.getTime(), LocalDateTime.now()).getSeconds();
        return secondsDifference > heartbeat * 2;//连续两次都没有心跳，设备就算真的凉了！
    }
}
