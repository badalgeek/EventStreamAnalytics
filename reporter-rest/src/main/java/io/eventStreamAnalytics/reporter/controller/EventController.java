package io.eventStreamAnalytics.reporter.controller;

import io.eventStreamAnalytics.model.TotalCustomer;
import io.eventStreamAnalytics.reporter.repository.EventRepository;
import io.eventStreamAnalytics.reporter.repository.EventRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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


    @Autowired
    private EventRepositoryImpl eventRepositoryImpl;

    @RequestMapping(value = "/events/customers", method = RequestMethod.GET)
    @ResponseBody
    public List<TotalCustomer> getUniqueCustomer() {
        return (eventRepositoryImpl.getCustomerCount());
    }
}
