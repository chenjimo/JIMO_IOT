package jimo.iot.ctrl_module.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.entity.ModuleLog;
import jimo.iot.ctrl_module.mapper.ModuleLogMapper;
import jimo.iot.ctrl_module.service.IModuleLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
     * 模块上线判断(心跳时间在300内就算上线)
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
     * 模块下线判断(600S内无心跳)
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

    /***
     * 根据模块ID和心跳时间以及模式来获取的0-当前是否在线、1-总在线时长、2-最近上线时间、3-最近下线时间、4-最近连续在线总时长！
     * @param moduleId
     * @return
     */
    @Override
    public Map<Integer, Object> getModuleTime(Integer moduleId) {
        Map<Integer, Object> map = new HashMap<>();
        long nearIntervalTime;//最近在线时间(单位S，2亿年左右才会溢出，int值68年左右溢出)
        long allIntervalTime;//全部在线时间
        LocalDateTime now = LocalDateTime.now();
        List<ModuleLog> moduleLogs = baseMapper.selectList(Wrappers.<ModuleLog>lambdaQuery().eq(ModuleLog::getModuleId, moduleId).orderByDesc(ModuleLog::getTime));
        LocalDateTime nearLastTime;//最后在线时间
        LocalDateTime nearFastTime = null; //最后一次上线时间
        long olineSize = 0;
        boolean nowOline = false;//现在是否在线判断
        if (moduleLogs != null) {//避免为空
            //判断现在是否在线
            if (Duration.between(moduleLogs.get(0).getTime(), now).getSeconds() < heartbeat + 5) {
                nearLastTime = now;//目前还在线
                nowOline = true;
            } else {
                nearLastTime = moduleLogs.get(0).getTime();
            }
            //找到最近一次的上线时间
                for (int i = 0; i < moduleLogs.size() - 1; i++) {//降序排列，越靠后时间越早！
                    if (Duration.between(moduleLogs.get(i + 1).getTime(), moduleLogs.get(i).getTime()).getSeconds() > heartbeat + 5) {
                        if (olineSize == 0) {
                            nearFastTime = moduleLogs.get(i).getTime();//获得最近一次的上线时间
                        }
                        olineSize++;//记录在线次数
                    }
                }
            if (nearFastTime == null) {//循环完毕还没有写入登录时间则代表最后一条数据即为第一次登录
                nearFastTime = moduleLogs.get(moduleLogs.size()-1).getTime();
            }
            //计算最近在线时间
            nearIntervalTime = Duration.between(nearFastTime, nearLastTime).getSeconds();
            allIntervalTime = (moduleLogs.size() - olineSize) * heartbeat + (nowOline ? heartbeat + 5 : 0);//在线的话要加上间隔误差，避免比最近在线数据少的情况
            map.put(0,nowOline);
            map.put(1, allIntervalTime);
            map.put(2,nearFastTime);
            map.put(3,nearLastTime);
            map.put(4,nearIntervalTime);
        }
        return moduleLogs == null ? null : map;
    }
}
