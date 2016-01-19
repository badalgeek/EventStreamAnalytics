package io.eventStreamAnalytics.reporter.controller;

import io.eventStreamAnalytics.reporter.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by badal on 1/18/16.
 */
@Controller
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @RequestMapping(value = "/events/count", method = RequestMethod.GET)
    @ResponseBody
    public String index() {
        return Long.toString(eventRepository.count());
    }
}
