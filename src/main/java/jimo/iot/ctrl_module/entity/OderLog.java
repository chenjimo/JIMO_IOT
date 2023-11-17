package jimo.iot.ctrl_module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.beans.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 命令日志表
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Data
public class OderLog implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer oderId;

    private Integer userId;

    private Integer moduleId;

    private LocalDateTime writeTime;

    private LocalDateTime readTime;

    private Integer status;

    private String bz;
    // 这个属性不会被映射到数据库
    @TableField(exist = false)
    private String oderMessage;
    @TableField(exist = false)
    private String oderName;
    @TableField(exist = false)
    private String moduleName;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String delay;

    public String getOderMessage() {
        return oderMessage;
    }

    public void setOderMessage(String oderMessage) {
        this.oderMessage = oderMessage;
    }

    public OderLog() {
    }

    public OderLog(Integer userId, Integer moduleId, LocalDateTime writeTime, LocalDateTime readTime, Integer status, String bz) {
        this.userId = userId;
        this.moduleId = moduleId;
        this.writeTime = writeTime;
        this.readTime = readTime;
        this.status = status;
        this.bz = bz;
    }

    public OderLog(Integer id, Integer status) {
        this.id = id;
        this.status = status;
    }

    public OderLog(Integer id, LocalDateTime readTime) {
        this.id = id;
        this.readTime = readTime;
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

    public LocalDateTime getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(LocalDateTime writeTime) {
        this.writeTime = writeTime;
    }

    public LocalDateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
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

    public Integer getOderId() {
        return oderId;
    }

    public void setOderId(Integer oderId) {
        this.oderId = oderId;
    }
}
