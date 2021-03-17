package com.kube.eventer4j.bean;

import lombok.Data;

import java.util.List;

@Data
public class EventResponse  {

    private String messages;

    private String  sources;

    private String sinks;

}
