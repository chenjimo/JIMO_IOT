package jimo.iot.info.send;

import jimo.iot.util.APIUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * <p>
 * 服务实现类
 * 适配器类，为了防止由于后期API变革导致的使用异常
 * 为了更好的适配API接口
 * </p>
 *
 * @author JIMO
 * @since 2022-08-07
 */
@Data
@Component
public class SendEmail {
    private String email;
    private String title;
    private String message;

    @Resource
    APIUtil apiUtil;
    public void open(){
        apiUtil.sendMail(email,title,message);
    }
}
