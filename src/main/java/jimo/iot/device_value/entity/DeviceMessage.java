package jimo.iot.device_value.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *  有关于传感器的相关数据
 * </p>
 *
 * @author JIMO
 * @since 2023-11-15
 */
@Data
public class DeviceMessage implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer moduleId;

    private String type;

    private String unit;

    private String max;

    private String min;

    private String bz;

    public DeviceMessage() {
    }

    public DeviceMessage(Integer id, String name, Integer moduleId, String type, String unit, String max, String min, String bz) {
        this.id = id;
        this.name = name;
        this.moduleId = moduleId;
        this.type = type;
        this.unit = unit;
        this.max = max;
        this.min = min;
        this.bz = bz;
    }
}