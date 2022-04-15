package online.pengpeng.tacocloud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author pengpeng
 * @date 2022/4/15
 */
@Controller     // <-- 控制器
public class HomeController {
    @GetMapping("/")
    public String home(){
        return "home"; // <--返回视图名
    }
}
