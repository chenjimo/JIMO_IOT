package jimo.iot.ctrl_module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *   模块信息表
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Data
public class ModuleMessage implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer status;

    private Integer mode;

    private String bz;


    public ModuleMessage() {
    }

    public ModuleMessage(String name, Integer status, Integer mode, String bz) {
        this.name = name;
        this.status = status;
        this.mode = mode;
        this.bz = bz;
    }

    public ModuleMessage(Integer id, Integer status) {
        this.id = id;
        this.status = status;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }
}
