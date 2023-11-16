package jimo.iot.device_value.service.impl;

import jimo.iot.device_value.entity.DeviceMessage;
import jimo.iot.device_value.mapper.DeviceMessageMapper;
import jimo.iot.device_value.service.IDeviceMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
