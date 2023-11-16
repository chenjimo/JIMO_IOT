package jimo.iot.ctrl_module.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@Data
public class UserMessage implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String pwd;

    private String email;

    private LocalDateTime loginTime;

    private Integer power;

    private String phone;

    private Integer sex;

    private Integer visit;

    private String bz;

    public UserMessage() {
    }

    public UserMessage(Integer id, Integer visit) {
        this.id = id;
        this.visit = visit;
    }

    public UserMessage(Integer id, LocalDateTime loginTime) {
        this.id = id;
        this.loginTime = loginTime;
    }

    public UserMessage(Integer id, String name, String pwd, String email, LocalDateTime loginTime, Integer power, String phone, Integer sex, Integer visit, String bz) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.email = email;
        this.loginTime = loginTime;
        this.power = power;
        this.phone = phone;
        this.sex = sex;
        this.visit = visit;
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

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getVisit() {
        return visit;
    }

    public void setVisit(Integer visit) {
        this.visit = visit;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }
}
