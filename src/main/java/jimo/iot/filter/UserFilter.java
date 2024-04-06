package jimo.iot.filter;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jimo.iot.ctrl_module.entity.UserMessage;
import jimo.iot.ctrl_module.service.IUserMessageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/***
 * 对用户身份的验证过滤器
 * **这里其实限制的不全面应该在后续的用户管理中进行进一步的优化过滤，保证更强的安全性***
 */
@Component
public class UserFilter implements Filter {
    private List<String> excludedUrls = Arrays.asList("/login/*", "/user/*", "/oder/*", "/device/*", "/module/*","/img/","/static/*，”/templates/img/*");//排除掉的映射地址
    @Resource
    IUserMessageService userService;

    //重写其中的doFilter方法
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse res1 = (HttpServletResponse) res;
        if (isExcluded(request.getRequestURI())) {//排除掉就直接进入下一步！
            chain.doFilter(req, res);
            return;
        }

        //判断是否有用户登录然后刷新IP或给用户调转注册与登录界面
        UserMessage user = (UserMessage) request.getSession().getAttribute("IOTUser");
        if (user == null) {
            res1.sendRedirect("/login");//跳转至登录页面
        } else {
            // 获取客户端真实IP地址
            String ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("X-Real-IP");
            }
            if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
            //更新IP地址
            if (user.getBz() == null || user.getBz().equals("IP:" + ipAddress)) {
                user.setBz("IP:" + ipAddress);
                userService.updateUserMessage(user);
            }
        }
        // 继续执行过滤器链
        chain.doFilter(req, res);
    }

    /***
     * 排除一部分过滤地址
     * @param requestURI
     * @return
     */
    private boolean isExcluded(String requestURI) {
        for (String excludedUrl : excludedUrls) {
            if (requestURI.startsWith(excludedUrl)) {
                return true;
            }
        }
        return false;
    }
    //继续执行下一个过滤器
        /*HttpServletRequest req1 = (HttpServletRequest) req;
        HttpServletResponse res1 = (HttpServletResponse) res;
        User user =(User) req1.getSession().getAttribute("CareUser");
        String key = req1.getParameter("key");
        String pwd = userService.UserGetUser("1517962688@qq.com").getPwd();
        if (key!=null&& key.equals(pwd)){
            chain.doFilter(req, res);
            return;
        }
        if (user!=null){
            if (user.getPower()>=UserPower.USER){
                chain.doFilter(req, res);
            }else {
                res1.sendError(400,"\t很遗憾,权限不足或账户已经注销,<a href=‘login/user’>请您重新登录！</a>\n\t请联系管理员QQ：1517962688");
            }
        }else {
            res1.sendRedirect("/login/user");
        }*/
}