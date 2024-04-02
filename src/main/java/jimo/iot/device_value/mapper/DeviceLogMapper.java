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

    /***
     *异常数据的展示罗列出来,最新的几条！
     * @param limit
     * @param error
     * @return
     */
    @Select("SELECT * FROM Device_Log   " +
            "WHERE LENGTH(bz) > #{error}   " +
            "ORDER BY time DESC " +
            "LIMIT #{limit}")
    // 按照月份降序排序
    List<DeviceLog> getDeviceLogErrorOrderList(Integer limit, Integer error);

    /***
     *异常数据的展示罗列出来,全部！
     * @param error
     * @return
     */
    @Select("SELECT * FROM Device_Log   " +
            "WHERE LENGTH(bz) > #{error}   " +
            "ORDER BY time DESC ")
    // 按照月份降序排序
    List<DeviceLog> getDeviceLogErrorOrderALL(Integer error);

}
