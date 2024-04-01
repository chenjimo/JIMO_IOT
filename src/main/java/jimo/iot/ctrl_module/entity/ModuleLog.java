package jimo.iot.ctrl_module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 模块的心跳记录类
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Data
public class ModuleLog implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer moduleId;

    private LocalDateTime time;

    private String bz;

    public ModuleLog() {
    }

    public ModuleLog( Integer moduleId, LocalDateTime time, String bz) {
        this.moduleId = moduleId;
        this.time = time;
        this.bz = bz;
    }
}
