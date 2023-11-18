package jimo.iot.device_value.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jimo.iot.device_value.entity.DeviceLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 *  1、用于存储传感器的数据和提取传感器中的数据
 *  2、异常数据检测到应该进行报警处理（报警设置一个冷却时间600S）
 *  3、详细的完善求每日平均值的周时间的数值JSON（最后在开发）
 * </p>
 *
 * @author JIMO
 * @since 2023-11-15
 */
public interface IDeviceLogService extends IService<DeviceLog> {
    /***
     * 记录传感器数据
     * @param deviceLog
     * @return
     */
     boolean insert(DeviceLog deviceLog);

    /***
     * 根据传感器的ID查询所有记录
     * @param deviceId
     * @return
     */
     List<DeviceLog> getDeviceLogsByDeviceId(Integer deviceId);
    /***
     * 根据传感器的ID查询最近的几条
     * @param deviceId
     * @return
     */
    List<DeviceLog> getDeviceLogsByDeviceIdAndJT(Integer deviceId, Integer jt);

    /***
     * 详细的完善求每日平均值的周时间的数值JSON（最后在开发）
     * @param deviceId
     * @param jt 条数限制
     * @param avg 平均方式
     * @return
     */
    List<Map<String,Object>> getDeviceLogByAVG(Integer deviceId, Integer jt, Integer avg);
}
