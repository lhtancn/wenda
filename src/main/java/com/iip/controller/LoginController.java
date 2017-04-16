package com.iip.controller;

import com.iip.async.EventModel;
import com.iip.async.EventProducer;
import com.iip.async.EventType;
import com.iip.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Demo on 4/13/2017.
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;


    @RequestMapping(value = {"/reg/"}, method = {RequestMethod.POST})
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam("next") String next,
                      @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                      HttpServletResponse response) {

        try{
            Map<String, Object> map = userService.register(username, password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch(Exception e) {
            logger.error("register error. " + e.getMessage());
            model.addAttribute("msg", "server has error.");
            return "login";
        }

    }

    @RequestMapping(value = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model,
                               @RequestParam(value = "next", required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }

    @RequestMapping(value = {"/login/"}, method = {RequestMethod.POST})
    public String login(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam("next") String next,
                      @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                      HttpServletResponse response) {

        try{
            Map<String, Object> map = userService.login(username, password);
            if(map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme) {
                    cookie.setMaxAge(3600 * 24 * 5);
                }
                response.addCookie(cookie);

//                eventProducer.fireEvent(new EventModel(EventType.LOGIN).setExts("username", username).
//                        setExts("email", "mailAddress"));

                if(StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }else {
                    return "redirect:/";
                }

            }else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        } catch(Exception e) {
            logger.error("login error. " + e.getMessage());
            model.addAttribute("msg", "server has error.");
            return "login";
        }

    }


    @RequestMapping(value = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }

}
