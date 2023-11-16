package jimo.iot.util;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/***
 * 钉钉消息处理类
 */
@Component
public class DingWebhook {
    @Value("${jimo.api-util.webhookURL}")
    private String robotWebhook;

    public void send(OapiRobotSendRequest request) throws ApiException {
        DingTalkClient client = new DefaultDingTalkClient(robotWebhook + LocalDateTime.now());
        OapiRobotSendResponse response = client.execute(request);
    }
}

