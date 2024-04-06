package jimo.iot.ctrl_module.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.entity.UserMessage;
import jimo.iot.ctrl_module.service.IUserMessageService;
import jimo.iot.info.Message;
import jimo.iot.util.CodeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * 1、用户信息的操作
 * 2、用户与模块信息的操作
 * 3、用户的登录
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@RestController
@RequestMapping("/user")
public class UserMessageController {
    private static final String success = "恭喜您，操作成功！";
    private static final String error = "抱歉，操作失败，请重试！";
    @Resource
    IUserMessageService userMessageService;

    /***
     * 用户登录
     * @param userMessage
     * @return
     */
    @PostMapping("/login")
    public Message login(UserMessage userMessage, HttpServletRequest request) {
        UserMessage userMessage1 = userMessageService.getBaseMapper().selectOne(Wrappers.<UserMessage>lambdaQuery()
                .eq(UserMessage::getEmail, userMessage.getEmail())
                .eq(UserMessage::getPwd, userMessage.getPwd()));
        String result;
        int b;
        if (userMessage1 != null) {//代表登录成功
            userMessageService.updateLoginTime(userMessage1.getId());//更新登录时间
            request.getSession().setAttribute("IOTUser", userMessage1);
            b = 200;
            result = success;
        } else {
            //先判断是否有用户存在不存在自动注册一个并标记权限为最低的1
            UserMessage userMessage2 = userMessageService.getBaseMapper().selectOne(Wrappers.<UserMessage>lambdaQuery()
                    .eq(UserMessage::getEmail, userMessage.getEmail()));
            if (userMessage2 == null) {//不存在进行注册
                userMessage.setLoginTime(LocalDateTime.now());
                userMessage.setName("访客-" + CodeUtils.getCode());
                // 获取客户端真实IP地址
                String ipAddress = request.getHeader("X-Forwarded-For");
                if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getHeader("X-Real-IP");
                }
                if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                    ipAddress = request.getRemoteAddr();
                }
                userMessage.setBz("IP:" + ipAddress);
                if (userMessageService.updateUserMessage(userMessage)) {
                    b = 201;
                    result = "恭喜您，注册成功！再次点击登录进入！";
                } else {
                    b = 500;
                    result = error;
                }
            } else {//密码错误提醒
                b = 400;
                result = "密码错误,请重试或重置密码！";
            }
        }
        return new Message(b, result, null);
    }

    /***
     * 用户注销登录
     * @param userMessage
     * @return
     */
    @GetMapping("/login")
    public Message clearLogin(UserMessage userMessage, HttpServletRequest request) {
        UserMessage iotUser = (UserMessage) request.getSession().getAttribute("IOTUser");
        if (userMessage.getId() == null || (iotUser != null && Objects.equals(userMessage.getId(), iotUser.getId()))) {
            request.getSession().setAttribute("IOTUser", null);
            return new Message(200, success, null);
        }
        return new Message(400, error, null);
    }

    /***
     * @param request
     * @return 获取当前登录的用户信息
     */
    @GetMapping("/message")
    public Message getCareUserMessage(HttpServletRequest request) {
        return new Message(200, success, request.getSession().getAttribute("IOTUser"));
    }
}
