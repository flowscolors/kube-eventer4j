package com.kube.eventer4j.bean;

import lombok.Data;

import java.util.Map;

@Data
public class EventSink {
    private String sinkKind;
    private String sinkName;
    private String sinkIP;
    private Map<String,String> sinkParams;
    private Object sinkClient;

    public EventSink () {

    }

    public EventSink (String sinkKind,String sinkName,String sinkIP,Map<String,String> sinkParams) {
        this.sinkKind = sinkKind;
        this.sinkName = sinkName;
        this.sinkIP = sinkIP;
        this.sinkParams = sinkParams;
    }
}
