package jimo.iot.device_value.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jimo.iot.device_value.entity.DeviceLog;
import jimo.iot.device_value.entity.DeviceMessage;
import jimo.iot.device_value.mapper.DeviceLogMapper;
import jimo.iot.device_value.service.IDeviceLogService;
import jimo.iot.info.send.SendEmail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.text.html.parser.Entity;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * 1、用于存储传感器的数据和提取传感器中的数据
 * 2、异常数据检测到应该进行报警处理（报警设置一个冷却时间60S）
 * 3、详细的完善求每日平均值的周时间的数值JSON（最后在开发）
 * </p>
 *
 * @author JIMO
 * @since 2024-03-31
 */
@Service
public class DeviceLogServiceImpl extends ServiceImpl<DeviceLogMapper, DeviceLog> implements IDeviceLogService {
    /*   private static LocalDateTime localDateTime = null; //1.5V中，废弃的冷却锁方式*/
    private static HashMap<Integer, LocalDateTime> intervalById = new HashMap<Integer, LocalDateTime>();
    @Resource
    DeviceMessageServiceImpl deviceMessageService;
    @Resource
    SendEmail sendEmail;
    @Value("${jimo.alter.time}")
    private Integer time;


    /***
     * 记录传感器数据（异常数据检测到应该进行报警处理（报警设置一个冷却时间60S）
     * 优化版本V1.5:1、增添特殊报警的特殊处理API；2、优化报警冷却锁个性化定义。
     */
    @Override
    public boolean insert(DeviceLog deviceLog) {
        LocalDateTime now = LocalDateTime.now();//统一上传数据时间。
        //根据不同的数据进行对应的特殊处理解决
        LocalDateTime localDateTime = intervalById.get(deviceLog.getDeviceId());
        DeviceMessage deviceMessageMinAndMax;
        switch (deviceLog.getDeviceId()) {
            case 1:
            case 5:
            case 8:
            case 9:
            case 11:
            case 12:
            case 15:
            case 16:
            case 17:
                //不需要报警也不需要特殊处理的参数信息，一般为为状态标识！
                break;
            case 4://由土壤湿度的过于干燥，需要浇水时没有水在进行报警处理！
                deviceMessageMinAndMax = deviceMessageService.getDeviceMessageMinAndMax(deviceLog.getDeviceId());
                if (Double.parseDouble(deviceLog.getValue()) > Double.parseDouble(deviceMessageMinAndMax.getMax())) {
                    //时间设置较为宽松，标准时间的两倍为一个冷却限制时间！
                    if (localDateTime == null || Duration.between(localDateTime, now).getSeconds() > time * 2) {
                        //当土壤湿度超过了极限值后判断水位信息，看是否需要提示报警！要最近5分钟内的数据信息的最近一条！
                        DeviceLog water = baseMapper.selectOne(Wrappers.<DeviceLog>lambdaQuery()
                                .eq(DeviceLog::getDeviceId, 8)
                                .ge(DeviceLog::getTime, now.minusMinutes(5))
                                .orderByDesc(DeviceLog::getTime)
                                .last("LIMIT 1"));
                        //若没有水的数据则代表才启动，暂不做处理仅备注一下，若水位异常则报警。水位正常不报警仅记录一下。
                        if (water != null) {
                            if (Integer.parseInt(water.getValue()) == 1) {
                                deviceLog.setBz("土壤过干！水库有水-自动浇水启动！");
                            } else {
                                String s = deviceMessageMinAndMax.getUnit() == null ? "" : deviceMessageMinAndMax.getUnit();//获取单位信息
                                //报警方法
                                sendEmail.alterEmail("智能浇水模块", "土壤湿度" + deviceLog.getValue() + s + "土壤过于干燥，需要浇水！水仓水位已经到达极限-请你尽快补水！", now);
                                deviceLog.setBz("该数据已触发报警！冷却时间为" + time * 2 + "S");
                                intervalById.put(deviceLog.getDeviceId(), now);
                            }
                        }
                    } else {
                        deviceLog.setBz("该数据超过阈值但未进行特殊处理！冷却时间已过：" + Duration.between(localDateTime, now).getSeconds() + "S");
                    }
                }
                break;
            case 13://特殊的报警信息增添，跟据参数信息以及模式状态进行提示出错！Bz写入具体问题信息！属于关键信息所以冷却时间较短！
                if (localDateTime == null || Duration.between(localDateTime, now).getSeconds() > time) {
                    //报警方法
                    sendEmail.alterEmail("智能安防模块", "报警代码“" + deviceLog.getValue() + "”详情为：" + deviceLog.getBz(), now);
                    deviceLog.setBz("已触发报警！冷却时间为" + time + "S,详情：" + deviceLog.getBz());
                    intervalById.put(deviceLog.getDeviceId(), now);
                } else {
                    deviceLog.setBz("冷却时间已过：" + Duration.between(localDateTime, now).getSeconds() + "S,详细信息：" + deviceLog.getBz());
                }
                break;
            case 14://需要根据雨滴情况结合模式状态以及关于晾衣架的状态的情况进行智能报警！提醒收回衣服，或者默默记录不需要提醒！
                if (Integer.parseInt(deviceLog.getValue()) == 0) {//代表有雨滴，且舵机位置处于伸开状态并未打开智能避雨模式
                    DeviceLog patternD = baseMapper.selectOne(Wrappers.<DeviceLog>lambdaQuery()
                            .eq(DeviceLog::getDeviceId, 16)
                            .ge(DeviceLog::getTime, now.minusMinutes(3))
                            .orderByDesc(DeviceLog::getTime)
                            .last("LIMIT 1"));
                    if (patternD != null) {//又符合的模式状态标识
                        int pattern = Integer.parseInt(patternD.getValue());
                        if (pattern == 4 || pattern == 5 || pattern == 6 || pattern == 7) {
                            //开启智能模式则记录即可
                            deviceLog.setBz("已开启智能避雨模式！检测到雨滴自动避雨启动！");
                        } else {
                            //未开启则判断旋转位置
                            DeviceLog rackD = baseMapper.selectOne(Wrappers.<DeviceLog>lambdaQuery()
                                    .eq(DeviceLog::getDeviceId, 15)
                                    .ge(DeviceLog::getTime, now.minusMinutes(3))
                                    .orderByDesc(DeviceLog::getTime)
                                    .last("LIMIT 1"));
                            if (rackD != null && Integer.parseInt(rackD.getValue()) > 145) {
                                //获取到参数且衣架处于伸开状态报警提示手动收回衣服或开启智能避雨模式！6倍的冷却时间！
                                if (localDateTime == null || Duration.between(localDateTime, now).getSeconds() > time * 6) {
                                    sendEmail.alterEmail("智能晾衣架模块", "下雨了下雨了，您暂未开启“智能避雨模式”且衣架处于伸开状态：“" + rackD.getValue() + "°”，JIMO-IOT建议您：手动开启“收回衣架”或“打开智能避雨模式”！", now);
                                    deviceLog.setBz("已触发报警！冷却时间为" + time * 6 + "S,详情：下雨了，暂未开启“智能避雨模式”且衣架处于：" + rackD.getValue());
                                    intervalById.put(deviceLog.getDeviceId(), now);
                                } else {
                                    deviceLog.setBz("冷却时间已过：" + Duration.between(localDateTime, now).getSeconds() + "S,详情：下雨了，暂未开启“智能避雨模式”且衣架处于：" + rackD.getValue());
                                }
                            }
                        }
                    }
                }
                break;
            default:
                deviceMessageMinAndMax = deviceMessageService.getDeviceMessageMinAndMax(deviceLog.getDeviceId());
                //V1.5版本中的数据判断报警方案，删除统一的冷却报警锁变为针对性的冷却锁！
                if (Double.parseDouble(deviceLog.getValue()) > Double.parseDouble(deviceMessageMinAndMax.getMax()) ||
                        Double.parseDouble(deviceLog.getValue()) < Double.parseDouble(deviceMessageMinAndMax.getMin())) {
                    //成功触发报警机制！intervalById.get(deviceLog.getDeviceId())代替localDateTime，4倍的冷却时间！
                    if (localDateTime == null || Duration.between(localDateTime, now).getSeconds() > time * 4) {
                        String s = deviceMessageMinAndMax.getUnit() == null ? "" : deviceMessageMinAndMax.getUnit();//获取单位信息
                        //报警方法
                        sendEmail.alterEmail(deviceMessageMinAndMax.getName(), "上传的数据：" + deviceLog.getValue() + s + "超出设置阈值报警！", now);
                        intervalById.put(deviceLog.getDeviceId(), now);
                        deviceLog.setBz("该数据已触发报警！冷却时间为" + time * 4 + "S");
                    } else {
                        deviceLog.setBz("该数据超过阈值但未进行报警！冷却时间已过：" + Duration.between(localDateTime, now).getSeconds() + "S");
                    }
                }
        }

        //V1.0版本中的数据判断报警方案
/*        if (Double.parseDouble(deviceLog.getValue()) > Double.parseDouble(deviceMessageMinAndMax.getMax()) ||
                Double.parseDouble(deviceLog.getValue()) < Double.parseDouble(deviceMessageMinAndMax.getMin())){
            if (localDateTime == null || Duration.between(localDateTime,now).getSeconds() > time){
                String s = deviceMessageMinAndMax.getUnit() == null ? "" : deviceMessageMinAndMax.getUnit();
                apiUtil.sendMail(email,tittle,
                        "\n\n"+"传感器的数据异常！\n\t\t您的：‘"+deviceMessageMinAndMax.getName()
                                +"’\n\t\t在"+ DateUtil.localDateTimeToString(now)+"\n\t\t上传的："
                                +deviceLog.getValue()+s+"超出阈值报警！！！\n\t\t"
                                +"\n\t\t本次信息短时发送批次随机码为："+new Random().nextInt(1000)
                                +"\n\t\t"+end+"\n\t\t<img src='"+logo+"'/>\n\n");
                deviceLog.setBz("该数据已触发报警！冷却时间为"+time+"S");
                localDateTime = now;
            }else {
                deviceLog.setBz("该数据超过阈值但未进行报警！冷却时间已过："+Duration.between(localDateTime,now).getSeconds()+"S");
            }
        }*/
        deviceLog.setTime(deviceLog.getTime() == null ? now : deviceLog.getTime());
        return baseMapper.insert(deviceLog) > 0;
    }

    /***
     * 根据传感器的ID查询所有记录
     * @param deviceId
     * @return
     */
    @Override
    public List<DeviceLog> getDeviceLogsByDeviceId(Integer deviceId) {
        return baseMapper.selectList(Wrappers.<DeviceLog>lambdaQuery().eq(DeviceLog::getDeviceId, deviceId));
    }

    /***
     * 根据传感器的ID查询所有记录
     * @param deviceId
     * @return
     */
    @Override
    public DeviceLog getDeviceLogsByIdOne(Integer deviceId) {
        return baseMapper.selectOne(Wrappers.<DeviceLog>lambdaQuery().eq(DeviceLog::getDeviceId, deviceId)
                .orderByDesc(DeviceLog::getTime)
                .ge(DeviceLog::getTime, LocalDateTime.now().minusMinutes(3))
                .last("LIMIT 1"));
    }

    /***
     * 异常数据的展示罗列出来,最新的几条！
     * @param jt
     * @param error 长度判断
     * @return
     */
    @Override
    public List<DeviceLog> getDeviceLogErrorOrder(Integer jt, Integer error) {
        List<DeviceLog> deviceLogs = null;
        if (jt >= 0) {
            deviceLogs = baseMapper.getDeviceLogErrorOrderList(jt,error);
        } else {
            deviceLogs = baseMapper.getDeviceLogErrorOrderALL(error);
        }
        return deviceLogs;
    }

    /***
     * 根据传感器的ID查询最近的几条
     * @param deviceId
     * @param jt
     * @return
     */
    @Override
    public List<DeviceLog> getDeviceLogsByDeviceIdAndJT(Integer deviceId, Integer jt) {
        Integer count = baseMapper.selectCount(Wrappers.<DeviceLog>lambdaQuery().eq(DeviceLog::getDeviceId, deviceId));
        jt = jt == null || jt > count ? count : jt;//做一个防止空处理和一个数量限制！
        List<DeviceLog> deviceLogs = baseMapper.selectList(Wrappers.<DeviceLog>lambdaQuery()
                .eq(DeviceLog::getDeviceId, deviceId)
                .orderByDesc(DeviceLog::getTime)// 根据时间升序排序
                .last("LIMIT " + jt));// 限制结果数量为最近的三十条; // 限制结果数量过小时能取几个取几个
        Collections.reverse(deviceLogs);
        return deviceLogs;
    }

    /***
     * 详细的完善求每日平均值的周时间的数值JSON（最后在开发）
     * @param deviceId
     * @param jt 条数限制
     * @param avg 平均方式（1：按小时、2：按天、3：按月）
     * @return
     */
    @Override
    public List<Map<String, Object>> getDeviceLogByAVG(Integer deviceId, Integer jt, Integer avg) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        switch (avg) {
            case 1:
                list.addAll(baseMapper.getAverageByHour(deviceId, jt));
                break;
            case 2:
                list.addAll(baseMapper.getAverageByDay(deviceId, jt));
                break;
            case 3:
                list.addAll(baseMapper.getAverageByMonth(deviceId, jt));
                break;
            default:
                // 处理默认情况，如果有的话
                break;
        }
        Collections.reverse(list);
        return list;
    }

}
