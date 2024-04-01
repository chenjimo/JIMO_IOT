package jimo.iot.info.send;

import jimo.iot.util.APIUtil;
import jimo.iot.util.CodeUtils;
import jimo.iot.util.DateUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;


/**
 * <p>
 * 服务实现类
 * 适配器类，为了防止由于后期API变革导致的使用异常
 * 为了更好的适配API接口
 * </p>
 *
 * @author JIMO
 * @since 2024-03-31
 */
@Data
@Component
public class SendEmail {
    @Resource
    APIUtil apiUtil;
    @Value("${jimo.message.email.title}")
    private String tittle;
    @Value("${jimo.message.email.end}")
    private String end;
    @Value("${jimo.message.email.logo}")
    private String logo;
    @Value("${jimo.message.email.to}")
    private String email;
    @Value("${jimo.alter.time}")
    private Integer time;

    private String userEmail;
    private String userTitle;
    private String userMessage;

    public void open() {
        apiUtil.sendMail(userEmail == null ? email : userEmail,
                userTitle == null ? tittle : userTitle,
                userMessage + "\n\t\t" + end + "\n\t\t<img src='" + logo + "'/>\n\n");
    }

    /***
     * 一个通用的信息报警内容方法
     * @param m 模块或传感器信息
     * @param a 提醒内容
     * @param t 提醒时间
     */
    public void alterEmail(String m, String a, LocalDateTime t) {
        apiUtil.sendMail(email, tittle,
                "\n\n" + "尊敬的用户您好！您的模块（传感器）‘" + m + "’\n\t\t"
                        + "\n\t\t在" + DateUtil.localDateTimeToString(t) + "\n\t\t"
                        + "\n\t\t发出提醒信息：" + a
                        + "\n\t\t本次信息短时发送批次随机码为：" + CodeUtils.getCode()
                        + "\n\t\t" + end + "\n\t\t<img src='" + logo + "'/>\n\n");
    }

}
