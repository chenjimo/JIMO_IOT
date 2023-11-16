package jimo.iot.ctrl_module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 命令信息
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Data
public class OderMessage implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String message;

    private Integer moduleId;

    private Integer status;

    private String bz;
    // 这个属性不会被映射到数据库
    @TableField(exist = false)
    private String moduleName;
    public OderMessage() {
    }

    public OderMessage(Integer id, String name, String message, Integer moduleId, Integer status, String bz) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.moduleId = moduleId;
        this.status = status;
        this.bz = bz;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }
}
