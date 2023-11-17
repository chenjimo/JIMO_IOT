package jimo.iot.device_value.service;

import jimo.iot.device_value.entity.DeviceMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 *  1、根据ID获取传感器的报警阈值。
 *  2、设置传感器的报警阈值。
 *  3、根据模块分组进行展示传感器数据。
 *  4、获取传感器的单位信息（直接获取全部也可）
 * </p>
 *
 * @author JIMO
 * @since 2023-11-15
 */
public interface IDeviceMessageService extends IService<DeviceMessage> {
    /***
     * 根据ID获取传感器的报警阈值。
     * @param deviceId
     * @return
     */
    DeviceMessage getDeviceMessageMinAndMax(Integer deviceId);

    /***
     * 设置传感器的报警阈值。
     * @param deviceMessage
     * @return
     */
    boolean updateDeviceMessage(DeviceMessage deviceMessage);

    /***
     * 根据模块分组进行展示传感器数据。
     * @return
     */
    Map<String, List<DeviceMessage>> getDeviceMessageByModuleGroup();

    /***
     * 获取传感器的单位信息（直接获取全部也可）
     */
    String getUnit(Integer deviceId);
}
