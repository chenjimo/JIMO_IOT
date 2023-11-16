package jimo.iot.device_value.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * <p>
 *  传感器的数据上传位置
 * </p>
 *
 * @author JIMO
 * @since 2023-11-15
 */
@Data
public class DeviceLog implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer deviceId;

    private String value;

    private LocalDateTime time;

    private String bz;

    public DeviceLog() {
    }

    public DeviceLog(Integer deviceId, String value, LocalDateTime time, String bz) {
        this.deviceId = deviceId;
        this.value = value;
        this.time = time;
        this.bz = bz;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }
}
