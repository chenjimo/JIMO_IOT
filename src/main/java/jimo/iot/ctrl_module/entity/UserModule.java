package jimo.iot.ctrl_module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户的模块对应
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Data
public class UserModule implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer moduleId;

    private Integer power;

    public UserModule() {
    }

    public UserModule(Integer userId, Integer moduleId, Integer power) {
        this.userId = userId;
        this.moduleId = moduleId;
        this.power = power;
    }

    public UserModule(Integer id, Integer userId, Integer moduleId, Integer power) {
        this.id = id;
        this.userId = userId;
        this.moduleId = moduleId;
        this.power = power;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }
}
