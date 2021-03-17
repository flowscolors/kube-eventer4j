package com.kube.eventer4j.controller;

import com.kube.eventer4j.bean.EventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class EventerController {

    @Value("${source}")
    String source;
    @Value("${sink}")
    String sink;

    @RequestMapping("/hello")
    public String HelloWorld()  {
        return "hello world";
    }

    @RequestMapping("/static")
    public EventResponse yaml(){
        EventResponse eventResponse = new EventResponse();
        eventResponse.setMessages("解析传入Source Sink参数");
        eventResponse.setSources(source);
        eventResponse.setSinks(sink);
        return eventResponse;
    }

}
