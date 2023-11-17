package jimo.iot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 *  前端控制器
 *  1、用于页面的展示
 *  2、用于页面跳转
 * </p>
 *
 * @author JIMO
 * @since 2023-11-16
 */
@RestController
public class PageController {
    /***
     * @return 常规页面的处理！！！
     */
    @GetMapping("/404")
    public ModelAndView notFind() {
        return new ModelAndView("error/404");
    }

    @GetMapping("/400")
    public ModelAndView noFind() {
        return new ModelAndView("error/400");
    }

    @GetMapping("/500")
    public ModelAndView error() {
        return new ModelAndView("error/500");
    }

    @GetMapping("/login/user")
    public ModelAndView user() {
        return new ModelAndView("user/login");
    }
    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    /***
     *数据展示页面的控制展示映射
     */
    @GetMapping("/templates/deviceView/temp.html")
    public ModelAndView temp() {
        return new ModelAndView("deviceView/temp");
    }
    @GetMapping("/templates/deviceView/humidity.html")
    public ModelAndView humidity() {
        return new ModelAndView("deviceView/humidity");
    }
    @GetMapping("/templates/deviceView/MQ2.html")
    public ModelAndView MQ2() {
        return new ModelAndView("deviceView/MQ2");
    }
    /***
     * 控制页面的映射输出
     */
    @GetMapping("/templates/controller/index.html")
    public ModelAndView ctrl() {
        return new ModelAndView("controller/index");
    }
    @GetMapping("/templates/controller/setOder.html")
    public ModelAndView setOder() {
        return new ModelAndView("controller/setOder");
    }

}
