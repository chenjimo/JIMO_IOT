> APIFOX的接口文档，以及基础开发文档：

[JIMO-IOT](https://www.yuque.com/jimoworld/javabj/jimo-iot?view=doc_embed)<br />[JIMO-IOT开发文档 - 智能家居API](https://apifox.com/apidoc/shared-55794de6-2e29-439d-8e95-f00259162eee/doc-3252500)
<a name="s2PX7"></a>
## 一、再次开发拓展
<a name="i7gyA"></a>
### 1、遇到的问题整理
**问题收集**:::warning
关于智能浇水：

- [x] 1、报警API的完善（数据报警和多个报警锁分开）。水量进行报警和记录报告提醒
- [x] 2、信息分割（展示数据的分割和处理）湿度数据的显示反转
- [x] 3、把模块分开！
- [x] 4、增添指令回馈的标识对应的展示！

关于智能门窗：

- [x] 1、智能门窗的状态显示（三进制两位的理解）
- [x] 2、模式同步（1、2、3的处理）显示处理
- [x] 3、报警标识不同对应不同的标识和信息传递

关于智能晾衣架：

- [x] 1、增添指令回馈的标识对应的展示！
- [x] 2、自动下发检测天气情况进行对应的处理（大于智能模式等操作权限）
- [x] 3、对应状态进行优化提醒的方式和内容
- [x] 4、相关衣架状态的展示和页面的处理
:::
<a name="Vv8TC"></a>
### 2、优化设计思路
> （1）关于传感器API的优化（**报警锁互不干涉、根据不同情况设置不同的锁时长**）
> （2）关于报警API的思路优化（**专用的API通过过滤传感器的数据刷新实现特殊报警并记录**）
> （3）关于网页数据展示的优化（分页处理、**平均计算**、传感数据的细节处理和单位）
> （3）关于不同模块的状态显示的优化（**主要对传感器数据的标识进行处理限制时间范围和最新**，**同步绑定3、4模块的上线**）
> （4）增设指令数据反馈的类型（**3不需要浇水、4水不足“报警”、5状态无需改变**）
> （5）智能控制的实现自动命令（自动定时”未来“天气的感知，对有效模块状态检测下加状态的综合判断下执行）

<a name="hXAvh"></a>
### 3、实现功能优化
> - [x] API优化
> - [x] 页面后端优化
> - [x] 页面显示优化
> - [x] 细节功能优化

<a name="s7Epa"></a>
## 二、实现操作
<a name="dA0Yr"></a>
### （1）API的优化
<a name="aFEIf"></a>
#### 1、数据上传报警锁锁优化
> intervalById.get(deviceLog.getDeviceId())代替localDateTime做冷却时间的登记即可！

```cpp
 /*   private static LocalDateTime localDateTime = null; //1.5V中，废弃的冷却锁方式*/
    private static HashMap<Integer, LocalDateTime> intervalById = new HashMap<Integer, LocalDateTime>();
```
<a name="N732I"></a>
#### 2、报警内容优化
> 用`switch`进行分别处理。
> 对于ID：1、5、8、9、11、12、14、15、16、17不需要做报警判断的处理！
> ID4:由土壤湿度的过于干燥，需要浇水时没有水在进行报警处理！
> ID13:特殊的报警信息增添，跟据参数信息以及模式状态进行提示出错！Bz写入具体问题信息！属于关键信息所以冷却时间较短！
> ID14:需要根据雨滴情况结合模式状态以及关于晾衣架的状态的情况进行智能报警！提醒收回衣服，或者默默记录不需要提醒！
> 其他则默认default报警处理！并封装了统一的报警通用方法！
> ![image.png](https://cdn.nlark.com/yuque/0/2024/png/26820301/1711733942297-f60be6c2-53e3-424d-a5b3-45b2354eea83.png#averageHue=%23dcdbd8&clientId=u93911ac9-3a18-4&from=paste&height=462&id=u8b1c6111&originHeight=462&originWidth=856&originalType=binary&ratio=1&rotation=0&showTitle=false&size=55439&status=done&style=none&taskId=u63909695-79aa-423d-9eab-4841c692a90&title=&width=856)

<a name="kAme8"></a>
#### 3、最终变动
> ![image.png](https://cdn.nlark.com/yuque/0/2024/png/26820301/1711873200255-15a368c1-faa2-4cc8-8d97-a5b7624b1d56.png#averageHue=%23d8a75e&clientId=udd3dac90-6968-4&from=paste&height=487&id=fOCx5&originHeight=487&originWidth=1302&originalType=binary&ratio=1&rotation=0&showTitle=false&size=70958&status=done&style=none&taskId=u8e49d8ff-0aa9-435c-9227-e3b7119eb0d&title=&width=1302)

```java
package jimo.iot.device_value.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jimo.iot.device_value.entity.DeviceLog;
import jimo.iot.device_value.entity.DeviceMessage;
import jimo.iot.device_value.mapper.DeviceLogMapper;
import jimo.iot.device_value.service.IDeviceLogService;
import jimo.iot.info.send.SendEmail;
import jimo.iot.util.APIUtil;
import jimo.iot.util.CodeUtils;
import jimo.iot.util.DateUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * 1、用于存储传感器的数据和提取传感器中的数据
 * 2、异常数据检测到应该进行报警处理（报警设置一个冷却时间60S）
 * 3、详细的完善求每日平均值的周时间的数值JSON（最后在开发）
 * </p>
 *
 * @author JIMO
 * @since 2023-11-15
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
     * 根据传感器的ID查询最近的几条
     * @param deviceId
     * @param jt
     * @return
     */
    @Override
    public List<DeviceLog> getDeviceLogsByDeviceIdAndJT(Integer deviceId, Integer jt) {
        Integer count = baseMapper.selectCount(Wrappers.<DeviceLog>lambdaQuery().eq(DeviceLog::getDeviceId, deviceId));
        jt = jt == null || jt > count ? count : jt;//做一个防止空处理和一个数量限制！
        return baseMapper.selectList(Wrappers.<DeviceLog>lambdaQuery()
                .eq(DeviceLog::getDeviceId, deviceId)
                .orderByDesc(DeviceLog::getTime) // 根据时间降序排序
                .last("LIMIT " + jt));// 限制结果数量为最近的三十条; // 限制结果数量过小时能取几个取几个
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
                list.addAll(baseMapper.getAverageByHour(deviceId));
                break;
            case 2:
                list.addAll(baseMapper.getAverageByDay(deviceId));
                break;
            case 3:
                list.addAll(baseMapper.getAverageByMonth(deviceId));
                break;
            default:
                // 处理默认情况，如果有的话
                break;
        }
        return list.subList(0, Math.min(jt, list.size()));
    }

}

```
```java
package jimo.iot.info.send;

import jimo.iot.util.APIUtil;
import jimo.iot.util.CodeUtils;
import jimo.iot.util.DateUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;


/**
 * <p>
 * 服务实现类
 * 适配器类，为了防止由于后期API变革导致的使用异常
 * 为了更好的适配API接口
 * </p>
 *
 * @author JIMO
 * @since 2022-08-07
 */
@Data
@Component
public class SendEmail {
    @Resource
    APIUtil apiUtil;
    @Value("${jimo.message.email.title}")
    private String tittle;
    @Value("${jimo.message.email.end}")
    private String end;
    @Value("${jimo.message.email.logo}")
    private String logo;
    @Value("${jimo.message.email.to}")
    private String email;
    @Value("${jimo.alter.time}")
    private Integer time;

    private String userEmail;
    private String userTitle;
    private String userMessage;

    public void open() {
        apiUtil.sendMail(userEmail == null ? email : userEmail,
                userTitle == null ? tittle : userTitle,
                userMessage + "\n\t\t" + end + "\n\t\t<img src='" + logo + "'/>\n\n");
    }

    /***
     * 一个通用的信息报警内容方法
     * @param m 模块或传感器信息
     * @param a 提醒内容
     * @param t 提醒时间
     */
    public void alterEmail(String m, String a, LocalDateTime t) {
        apiUtil.sendMail(email, tittle,
                "\n\n" + "尊敬的用户您好！您的模块（传感器）‘" + m + "’\n\t\t"
                        + "\n\t\t在" + DateUtil.localDateTimeToString(t) + "\n\t\t"
                        + "\n\t\t发出提醒信息：" + a
                        + "\n\t\t本次信息短时发送批次随机码为：" + CodeUtils.getCode()
                        + "\n\t\t" + end + "\n\t\t<img src='" + logo + "'/>\n\n");
    }

}

```
<a name="vjbRt"></a>
#### 4、测试与补充
> 注意：
> 1、`Integer.getInteger()`和`Integer.parseInt()`的区分运用，建议后者！
> 2、`baseMapper.selectOne(Wrappers.<DeviceLog>lambdaQuery()                         .eq(DeviceLog::getDeviceId, 16).ge(DeviceLog::getTime, now.minusMinutes(3)).orderByDesc(DeviceLog::getTime).last("LIMIT 1")）`，避免输出多个值一定要加上`.last("LIMIT 1")`！

<a name="Vbzth"></a>
### （2）页面后端优化
<a name="I9DRk"></a>
#### 1、信息按时间分页处理数据以及平均时间段进行输出
:::success
下发的指令进行分开数据显示处理（通过特殊处理使数据分开显示，前端页面做一些分开和限制显示即可）<br />传感器的数据进行分页（默认的显示最近一天（小时平均），通过按钮显示最近一月（天平均），和从一开始的数据显示（按月平均））
:::
```java
    /***
     * （根据status和ReadTime分类为等待中、执行中、执行成功、与执行失败、已撤销、其他执行结果状态回馈。）
     * isReadTime=true示查询模块已经度过数据的记录（status执行中0、执行成功1-3-5、与执行失败2-4（邮箱提示4））
     * isReadTime=false示查询模块还未读取数据的记录（status等待中0、撤销-1）
     * @return 一个经过分页后的数据处理
     */
    @GetMapping("/logs/limit")
    public Message getLogsLimit(boolean isReadTime,Integer status,Integer limitNum){
        List logs = new ArrayList<OderLog>();
        oderLogService.getLogs(isReadTime, status,limitNum).forEach(
                oderLog -> {
                    oderLog.setOderName(oderMessageService.readOderMessage(oderLog.getOderId()).getName());
                    oderLog.setUserName(userMessageService.getUserMessage(oderLog.getUserId()).getName());
                    oderLog.setModuleName(moduleMessageService.getModuleMessage(oderLog.getModuleId()).getName());
                    logs.add(oderLog);
                }
        );
        return new Message(200,success,logs);
    }
```
```java
package jimo.iot.device_value.mapper;

import jimo.iot.device_value.entity.DeviceLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * 自定义的一些方法，用于求平均数
 * </p>
 *
 * @author JIMO
 * @since 2023-11-15
 */
public interface DeviceLogMapper extends BaseMapper<DeviceLog> {
    /***
     * 按小时进行平均数的统计
     * @param deviceId
     * @return (键为 ： hour 、 average_value ， 值为 ： 小时 、 平均值)
     */
    @Select("SELECT DATE_FORMAT(time, '%Y-%m-%d %H:00:00') AS hour, AVG(value) AS average_value " +
            "FROM device_log " +
            "WHERE device_id = #{deviceId} " +
            "GROUP BY hour " +  // 添加空格
            "ORDER BY hour DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getAverageByHour(Integer deviceId, Integer limit);

    /***
     * 按天进行平均数的统计
     * @param deviceId
     * @return (键为 ： day 、 average_value ， 值为 ： 天 、 平均值)
     */
    @Select("SELECT DATE_FORMAT(time, '%Y-%m-%d') AS day, AVG(value) AS average_value " +
            "FROM device_log " +
            "WHERE device_id = #{deviceId} " +
            "GROUP BY day " +  // 添加空格
            "ORDER BY day DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> getAverageByDay(Integer deviceId, Integer limit);

    /***
     * 按月进行平均数的统计
     * @param deviceId
     * @return (键为 ： Month 、 average_value ， 值为 ： 月 、 平均值)
     */
    @Select("SELECT DATE_FORMAT(time, '%Y-%m') AS month, AVG(value) AS average_value " +
            "FROM device_log " +
            "WHERE device_id = #{deviceId} " +
            "GROUP BY month " +
            "ORDER BY month DESC " +
            "LIMIT #{limit}")
    // 按照月份降序排序
    List<Map<String, Object>> getAverageByMonth(Integer deviceId, Integer limit);

}

```
<a name="leQ6t"></a>
#### 2、处理新的指令回馈优化
:::success
对于模块二增设不同的定义：3-不需要浇水、4-水不够(需要进行提示水量不足)。<br />对于模块三：需要增设5状态无需改变！<br />通过再写入时进行备注进行区分，前端改一下信息加一个备注即可！
:::
```java
    /***
     * 根据执行的指令记录ID进行更新status
     * @author JIMO
     * @since 2024-03-31
     */
    @Override
    public boolean updateLog(Integer oderLogId, Integer status) {
        String Bz = "test";
        switch (status){
            case 4:
                sendEmail.alterEmail("智能浇水模块","在您下发的远程浇水水任务中，检测到水仓水量不足！建议您及时补水！",LocalDateTime.now());
                status = 2;//属于执行失败的那一类型
                Bz = "水仓缺水-已邮箱提示！";
                break;
            case 3:
                Bz = "土壤湿度很低无需浇水！";
                status = 1;
                break;
            case 5:
                Bz = "状态无需改变！";
                status = 1;
                break;
        }
        return baseMapper.updateById(new OderLog(oderLogId,status,Bz)) > 0;
    }
```
<a name="tRWNg"></a>
#### 3、优化心跳函数的同步问题
```java
    /***
     * 写入心跳记录，根据ID和time
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

```
<a name="oeXWa"></a>
### （3）页面显示优化
> 1、模块之间的内容显示切换以及状态值的刷新与样式HTML；2、单位与数据的处理显示以及新的状态返回状况显示、3、一个综合显示台为首页信息展示的大数据展示！

**留给以后的优化**> 参考：（关于这些炫酷的页面展示，我打算留给下一个本版本加入优化）
> [echarts可视化大数据模板-门窗安防.zip](https://www.yuque.com/attachments/yuque/0/2024/zip/26820301/1711955791116-55c42dc6-f774-4132-885a-b814ad91c5f1.zip?_lake_card=%7B%22src%22%3A%22https%3A%2F%2Fwww.yuque.com%2Fattachments%2Fyuque%2F0%2F2024%2Fzip%2F26820301%2F1711955791116-55c42dc6-f774-4132-885a-b814ad91c5f1.zip%22%2C%22name%22%3A%22echarts%E5%8F%AF%E8%A7%86%E5%8C%96%E5%A4%A7%E6%95%B0%E6%8D%AE%E6%A8%A1%E6%9D%BF-%E9%97%A8%E7%AA%97%E5%AE%89%E9%98%B2.zip%22%2C%22size%22%3A362126%2C%22ext%22%3A%22zip%22%2C%22source%22%3A%22%22%2C%22status%22%3A%22done%22%2C%22download%22%3Atrue%2C%22taskId%22%3A%22u8ba7eb1b-4a82-48bb-93e1-32ce00fd1ff%22%2C%22taskType%22%3A%22upload%22%2C%22type%22%3A%22application%2Fx-zip-compressed%22%2C%22__spacing%22%3A%22both%22%2C%22mode%22%3A%22title%22%2C%22id%22%3A%22pHIBT%22%2C%22margin%22%3A%7B%22top%22%3Atrue%2C%22bottom%22%3Atrue%7D%2C%22card%22%3A%22file%22%7D)[echarts数据可视化模板-模块展示.zip](https://www.yuque.com/attachments/yuque/0/2024/zip/26820301/1711955791172-0ef038c7-4d73-4765-a86b-a3a7c6ebe309.zip?_lake_card=%7B%22src%22%3A%22https%3A%2F%2Fwww.yuque.com%2Fattachments%2Fyuque%2F0%2F2024%2Fzip%2F26820301%2F1711955791172-0ef038c7-4d73-4765-a86b-a3a7c6ebe309.zip%22%2C%22name%22%3A%22echarts%E6%95%B0%E6%8D%AE%E5%8F%AF%E8%A7%86%E5%8C%96%E6%A8%A1%E6%9D%BF-%E6%A8%A1%E5%9D%97%E5%B1%95%E7%A4%BA.zip%22%2C%22size%22%3A568281%2C%22ext%22%3A%22zip%22%2C%22source%22%3A%22%22%2C%22status%22%3A%22done%22%2C%22download%22%3Atrue%2C%22taskId%22%3A%22u021bdf31-370b-4913-acaa-e3a6fb3a431%22%2C%22taskType%22%3A%22upload%22%2C%22type%22%3A%22application%2Fx-zip-compressed%22%2C%22__spacing%22%3A%22both%22%2C%22mode%22%3A%22title%22%2C%22id%22%3A%22vL2EW%22%2C%22margin%22%3A%7B%22top%22%3Atrue%2C%22bottom%22%3Atrue%7D%2C%22card%22%3A%22file%22%7D)[echarts数据同步监控大盘模板-浇水.zip](https://www.yuque.com/attachments/yuque/0/2024/zip/26820301/1711955791066-40ce7081-5a48-4a05-950a-cd6c0e3eac98.zip?_lake_card=%7B%22src%22%3A%22https%3A%2F%2Fwww.yuque.com%2Fattachments%2Fyuque%2F0%2F2024%2Fzip%2F26820301%2F1711955791066-40ce7081-5a48-4a05-950a-cd6c0e3eac98.zip%22%2C%22name%22%3A%22echarts%E6%95%B0%E6%8D%AE%E5%90%8C%E6%AD%A5%E7%9B%91%E6%8E%A7%E5%A4%A7%E7%9B%98%E6%A8%A1%E6%9D%BF-%E6%B5%87%E6%B0%B4.zip%22%2C%22size%22%3A49993%2C%22ext%22%3A%22zip%22%2C%22source%22%3A%22%22%2C%22status%22%3A%22done%22%2C%22download%22%3Atrue%2C%22taskId%22%3A%22u830ac240-7700-4b2d-be0c-226a0eba216%22%2C%22taskType%22%3A%22upload%22%2C%22type%22%3A%22application%2Fx-zip-compressed%22%2C%22__spacing%22%3A%22both%22%2C%22mode%22%3A%22title%22%2C%22id%22%3A%22WOc3Z%22%2C%22margin%22%3A%7B%22top%22%3Atrue%2C%22bottom%22%3Atrue%7D%2C%22card%22%3A%22file%22%7D)[echarts物流云数据看板平台模板-登录页面.zip](https://www.yuque.com/attachments/yuque/0/2024/zip/26820301/1711955791258-09efc900-cf82-4d4c-b384-a0f046241cae.zip?_lake_card=%7B%22src%22%3A%22https%3A%2F%2Fwww.yuque.com%2Fattachments%2Fyuque%2F0%2F2024%2Fzip%2F26820301%2F1711955791258-09efc900-cf82-4d4c-b384-a0f046241cae.zip%22%2C%22name%22%3A%22echarts%E7%89%A9%E6%B5%81%E4%BA%91%E6%95%B0%E6%8D%AE%E7%9C%8B%E6%9D%BF%E5%B9%B3%E5%8F%B0%E6%A8%A1%E6%9D%BF-%E7%99%BB%E5%BD%95%E9%A1%B5%E9%9D%A2.zip%22%2C%22size%22%3A170663%2C%22ext%22%3A%22zip%22%2C%22source%22%3A%22%22%2C%22status%22%3A%22done%22%2C%22download%22%3Atrue%2C%22taskId%22%3A%22u9604e02a-9619-4237-b0f4-cd242aa9650%22%2C%22taskType%22%3A%22upload%22%2C%22type%22%3A%22application%2Fx-zip-compressed%22%2C%22__spacing%22%3A%22both%22%2C%22mode%22%3A%22title%22%2C%22id%22%3A%22q0eSp%22%2C%22margin%22%3A%7B%22top%22%3Atrue%2C%22bottom%22%3Atrue%7D%2C%22card%22%3A%22file%22%7D)[基于echarts大数据统计模板-晾衣架.zip](https://www.yuque.com/attachments/yuque/0/2024/zip/26820301/1711955791061-f80dc49f-0020-4bf3-b605-8421c7fec6ad.zip?_lake_card=%7B%22src%22%3A%22https%3A%2F%2Fwww.yuque.com%2Fattachments%2Fyuque%2F0%2F2024%2Fzip%2F26820301%2F1711955791061-f80dc49f-0020-4bf3-b605-8421c7fec6ad.zip%22%2C%22name%22%3A%22%E5%9F%BA%E4%BA%8Eecharts%E5%A4%A7%E6%95%B0%E6%8D%AE%E7%BB%9F%E8%AE%A1%E6%A8%A1%E6%9D%BF-%E6%99%BE%E8%A1%A3%E6%9E%B6.zip%22%2C%22size%22%3A49271%2C%22ext%22%3A%22zip%22%2C%22source%22%3A%22%22%2C%22status%22%3A%22done%22%2C%22download%22%3Atrue%2C%22taskId%22%3A%22u0c6773ce-d849-4f1f-ac53-de7f2272d2c%22%2C%22taskType%22%3A%22upload%22%2C%22type%22%3A%22application%2Fx-zip-compressed%22%2C%22__spacing%22%3A%22both%22%2C%22mode%22%3A%22title%22%2C%22id%22%3A%22hVY71%22%2C%22margin%22%3A%7B%22top%22%3Atrue%2C%22bottom%22%3Atrue%7D%2C%22card%22%3A%22file%22%7D)[app智能家居首页模板.zip](https://www.yuque.com/attachments/yuque/0/2024/zip/26820301/1711955791410-d3901bb3-0190-4513-929b-5a467f1792ed.zip?_lake_card=%7B%22src%22%3A%22https%3A%2F%2Fwww.yuque.com%2Fattachments%2Fyuque%2F0%2F2024%2Fzip%2F26820301%2F1711955791410-d3901bb3-0190-4513-929b-5a467f1792ed.zip%22%2C%22name%22%3A%22app%E6%99%BA%E8%83%BD%E5%AE%B6%E5%B1%85%E9%A6%96%E9%A1%B5%E6%A8%A1%E6%9D%BF.zip%22%2C%22size%22%3A160177%2C%22ext%22%3A%22zip%22%2C%22source%22%3A%22%22%2C%22status%22%3A%22done%22%2C%22download%22%3Atrue%2C%22taskId%22%3A%22u92951b06-c8d2-4502-a037-35cd67c60c9%22%2C%22taskType%22%3A%22upload%22%2C%22type%22%3A%22application%2Fx-zip-compressed%22%2C%22__spacing%22%3A%22both%22%2C%22mode%22%3A%22title%22%2C%22id%22%3A%22DLsG0%22%2C%22margin%22%3A%7B%22top%22%3Atrue%2C%22bottom%22%3Atrue%7D%2C%22card%22%3A%22file%22%7D)

<a name="PkFnI"></a>
#### logo

<a name="oaXU9"></a>
### (4)细节优化
<a name="UFHai"></a>
#### 1、关于页面的显示以及状态展示的合理化；
```css
.health {
  /* 使用百分比设置宽度，使其在不同屏幕尺寸下都能适应 */
  width: 100%;
  /*max-width: 1200px; !* 限制最大宽度，防止过大 *!*/
  height: 55vh; /* 高度自适应，根据内容自动调整 */
  margin: 0 auto; /* 水平居中 */
  border: none; /* 移除边框 */
  overflow: auto; /* 内容过多时出现滚动条 */
}

/* 媒体查询，为小屏幕设备设置不同样式 */
@media (max-width: 1000px) {
  .health {
    /* 在小屏幕上，你可以进一步调整宽度或高度 */
    max-width: 100%; /* 或者根据需要设置其他值 */
    height: 30vh; /* 高度自适应，根据内容自动调整 */
  }
  .status {
    font-size: 12px;
    margin-top: 5px;
    font-weight: bold;
    color: rgb(255, 115, 0);
  }
  button {
    background-color: #4CAF50;
    color: white;
    border: none;
    padding: 5px 10px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 14px;
    margin: 2px 1px;
    cursor: pointer;
    border-radius: 2px;
  }

  .modelV {
    font-size: 16px;
    font-weight: bold;
    color: #fc0404;
  }
}
/* 使用 flexbox 布局，横向排列 */
.container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between; /* 左右两侧对齐 */
  align-items: stretch; /* 垂直方向占满整个高度 */
  height :100vh;
}

/* 前三个页面占整个屏幕宽度的30% */
iframe:nth-child(-n+3) {
  /*flex: 32% 32% 32%;*/
  width: 32%;
  height: 20vh;
  margin-bottom: 20px; /* 设置页面之间的间隔，根据需要调整 */
}
/* 默认样式，适用于较大的屏幕，如电脑 */
iframe:nth-child(4) {
  flex: 0 0 90%; /* 使用 flex 属性来设置宽度 */
  height: 70vh;
  margin: auto; /* 水平居中 */
}
/* 在较小屏幕上纵向排列 */
@media (max-width: 1000px) {
  .container {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between; /* 左右两侧对齐 */
    align-items: stretch; /* 垂直方向占满整个高度 */
    height :100%;
  }
  iframe:nth-child(-n+3) {
    width: 100%; /* 占满整个宽度 */
    height: 10vh;
    margin-bottom: 5px; /* 设置页面之间的间隔，根据需要调整 */
  }
  /* 第四个页面占整个屏幕宽度的60% */
  iframe:nth-child(4) {
    flex: 0 0 90%;
    width: 90%;
    height: 65vh;
    margin: auto; /* 在水平方向上居中 */
  }
}

```
```css
.background-image {
  /*width: 100%; !* 设置 div 的宽度 *!*/
  height: 120%; /* 设置 div 的高度 */
  background-image: url('../static/img/img_1.png'); /* 设置背景图片的路径 */
  background-size: cover; /* 背景图片大小适应div，并保持比例 */
  background-position: center; /* 背景图片在 div 中居中显示 */
  background-repeat: no-repeat; /* 禁止背景图片重复显示 */
}
/* 定义滚动条的宽度、颜色和圆角 */
::-webkit-scrollbar {
  width: 8px; /* 宽度 */
}

::-webkit-scrollbar-thumb {
  background-color: rgba(4, 245, 28, 0.83); /* 滑块颜色 */
  border-radius: 4px; /* 圆角 */
}

::-webkit-scrollbar-track {
  background-color: #f1f1f1; /* 背景颜色 */
}

/* 鼠标悬停在滚动条上时的样式 */
::-webkit-scrollbar-thumb:hover {
  background-color: #333; /* 滑块颜色变化 */
}

/* Firefox 滚动条样式 */
html {
  scrollbar-width: thin;
  scrollbar-color: #4285f4 #f1f1f1;
}
/*页面居中等  */
body::-webkit-scrollbar-corner {
  background-color: #f1f1f1;
}
/*按钮方块化  */
.aId {
  display: inline-block;
  font-size: 18px;
  padding: 10px 18px;
  background-color: #dd92e8;
  color: #03ff11;
  text-decoration: none;
  border-radius: 8px;
  border: 2px solid #9911e7;
}
button {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 10px 20px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  font-size: 16px;
  margin: 4px 2px;
  cursor: pointer;
  border-radius: 4px;
}
/* 自动模式的字体颜色为蓝色 */
.option1 {
  color: rgb(247, 203, 10);
}

/* 智能模式的字体颜色为绿色 */
.option2 {
  color: rgb(14, 255, 54);
}

/* 严防模式的字体颜色为红色 */
.option3 {
  color: red;
}
/* 让后两个 p 并排显示 */
.status p:nth-child(2),
.status p:nth-child(3) {
  display: inline-block;
  margin-left: 20px;
  /* 调整间距 */
}
```
<a name="Pky1M"></a>
#### 2、关于定时指令的处理和定时失败指令。
> 这里需要集成对于智能感知模块的控制实现，比如开启或关闭智能感知、和对应的感知功能的更新、以及对应的感知数据展示以及记录。
> 因为此模块为虚拟模块与JIMO-IOT的运行共存，所以以上的功能均默认为开启状态！具体的优化细节留给后续根据需求再次扩展！
> 这里暂时实现定时处理实物即可（定时处理长时间未响应的指令、获取未来天气根据状态情况自动下发指令、在前端上模拟显示在线提示）
> 天气API：[控制台 · 天气API](http://tianqiapi.com/user/alarm)、[实时天气预报api 24小时天气预报接口 实时气象预警 空气质量预报](http://tianqiapi.com/index/doc?version=v63)；

```json
{
  "cityid": "101010100",
  "date": "2024-04-04",
  "week": "星期四",
  "update_time": "15:10",
  "city": "北京",
  "cityEn": "beijing",
  "country": "中国",
  "countryEn": "China",
  "wea": "多云",
  "wea_img": "yun",
  "tem": "16.4",
  "tem1": "18",
  "tem2": "7",
  "win": "东南风",
  "win_speed": "2级",
  "win_meter": "5km/h",
  "humidity": "38%",
  "visibility": "11km",
  "pressure": "1016",
  "air": "73",
  "air_pm25": "73",
  "air_level": "良",
  "air_tips": "各类人群可多参加户外活动，多呼吸一下清新的空气。",
  "alarm": {
    "alarm_type": "",
    "alarm_level": "",
    "alarm_title": "",
    "alarm_content": ""
  },
  "rain_pcpn": "0",
  "uvIndex": "5",
  "uvDescription": "中等",
  "wea_day": "多云",
  "wea_day_img": "yun",
  "wea_night": "多云",
  "wea_night_img": "yun",
  "sunrise": "05:53",
  "sunset": "18:41",
  "hours": [
    {
      "hours": "14:00",
      "wea": "晴",
      "wea_img": "qing",
      "tem": "14",
      "win": "南风",
      "win_speed": "3级",
      "vis": "16.9",
      "aqinum": "76",
      "aqi": "良"
    },
    {
      "hours": "15:00",
      "wea": "晴",
      "wea_img": "qing",
      "tem": "14",
      "win": "南风",
      "win_speed": "3级",
      "vis": "27.04",
      "aqinum": "44",
      "aqi": "优"
    },
    {
      "hours": "16:00",
      "wea": "晴",
      "wea_img": "qing",
      "tem": "14",
      "win": "东南风",
      "win_speed": "3级",
      "vis": "27.04",
      "aqinum": "44",
      "aqi": "优"
    },
    {
      "hours": "17:00",
      "wea": "多云",
      "wea_img": "yun",
      "tem": "14",
      "win": "东南风",
      "win_speed": "4级",
      "vis": "27.04",
      "aqinum": "44",
      "aqi": "优"
    },
    {
      "hours": "18:00",
      "wea": "晴",
      "wea_img": "qing",
      "tem": "13",
      "win": "东南风",
      "win_speed": "4级",
      "vis": "27.04",
      "aqinum": "44",
      "aqi": "优"
    },
    {
      "hours": "19:00",
      "wea": "多云",
      "wea_img": "yun",
      "tem": "11",
      "win": "东南风",
      "win_speed": "4级",
      "vis": "27.04",
      "aqinum": "44",
      "aqi": "优"
    },
    {
      "hours": "20:00",
      "wea": "晴",
      "wea_img": "qing",
      "tem": "10",
      "win": "东南风",
      "win_speed": "3级",
      "vis": "27.04",
      "aqinum": "44",
      "aqi": "优"
    },
    {
      "hours": "21:00",
      "wea": "晴",
      "wea_img": "qing",
      "tem": "9",
      "win": "东南风",
      "win_speed": "2级",
      "vis": "27.04",
      "aqinum": "45",
      "aqi": "优"
        },
        {
            "hours": "22:00",
            "wea": "小雨",
            "wea_img": "yu",
            "tem": "8",
            "win": "东南风",
            "win_speed": "2级",
            "vis": "27.04",
            "aqinum": "47",
            "aqi": "优"
        },
        {
            "hours": "23:00",
            "wea": "小雨",
            "wea_img": "yu",
            "tem": "6",
            "win": "东南风",
            "win_speed": "2级",
            "vis": "27.04",
            "aqinum": "50",
            "aqi": "优"
        },
        {
            "hours": "00:00",
            "wea": "多云",
            "wea_img": "yun",
            "tem": "6",
            "win": "东南风",
            "win_speed": "2级",
            "vis": "27.04",
            "aqinum": "51",
            "aqi": "良"
        },
        {
            "hours": "01:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "5",
            "win": "东南风",
            "win_speed": "2级",
            "vis": "27.04",
            "aqinum": "52",
            "aqi": "良"
        },
        {
            "hours": "02:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "5",
            "win": "东南风",
            "win_speed": "2级",
            "vis": "27.04",
            "aqinum": "55",
            "aqi": "良"
        },
        {
            "hours": "03:00",
            "wea": "多云",
            "wea_img": "yun",
            "tem": "4",
            "win": "东风",
            "win_speed": "1级",
            "vis": "27.04",
            "aqinum": "56",
            "aqi": "良"
        },
        {
            "hours": "04:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "4",
            "win": "东风",
            "win_speed": "1级",
            "vis": "22.53",
            "aqinum": "58",
            "aqi": "良"
        },
        {
            "hours": "05:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "3",
            "win": "东北风",
            "win_speed": "1级",
            "vis": "19.31",
            "aqinum": "61",
            "aqi": "良"
        },
        {
            "hours": "06:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "4",
            "win": "东北风",
            "win_speed": "1级",
            "vis": "16.9",
            "aqinum": "63",
            "aqi": "良"
        },
        {
            "hours": "07:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "5",
            "win": "东北风",
            "win_speed": "1级",
            "vis": "15.02",
            "aqinum": "66",
            "aqi": "良"
        },
        {
            "hours": "08:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "6",
            "win": "东北风",
            "win_speed": "1级",
            "vis": "13.52",
            "aqinum": "68",
            "aqi": "良"
        },
        {
            "hours": "09:00",
            "wea": "多云",
            "wea_img": "yun",
            "tem": "9",
            "win": "东北风",
            "win_speed": "1级",
            "vis": "12.88",
            "aqinum": "70",
            "aqi": "良"
        },
        {
            "hours": "10:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "11",
            "win": "东北风",
            "win_speed": "1级",
            "vis": "12.29",
            "aqinum": "71",
            "aqi": "良"
        },
        {
            "hours": "11:00",
            "wea": "多云",
            "wea_img": "yun",
            "tem": "14",
            "win": "东北风",
            "win_speed": "1级",
            "vis": "11.76",
            "aqinum": "72",
            "aqi": "良"
        },
        {
            "hours": "12:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "16",
            "win": "东南风",
            "win_speed": "1级",
            "vis": "11.76",
            "aqinum": "72",
            "aqi": "良"
        },
        {
            "hours": "13:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "18",
            "win": "南风",
            "win_speed": "1级",
            "vis": "11.27",
            "aqinum": "73",
            "aqi": "良"
        },
        {
            "hours": "14:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "20",
            "win": "西南风",
            "win_speed": "2级",
            "vis": "10.82",
            "aqinum": "75",
            "aqi": "良"
        },
        {
            "hours": "15:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "20",
            "win": "西南风",
            "win_speed": "2级",
            "vis": "10.82",
            "aqinum": "75",
            "aqi": "良"
        },
        {
            "hours": "16:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "21",
            "win": "西南风",
            "win_speed": "2级",
            "vis": "10.82",
            "aqinum": "75",
            "aqi": "良"
        },
        {
            "hours": "17:00",
            "wea": "晴",
            "wea_img": "qing",
            "tem": "21",
            "win": "西南风",
            "win_speed": "3级",
            "vis": "10.4",
            "aqinum": "76",
            "aqi": "良"
        }
    ],
    "aqi": {
        "update_time": "14:01",
        "air": "76",
        "air_level": "良",
        "air_tips": "各类人群可多参加户外活动，多呼吸一下清新的空气。",
        "pm25": "46",
        "pm25_desc": "良",
        "pm10": "101",
        "pm10_desc": "轻度污染",
        "o3": "57",
        "o3_desc": "",
        "no2": "30",
        "no2_desc": "",
        "so2": "9",
        "so2_desc": "",
        "co": "0.6",
        "co_desc": "",
        "kouzhao": "不用佩戴口罩",
        "yundong": "适宜运动",
        "waichu": "适宜外出",
        "kaichuang": "适宜开窗",
        "jinghuaqi": "不需要打开"
    },
    "zhishu": {
        "chuanyi": {
            "level": "较冷",
            "tips": "建议着厚外套加毛衣等服装。"
        },
        "daisan": {
            "level": "不带伞",
            "tips": "天气较好，不用带雨伞。"
        },
        "ganmao": {
            "level": "易发",
            "tips": "易发生感冒，体弱人群注意防护。"
        },
        "chenlian": {
            "level": "较适宜",
            "tips": "请选择避风的地点晨练，避免迎风锻炼。"
        },
        "ziwaixian": {
            "level": "强",
            "tips": "涂擦SPF大于15、PA+防晒护肤品。"
        },
        "liangshai": {
            "level": "适宜",
            "tips": "天气不错，抓紧时机让衣物晒太阳吧。"
        },
        "kaiche": {
            "level": "",
            "tips": ""
        },
        "xiche": {
            "level": "较不宜",
            "tips": "风力较大，洗车后会蒙上灰尘。"
        },
        "lvyou": {
            "level": "适宜",
            "tips": "风稍大，但仍可尽情地享受大自然风光。"
        },
        "diaoyu": {
            "level": "较适宜",
            "tips": "风稍大会对垂钓产生一定影响。"
        }
    }
}
```
> 思路：获取到对应的天气信息，应该对于实时信息和未来信息进行区分。实时信息用于对于环境要求直接进行响应！
> 1、对于智能晾衣架仅考虑是否下雨当前和未来三个小时内进行定时或者及时收衣服！然后根据情况如果有天气预警则优先级大于下雨情况并需要向用户汇报！
> 2、对于智能门窗（安防）模块，需要先获取天气信息，实时信息不需要考虑，仅考虑极端天气的预警信息，然后根据模块的模式状态进行区分处理，看情况是否需要提醒用户！
> 对应的控制细节，这里控制的开关在数据库中存在！并可以单独控制和查看历史记录！

> 注意字符串的空处理：alarm.getAlarm_type() != null && !alarm.getAlarm_type().isEmpty() && alarm.getAlarm_level() != null && !alarm.getAlarm_level().isEmpty()

<a name="llszc"></a>
#### 3、设备的在线时长
> （见上方图片）

<a name="kI3sv"></a>
#### 4、异常数据的展示罗列出来（限制最新的10条）！
![image.png](https://cdn.nlark.com/yuque/0/2024/png/26820301/1712084673731-a3fff0b8-6de3-4d49-8ac9-e659c842f0b9.png#averageHue=%232b2b29&clientId=u9771acf6-0c25-4&from=paste&height=1288&id=za6mI&originHeight=1288&originWidth=2271&originalType=binary&ratio=1&rotation=0&showTitle=false&size=175273&status=done&style=none&taskId=ue7d4b366-4e89-4962-86ec-8c9d97b17e4&title=&width=2271)
<a name="mGTxI"></a>
#### 5、优化响应速度
> 优化引入JS、CSS等，将图片PNG转化为更小的JPG。优化后总的快了3~8倍速！
> ![image.png](https://cdn.nlark.com/yuque/0/2024/png/26820301/1712126123760-907c74b7-5dda-4a2e-9ca8-859da9c2e7b4.png#averageHue=%23a6c897&clientId=u8a1cf15f-ed2c-4&from=paste&height=583&id=ARdCo&originHeight=583&originWidth=1040&originalType=binary&ratio=1&rotation=0&showTitle=false&size=116755&status=done&style=none&taskId=u9b29da94-6136-46dc-a50b-54153991eea&title=&width=1040)

```html
 <!-- https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.7.0/chart.min.js -->
<script src="../../static/js/chart-3.7.0.min.js"></script>
 <!-- https://code.jquery.com/jquery-3.6.4.min.js -->
<script src="../../static/js/jquery-3.6.4.min.js"></script>
<!--https://www.layuicdn.com/layui/layui.js-->
<script src="../../static/layer/layui-2.9.7.min.js"></script>
<!--https://www.layuicdn.com/layui/css/layui.css-->
<script src="../../static/css/layui.css"></script>
```
<a name="ElToO"></a>
#### 6、对于用户过滤的安全限制登录优化
> 这里暂时针对一个简单的注册和登录验证，必须是通过已经注册的用户或IP进行使用。
> 其他的会记录访客记录IP等后才可访问，但是访客不可一下发指令和改变一些参数信息！只可以看页面！

