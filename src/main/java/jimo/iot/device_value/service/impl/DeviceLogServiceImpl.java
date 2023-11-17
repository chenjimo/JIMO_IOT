package jimo.iot.device_value.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jimo.iot.device_value.entity.DeviceLog;
import jimo.iot.device_value.entity.DeviceMessage;
import jimo.iot.device_value.mapper.DeviceLogMapper;
import jimo.iot.device_value.service.IDeviceLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jimo.iot.info.send.SendEmail;
import jimo.iot.util.APIUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * <p>
 *  服务实现类
 *  1、用于存储传感器的数据和提取传感器中的数据
 *  2、异常数据检测到应该进行报警处理（报警设置一个冷却时间60S）
 *  3、详细的完善求每日平均值的周时间的数值JSON（最后在开发）
 * </p>
 *
 * @author JIMO
 * @since 2023-11-15
 */
@Service
public class DeviceLogServiceImpl extends ServiceImpl<DeviceLogMapper, DeviceLog> implements IDeviceLogService {
    private static  LocalDateTime localDateTime ;
    @Resource
    DeviceMessageServiceImpl deviceMessageService;
    @Resource
    APIUtil apiUtil;
    @Value("${jimo.message.email.title}")
    private String tittle ;
    @Value("${jimo.message.email.end}")
    private String end;
    @Value("${jimo.message.email.logo}")
    private String logo;
    @Value("${jimo.message.email.to}")
    private String email;
    @Value("${jimo.alter.time}")
    private Integer time;

    public static void setLocalDateTime(LocalDateTime localDateTime) {
        DeviceLogServiceImpl.localDateTime = localDateTime;
    }

    /***
     * 记录传感器数据（异常数据检测到应该进行报警处理（报警设置一个冷却时间60S）
     * @param deviceLog
     * @return
     */
    @Override
    public boolean insert(DeviceLog deviceLog) {
        DeviceMessage deviceMessageMinAndMax = deviceMessageService.getDeviceMessageMinAndMax(deviceLog.getDeviceId());
        LocalDateTime now = LocalDateTime.now();//统一上传数据时间。
        if (Integer.getInteger(deviceLog.getValue()) > Integer.getInteger(deviceMessageMinAndMax.getMax()) ||
                Integer.getInteger(deviceLog.getValue()) < Integer.getInteger(deviceMessageMinAndMax.getMin())){
            if (localDateTime == null || Duration.between(localDateTime,now).getSeconds() > time){
                apiUtil.sendMail(email,tittle,
                        "\n\n"+"传感器的数据异常！您的：‘"+deviceMessageMinAndMax.getName()+"’在"+now+"上传："
                                +deviceLog.getValue()+deviceMessageMinAndMax.getUnit()+"超出阈值报警！！！\n\t\t"
                                +"\n\n本次信息短时发送批次随机码为："+new Random().nextInt(1000)
                                +"\n\t\t"+end+"\n\t\t<img src='"+logo+"'/>\n\n");
                deviceLog.setBz("该数据已触发报警！冷却时间为"+time+"S");
            }else {
                deviceLog.setBz("该数据超过阈值但未进行报警！冷却时间已过："+Duration.between(localDateTime,now).getSeconds()+"S");
            }
        }
        deviceLog.setTime(deviceLog.getTime()==null? now:deviceLog.getTime());
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
