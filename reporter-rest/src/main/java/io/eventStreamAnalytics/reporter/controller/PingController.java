package io.eventStreamAnalytics.reporter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by badal on 1/18/16.
 */
@Controller
public class PingController {

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    @ResponseBody
    public String index() {
        return "Hola";
    }

}
