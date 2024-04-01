package jimo.iot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 前端控制器
 * 1、用于页面的展示
 * 2、用于页面跳转
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

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login/index");
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

    @GetMapping("/templates/controller/indexAll.html")
    public ModelAndView getOderAll() {
        return new ModelAndView("controller/indexAll");
    }

    @GetMapping("/templates/controller/indexAll-1.html")
    public ModelAndView getOderAll1() {
        return new ModelAndView("controller/indexAll-1");
    }

    @GetMapping("/templates/controller/indexAll-2.html")
    public ModelAndView getOderAll2() {
        return new ModelAndView("controller/indexAll-2");
    }

    @GetMapping("/templates/controller/indexAll-3.html")
    public ModelAndView getOderAll3() {
        return new ModelAndView("controller/indexAll-3");
    }

    @GetMapping("/templates/controller/indexAll-4.html")
    public ModelAndView getOderAll4() {
        return new ModelAndView("controller/indexAll-4");
    }

    @GetMapping("/templates/controller/indexAll-5.html")
    public ModelAndView getOderAll5() {
        return new ModelAndView("controller/indexAll-5");
    }
    /***
     * 模块信息展示页面
     */
    @GetMapping("/templates/modulesView/SmartAll.html")
    public ModelAndView getModulesAll() {
        return new ModelAndView("modulesView/SmartAll");
    }
    @GetMapping("/templates/modulesView/SmartRack.html")
    public ModelAndView getModules1() {
        return new ModelAndView("modulesView/SmartRack");
    }
    @GetMapping("/templates/modulesView/SmartRack-week.html")
    public ModelAndView getModules11() {
        return new ModelAndView("modulesView/SmartRack-week");
    }
    @GetMapping("/templates/modulesView/SmartRack-mouth.html")
    public ModelAndView getModules12() {
        return new ModelAndView("modulesView/SmartRack-mouth");
    }
    @GetMapping("/templates/modulesView/SmartRack-all.html")
    public ModelAndView getModules13() {
        return new ModelAndView("modulesView/SmartRack-all");
    }
    @GetMapping("/templates/modulesView/SmartWatering.html")
    public ModelAndView getModules2() {
        return new ModelAndView("modulesView/SmartWatering");
    }
    @GetMapping("/templates/modulesView/SmartWatering-week.html")
    public ModelAndView getModules21() {
        return new ModelAndView("modulesView/SmartWatering-week");
    }
    @GetMapping("/templates/modulesView/SmartWatering-mouth.html")
    public ModelAndView getModules22() {
        return new ModelAndView("modulesView/SmartWatering-mouth");
    }
    @GetMapping("/templates/modulesView/SmartWatering-all.html")
    public ModelAndView getModules23() {
        return new ModelAndView("modulesView/SmartWatering-all");
    }
    @GetMapping("/templates/modulesView/SmartWatchdog.html")
    public ModelAndView getModules3() {
        return new ModelAndView("modulesView/SmartWatchdog");
    }
    @GetMapping("/templates/modulesView/SmartWatchdog-week.html")
    public ModelAndView getModules31() {
        return new ModelAndView("modulesView/SmartWatchdog-week");
    }
    @GetMapping("/templates/modulesView/SmartWatchdog-mouth.html")
    public ModelAndView getModules32() {
        return new ModelAndView("modulesView/SmartWatchdog-mouth");
    }
    @GetMapping("/templates/modulesView/SmartWatchdog-all.html")
    public ModelAndView getModules33() {
        return new ModelAndView("modulesView/SmartWatchdog-all");
    }

}
