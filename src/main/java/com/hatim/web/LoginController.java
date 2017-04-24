package com.hatim.web;

import com.hatim.service.SmartQQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Hatim on 2017/4/22.
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    SmartQQService smartQQService;

    @RequestMapping(value = "/qq", method = RequestMethod.GET)
    private String qqLogin(ModelMap map) {
        map.addAttribute("title", "qq登录");
        map.addAttribute("imgPath", smartQQService.startService());
        return "login";
    }

    @RequestMapping(value = "/weChat", method = RequestMethod.GET)
    private String weChatLogin(ModelMap map) {
        map.addAttribute("title", "WeChat登录");
        return "login";
    }
}
