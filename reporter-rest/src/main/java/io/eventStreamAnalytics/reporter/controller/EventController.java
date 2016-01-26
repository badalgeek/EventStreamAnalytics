package io.eventStreamAnalytics.reporter.controller;

import io.eventStreamAnalytics.dto.UniqueCustomerCountByDeviceType;
import io.eventStreamAnalytics.dto.UniqueEndUsersCountAtTime;
import io.eventStreamAnalytics.reporter.repository.EventRepository;
import io.eventStreamAnalytics.reporter.repository.EventRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by badal on 1/18/16.
 */
@Controller
public class EventController {

    public static final String LOCALHOST = "http://localhost:5555";

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRepositoryImpl eventRepositoryImpl;

    @RequestMapping(value = "/events/count", method = RequestMethod.GET)
    @ResponseBody
    public String index() {
        return Long.toString(eventRepository.count());
    }

    @CrossOrigin(origins = LOCALHOST)
    @RequestMapping(value = "/events/customers", method = RequestMethod.GET)
    @ResponseBody
    public List<UniqueCustomerCountByDeviceType> getUniqueCustomer() {
        return (eventRepositoryImpl.getCustomerCount());
    }


    @CrossOrigin(origins = LOCALHOST)
    @RequestMapping(value = "/events/customers_by_time", method = RequestMethod.GET)
    @ResponseBody
    public List<UniqueEndUsersCountAtTime> getUniqueEndUsersCountByTime() {
        return (eventRepositoryImpl.getUniqueEndUsersCountByTime());
    }
}
